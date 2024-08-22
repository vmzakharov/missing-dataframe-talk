package dftalk.onebrc.util;

public class Stopwatch
{
    private long startTimeMillis;
    private long endTimeMillis;
    private long totalMemoryBytes;
    private long usedMemoryBytes;
    private long freeMemoryBytes;

    public Stopwatch()
    {
    }

    public Stopwatch start()
    {
        this.recordMemoryUsage();
        this.startTimeMillis = System.currentTimeMillis();
        return this;
    }

    public Stopwatch stop()
    {
        this.endTimeMillis = System.currentTimeMillis();
        this.recordMemoryUsage();
        return this;
    }

    public long elapsedTimeMillis()
    {
        return this.endTimeMillis - this.startTimeMillis;
    }

    public long totalMemoryBytes()
    {
        return this.totalMemoryBytes;
    }

    public long usedMemoryBytes()
    {
        return this.usedMemoryBytes;
    }

    public long freeMemoryBytes()
    {
        return this.freeMemoryBytes;
    }

    private void recordMemoryUsage()
    {
//        System.gc();
//        System.gc();
//        System.gc();
        Runtime runtime = Runtime.getRuntime();
        this.freeMemoryBytes = runtime.freeMemory();
        this.totalMemoryBytes = runtime.totalMemory();
        this.usedMemoryBytes = this.totalMemoryBytes - this.freeMemoryBytes;
    }

    public Stopwatch printStats(String message)
    {
        System.out.printf("T: %,d, U: %,d, F: %,d\n", this.totalMemoryBytes(), this.usedMemoryBytes(), this.freeMemoryBytes());
        System.out.printf("%s, ms: %,d\n", message, this.elapsedTimeMillis());
        return this;
    }
}
