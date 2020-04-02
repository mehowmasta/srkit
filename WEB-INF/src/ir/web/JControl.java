package ir.web;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringEscapeUtils;
import ir.data.ColDef;
import ir.data.Database;
import ir.data.IRecord;
import ir.data.ITranslator;
import ir.data.IValidator;
import ir.data.NameRow;
import ir.data.PairRow;
import ir.util.Coerce;
import ir.util.IndyMath;
import ir.util.JDate;
import ir.util.JDateTime;
import ir.util.StringKit;
import ir.util.TypeKit;
import sr.data.AppRec;
import sr.data.SrRec;

/**
 * Utility class to write controls.
 */
public class JControl implements IControl
{
  protected static String[] _mon = null;
  public static final int DefaultTextAreaWidth = 60;
  public static final int MobileTextAreaWidth = 25;
  private String trailerLead=" ";
  private Map<String,String>data=null;
  private boolean _badInput = false;
  private String _badInputMsg = null;
  protected String _class = "hover";
  protected int _columns = -1;
  protected Database _db = null;
  protected String _dbColumn = null;
  protected String _dbTable = null;
  protected int _decimals = -1;
  protected String _default = null;
  protected boolean _disabled = false;
  protected String _events = "";
  protected boolean _hidden = false;
  protected List<String> _labels = null;
  private String _leader = "<div class='inputWrap'>";
  protected int _maxLength = -1;
  protected String _name = null;
  //private String numberMax="";
  //private String numberMin="";
  private String _optGroupDelimiter = null;
  protected String _placeholder = "";
  protected boolean _pwd = false;
  protected IRecord _rec = null;
  protected int _size = -1;
  protected boolean _suppressTextArea = true;
  protected String _style = "";
  protected int _textAreaCols = 0;
  protected int _textAreaRows = 0;
  protected String _title = "";
  private String _trailer = "</div>";
  public Object _value = "";
  protected List<String> _values = null;
  protected int _yearFrom = 2001;
  protected int _yearTo = JDate.today().year() + 1;
  private boolean groupIfNecessary = true;
  private boolean readonly=false;
  private final List<String>optionData=new ArrayList<String>();
  /**
   * returns a checkbox control
   */
  public static String check(String name, boolean bVal)
  {
    return check(name, bVal, "");
  }
  public static String check(String name, boolean bVal, String otherAttributes)
  {
    return check(name,null,bVal,otherAttributes);
  }
  /**
   * returns a checkbox control wrapped with a following label
   */
  public static String check(String name,String label, boolean bVal)
  {
    return check(name, label,bVal, "");
  }
  public static String check(String name,String label, boolean bVal, String otherAttributes)
  {
    if (otherAttributes == null)
    {
      otherAttributes = "";
    }
    String alc = otherAttributes.toLowerCase();
    if (!alc.contains("tabindex="))
    {
      otherAttributes += " tabindex='1'";
    }
    StringBuilder b = new StringBuilder();
    b.append("<div class='checkWrap'>")
     .append("<input type='checkbox' name='").append(name).append("' id='").append(name).append("' ").append(bVal ? "checked" : "").append(" ").append(otherAttributes).append(">");
    if (label != null)
    {
    	b.append("<label for='").append(name).append("'>").append(label).append("</label>");
    }
    return b.append("</div>").toString();
  }
  /**
   * returns a hidden control
   */
  public static String hidden(String name, Object value)
  {
    return "<input type='hidden' name='" + name + "' id='" + name + "' value='" + StringKit.str(value) + "'>";
  }
  /**
   * returns a password control
   */
  public static String password(String name, int size)
  {
    return "<div class='inputWrap'><input tabindex=1 type=password name=" + q(name) + " id=" + q(name) + " size=" + q(size) + "><label class='inputLabel'>"+name+"</label></div>";
  }
  public static String password(String name, String label, String attrs)
  {
      return "<div class='inputWrap'><input tabindex='1' type='password' name=" + q(name) + " id=" + q(name) +  " ><label class='inputLabel'>"+label+"</label></div>";
  }
  static String q(Object v)
  {
    return WebKit.quote(v);
  }

  public static void setClass(String className,JControl... ca)
  {
      for (JControl c : ca)
      {
          c.setClass(className);
      }
  }
  public static void setSize(int sz,JControl... ca) throws Exception
  {
    for (JControl c : ca)
    {
      c.setSize(sz);
    }
  }

