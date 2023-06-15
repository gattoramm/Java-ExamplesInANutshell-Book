package org.example.part4_Threads.example;

public class Deadlock {
    public static void main(String[] args) {
        final Object resource1 = "resource1";
        final Object resource2 = "resource2";

        // Первый поток пытается захватить вначале resource1, затем resource2
        Thread t1 = new Thread(){
            @Override
            public void run() {
                // Захват resource1
                synchronized (resource1) {
                    System.out.println("Поток исполнения 1: захвачен resource1");
                    try { Thread.sleep(50); }
                    catch (InterruptedException e) {}

                    // Теперь ждем, можно ли захватить resource2
                    synchronized (resource2) {
                        System.out.println("Поток исполнения 1: захвачен resource2");
                    }
                }
            }
        };

        // Второй поток пытается захватить вначале resource2, затем resource1
        Thread t2 = new Thread(){
            @Override
            public void run() {
                // Захват resource2
                synchronized (resource2) {
                    System.out.println("Поток исполнения 2: захвачен resource2");
                    try { Thread.sleep(50); }
                    catch (InterruptedException e) {}

                    // Теперь ждем, можно ли захватить resource1
                    synchronized (resource1) {
                        System.out.println("Поток исполнения 2: захвачен resource1");
                    }
                }
            }
        };

        // Запуск 2х потоков исполнения
        t1.start();
        t2.start();
    }
}
