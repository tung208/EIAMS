package EIAMS.services;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TestThread implements Runnable{
    private final int i;

    @Override
    public void run() {
        System.out.println("Task " + i + " executed by thread: " + Thread.currentThread().getName());
    }
}