  public static void setHidden(JControl... ca)
  {
      for (JControl c : ca)
      {
          c.setHidden();
      }
  }
  /**
   * returns a text box with the name value and size passed
   */
  public static String text(String name, Object value, String otherAttrs)
  {
    try
    {
      if (TypeKit.isNumber(value) && 0 == TypeKit.convert(value, Long.class))
        value = "";
    }
    catch (Exception e)
    {
    }
    return "<input tabindex='1' type='text' " + " name=" + q(name) + " id=" + q(name) + " value=" + q(value) + " "
    + (otherAttrs == null ? "" : otherAttrs) + ">";
  }
  public static String textArea(String id, int rows, int cols, String value)
  {
    return textArea(id, rows, cols, value, "");
  }
  public static String textArea(String id, int rows, int cols, String value, String otherAttrs)
  {
    return StringKit.format("<textarea id={0} name={0} rows={1} cols={2} {4}>{3}</textarea>", q(id), rows, cols, value, otherAttrs);
  }
  public static String textArea(String id, String value, String otherAttrs)
  {
    return StringKit.format("<textarea id={0} name={0} {2}>{1}</textarea>", q(id), value, otherAttrs);
  }
  /**
   * returns a file upload box with the name and size passed
   */
  public static String upload(String name, int nameBoxSize, int maxFileSize)
  {
    return upload(name, nameBoxSize, maxFileSize, "");
  }
  /**
   * returns a file upload box with the name and size passed and maxlength
   */
  public static String upload(String name, int nameBoxSize, int maxFileSize, String otherAttrs)
  {
    String alc = otherAttrs == null ? "" : otherAttrs.toLowerCase();
    StringBuilder b = new StringBuilder("<input type='file'");
    b.append(" size='").append(nameBoxSize).append("'");
    if (-1 == alc.indexOf("class="))
    {
      b.append(" class='irFileInput'");
    }
    if (-1 == alc.indexOf("tabindex="))
    {
      b.append(" tabindex='1'");
    }
    if (-1 == alc.indexOf("name="))
    {
      b.append(" name='").append(name).append("'");
    }
    if (-1 == alc.indexOf("id="))
    {
      b.append(" id='").append(name).append("'");
    }
    if (-1 == alc.indexOf("onchange="))
    {
      b.append(" onchange='return ir.onFileSelect(this)'");
    }
    if (otherAttrs != null)
    {
      b.append(" ").append(otherAttrs);
    }
    b.append(">");
    b.append("<input type='hidden' id='hhMaxSize" + name + "' value='" + maxFileSize + "'>");
    return b.toString();
  }
  /**
   * returns a yes/no selectbox
   */
  public static String yn(ITranslator t, String name, boolean bVal) throws Exception
  {
    JControl c = new JControl(name);
    c.addValue(true, t.xlate("Yes"));
    c.addValue(false, t.xlate("No"));
    c.setValue(bVal);
    return c.toSelect();
  }
  /**
   * create a control with a null name - to be provided later.
   * ** Note if you declare a JControl public on a container but do not instantiate it,
   * some containers will instantiate with its own variable name, so:<br>
   * public JControl ctlAbc
   * <br>..may result in the controller initializing ctlAbc to new JControl("ctlAbc")
   */
  public JControl()
  {
  }
  /**
   * create a control for the passed Database field
   */
  public JControl(Database db, String tableName, String columnName) throws Exception
  {
    setColumn(db, tableName, columnName);
  }
  /**
   * create a control with the passed name
   */
  public JControl(String controlName)
  {
    _name = controlName;
  }
  /**
   * create a control with the passed name
   */
  public JControl(String controlName,Object value)
  {
    _name = controlName;
    _value = value;
  }
  public void addData(String suffix,Object value)
  {
    if (data == null)
    {
      data = new HashMap<String,String>();
    }
    data.put(suffix,Coerce.toString(value));
  }
  /**
   * loads integers as labels and values
   *
   * @param int from inclusive
   * @param int to inclusive
   */
  public void addIntegers(int from, int to)
  {
    if (from < to)
    {
      for (int i = from; i <= to; i++)
      {
        addValue(i, i + "");
      }
    }
  }
  public void addPairs(List<PairRow> na)
  {
    for (PairRow r : na)
    {
      addValue(r.o1, r.o2 == null ? "" : r.o2.toString());
    }
  }
  public void addStyle(String attribute, String value)
  {
    if (_style.toLowerCase().indexOf(attribute.toLowerCase() + ":") < 0)
    {
      _style += attribute + ":" + value + ";";
    }
  }
  /**
   * adds an Enum name and its translated label
   */
  public void addValue(Enum<?> e, ITranslator t) throws Exception
  {
    addValue(e.toString(), t.xlate(e.toString()));
  }
  public void addValue(NameRow[] rows)
  {
    addValues(rows);
  }
  public void addValue(Object valueAndLabel)
  {
    addValue(valueAndLabel, valueAndLabel);
  }
  /**
   * adds a value/label pair
   *
   * @param int Value
   * @param String
   *            Label
   */
  public void addValue(Object val, Object Label)
  {
    addValue(val,Label,null);
  }
  public void addValue(Object val, Object Label,String optionDataString)
  {
    if (null == _values)
    {
      _values = new ArrayList<String>(64);
    }
    if (null == _labels)
    {
      _labels = new ArrayList<String>(64);
    }
    _values.add(Coerce.toString(val));
    _labels.add(Coerce.toString(Label));
    optionData.add(optionDataString);
  }
  /**
   * loads Strings as labels and values
   */
  public <T extends Enum<T>> void addValues(Class<T> type, ITranslator tr) throws Exception
  {
    for (T t : type.getEnumConstants())
    {
      addValue(t.toString(), tr.xlate(t.toString().replace('_', ' ')));
    }
  }
  public <T extends Enum<T>> void addValues(Class<T> type, ITranslator tr, String valueMethod) throws Exception
  {
    Method m = type.getMethod(valueMethod);
    for (T t : type.getEnumConstants())
    {
      addValue(m.invoke(t), tr.xlate(t.name().replace('_', ' ')));
    }
  }
  public void addValues(Collection<? extends Object> coll)
  {
    for (Object o : coll)
    {
    	if(o instanceof SrRec)
    	{
    		SrRec s = (SrRec) o;
    		addValue(s.Row, s.Name);
    	}
    	else
    	{
    	      String s = Coerce.toString(o);
    	      addValue(s, s);
    	}
    }
  }

