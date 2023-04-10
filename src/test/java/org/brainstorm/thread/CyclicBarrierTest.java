package org.brainstorm.thread;

import org.junit.jupiter.api.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierTest {
    @Test
    public void test() {
        int num = 6;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(num, () -> {
            System.out.println("6个线程执行完毕");
        });
        for (int i = 0; i < num; i++) {
            new Thread(() -> {
                String name = "线程" + Thread.currentThread().getName();
                System.out.println(name + "正在执行");
                try {
                    cyclicBarrier.await();
                    System.out.println(name+":结束");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }, String.valueOf(i)).start();
        }
    }
}
