package sr.web.page;


import ir.web.JControl;
import sr.data.ImageRec;
import sr.data.MapDataRec;

/**
 * Controls similarly named jsp file
 */
public class MapDetailPage extends AppBasePage {

	public JControl ctlRow;
	public JControl ctlName;
	public JControl ctlData;
	public JControl ctlDivision;
	public JControl ctlInactive;
	public JControl ctlZoom;
	private ImageRec currentMap;
	@Override
	protected void init() throws Exception {
		if(hasParm("ctlRow"))
		{
			int row = readInt("ctlRow");
			if(row>0)
			{
				
				currentMap = ImageRec.selectImage(db, currentUser.Row, row);
				if(currentMap.Row==0 || !currentMap.Type.equals(ImageRec.ImageType.Map))
				{
					setError("Failed to retrieve map.");
				  	setRedirectBack();
				  	return;
				}
				if(currentMap.User != currentUser.Row)
				{
					setError("Sneaky sneaky.");
				  	setRedirectBack();
				  	return;
				}
				
			}
		}
		else
		{
			setError("Missing map parameter.");
	  	  	setRedirectBack();
	  	  	return;
		}
		mapControl(ctlRow,currentMap,ImageRec.ROW);
		mapControl(ctlName,currentMap,ImageRec.NAME);
		mapControl(ctlData,currentMap,ImageRec.DATA);
		mapControl(ctlDivision,currentMap,ImageRec.DIVISION);
		mapControl(ctlInactive, currentMap, ImageRec.INACTIVE);
		mapControl(ctlZoom,currentMap,ImageRec.ZOOM);
		ctlRow.setHidden();
		ctlData.setHidden();
	}
	@Override
	protected void read() throws Exception {
	}

	@Override
	protected void update() throws Exception {
		if(currentUser.isGuest())
		{
			setError("Buzz!");
			return;
		}
		if(hasParm(DELETE_BUTTON))
		{
			db.delete(currentMap);
			setStatus("{0} has been deleted.",currentMap.Name);
		  	setRedirectBack();
		    return;
		}

		if(currentMap.Row==0)
		{
			currentMap.User = currentUser.Row;
			db.insert(currentMap);
			setStatus("Created map {0}.",currentMap.Name);
			setRedirect(Page.MapDetail, "ctlRow",currentMap.Row);
		    return;
		}
		else
		{
			if (db.update(currentMap))
		    {
		      setStatus("{0} has been updated.",currentMap.Name);
			  setRedirect(Page.MapDetail, "ctlRow",currentMap.Row);
			  return;
		    }
		}
	}
	@Override
	protected void write() throws Exception {
		set("ImageMap",currentMap.toString());
		set("DivisionValue",currentMap.Division);
		set("MapData",MapDataRec.selectAll(db, currentMap.Row).toString());
		set("ZoomValue",currentMap.Zoom);
		writeButtons();
		
	}	
	private void writeButtons() {
		StringBuilder btns = new StringBuilder("");
		if(currentMap.Row>0)
		{
			btns.append(eventButton(DELETE_BUTTON, "Delete",  "view.deleteMap()",currentUser.isGuest()?" disabled " : ""));
		}
		btns.append(eventButton(SUBMIT_BUTTON,"Update","view.submit()",currentUser.isGuest()?" disabled " : ""));
		set("Buttons", btns.toString());
	}
}
