package com.slyak.core.config;

import com.slyak.core.config.livereload.LiveReloadServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;

@Configuration
@Slf4j
public class LiveReloadConfig {

    @ConditionalOnProperty(name = "spring.profiles.active", matchIfMissing = true, havingValue = "test")
    static class LiveReloadConfiguration {

        private static final BlockingDeque<LeakSafeThreadFactory.LeakSafeThread> leakSafeThreads = new LinkedBlockingDeque<>();

        @Configuration
        static class LiveReloadServerConfiguration {

            @Bean(initMethod = "start")
            @ConditionalOnMissingBean
            public LiveReloadServer liveReloadServer() {
                leakSafeThreads.add(new LeakSafeThreadFactory.LeakSafeThread());
                return new LiveReloadServer(35729, new LeakSafeThreadFactory());
            }

        }

        private static class LeakSafeThreadFactory implements ThreadFactory {

            @Override
            public Thread newThread(final Runnable runnable) {
                return getLeakSafeThread().callAndWait(new Callable<Thread>() {
                    @Override
                    public Thread call() {
                        Thread thread = new Thread(runnable);
                        thread.setContextClassLoader(getClass().getClassLoader());
                        return thread;
                    }

                });
            }

            private LeakSafeThread getLeakSafeThread() {
                try {
                    return leakSafeThreads.takeFirst();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(ex);
                }
            }

            public static class LeakSafeThread extends Thread {

                private Callable<?> callable;

                private Object result;

                LeakSafeThread() {
                    setDaemon(false);
                }

                public void call(Callable<?> callable) {
                    this.callable = callable;
                    start();
                }

                @SuppressWarnings("unchecked")
                public <V> V callAndWait(Callable<V> callable) {
                    this.callable = callable;
                    start();
                    try {
                        join();
                        return (V) this.result;
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException(ex);
                    }
                }

                @Override
                public void run() {
                    // We are safe to refresh the ActionThread (and indirectly call
                    // AccessController.getContext()) since our stack doesn't include the
                    // RestartClassLoader
                    try {
                        leakSafeThreads.put(new LeakSafeThread());
                        this.result = this.callable.call();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.exit(1);
                    }
                }

            }

        }

    }
}
