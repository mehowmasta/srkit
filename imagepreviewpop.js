var imagePreviewPop = {
	currentImage:null,
	id:"imagePreviewPop",
	imageElement:null,
	mapData:null,
	buildImage:function(){
		var self = imagePreviewPop;
		var i = self.currentImage;
		var container = ir.get(self.id + "ImageWrap");
		var image = document.createElement("IMG");
		var zoom = ir.vn(self.id+"Zoom");
		image.id = self.id + "Image";
		image.className = "imagePreview";
		image.src = sr5.getImagePath(i,false);
		image.style.width = (100*zoom) + "%";
		image.style.height = (100*zoom) + "%";
		container.appendChild(image);
		self.imageElement=image;
		//ir.set(self.id+"Name",i.Name);
		ir.set(self.id+"Data",i.Data);
		self.draw();
	},
	changeZoom:function(){
		var self = imagePreviewPop;
		var zoom = Math.max(Math.min(5,ir.vn(self.id + "Zoom")),0.2);
		ir.set(self.id + "Zoom",zoom);
		var container = ir.get(self.id + "ImageWrap");
		if(zoom>1)
		{
			container.classList.remove("flex");
		}
		else
		{
			container.classList.add("flex");
		}		
		var image = ir.get(self.id+ "Image");
		image.style.width = (100*zoom) + "%";
		image.style.height = (100*zoom) + "%";
	},
	close:function(){
		var self = imagePreviewPop;
		mapData=null;
		popup(self.id);
	},
	draw:function(){
		var self = imagePreviewPop;
		if(!self.imageElement.complete)
		{
			return window.setTimeout(self.draw,100);
		}
		ir.get(self.id + "ImageWrap").classList.add("show");
		if(self.currentImage.Type === "Map")
		{
			self.selectMapData();
		}
	},
	drawGrid:function(){
		var self = imagePreviewPop;
		if(self.imageElement==null)
		{
			return;
		}			
		if(!self.imageElement.complete)
		{
			return window.setTimeout(self.drawGrid,100);
		}
		self.changeZoom();
		var divisionWidth = self.currentImage.Division;
		var parentId = self.id + "ImageWrap";
		var p = ir.get(parentId);
		if(ir.exists("gridOverlayFor"+parentId))
		{
			var oldGrid = ir.get("gridOverlayFor"+parentId);
			p.removeChild(oldGrid);
		}
		var pWidth = self.imageElement.clientWidth;
		var pHeight = self.imageElement.clientHeight;
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
	        	square.id="squarePreview"+j+","+i;
	        	square.dataset.y=i;
	        	square.dataset.x=j;
	        	square.className = "squarePreview flex";
	        	square.style.width = squareWidth+"px";
	        	square.style.height = squareHeight+"px";
	        	square.style.maxWidth = squareWidth+"px";
	        	square.style.maxHeight = squareHeight+"px";
	        	grid.appendChild(square);
	        }
	    }
		p.appendChild(grid);
		ir.get(self.id + "ImageWrap").classList.add("show");
	},
	drawMap:function(res){
		var self = imagePreviewPop;
		if(!self.imageElement.complete)
		{
			return window.setTimeout(self.drawMapData,100);
		}
		self.drawGrid();
		self.drawMapData();
	},
	drawMapData:function(){
		var self = imagePreviewPop;
		if(!self.imageElement.complete)
		{
			return window.setTimeout(self.drawMapData,100);
		}
		var data = self.mapData.values;
		for(var i =0, z=data.length;i<z;i++)
		{
			var a = data[i];
			if(self.currentImage.User != sr5.user.Row && a.Layer !== "Player")
			{
				continue;
			}
			var square = ir.get(("squarePreview"+a.X+","+a.Y),true);
			if(square!=null)
			{
				var div = document.createElement("DIV");
				div.id="mapDataPreview"+a.Row;
				div.className = "hover";
				div.style.width="inherit";
				div.style.height="inherit";
				square.appendChild(div);
				div.innerHTML = self.getMapDataToken(a);	
			}			
		}
		sr5.initHover();
	},
	getMapDataToken:function(mapData){
		var template = "<img class='mapToken hover {2}' draggable='false' data-hover='{1}' src='{0}'/>";
		var hover = mapData.Name;
		var imagePath = "";
		if(mapData.Type == "PC" || mapData.Type == "NPC")
		{
			var char = sr5.characters.get(mapData.ObjectRow);
			if(char==null)
			{
				var callback=function(res){
					if(res.Portrait.Row>0)
					{
						ir.get("mapDataPreview"+mapData.Row).innerHTML = ir.format(template,sr5.getImagePath(res.Portrait,true),hover,mapData.Type.toLowerCase());
						sr5.initHover();
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
		return ir.format(template,imagePath,hover,mapData.Type.toLowerCase());
	},
	selectMapData:function(mapRow,callback){
		var self = imagePreviewPop;
		var innerCallback= function(res){
			if(res.ok)
			{
				self.mapData = new KeyedArray("Row",res.mapData);
				self.drawMap();
			}
		};
		sr5.ajaxAsync({fn:"selectMapData",mapRow:self.currentImage.Row},innerCallback);
	},
	show:function(imageRec){
		var self = imagePreviewPop;
		var pop = ir.get(self.id);
		ir.get(self.id + "ImageContainer").classList.remove("show");
		if(self.currentImage==null)
		{
			self.currentImage = imageRec;
			self.buildImage();
		}
		else if(self.currentImage.Row!=imageRec.Row)
		{
			self.imageElement.parentNode.removeChild(self.imageElement);
			self.currentImage = imageRec;
			self.buildImage();
		}	
		else
		{
			ir.get(self.id + "ImageContainer").classList.add("show");
		}
		ir.show(self.id+"SaveBtn",imageRec.User>0 && imageRec.User==sr5.user.Row);
		ir.show(self.id+"DataWrap",imageRec.User>0 && imageRec.User==sr5.user.Row);
		popup(pop);
	},
	update:function(){
		var self = imagePreviewPop;
		var image = self.currentImage;
		//image.Name = ir.escapeHtml(ir.v(self.id+"Name"));
		image.Data = ir.escapeHtml(ir.v(self.id+"Data"));
		var callback = function(res){
			if(res.ok)
			{
				Status.success("Portrait updated.");
			}			
		};
		sr5.ajaxAsync({fn:"updatePortrait",row:image.Row,data:image.Data,user:image.User},callback);
		self.close();
	},
	updateZoom:function(){
		var self = imagePreviewPop;
		self.drawGrid();
		self.drawMapData();
	},
	zz_imagePreviewPop:0
		
};