package com.bjpowernode.springboot;

import java.lang.reflect.Executable;
import java.util.concurrent.*;

/**
 * ClassName:ThreadPoolTtest
 * Package:com.bjpowernode.springboot
 * Description
 *
 * @Date:2020/5/517:09
 * @author:xyh
 */
public class ThreadPoolTtest {
    public static void main(String[] args) {
        System.out.println(Runtime.getRuntime().availableProcessors());
    }

    private static void mySelfThreadPoll() {
        ExecutorService pool = new ThreadPoolExecutor(
                2,
                5,
                1L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    private static void ThreadPoolDemo() {
        //创建一个固定线程的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        //创建一个单个线程的线程池
        ExecutorService executorService2 = Executors.newSingleThreadExecutor();
        //创建一个会自动扩容的线程池
        ExecutorService executorService3 = Executors.newCachedThreadPool();

        try {
            for (int i = 0; i < 10; i++) {
                executorService3.execute(() -> {
                    System.out.println(Thread.currentThread().getName() + "*****");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService3.shutdown();
        }
    }


}
