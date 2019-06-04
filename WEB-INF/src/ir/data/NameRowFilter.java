package ir.data;

import java.lang.reflect.ParameterizedType;

public abstract class NameRowFilter<T>
{
    /**
     * Returns a NameRow created from the received record instance IF the
     * instance meets the filter criteria, or null if not.
     */
    public abstract NameRow create(Database db, T rec) throws Exception;
    /**
     * RecordFilter is used as a submission to SiteCache.filterNameRows() to
     * select name rows created from records of an eager-loaded record class.
     */
    public Class<T> getParameterClass()
    {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        @SuppressWarnings("unchecked")
        Class<T> ret = (Class<T>) parameterizedType.getActualTypeArguments()[0];
        return ret;
    }
}
