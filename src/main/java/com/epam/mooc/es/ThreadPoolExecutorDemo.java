package com.epam.mooc.es;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorDemo {

    static class ProcessRquest implements Runnable {

        private final int id;

        public ProcessRquest(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            long threadId = Thread.currentThread().getId();
            
            System.out.println("Start processing request " + id + " by thread " + threadId);
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            System.out.println("Finish processing request " + id + " by thread " + threadId);
        }
    }

    public static void main(String[] args) {
               
        ExecutorService executor = new ThreadPoolExecutor(
                5, // core pool size
                10, // max pool size
                100L, TimeUnit.MILLISECONDS, // keep alive
                new LinkedBlockingQueue<Runnable>(30), // queue
                new ThreadPoolExecutor.CallerRunsPolicy()); // handler

        System.out.println("Main thread id " + Thread.currentThread().getId());
        
        for (int i = 0; i < 100; i++) {
            executor.submit(new ProcessRquest(i));
        }

        executor.shutdown();
      
    }

}
