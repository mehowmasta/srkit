package ir.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Holds information about updatable public fields for an object class.
 */
class ReflectorElement
{
  public final Class<?> objectClass;
  public final List<String>fieldNames=new ArrayList<String>();
  public final List<Field>fields=new ArrayList<Field>();
  public final Map<String,Field>fieldMap=new HashMap<String,Field>();
  public final Map<String,List<Field>> fieldAnnotations = new HashMap<String,List<Field>>();
  ReflectorElement(Class<?>  c)
  {
    this.objectClass = c;
    init();
  }
  public Field getField(String n)
  {
    return fieldMap.get(n.toLowerCase());
  }
  /**
   * Gets never-null list of Fields marked with passed annotation.
   */
  public List<Field> getFieldsWith(Class<? extends Annotation> annoClass)
  {
    List<Field> myList = fieldAnnotations.get(annoClass.getName());
    List<Field> result = new ArrayList<Field>();
    if (myList != null)
    {
      result.addAll(myList);
    }
    return result;
  }
  private void init()
  {
    Field[] fa = objectClass.getFields();
    for (Field f : fa)
    {
      int mod = f.getModifiers();
      if (!Modifier.isPublic(mod))
      {
        continue;
      }
      if (Modifier.isFinal(mod))
      {
        continue;
      }
      if (Modifier.isStatic(mod))
      {
        continue;
      }
      fields.add(f);
      fieldNames.add(f.getName());
      fieldMap.put(f.getName().toLowerCase(), f);
      Annotation[] annos = f.getAnnotations();
      for (Annotation anno : annos)
      {
        String annoKey = anno.annotationType().toString().replace("interface ","");
        List<Field>fieldList = fieldAnnotations.get(annoKey);
        if (fieldList == null)
        {
          fieldList = new ArrayList<Field>();
          fieldAnnotations.put(annoKey, fieldList);
        }
        fieldList.add(f);
      }
    }
  }
}
