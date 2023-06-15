package org.example.part4_Threads.example;

import java.util.Comparator;
import java.util.TreeSet;

public class Timer {
    /*
    * В этом упорядоченном наборе хранятся задачи, за исполнение которых
    * отвечает данный Timer. Здесь для упорядочения задач по времени их
    * исполнения используется компаратор
    * */
    TreeSet tasks = new TreeSet(new TimerTaskCompartor());

    /*
    * Поток исполнения, используемый классом для исполнения подконтрольных задач
    * */
    TimerThread timer;

    /*
    * Конструктор для создания класса Timer, не использующего поток-демон
    * */
    public Timer() { this(false);}

    /*
    * Главный конструктор: внутренний поток исполнения будет демоном, если это указано
    * */
    public Timer(boolean isDaemon) {
        timer = new TimerThread(isDaemon);
        timer.start();
    }

    /*
    * Внутренний класс для упорядочивания задач по времени их следующего исполнения
    * */
    static class TimerTaskCompartor implements Comparator {
        @Override
        public int compare(Object a, Object b) {
            TimerTask t1 = (TimerTask) a;
            TimerTask t2 = (TimerTask) b;

            long diff = t1.nextTime - t2.nextTime;

            if (diff < 0) return -1;
            else if (diff > 0) return 1;
            else return 0;
        }

        public boolean equals(Object o) { return this == o; }
    }

    /*
    * Внутренний класс определяет поток исполнения, запускающий каждую
    * задачу в назначенное ей по расписанию время
    * */
    class TimerThread extends Thread {

    }
}
