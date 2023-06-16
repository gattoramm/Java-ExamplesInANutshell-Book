package org.example.part4_Threads.example;

import java.util.Comparator;
import java.util.Date;
import java.util.TreeSet;

public class Timer {
    /*
    * В этом упорядоченном наборе хранятся задачи, за исполнение которых
    * отвечает данный Timer. Здесь для упорядочения задач по времени их
    * исполнения используется компаратор
    * */
    TreeSet tasks = new TreeSet(new TimerTaskComparator());

    /*
    * Поток исполнения, используемый классом для исполнения подконтрольных задач
    * */
    TimerThread timer;

    /*
    * Конструктор для создания класса Timer, не использующего поток-демон
    * */
    public Timer() { this(false); }

    /*
    * Главный конструктор: внутренний поток исполнения будет демоном, если это указано
    * */
    public Timer(boolean isDaemon) {
        timer = new TimerThread(isDaemon);
        timer.start();
    }

    // Останавливаем поток исполнения timer и снимаем все поставленные задачи
    public void cancel() {
        synchronized (tasks) {  // Только один поток исполнения в каждый момент
            timer.pleaseStop(); // Устанавливаем флаг запроса на остановку потока исполнения
            tasks.clear();      // Снимаем все задачи
            tasks.notify();     // Пробуждаем поток на тот случай, если он находится в состоянии ожидания
        }
    }

    // Включаем в расписание однократное исполнение задачи после 1ms задержки
    public void schedule(TimerTask task, long delay) {
        task.schedule(System.currentTimeMillis() + delay, 0, false);
        schedule(task);
    }

    // Включаем в расписание однократное исполнение задачи назначенное на заданный момент времени
    public void schedule(TimerTask task, Date time) {
        task.schedule(time.getTime(), 0, false);
        schedule(task);
    }

    // Включаем в расписание периодическое исполнение, начинающееся в заданное время
    public void schedule(TimerTask task, Date firstTime, long period) {
        task.schedule(firstTime.getTime(), period, false);
        schedule(task);
    }

    // Включаем в расписание периодическое исполнение, стартующее после заданной задержки
    public void schedule(TimerTask task, long delay, long period) {
        task.schedule(System.currentTimeMillis() + delay, period, false);
        schedule(task);
    }

    // Включаем в расписание периодическое исполнение, стартующее после заданной задержки.
    // Включаем в расписание периодические исполнения с фиксированной частотой (fixed-rate),
    // когда заданный интервал в ms отсчитывается от начала предыдущего исполнения в отличие
    // от исполнений с фиксированным интервалом, отсчитываемым от конца предыдущего исполнения.
    public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
        task.schedule(System.currentTimeMillis() + delay, period, true);
        schedule(task);
    }

    // Включаем в расписание периодические исполнения, стартующие через заданное время
    public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {
        task.schedule(firstTime.getTime(), period, true);
        schedule(task);
    }

    // Этот внутренний метод добавляет задачу к упорядоченному набору задач
    void schedule(TimerTask task) {
        synchronized (tasks) {  // Только один поток исполнения в каждый момент может изменять tasks
            tasks.add(task);    // Добавляем задачу к упорядоченному набору задач
            tasks.notify();     // Пробуждаем находящиеся в состоянии ожидания потоки
        }
    }

    /*
    * Внутренний класс для упорядочивания задач по времени их следующего исполнения
    * */
    static class TimerTaskComparator implements Comparator {
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
        // Флаг = true, чтобы указать потоку остановить исполнение
        volatile boolean stopped = false;

        public TimerThread(boolean isDaemon) { setDaemon(isDaemon); }

        // Просим поток остановится, устанавливая флаг, описанный выше
        public void pleaseStop() { stopped = true; }

        // Тело потока исполнения
        public void run() {
            TimerTask readyToRun = null;    // Есть ли задача, которую пора запускать?

            while (!stopped) {
                if (readyToRun != null) {
                    if (readyToRun.cancelled) {
                        readyToRun = null;
                        continue;
                    }

                    readyToRun.run();

                    // Предлагаем задаче переустановить свое расписание, и если она намерена
                    // снова запускаться и опять включаем ее в набор задач
                    if (readyToRun.reschedule())
                        schedule(readyToRun);

                    // Теперь запускать нечего
                    readyToRun = null;
                    continue;
                }

                // Ставим блокировку на набор задач
                synchronized (tasks) {
                    long timeout;
                    // Если задач не осталось, ждем пока не появится (notify()) следующая
                    if (tasks.isEmpty()) timeout = 0;

                    // Если в расписании есть задачи, берем первую
                    else {
                        TimerTask t = (TimerTask) tasks.first();
                        timeout = t.nextTime - System.currentTimeMillis();

                        if (timeout <= 0) {
                            readyToRun = t;
                            tasks.remove(t);
                            continue;
                        }
                    }

                    // Если оказались здесь, значит, нет задач, готовых к запуску, так как ждем
                    // времени запуска или команды notify() от новой задачи, желающей встать в очередь
                    try { tasks.wait(timeout); }
                    catch (InterruptedException e) {}
                }
            }
        }
    }

    public static class Test {
        public static void main(String[] args) {
            final TimerTask t1 = new TimerTask() {
                public void run() {
                    System.out.println("boom");
                }
            };

            final TimerTask t2 = new TimerTask() {
                public void run() {
                    System.out.println("\tBOOM");
                }
            };

            final TimerTask t3 = new TimerTask() {
                public void run() {
                    t1.cancel();
                    t2.cancel();
                }
            };

            final Timer timer = new Timer();
            timer.schedule(t1, 0, 500);     // boom каждые 0,5 сек, начав немедленно
            timer.schedule(t2, 2000, 2000); // BOOM каждые 2 сек, начав через 2 сек
            timer.schedule(t3, 5000);              // Остановить их через 5 сек

            // Включаем в расписание заключительную задачу: начав через 5 сек, провести обратный отсчет от 5,
            // затем уничтожить timer как последний оставшийся поток исполнения, в результате чего программа завершится
            timer.scheduleAtFixedRate(new TimerTask() {
                                          public int times = 5;

                                          public void run() {
                                              System.out.println(times--);
                                              if (times == 0) timer.cancel();
                                          }
                                      },
                    5000, 500);
        }
    }
}
