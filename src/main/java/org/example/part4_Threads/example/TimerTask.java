package org.example.part4_Threads.example;

public abstract class TimerTask implements Runnable{
    boolean cancelled = false;      // Была ли отмена?
    long nextTime = -1;             // На какой момен назначено следующее исполнение?
    long period;                    // С каким интервалом происходит исполнение?
    boolean fixedRate;              // Запуск исполнения с фиксированной частотой?

    protected TimerTask() {}

    /*
     * Прекращаем исполнение задачи. Возвращаем true, если задача
     * действительно исполнялась, и false - если ее исполнение
     * уже прекращено или время исполнения никогда не назначалось
     */
    public boolean cancel() {
        if (cancelled) return false;
        cancelled = true;
        return nextTime != -1;
    }

    /*
    * На какой момент назначено очередное исполнение? Метод run() может
    * использовать этот метод, чтобы увидеть,
    * бфл ли он вызван в предполагаемый момент
    * */
    public long scheduleExecutionTime() {return nextTime;}

    /*
    * Подклассы должны замещать этот метод, задавая код, который
    * должен исполняться. Класс Timer будет вызывать его из
    * своего внутреннего потока исполнения.
    * */
    public abstract void run();

    /*
     * Этот метод будет использоваться классом Timer для передачи
     классу Task сведений о расписании.
     */
    void schedule(long nextTime, long period, boolean fixedRate) {
        this.nextTime = nextTime;
        this.period = period;
        this.fixedRate = fixedRate;
    }

    /*
    * Этот метод будет вызываться классом Timer после того,
    * как Timer вызовет метод run().
    * */
    boolean reschedule() {
        if (period == 0 || cancelled) return false; // Больше не запускать
        if (fixedRate) nextTime += period;
        else nextTime = System.currentTimeMillis() + period;
        return true;
    }
}
