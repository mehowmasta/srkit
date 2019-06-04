"use strict";
var model = {
		imageElement:null,
		imageMap:null,
		mapData:null,
	zz_model:0	
};
var view = {
	aaOnLoad:function(){
		var form = ir.get("f1",true).classList.remove("loading");
		view.addResizeEvent();
		view.getMapData();
		view.buildImage();	
	},
	addResizeEvent:function(){
		if(window.attachEvent) {
		    window.attachEvent('onresize', function() {
		        view.drawGrid();
				view.drawMapData();
		    });
		}
		else if(window.addEventListener) {
		    window.addEventListener('resize', function() {
		        view.drawGrid();
				view.drawMapData();
		    });
		}
	},
	buildImage:function(){
		var container = ir.get("mapDiv");
		var image = document.createElement("IMG");
		image.id = "fullImage"+model.imageMap.Row;
		image.className = "fullImage";
		image.src = sr5.getImagePath(model.imageMap,false);
		container.appendChild(image);
		model.imageElement=image;
		view.drawGrid();
		view.drawMapData();
	},
	changeZoom:function(){
		var zoom = Math.min(3,ir.vn("ctlZoom"));
		ir.set("ctlZoom",zoom);
		var container = ir.get("mapDiv");
		if(zoom>1)
		{
			container.classList.remove("flex");
		}
		else
		{
			container.classList.add("flex");
		}
		
		var image = ir.get("fullImage"+model.imageMap.Row);
		image.style.width = (100*zoom) + "%";
		image.style.height = (100*zoom) + "%";
	},
	deleteMap:function(){
		var callback = function(yes)
		{
			if(yes)
			{
				sr5.submitDelete();
			}
		};
		confirmPop.show("Are you sure you want to delete this map?",callback);
	},
	drawGrid:function(){
		if(!model.imageElement.complete)
		{
			return window.setTimeout(view.drawGrid,100);
		}
		view.changeZoom();
		var division = Math.min(60,ir.vn("ctlDivision"));
		ir.set("ctlDivision",division);
		srMap.drawGrid("mapDiv",division);
		ir.get("mapDiv").classList.add("show");
	},
	drawMapData:function(){
		if(!model.imageElement.complete)
		{
			return window.setTimeout(view.drawMapData,100);
		}
		srMap.drawMapData();
	},
	getMapData:function(){
		srMap.currentMapData = new KeyedArray("Row",model.mapData.values);
		srMap.currentMapRow = model.imageMap.Row;
		//sr5.parseMapData(ir.v("ctlData"));
	},
	getPortrait:function(s)
	{
		if(s!=null && s.Extension && s.Extension.length>0)
		{
			var img = {Row:s.Portrait,User:s.UserImage,Extension:s.Extension,Type:"Face"};
			return sr5.getThumb(img);
		}
		else
		{
			return sr5.getThumbUnknown(true);
		}
	},
	submit:function(){
		ir.submit();
	},
	updateDivision:function(){
		view.drawGrid();
		view.drawMapData();
	},
	updateZoom:function(){
		view.drawGrid();
		view.drawMapData();
	},
	zz_view:0
};