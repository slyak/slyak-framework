package com.slyak.core.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
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
        List<R> rs = startCompetition(competition, runners, 1, timeout);
        assert rs != null;
        return rs.get(0);
    }

    public static <R> List<R> startCompetition(Competition<R> competition, int runners, int maxWinners, int timeout) {
        final int finalWinners = maxWinners < 0 ? runners : Math.min(runners, maxWinners);
        CountDownLatch latch = new CountDownLatch(maxWinners);
        List<R> results = new ArrayList<>(maxWinners);
        for (int i = 0; i < runners; i++) {
            int test = i;
            executorService.execute(() -> {
                try {
                    R result = competition.start(test);
                    log.info("Runner {} finished competition", test);
                    synchronized (results) {
                        if (results.size() < finalWinners) {
                            log.info("Runner {} is the winner, result is {}", test, result);
                            results.add(result);
                        }
                    }
                } catch (Exception e) {
                    log.error("Competition failed, error is {}", e);
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return results;
    }

    public static class ResultHolder<R> {
        R result;

        ResultHolder() {
        }

        R getResult() {
            return result;
        }

        void setResult(R result) {
            this.result = result;
        }
    }

    public static void main(String[] args) {
        System.out.println(ExecutorUtils.startCompetition((Competition<Double>) index -> Math.random(), 10, 0, 100));
    }
}
