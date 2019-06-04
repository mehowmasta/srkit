package ir.web;

import ir.data.Database;
import ir.data.IValidator;
import ir.util.NamedObject;

public interface IControl extends NamedObject
{
    public int getMaxLength();
    public String getName();
    public Object getRawValue();
    public <A> A getValue(A dft) throws Exception;
    public boolean hasValidValue();
    public boolean isDisabled();
    public <T> T read(JRequest req, T dftVal, IValidator iv) throws Exception;
    public void setClass(String cls);
    public void setColumn(Database db, String tableName, String columnName) throws Exception;
    public void setDecimals(int decs);
    public void setLeader(Object o);
    public void setMaxLength(int maxLength);
    public void setName(String name);
    public void setTitle(String title);
    public void setTrailer(Object o);
    public void setValue(Object o) throws Exception;
}
