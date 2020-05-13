package com.bjpowernode.springboot;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * ClassName:CallableDemo
 * Package:com.bjpowernode.springboot
 * Description
 *
 * @Date:2020/5/516:12
 * @author:xyh
 */
class myThread implements Callable<Integer>{

    @Override
    public Integer call() throws Exception {
        System.out.println("come in");
        return 1024;
    }
}

public class CallableDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<Integer> futureTask = new FutureTask(new myThread());
        new Thread(futureTask, "AA").start();
        Integer o = futureTask.get();
        System.out.println(o);
    }

}
