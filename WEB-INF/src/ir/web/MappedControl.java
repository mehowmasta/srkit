package ir.web;

import ir.data.Database;
import ir.data.IRecord;
import ir.data.IValidator;
import ir.util.Coerce;
import java.lang.reflect.Field;

public class MappedControl
{
    IControl control = null;
    Field field = null;
    Object object = null;
    public MappedControl(IControl c, Database db, IRecord r, String field) throws Exception
    {
        init(c, r, field);
        int fidx = r.getFieldIndex(field);
        if (fidx < 0)
        {
            throw new Exception("Field " + field + " not found on " + r.getClass());
        }
        c.setColumn(db, r.getTable(), r.getColumns()[fidx]);
    }
    //
    public MappedControl(IControl c, Object container, String field) throws Exception
    {
        init(c, container, field);
    }
    public String getName()
    {
        return control.getName();
    }
    private void init(IControl c, Object container, String fieldName) throws Exception
    {
        control = c;
        object = container;
        field = null;
        try
        {
            field = object.getClass().getField(fieldName);
        }
        catch (Exception e)
        {
        }
        if (field == null)
        {
            throw new Exception("Field " + fieldName + " not found or not accessible on " + container.getClass().getName());
        }
        c.setValue(field.get(container));
    }
    public void read(JRequest req, IValidator iv) throws Exception
    {
        if (!control.isDisabled())
        {
            Object dft = field.get(object);
            if (dft == null)
            {
                dft = Coerce.to(null, field.getType());
            }
            control.read(req, dft, iv);
            if (control.hasValidValue())
            {
                field.set(object, control.getValue(dft));
            }
        }
    }
    public String toString()
    {
        return control.toString();
    }
}
