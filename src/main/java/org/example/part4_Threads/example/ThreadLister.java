package org.example.part4_Threads.example;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;


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

        out.println(indent + " Группа потоков исполнения: " + g.getName() +
                " Наивысший приоритет: " + g.getMaxPriority());

        for (int i = 0; i < num_threads; i++)
            printThreadInfo(out, threads[i], indent + "    ");

        for (int i = 0; i < num_groups; i++)
            printGroupInfo(out, groups[i], indent + "    ");

    }

    // Находим корневую группу и рекурсивно распечатываем ее содержимое
    public static void listAllThreads(PrintWriter out) {
        ThreadGroup current_thread_group;
        ThreadGroup root_thread_group;
        ThreadGroup parent;

        // Получаем группу текущего потока исполнения
        current_thread_group = Thread.currentThread().getThreadGroup();

        // Ищем корневую группу
        root_thread_group = current_thread_group;
        parent = root_thread_group.getParent();

        while (parent != null) {
            root_thread_group = parent;
            parent =  parent.getParent();
        }

        // Рекурсивно ее распечатываем
        printGroupInfo(out, root_thread_group, "");
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ThreadLister Demo");
        JTextArea textArea = new JTextArea();
        frame.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
        frame.setSize(500, 400);
        frame.setVisible(true);

        // Получаем строку threadListing (распечатку потоков исполнения)
        StringWriter sout = new StringWriter();
        PrintWriter out = new PrintWriter(sout);
        ThreadLister.listAllThreads(out);

        out.close();
        String threadListing = sout.toString();

        textArea.setText(threadListing);
    }
}
