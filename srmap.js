var srMap = {
	blankMapData:null,
	currentMapData:null,
	currentMapRow:0,
	currentSquare:null,
	dragging:null,
	mapColors:["none","red","orange","yellow","green","blue","purple"],
	mapDataList:[],
	newRow:0,
	addMapData:function(obj){
		var tableName = sr5.getTableName(obj);		
		var rec = ir.copy(srMap.blankMapData);
		rec.fn = "addMapData"
		rec.Row = --srMap.newRow;
		rec.MapRow = srMap.currentMapRow;
		rec.X = ir.n(srMap.currentSquare.dataset.x);
		rec.Y = ir.n(srMap.currentSquare.dataset.y);
		rec.ObjectRow = obj.Row;
		rec.Name = obj.Name + (obj.Quantity && obj.Quantity>1?" x"+obj.Quantity:"");
		rec.Type = sr5.getTableName(obj);
		if(rec.Type==="Character")
		{
			rec.Type = obj.Type
		}
		else if(rec.Type ==="Gear")
		{
			rec.Type = obj.Type;
		}
		rec.Layer = obj.Type==="PC"?"Player":"GM";
		srMap.currentMapData.add(rec);
		var callback = function(res){
			if(res.ok)
			{
				var data = srMap.currentMapData.get(res.oldRow);
				data.Row = res.newRow;
				var div = ir.get("mapData"+res.oldRow);
				div.id="mapData"+res.newRow;
				div.dataset.row = res.newRow;
			}			
		};
		sr5.ajaxAsync(rec,callback);	
	},
	afterPick:function(list){	
		if(list.length && list.length>0)
		{
			for(var i =0, z=list.length;i<z;i++)
			{
				var a = list[i];
				srMap.addMapData(a);
			}
		}
		else
		{
			srMap.addMapData(list);
		}
		srMap.clearMapData();
		srMap.drawMapData();
		srMap.currentSquare = null;
	},
	afterPickObject:function(type){
		switch(type) {
		  case "Character":
			  pickCharacterPop.show([],false,srMap.afterPick);
		    break;
		  case "Armor":
			  pickArmorPop.callback = function(res){
				pickArmorPop.close();
				detailPop.characterRow = 0;
				detailPop.show(res,srMap.afterPick);
			};
			pickArmorPop.show(callback);
		    break;
		  case "Cyberdeck":
			  pickCyberdeckPop.callback = function(res){
			  pickCyberdeckPop.close();
				detailPop.characterRow = 0;
				detailPop.show(res,srMap.afterPick);
			};
			pickCyberdeckPop.show(callback);
		    break;
		  case "Cyberware":
			  pickCyberwarePop.callback = function(res){
			  pickCyberwarePop.close();
				detailPop.characterRow = 0;
				detailPop.show(res,srMap.afterPick);
			};
			pickCyberwarePop.show();
		    break;
		  case "Drone":
			  pickDronePop.callback = function(res){
			  pickDronePop.close();
				detailPop.characterRow = 0;
				detailPop.show(res,srMap.afterPick);
			};
			pickDronePop.show(callback);
		    break;
		  case "Gear":
			  var callback = function(res){
				pickGearPop.close();
				detailPop.characterRow = 0;
				detailPop.show(res,srMap.afterPick);
			};
			pickGearPop.show(callback);
		    break;
		  case "Vehicle":
			  pickVehiclePop.callback = function(res){
				pickVehiclePop.close();
				detailPop.characterRow = 0;
				detailPop.show(res,srMap.afterPick);
			};
			pickVehiclePop.show();
			break;
		  case "Weapon":
			  pickWeaponPop.callback = function(res){
			  pickWeaponPop.close();
				detailPop.characterRow = 0;
				detailPop.show(res,srMap.afterPick);
			};
			pickWeaponPop.show();
			break;
		  case "Registry":
			  var callback = function(res){
			  pickNPCPop.close();
			};
			pickNPCPop.show(srMap.afterPick);
		  default:
		    // code block
		} 
	},
	buildMapColors:function(containerId){
		var colors = srMap.mapColors;
		var htm="";
		var template = "<div class='checkWrap'><input id='mapColor{0}' {1} name='mapColorRadio' type='radio' onclick='sr5.setMapColor(this)'><label class='mapColor {0}' for='mapColor{0}'>{2}</label></div>";
		for(var i =0, z=colors.length;i<z;i++)
		{
			var c = colors[i];
			htm += ir.format(template,c,i==1?"checked":"",c.toUpperCase());
		}
		ir.set(containerId,htm);
	},
	changeLayer:function(ele){
		var mapData = srMap.dragging;	
		var dataRow = mapData.dataset.row;
		var data = srMap.currentMapData.get(dataRow);
		var newLayer = ele.id.substring("MapTokenOptionsLayer".length,ele.id.length);
		data.Layer = newLayer;
		var container = mapData.getElementsByClassName("layerContainer")[0];
		container.className = "layerContainer " + newLayer;
		srMap.updateMapDataLayer(dataRow,newLayer);
		srMap.dragging.classList.remove("dragging");
		srMap.dragging=null;
		popup("mapTokenOptionsPop",true);
		//ir.get("mapTokenOptionsPop").classList.remove("show");
		Status.info(mapData.dataset.hover + " moved to "+newLayer+" layer.");
	},
	clearMapData:function(){
		var data = document.getElementsByClassName("mapData");
		while(data.length>0 && data!=null)
		{
			var d = data[0];
			d.parentNode.removeChild(d);
		}		
	},
	closeOptions:function(){
		if(srMap.dragging!=null)
		{
			srMap.dragging.classList.remove("dragging");
			srMap.dragging=null;
		}
		popup("mapTokenOptionsPop",true);
		//ir.get("mapTokenOptionsPop").classList.remove("show");
	},
	deleteClick:function(){
		if(srMap.dragging!=null)
		{
			var callback = function(yes)
			{
				if(yes)
				{
					srMap.deleteMapData();
					popup("mapTokenOptionsPop",true);
					//ir.get("mapTokenOptionsPop").classList.remove("show");
				}
			};
			confirmPop.show("Are you sure you want to remove " + srMap.dragging.dataset.hover + "?",callback);
		}
	},
	deleteDragOver:function(e){
		e.preventDefault();
	},
	deleteDrop:function(e){
		e.preventDefault();
		//srMap.deleteMapData();
		var callback=function(yes){
			if(yes)
			{
				srMap.deleteMapData();
				popup("mapTokenOptionsPop",true);
			}
		};
		confirmPop.show("Are you sure you want to remove " + srMap.dragging.dataset.hover + "?",callback);
	},
	deleteMapData:function(){
		var mapData = srMap.dragging;		
		sr5.ajaxAsync({fn:"removeMapData",Row:mapData.dataset.row},null);
		srMap.currentMapData.remove(mapData.dataset.row);
		mapData.parentNode.removeChild(mapData);
		srMap.dragging = null;
	},
	drawGrid:function(parentId,divisionWidth){
		var p = ir.get(parentId);
		if(ir.exists("gridOverlayFor"+parentId))
		{
			var oldGrid = ir.get("gridOverlayFor"+parentId);
			p.removeChild(oldGrid);
		}
		var pWidth = p.clientWidth;
		var pHeight = p.clientHeight;
		var image = p.getElementsByClassName("fullImage")[0];
		if(image)
		{
			pWidth = image.clientWidth;
			pHeight = image.clientHeight;
		}
		var squareWidth = pWidth/divisionWidth;
        var squareHeight = squareWidth;
        var divisionHeight = pHeight/squareHeight;
        //var heightPadding = Math.floor(pHeight - (divisionHeight*squareHeight));
        //var widthPadding = Math.floor(pWidth - (divisionWidth*squareWidth));
		var grid = document.createElement("DIV");
		grid.id = "gridOverlayFor"+parentId;
		grid.className = "gridOverlay";
		grid.style.width = pWidth + 3 +"px";
		grid.style.height = pHeight -  +"px";
		//grid.style.marginTop = Math.floor(heightPadding/2) +"px";
		grid.style.marginLeft = -3+"px";
		for (var i = 0; i < divisionHeight; i++) {
	        for(var j = 0; j < divisionWidth; j++){
	        	var square = document.createElement("DIV");
	        	square.id="square"+j+","+i;
	        	square.dataset.y=i;
	        	square.dataset.x=j;
	        	square.dataset.color="none";
	        	square.className = "square flex";
	        	square.droppable = true;
	        	square.style.width = squareWidth+"px";
	        	square.style.height = squareHeight+"px";
	        	square.style.maxWidth = squareWidth+"px";
	        	square.style.maxHeight = squareHeight+"px";
	        	square.onclick = function(){srMap.squareClick(this);};
	        	square.ondrop = srMap.squareDrop;
	        	square.ondragover = srMap.squareDragOver;
	        	grid.appendChild(square);
	        }
	    }
		p.appendChild(grid);
	},
	drawMapData:function(){
		var data = srMap.currentMapData.values;
		for(var i =0, z=data.length;i<z;i++)
		{
			var a = data[i];
			var square = ir.get(("square"+a.X+","+a.Y),true);
			if(square!=null)
			{
				var div = document.createElement("DIV");
				div.className = "mapData hover";
				div.id = "mapData"+a.Row;
				div.dataset.row = a.Row;
				div.draggable = true;
				div.ondragstart = srMap.mapTokenDragStart;
				div.ondragend = srMap.mapTokenDragEnd;
				div.dataset.hover = a.Name;
				square.appendChild(div);
				div.innerHTML = srMap.getMapDataToken(a);	
			}			
		}
		sr5.initHover();
	},
	getMapDataToken:function(mapData){
		var template = "<img class='mapToken hover {2}' onclick='srMap.mapTokenClick(event)' data-hover='{1}' src='{0}'/><div class='layerContainer {3}'>"
			+ "<div class='layerBall hover' data-hover='Player Layer'></div>"
			+ "<div class='layerBall hover' data-hover='GM Layer'></div>"
			+ "<div class='layerBall hover' data-hover='Map Layer'></div>"
			+ "</div>";
		var hover = mapData.Name;
		var imagePath = "";
		if(mapData.Type === "PC" || mapData.Type === "NPC")
		{
			var char = sr5.characters.get(mapData.ObjectRow);
			if(char==null)
			{
				var callback=function(res){
					if(res.Portrait.Row>0)
					{
						ir.get("mapData"+mapData.Row).innerHTML = ir.format(template,sr5.getImagePath(res.Portrait,true),hover,mapData.Type,mapData.Layer);
					}
				};
				sr5.selectPlayer(mapData.ObjectRow,callback);
				imagePath = "images/Face/thumb_0_"+sr5.unknownFaces[parseInt(Math.random()*sr5.unknownFaces.length) + 1]+".jpg";
			}
			else
			{
				if(char.Portrait.Row>0)
				{
					imagePath = sr5.getImagePath(char.Portrait,true);
				}
				else
				{
					imagePath = "images/Face/thumb_0_"+sr5.unknownFaces[parseInt(Math.random()*sr5.unknownFaces.length) + 1]+".jpg";
				}
			}
		}
		else
		{
			var objectType = sr5.mapObjectType.get(mapData.Type);
			imagePath = sr5.iconPath + objectType.imgSrc;
		}
		return ir.format(template,imagePath,hover,mapData.Type,mapData.Layer);
	},
	mapTokenClick:function(event){
		if(srMap.dragging==null)
		{
			srMap.dragging = event.target.parentNode;
			srMap.dragging.classList.add("dragging");
			ir.set("mapTokenOptionsPopTitle",srMap.dragging.dataset.hover);
			popup("mapTokenOptionsPop",true);
			//ir.get("mapTokenOptionsPop").classList.add("show");
			var dataRow = srMap.dragging.dataset.row;
			var data = srMap.currentMapData.get(dataRow);
			ir.set("mapTokenOptionsLayer","mapTokenOptionsLayer"+data.Layer);
		}
		else if(srMap.dragging==event.target.parentNode)
		{
			srMap.dragging.classList.remove("dragging");
			srMap.dragging.blur();
			popup("mapTokenOptionsPop",true);
			//ir.get("mapTokenOptionsPop").classList.remove("show");
			srMap.dragging=null;
		}
		else if(event.target.classList.contains("mapToken"))
		{
			srMap.squareClick(event.target.parentNode.parentNode);
		}
		var data = "";
		event.stopPropagation();
	},
	mapTokenDragEnd:function(ele){
		ele = ele.currentTarget;
		//popup("mapTokenOptionsPop");
		//ir.get("mapTokenOptionsPop").classList.remove("show");
	},
	mapTokenDragStart:function(ele){
		ele = ele.currentTarget;
		if(ele.classList.contains("mapData"))
		{
			srMap.dragging = ele;
			var dataRow = srMap.dragging.dataset.row;
			var data = srMap.currentMapData.get(dataRow);
			ir.set("mapTokenOptionsPopTitle",srMap.dragging.dataset.hover);
			ir.set("mapTokenOptionsLayer","mapTokenOptionsLayer"+data.Layer);
			popup("mapTokenOptionsPop",true);
			//ir.get("mapTokenOptionsPop").classList.add("show");
		}
	},
	squareClick:function(ele){
		srMap.currentSquare=ele;
		if(srMap.dragging!=null)
		{
			ele.appendChild(srMap.dragging);
		    srMap.updateMapDataPosition(srMap.dragging.dataset.row,ele.dataset.x,ele.dataset.y);
		    srMap.dragging.classList.remove("dragging");
			srMap.dragging.blur();
			srMap.dragging = null;
			popup("mapTokenOptionsPop",true);
			//ir.get("mapTokenOptionsPop").classList.remove("show");
		}
		else if(ele.children.length==0)
		{
			pickMapObjectPop.show(srMap.afterPickObject);			
		}
	},
	squareDragOver:function(e){
		e.preventDefault();
		
	},
	squareDrop:function(e){
		e.preventDefault();
		var ele = srMap.dragging;
		var target = e.target;
		while(target!=null && !target.classList.contains("square"))
		{
			target = target.parentNode;
		}
		if(ele!=null && ele.classList.contains("mapData"))
		{
		    target.appendChild(ele);
		    srMap.updateMapDataPosition(ele.dataset.row,target.dataset.x,target.dataset.y);
		}
		srMap.dragging=null;

		ir.set("mapTokenOptionsPopTitle","");
		popup("mapTokenOptionsPop",true);
		//ir.get("mapTokenOptionsPop").classList.remove("show");
	},
	updateMapDataLayer:function(mapDataRow,newLayer){
		sr5.ajaxAsync({fn:"updateMapDataLayer",mapDataRow:mapDataRow,newLayer:newLayer},null);
	},
	updateMapDataPosition:function(mapDataRow,newX,newY){
		var callback= function(res){
			if(res.ok)
			{
				var md = srMap.currentMapData.get(mapDataRow);
				if(md!=null)
				{
					md.X = newX;
					md.Y = newY;
				}
			}
			
		};
		sr5.ajaxAsync({fn:"updateMapDataPosition",mapDataRow:mapDataRow,newX:newX,newY:newY},callback);
	},
	zz_srMap:0
};