package ir.data;

import java.lang.reflect.Field;

class DbReaderExchanger
{
    public final int columnOrdinal;
    public final int columnScale;
    public final Field field;
    DbReaderExchanger(Field field, int columnOrdinal, int columnScale)
    {
        this.field = field;
        this.columnOrdinal = columnOrdinal;
        this.columnScale = columnScale;
    }
}