  public void addValues(List<NameRow> na)
  {
    for (NameRow r : na)
    {
      addValue(r.Row, r.Name);
    }
  }
  /**
   * loads NameRows as labels and values
   */
  public void addValues(NameRow[] na)
  {
    for (NameRow r : na)
    {
      addValue(r.Row, r.Name);
    }
  }
  public void addValues(PairRow[] na)
  {
    for (PairRow r : na)
    {
      addValue(r.o1, r.o2 == null ? "" : r.o2.toString());
    }
  }
  /**
   * loads Strings as labels and values
   */
  public void addValues(String[] sa)
  {
    for (String s : sa)
    {
      addValue(s, s);
    }
  }
  public void clearValues()
  {
    _values = null;
    _labels = null;
  }
  private void dftDecimals(int dec)
  {
    if (_decimals == -1)
    {
      _decimals = dec;
    }
  }
  private void dftMaxLength(int len)
  {
    if (_maxLength < 0)
      _maxLength = len;
  }
  protected String events()
  {
    return _events == null ? "" : " " + _events + " ";
  }
  private void fixupNumberValue()
  {
    if (TypeKit.isNumber(_value))
    {
      double dblVal = Coerce.toDouble(_value);
      if (dblVal == 0)
      {
        _value = "";
      }
      else
      {
        if (_value instanceof BigDecimal)
        {
          BigDecimal bdv = (BigDecimal) _value;
          if (_decimals == -1)
          {
            _value = bdv.stripTrailingZeros().toPlainString();
          }
          else
          {
            _value = bdv.setScale(_decimals, IndyMath.RoundMode).toPlainString();
          }
        }
        else
        {
          if (_decimals == -1)
          {
            _value = StringKit.num(dblVal);
          }
          else
          {
            _value = StringKit.format(dblVal, _decimals, false);
          }
        }
      }
    }
  }
  public String getBadInputMessage()
  {
    if (_badInputMsg != null)
      return _badInputMsg;
    String n = getName();
    if (n.startsWith("_c"))
      n = n.substring(2);
    else if (n.startsWith("ctl"))
      n = n.substring(3);
    return "Invalid value for entered for " + n + ".";
  }
  public BigDecimal getBigDecimal() throws Exception
  {
    return getValue(BigDecimal.ZERO);
  }
  public boolean getBool()
  {
    return StringKit.True(_value);
  }
  private String getDataString()
  {
    if (data == null || data.size()==0)
    {
      return "";
    }
    String b = " ";
    for  (String k : data.keySet())
    {
      b += "data-" + k + "=" + q(data.get(k)) + " ";
    }
    return b;
  }
  /**
   * get the html string to present the control
   */
  protected String getDisabled()
  {
    setDisabled();
    return toInput();
  }
  public double getDouble() throws Exception
  {
    return getValue(0.0);
  }
  public int getInt()
  {
    return StringKit.intDft(getStr(), 0);
  }
  /**
   * @return the label corresponding to the current value, if this is rendered
   * as a select box or radio button grouup, otherwise, returns the value coerced to a string.
   */
  public String getLabel()
  {
    String selectedValue = Coerce.toString(_value);
    for (int i = 0;_labels!=null && i < _values.size() && i < _labels.size(); i++)
    {
      String value = _values.get(i);
      if (selectedValue.equals(value))
      {
        return _labels.get(i);
      }
    }
    return selectedValue;
  }
  public String getLabelAt(int i)
  {
    if (_labels == null || i < 0 || i >= _labels.size())
    {
      return getValueAt(i);
    }
    return Coerce.toString(_labels.get(i));
  }
  /**
   * get the max length allowed for the value
   */
  @Override
  public int getMaxLength()
  {
    return _maxLength;
  }
  /**
   * returns the control's name
   */
  @Override
  public String getName()
  {
    return _name;
  }
  @Override
  public Object getRawValue()
  {
    return _value;
  }
  public String getStr()
  {
    return StringKit.str(_value);
  }
  private String getTrailer()
  {
    if (_trailer==null || _trailer.length()==0)
    {
      return "";
    }
    if (_trailer.startsWith(" ") || _trailer.startsWith("&") || _trailer.startsWith("<"))
    {
      return _trailer;
    }
    return trailerLead + _trailer;
  }
  @Override
  public <A> A getValue(A dft) throws Exception
  {
    if (_value == null)
    {
      return dft;
    }
    return TypeKit.convert(_value, dft);
  }
  public String getValueAt(int i)
  {
    if (_values == null || i < 0 || i >= _values.size())
    {
      return "";
    }
    return Coerce.toString(_values.get(i));
  }
  public int getValueCount()
  {
    return _values == null ? 0 : _values.size();
  }
  public double getWidth()
  {
    int sz = 20;
    if (_size > 0)
    {
      sz = _size;
    }
    return sz;
  }
  @Override
  public boolean hasValidValue()
  {
    return !_badInput;
  }
  public boolean isCheckbox()
  {
    return _value != null && _value instanceof Boolean;
  }
  public boolean isDate()
  {
    return _value instanceof JDate;
  }
  public boolean isDateTime()
  {
    return _value instanceof JDateTime;
  }
  @Override
  public boolean isDisabled()
  {
    return _disabled;
  }
  public boolean isHidden()
  {
    return _hidden;
  }
  public boolean isNumber()
  {
    return TypeKit.isNumber(_value);
  }
  public boolean isPassword()
  {
    return _pwd;
  }
  public boolean isRadio()
  {
    return _columns > 0;
  }
  public boolean isSelect()
  {
    return null != _values && null != _labels;
  }
  public boolean isTextArea()
  {
    return _textAreaRows > 0 || _textAreaCols > 0;
  }
  protected String name()
  {
    return " name='" + _name + "' id='" + _name + "' ";
  }
  /**
   * Reads the value from the request which corresponds to this control's
   * name.
   */
  @Override
  @SuppressWarnings({ "unchecked" })
  public <T> T read(JRequest req, T dftVal, IValidator iv) throws Exception
  {
    _badInput = false;
    if (dftVal instanceof Boolean)
    {// because unchecked check boxes are not submitted, we force default
      // to false
      setValue(req.read(_name, false, 10));
    }
    else
    {// don't trim input of numbers as allowable value strings may be
      // larger than box size
      if (TypeKit.isNumber(dftVal))
      {
        _value = req.read(_name, "", 30);
        try
        {
          T t = TypeKit.convert(_value, dftVal);
          setValue(t);
          return t;
        }
        catch (Exception e)
        {
          _badInput = true;
          iv.addPageError(getBadInputMessage());
          return dftVal;
        }
      }
      else
      {
        setValue(req.read(_name, dftVal, _maxLength > 0 ? _maxLength : 60));
      }
    }
    return (T) _value;
  }
  public void removeValue(Object v)
  {
    String vs = Coerce.toString(v);
    for (int i = 0; i < _values.size(); i++)
    {
      if (vs.equals(Coerce.toString(_values.get(i))))
      {
        _values.remove(i);
        if (_labels != null)
          _labels.remove(i);
        return;
      }
    }
  }
  public void setBadInputMessage(String m)
  {
    _badInputMsg = m;
  }
  @Override
  public void setClass(String c)
  {
    _class = c;
  }
  /**
   * create a control for the passed Database field
   */
  @Override
  public void setColumn(Database db, String tableName, String columnName) throws Exception
  {
    if (_name == null)
    {
      _name = tableName + "_" + columnName;
    }
    _dbTable = tableName;
    _dbColumn = columnName;
    _db = db;
    ColDef cd = _db.getColDef(_dbTable, _dbColumn);
    if (cd == null)
    {
      throw new Exception("Definition for column " + _dbTable + "." + _dbColumn + " not found.");
    }
    if (cd.isBoolean())
    {
      setMaxLength(10);
    }
    if (cd.isNumeric())
    {
      int decs = cd.getScale();
      if (decs < 10)
      {
        setDecimals(decs);
      }
      setMaxLength(StringKit.format(cd.getMax(), decs, false).length());
      // numberMax = cd.getMax() + "";
      //  numberMin = cd.getMin() + "";
      if (decs == 0)
      {
        setPlaceHolder("0");
      }
      else if (decs>0 && decs<10)
      {
        setPlaceHolder("0." + StringKit.lpad(0,decs,'0'));
      }
    }
    else if (cd.isDate())
    {
      setMaxLength(cd.getSize() + 8);
    }
    else
    {
      setMaxLength(cd.getSize());
    }
  }
  @Override
  public void setDecimals(int d)
  {
    _decimals = d;
  }
  /**
   * sets the box as a disabled one
   */
  public void setDisabled()
  {
    setDisabled(true);
  }
  public void setDisabled(boolean which)
  {
    _disabled = which;
  }
  public void setEvents(String evn)
  {
    _events = evn;
  }
  public void setGrouping(boolean yesNo)
  {
    groupIfNecessary = yesNo;
  }
  /**
   * sets the box as a hidden one
   */
  public JControl setHidden()
  {
    _hidden = true;
    return this;
  }
  @Override
  public void setLeader(Object o)
  {
    _leader = Coerce.toString(o);
  }
  /**
   * sets maximum size of data to return
   *
   * @param int
   */
  @Override
  public void setMaxLength(int maxLength)
  {
    _maxLength = maxLength;
  }
  @Override
  public void setName(String nm)
  {
    _name = nm;
  }

