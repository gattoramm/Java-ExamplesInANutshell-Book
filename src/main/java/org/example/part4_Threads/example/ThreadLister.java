package org.example.part4_Threads.example;

import java.io.PrintWriter;


public class ThreadLister {
    // Отображаем информацию о потоке
    public static void printThreadInfo(PrintWriter out, Thread t, String indent) {
        if (t == null) return;

        out.println(indent + "Поток: " +
                t.getName() + "  Приоритет: " + t.getPriority() +
                (t.isDaemon()?" Демон":"") +
                (t.isAlive()?"":" Не активен"));
    }

    // Отображаем информацию о группе потоков исполнения и содержащихся в ней группах и потоках
    public static void printGroupInfo(PrintWriter out, ThreadGroup g, String indent) {
        if (g == null) return;

        int num_threads = g.activeCount();
        int num_groups = g.activeGroupCount();

        Thread[] threads = new Thread[num_threads];
        ThreadGroup[] groups = new ThreadGroup[num_threads];

        g.enumerate(threads, false);
        g.enumerate(groups, false);

        out.println(indent + " Группа потоков исполнения: " + g.getName());
    }

    // Находим корневую группу и рекурсивно распечатываем ее содержимое
    public static void listAllThreads(PrintWriter out) {

    }

    public static void main(String[] args) {

    }
}
