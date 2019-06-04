package sr.data;

import java.util.ArrayList;
import java.util.List;

import ir.data.NameRow;
import static ir.util.StringKit.jsq;

public enum ProfessionalRating{
	PR0(0,"Thugs & Mouth Breathers"),
	PR1(1,"Gangers & Street Scum"),
	PR2(2,"Corporate Security"),
	PR3(3,"Police Patrols"),
	PR4(4,"Organized Crime Gang"),
	PR5(5,"Elite Corporate Security"),
	PR6(6,"Elite Special Forces");
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
			 .append(",text:").append(jsq(a.value + " - " + a.text))
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
			row.Name = a.value + " " + a.text;
			row.Row = a.value;
			list.add(row);
		}
		return list;
	}	
}