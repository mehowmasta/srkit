package ir.data;

public interface StatementInfo
{
    public long getAvgTime();
    public int getCallCount();
    public java.util.Date getLastUsed();
    public String getSQL();
}