  public void setOptGroupDelimiter(String v)
  {
    _optGroupDelimiter = v;
  }
  /**
   * sets the box as a hidden one
   */
  public void setPassword()
  {
    _pwd = true;
  }
  public void setPlaceHolder(Object v)
  {
    _placeholder = Coerce.toString(v);
  }
  public void setRadio()
  {
    setRadioColumns(_values == null ? 4 : _values.size());
  }
  /**
   * sets the number of columns in which to present the values of a radio
   * button group use -1 to place all horizontal
   */
  public void setRadioColumns(int columns)
  {
    _columns = columns;
  }
  public void setReadOnly()
  {
    readonly = true;
  }
  public void setSize(int i)
  {
    _size = i;
  }
  /**
   * Sets attributes not auto-generated from values
   */
  public void setStyle(String val)
  {
    _style = val == null ? "" : val;
  }
  public void setSuppressTextarea(boolean yes)
  {
	  _suppressTextArea = yes;
  }
  public void setTextArea()
  {
    _textAreaCols=1;
  }
  /**
   * sets max box width for text areas
   */
  public void setTextArea(int rows, int cols)
  {
    _textAreaCols = cols;
    _textAreaRows = rows;
    if (_maxLength < 0)
    {
      _maxLength = rows * cols;
    }
  }
  @Override
  public void setTitle(String title)
  {
	  _title = title;
  }
  @Override
  public void setTrailer(Object o)
  {
    _trailer = Coerce.toString(o);
  }
  public void setTrailerLead(String what)
  {
    trailerLead=what;
  }
  public void setValue(boolean v, Object trueVal, Object trueLbl, Object falseVal, Object falseLbl) throws Exception
  {
    _value = v ? trueVal : falseVal;
    addValue(trueVal, Coerce.toString(trueLbl));
    addValue(falseVal, Coerce.toString(falseLbl));
  }
  public void setValue(boolean v, String trueLbl, String falseLbl) throws Exception
  {
    setValue(v, true, trueLbl, false, falseLbl);
  }
  /**
   * sets item value
   *
   * @param String
   *            Value
   */
  public void setValue(int[] Value)
  {
    setValue(StringKit.join(",", Value));
  }
  /**
   * sets item value
   *
   * @param String
   *            Value
   */
  @Override
  public void setValue(Object value)
  {
    _value = value == null ? "" : value;
    if (value == null)
    {
      return;
    }
    if (_value instanceof Integer || _value instanceof Long)
    {
      dftMaxLength(10);
      dftDecimals(0);
    }
    else if (_value instanceof Float || _value instanceof Double)
    {
      dftMaxLength(10);
      dftDecimals(2);
    }
    else if (_value instanceof Boolean)
    {
      dftMaxLength(10);
    }
    else if (_value instanceof JDate)
    {
      dftMaxLength(15);
    }
    else if (_value instanceof JDateTime)
    {
      dftMaxLength(25);
    }
  }
  protected String style()
  {
    return (_style == null || _style.equals("") ? "" : " style='" + _style + "' ")
        + (_class == null || _class.equals("") ? "" : " class='" + _class + "' ");
  }
  private String tabIndex()
  {
    return _disabled ? " " : " tabindex='1' ";
  }
  /**
   *
   * get the html string to present the radio button group
   *
   * @return String html
   */
  protected String toCheckbox()
  {
    if (_disabled)
    {
      return toHidden() + "<input type='checkbox' " + style() + events() + (_disabled ? " disabled " : "")
          + (StringKit.True(_value) ? " checked " : "") + ">";
    }
    else
    {
      boolean checked = StringKit.True(_value);
      String style = style();
      String events = events();
      if (events.indexOf("onclick")==-1)
      {
        //events += " onclick=ir.checkBold(" + StringKit.jsq(_name) + ")";
      }
      return "<input type='checkbox' " + name() + style + events + tabIndex() + (checked ? " checked " : "") + "><label for="+_name+">"+_title+"</label>";
    }
  }

