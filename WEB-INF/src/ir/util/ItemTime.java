package ir.util;

public class ItemTime implements Comparable<ItemTime>
{
    public final static int scale = 3;
    private int hits = 0;
    private long max = 0;
    private long min = 999999999;
    private String name = "";
    private double seconds = 0;
    //
    public ItemTime(String name)
    {
        this.name = name;
    }
    void add(ItemTime that)
    {
        this.hits += that.getHits();
        this.seconds += that.getTime();
        this.min = Math.min(this.min, that.min);
        this.max = Math.max(this.max, that.max);
    }
    //
    public void addHit(long timeMillis)
    {
        hits++;
        seconds += timeMillis / 1000.0;
        min = Math.min(min, timeMillis);
        max = Math.max(max, timeMillis);
    }
    @Override
    public int compareTo(ItemTime that)
    {// reverse comparator
        int res = Double.compare(this.seconds, that.seconds);
        if (0 == res)
        {
            return 0 - this.name.compareTo(that.name);
        }
        return 0 - res;
    }
    public double getAvgSeconds()
    {
        return seconds / hits;
    }
    public int getHits()
    {
        return hits;
    }
    public double getMaxMillis()
    {
        return max;
    }
    public double getMin()
    {
        return min;
    }
    public String getName()
    {
        return name;
    }
    public String getText(double totalTime)
    {
        String sumS = "" + (int) seconds;
        String hitS = "" + hits;
        String avg = "" + StringKit.format(seconds / hits, 3);
        String maxS = "" + StringKit.format(max / 1000, 3);
        String pct = "" + StringKit.format(getTime() * 100 / totalTime, 2);
        return sumS + "," + avg + "," + hitS + "," + maxS + "," + pct + "," + name.replace(',', '.');
    }
    public double getTime()
    {
        return seconds;
    }
    @Override
    public String toString()
    {
        return "{n:'" + name + "',t:" + seconds + ",h:" + hits + "}";
    }
}
