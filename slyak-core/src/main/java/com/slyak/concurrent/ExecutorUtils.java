package com.slyak.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * .
 *
 * @author stormning 2018/4/4
 * @since 1.3.0
 */
@Slf4j
public class ExecutorUtils {

    private static ExecutorService executorService = Executors.newCachedThreadPool();

    public static <R> R startCompetition(Competition<R> competition, int runners, int timeout) {
        CountDownLatch latch = new CountDownLatch(1);
        ResultHolder<R> holder = new ResultHolder<>();
        for (int i = 0; i < runners; i++) {
            int test = i;
            executorService.execute(() -> {
                try {
                    R result = competition.start(test);
                    log.info("Runner {} finished competition", test);
                    synchronized (holder) {
                        if (holder.getResult() == null) {
                            log.info("Runner {} is the winner, result is {}", test, result);
                            holder.setResult(result);
                        }
                    }
                    latch.countDown();
                } catch (Exception e) {
                    log.error("Competition failed, error is {}", e);
                }
            });
        }
        try {
            latch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return holder.getResult();
    }

    public static class ResultHolder<R> {
        R result;

        public ResultHolder() {
        }

        public R getResult() {
            return result;
        }

        public void setResult(R result) {
            this.result = result;
        }
    }

    public static void main(String[] args) {
        System.out.println(ExecutorUtils.startCompetition((Competition<Double>) index -> Math.random(), 10, 100));
    }
}