  /**
   * get the html string to present the control as an input box
   */
  public String toDateTime()
  {
	  int len = 20;
	    boolean bTime = false;
	    String ds = _value == null ? "" : _value.toString();
	    if (_value instanceof JDateTime)
	    {
	    	_leader = "<div class='dateTimeWrap hidden'>";
	        len += 10;
	        bTime = true;        
	        if (! ((JDateTime)_value).isZero())
	        {
	            ds = new SimpleDateFormat(JDateTime.DefaultFormat2).format(((JDateTime)_value).getDate());
	        }
	    }
	    else if (_value instanceof JDate)
	    {        
	    	_leader = "<div class='dateWrap hidden'>";
	        if (! ((JDate)_value).isZero())
	        {
	            ds = new SimpleDateFormat(JDate.DefaultFormat).format(((JDate)_value).getDate());
	        }
	    }
	    if (_disabled)
	    {        
	        return _leader + JControl.hidden(_name,ds) + JControl.text("",ds,"disabled")
	            + _trailer;
	    }
	    if (_hidden)
	    {
	        return _leader + JControl.hidden(_name,_value) + _trailer;
	    }
	    String style = "";
	    StringBuilder sb = new StringBuilder(_leader)
	     .append("<input type='text' tabindex='1'")
	     .append(" "+events()+" ")
	     .append(" style='").append(style).append("'")
	     .append(" value='").append(ds).append("'")
	     .append(readonly ? " readonly='readonly'" : "")
	     .append(" name='").append(_name).append("'")
	     .append(" id='").append(_name).append("'")
	     .append(" class='").append(_class).append(bTime ? " jdatetime" : " jdate").append("'")
	     .append(" maxlength='").append(len).append("'");
	    if (_placeholder != null && _placeholder.length() > 0)
	      {
	        sb.append(" placeholder='").append(_placeholder).append("'");
	      }
	     sb.append(">");
	     sb.append("<label class='inputLabel'>").append(_title).append("</label>");
	    return sb.toString();
  }
  /**
   * get the html string to present the control
   */
  public String toHidden()
  {
    return "<input type='hidden' " + name() + val() + getDataString() + ">";
  }
  /**
   * get the html string to present the control as an input box
   */
  public String toInput()
  {
    Object rawVal = _value;
    fixupNumberValue();
    //
    StringBuilder sb = new StringBuilder("<input ");
    sb.append(tabIndex()).append(" value=").append(q(_value));
    sb.append(getDataString());
    if (TypeKit.isNumber(rawVal))
    {
      if (_disabled)
      {
        sb.append(" type='text'");
      }
      else
      {
        sb.append(" type='number'");
        String numberStep="any";
        if (_decimals == 0)
        {
          numberStep = "1";
        }
        else if(_decimals >0)
        {
        	numberStep = "" + Math.pow(10, _decimals *-1);
        }
        sb.append(" step='").append(numberStep).append("'");
      }
      if (_style.indexOf("text-align") == -1)
      {
        addStyle("text-align", "center");
      }
      addStyle("width", getWidth() + "rem !important");
      sb.append(" maxlength='" + _maxLength + "'");
    }
    else
    {
      sb.append(" type='text' ");
      if (_maxLength > 0)
      {
        sb.append(" maxlength='" + _maxLength + "'");
      }
      addStyle("width", getWidth() + "rem !important");
    }
    if (_badInput)
    {
      addStyle("background-color", "rgb(255,255,183)");
    }
    //
    if(_events.indexOf("onclick")==-1)
    {
    	_events += " onfocus='this.select()' ";
    }
    sb.append(style() + events());
    //
    if (readonly)
    {
      sb.append(" readonly='readonly' ");
    }
    if (_disabled)
    {// disabled boxes don't get read, so we hidden with value and name
      sb.append(" id='").append(_name).append("Dis' disabled>");
      sb.append(toHidden());
    }
    else
    {
      if (_placeholder != null && _placeholder.length() > 0)
      {
        sb.append(" placeholder='").append(_placeholder).append("'");
      }
      sb.append(name() + ">");
    }
    sb.append("<label class='inputLabel'>").append(_title).append("</label>");
    //
    return sb.toString();
  }
  /**
   * get the html string to present the radio button group
   *
   * @param String
   *            controlname
   * @return String html
   */
  protected String toRadio()
  {
    if (_values == null || _labels == null)
    {
      return "Set labels and values.";
    }
    if (_values.size() != _labels.size())
    {
      return "Label and Value counts don't match.";
    }
    if (_name == null)
    {
      return "control name passed cannot be null.";
    }
    if (_value == null && _values.size() > 0)
    {
      _value = _values.get(0);
    }
    //
    StringBuilder b = new StringBuilder();
    boolean table = _columns > 0 && _columns < _values.size();
    int cellOrdinal = 1;
    if (table)
    {
      b.append("<table border='0' id='" + _name + "_table'>");
    }
    String selection = Coerce.toString(_value);
    String events = events();
    if (events.indexOf("onclick")==-1)
    {
      events += " onclick=ir.radioBold(" + StringKit.jsq(_name) + ")";
    }
    for (int i = 0; i < _values.size(); i++)
    {
      if (table)
      {
        if (cellOrdinal == 1)
        {
          b.append("<tr valign='middle'>");
        }
        b.append("<td nowrap>");
      }
      String value = _values.get(i);
      String id = _name + "_" + value;
      boolean selected = selection.equals(value);
      b.append("<label>");
      b.append("<input type='radio'")
      .append(" name=").append(q(_name)).append(" id=").append(q(id))
      .append(" value=").append(q(value))
      .append(style()).append(selected ? " checked" : "")
      .append(events)
      .append(">").append(_labels.get(i)).append("</label>");
      if (table)
      {
        b.append("</td>");
        if (cellOrdinal == _columns)
        {
          b.append("</tr>");
          cellOrdinal = 1;
        }
        else
        {
          cellOrdinal++;
        }
      }
      else
      {
        b.append("&emsp;");
      }
    }
    if (table)
    {
      if (!b.toString().endsWith("</tr>"))
      {
        b.append("</tr>");
      }
      b.append("</table>\n");
    }
    //
    return b.toString();
  }
  /**
   * get the html string to present the select box
   */
  protected String toSelect()
  {
    if (_values == null)
    {
      return "Set values.";
    }
    if (_labels != null && _values.size() != _labels.size())
    {
      return "Label and Value counts don't match.";
    }
    if (_name == null)
    {
      return "Control name cannot be null.";
    }
    //
    if (_value == null)
    {
      if (_default == null)
      {
        _value = _values.get(0);
      }
      else
      {
        _value = _default;
      }
    }
    //
    if (_disabled)
    {
      return toSelectDisabled();
    }
    //
    StringBuilder b = new StringBuilder();
    b.append("<select ").append(tabIndex()).append(style()).append(events()).append(name());
    if (_size > 1)
    {
      b.append(" size='").append(_size).append("'");
      b.append(getDataString());
    }
    b.append(">");
    //
    String selectedValue = Coerce.toString(_value);
    int pfxLen = _values.size() > 100 ? 1 : 0;
    String prvGroup = "";
    boolean inGroup = false;
    for (int i = 0; i < _values.size(); i++)
    {
      String value = _values.get(i);
      String label = (_labels == null ? value : _labels.get(i));
      String optionDataStr = optionData.get(i);
      String prefix = null;
      if (groupIfNecessary)
      {
        if (_optGroupDelimiter != null)
        {
          int dAt = label.indexOf(_optGroupDelimiter);
          if (dAt > 0)
          {
            prefix = label.substring(0, dAt);
            label = label.substring(dAt + _optGroupDelimiter.length());
          }
        }
        else
        {
          if (pfxLen > 0 && label.length() > 0 && Character.isLetterOrDigit(label.charAt(0)))
          {
            prefix = StringKit.left(label, pfxLen);
          }
        }
        if (prefix != null && !prefix.equalsIgnoreCase(prvGroup))
        {
          prvGroup = prefix;
          if (inGroup)
          {
            b.append("</optgroup>");
          }
          inGroup = true;
          b.append("<optgroup style='font-weight:bold;' label=");
          b.append(q(prefix + "...")).append(">");
        }
      }
      b.append("<option value=").append(q(value));
      if (selectedValue.equals(value))
      {
        b.append(" selected ");
      }
      if (optionDataStr != null && optionDataStr.length()>0)
      {
        b.append(" ").append(optionDataStr).append(" ");
      }
      b.append(">").append(label).append("</option>");
    }
    if (inGroup)
    {
      b.append("</optgroup>");
    }
    b.append("</select>")
     .append("<label class='inputLabel'>").append(_title).append("</label>");
    //
    return b.toString();
  }
  /**
   * get the html string to present the select box
   */
  private String toSelectDisabled()
  {
    String valueSelected = Coerce.toString(_value);
    StringBuilder b = new StringBuilder();
    if (_labels != null)
    {
      for (int i = 0; i < _values.size(); i++)
      {
        if (valueSelected.equals(_values.get(i)))
        {
          valueSelected = _labels.get(i);
          break;
        }
      }
    }
    b.append("<input type='text' disabled value=").append(q(valueSelected.trim())).append(">")
     .append(hidden(_name, valueSelected))
     .append("<label class='inputLabel'>").append(_title).append("</label>");
    return b.append(getTrailer()).toString();
  }
  /**
   * get the html string to present the control in the form suggested by the
   * values supplied
   */
  @Override
  public String toString()
  {
    try
    {
      if(isHidden())
      {
    	  _leader = "<div class='inputWrap hidden'>";
      }
      StringBuilder b = new StringBuilder(_leader);
      if (isHidden())
      {
        b.append(toHidden());
      }
      else if (isDateTime() || isDate())
      {
    	b = new StringBuilder();
    	b.append(toDateTime());
      }
      else if (isRadio())
      {
        b.append(toRadio());
      }
      else if (isSelect())
      {
    	b = new StringBuilder(isHidden()?"<div class='selectWrap hidden'>" : "<div class='selectWrap'>");
        b.append(toSelect());
      }
      else if (isCheckbox())
      {
        b.append(toCheckbox());
      }
      else if (isPassword())
      {
        b.append("<input type=password " + tabIndex() + (_class == null || _class.length() == 0 ? "" : " class='" + _class + "'")
            + " value=" + q(_value) + name() + ">");
      }
      else if (isTextArea() && !_suppressTextArea)
      {
        b.append(toTextArea());
      }
      else
      {
        b.append(toInput());
      }
      b.append(getTrailer());
      return b.toString();
    }
    catch (Exception e)
    {
      return "Error:" + e.getMessage();
    }
  }
  /**
   * get the html string to present the control as a text area
   */
  protected String toTextArea()
  {
    StringBuilder b = new StringBuilder("");
    int rows = _textAreaRows;
    int cols = _textAreaCols;
    if (cols <= 1)
    {
      cols = DefaultTextAreaWidth;
    }
    if (rows <= 0)
    {// size to content
      String[] va = getStr().split("\n");
      rows = va.length;
      for (String v : va)
      {// account for long lines
        if (v.length() > cols)
        {
          rows += Math.ceil(v.length() / cols);
        }
      }
    }
    b.append("<textarea ").append(tabIndex()).append(events());
    b.append(getDataString());
    if (_style == null)
    {
      _style = "";
    }
    if (_style.indexOf("height:") == -1)
    {
      addStyle("height",rows + "em;");
    }
    if (_style.indexOf("width:") == -1)
    {
      b.append(" cols='").append(cols).append("'");
    }
    b.append(style());
    if (readonly)
    {
      b.append(" readonly='readonly' ");
    }
    if (_disabled)
    {
      b.append(" disabled>");
    }
    else
    {
      if (_placeholder != null && _placeholder.length() > 0)
      {
        b.append(" placeholder=").append(q(_placeholder));
      }
      b.append(name() + ">");
    }
    b.append(StringEscapeUtils.escapeHtml(Coerce.toString(_value))).append("</textarea>");
    //
    if (_disabled)
    {// disabled boxes don't get read, so do hidden with value and name
      b.append(toHidden());
    }
    b.append("<label class='inputLabel'>").append(_title).append("</label>");
    //
    return b.toString();
  }
  protected String val()
  {
    return " value=" + q(getStr());
  }
}
