package com.bjpowernode.springboot;

/**
 * ClassName:DeadLock
 * Package:com.bjpowernode.springboot
 * Description
 *
 * @Date:2020/5/518:41
 * @author:xyh
 */
class Resource implements Runnable{
    private String lockA;
    private String lockB;

    public Resource(String lockA, String lockB) {
        this.lockA = lockA;
        this.lockB = lockB;
    }

    @Override
    public void run() {
        synchronized (lockA) {
            System.out.println(Thread.currentThread().getName() + "获取了锁"+lockA+ "尝试获得锁B"+lockB);
            synchronized (lockB) {

            }
        }
    }
}

public class DeadLock {

    public static void main(String[] args) {
        new Thread(new Resource("lockA","lockB"),"AAA").start();
        new Thread(new Resource("lockB","lockA"),"BBB").start();
    }
}
