package sr.data;

import java.util.ArrayList;
import java.util.List;

import ir.data.NameRow;
import static ir.util.StringKit.jsq;

public enum ProfessionalRating{
	PR0(0,"Professional Rating 0"),
	PR1(1,"Professional Rating 1"),
	PR2(2,"Professional Rating 2"),
	PR3(3,"Professional Rating 3"),
	PR4(4,"Professional Rating 4"),
	PR5(5,"Professional Rating 5"),
	PR6(6,"Professional Rating 6");
	private final int value;
	private final String text;
	private ProfessionalRating(int value,String text)
	{
		this.value =value;
		this.text = text;
	}
	public static String toJson()
	{
		StringBuilder b = new StringBuilder("[");
		String comma = "";
		for(ProfessionalRating a : values())
		{
			b.append(comma)
			 .append("{row:").append(jsq(a.value))
			 .append(",name:").append(jsq(a.name()))
			 .append(",text:").append(jsq(a.text))
			 .append("}");
			comma = ",";
		}
		return b.append("]").toString();
	}
	public static List<NameRow> toNameRows()
	{
		List<NameRow> list = new ArrayList<NameRow>();
		for(ProfessionalRating a : values())
		{
			NameRow row = new NameRow();
			row.Name = a.text;
			row.Row = a.value;
			list.add(row);
		}
		return list;
	}	
}