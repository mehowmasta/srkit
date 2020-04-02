var track = {
	id:"track",
	TRACK_BOX_WIDTH:"3rem",
	blankType:{id:null},
	gridCounter:0,
	scoreBoardType:null,
	showCloseButton:true,
	showNamePlate:true,
	showTitle:true,
	boxCount:function(ele,count){
		var parent = ele.parentNode.parentNode;
		var wrapper = parent;
		if(!parent.classList.contains("gridWrap"))
		{
			var wrapper = parent.parentNode.parentNode;
		}	
		var title = parent.getElementsByClassName("title")[0];
		var type = track.scoreBoardType.get(wrapper.dataset.type);
		var isAmmo = type.name === "Ammo";
		var boxes = parent.getElementsByClassName("trackBox");
		if(count>0)
		{
			var newBox = document.createElement("DIV");
			newBox.className = "trackBox " + (isAmmo?"ammo":"");
			var num = boxes.length;
			newBox.onclick = function(){track.fillTo(this,num)};
			if(type.showModifiers && (num+1)%type.columns==0)
			{
				newBox.innerHTML = "-" + Math.ceil(num/type.columns); 
			}
			if(isAmmo)
			{

				if((boxes.length)%10==0)
				{
					var spacer = document.createElement("DIV");
					spacer.className = "flexSpacer";
					parent.insertBefore(spacer, boxes[boxes.length-1].nextSibling);
					parent.insertBefore(newBox, spacer.nextSibling);
				}
				else if((boxes.length)%5==0)
				{
					var space = document.createElement("SPAN");
					space.innerHTML ="&emsp;";
					parent.insertBefore(space, boxes[boxes.length-1].nextSibling);
					parent.insertBefore(newBox, space.nextSibling);
				}
				else
				{
					parent.insertBefore(newBox, boxes[boxes.length-1].nextSibling);
				}
			}
			else
			{
				parent.insertBefore(newBox, boxes[boxes.length-1].nextSibling);
			}
		}
		else
		{
			var box = boxes[boxes.length-1];
			var prevBox = box.previousElementSibling;
			box.parentNode.removeChild(box);
			while(prevBox.nextSibling!=null && !prevBox.nextSibling.classList.contains("trackControls"))
			{
				prevBox.parentNode.removeChild(prevBox.nextSibling);
			}
		}
		if(isAmmo && title!=null)
		{
			title.innerHTML = type.name + " [" + boxes.length + "]";
		}
		var minus = parent.getElementsByClassName("trackMinus")[0];
		ir.disable(minus,boxes.length<2);
	},
	/**
	 * ex. buildGrid(Type.Damage,"Mehow",function(),10,9,4,5) 10 will be physical max and 4 will be physical current
	 */
	buildGrid:function(type,name,closeCallback/*args for max values,args for fill values*/){
		var t = type;
		if(name == null)
		{
			name="";
		}
		var wrapper = document.createElement("DIV");
		wrapper.className = t.name + " gridWrap";
		wrapper.closeCallback = closeCallback;
		wrapper.id = "gridWrapper_" + track.gridCounter++;
		wrapper.dataset.type = t.name;
		var htm = "";
		if(t.columns>0)
		{
			if(t.name === "Damage")
			{
				wrapper.style.width = "calc("+track.TRACK_BOX_WIDTH+" * "+(t.columns*2)+" + 4rem);";
				if(track.showCloseButton)
				{
					htm += "<div class='x' onclick='track.closeGrid(this)'>X</div>";
				}
				if(track.showTitle)
				{
					htm += "<div class='flex flexSpacer titleWrap'><img class='smallIcon' src='"+sr5.iconPath+t.imgSrc+"'><div class='title'>" + t.name + "</div></div>";
				}
				if(track.showNamePlate)
				{
					htm += "<div class='inputWrap'><input class='boardName' type='text' value='"+name+"'><label class='inputLabel'>Name</label></div><div class='flexSpacer'></div>";
				}
				htm += "<div class='damageTrackWrap flex'>"
					+ "<div class='damageTrack physical'>"
					+ "<div class='subtitle'>Physical Track</div>" 
					+ track.getCubes(t,arguments[3],arguments[5]) 
					+ track.getControls(t)
					+ "</div>"
					+ "<div class='damageTrack stun'>"
					+ "<div class='subtitle' >Stun Track</div>" 
					+ track.getCubes(t,arguments[4],arguments[6]) 
					+ track.getControls(t)
					+ "</div>"
					+ "</div>";					
				wrapper.innerHTML = htm;
			}
			else if (t.name === "Status")
			{

				//wrapper.style.minWidth = "22rem";
				if(track.showCloseButton)
				{
					htm += "<div class='x' onclick='track.closeGrid(this)'>X</div>";
				}
				if(track.showTitle)
				{
					htm += "<div class='flex flexSpacer titleWrap'><img class='smallIcon' src='"+sr5.iconPath+t.imgSrc+"'><div class='title' >" + t.name + "</div></div>";
				}
				if(track.showNamePlate)
				{
					htm += "<div class='inputWrap'><input class='boardName' type='text'><label class='inputLabel'>Name</label></div><div class='flexSpacer'></div>";
				}
				htm	+= track.getStatus();
				wrapper.innerHTML = htm;
			}
			else
			{
				wrapper.style.maxWidth = "calc("+track.TRACK_BOX_WIDTH+" * "+t.columns+" + 2.2rem)";
				if(track.showCloseButton)
				{
					htm += "<div class='x' onclick='track.closeGrid(this)'>X</div>";
				}
				if(track.showTitle)
				{
					if(t.name=="Ammo")
					{
						var amount = arguments[3] || t.max;
						htm += "<div class='flex flexSpacer titleWrap ammo'><img class='smallIcon' src='"+sr5.iconPath+t.imgSrc+"'><div class='title' >" + t.name + " [" + amount + "]</div></div>" ;
					}
					else
					{
						htm += "<div class='flex flexSpacer titleWrap'><img class='smallIcon' src='"+sr5.iconPath+t.imgSrc+"'><div class='title' >" + t.name + "</div></div>" ;
					}						
				}
				if(track.showNamePlate)
				{
					htm += "<div class='inputWrap'><input class='boardName' type='text'><label class='inputLabel'>Name</label></div><div class='flexSpacer'></div>";
				}
				htm	+=track.getCubes(t,arguments[3],arguments[4]) 
					+ track.getControls(t);
				wrapper.innerHTML = htm;
			}
		}
		return wrapper;
	},	
	clearTrack:function(title,count){
		if(title.parentNode.nextSibling)
		{
			track.fillTo(title.parentNode.nextSibling,count);
		}
		else
		{
			track.fillTo(title.parentNode.previousSibling,count);
		}
		
	},
	closeGrid:function(x){
		if(x.parentNode.closeCallback)
		{
			x.parentNode.closeCallback(x.parentNode.id);
		}		
		x.parentNode.parentNode.removeChild(x.parentNode);
	},
	fillTo:function(box,count){
		var parent = box.parentNode;
		var boxes = parent.getElementsByClassName("trackBox");
		for(var i=0, z=boxes.length;i<z;i++)
		{
			if(i>count)
			{
				boxes[i].classList.remove("fill");
			}
			else
			{
				boxes[i].classList.add("fill");
			}
		}
		track.save(parent,count);
	},
	getCharacterRow:function(ele){
		var parent = ele;
		while(parent!=null && !parent.classList.contains("playerCharacterPopTrack"))
		{
			parent = parent.parentNode;
			if(parent.classList==null)
			{
				parent = parent.parentNode;
			}
		}		
		if(parent==null)
		{
			return 0;
		}
		return ir.n(parent.id.substring("playerCharacterPopDamageTrack".length,parent.id.length));
	},
	getControls:function(type)
	{
		return "<div class='flex trackControls' style='align-items:center;'><div class='flex trackMinus hover' data-hover='Remove block'onclick='track.boxCount(this,-1)' ondblclick='track.boxCount(this,-5)'>⇦</div>" 
				+ "<div class='flex hover imgBtn' data-hover='Clear track' style='margin:0;cursor:pointer;width:2.5rem;height:2.5rem;align-items:center;padding:0;' onclick='track.clearTrack(this,-1)'><img class='smallIcon' src='"+sr5.iconPath+"bulldozer.svg'></div>" 
				+ "<div class='flex trackPlus hover' data-hover='Add block' onclick='track.boxCount(this,1)' ondblclick='track.boxCount(this,5)'>⇨</div></div>";
	},
	getCubes:function(type,maxValue,currentValue)
	{
		var t = type;
		var max = maxValue || t.max;
		var current = currentValue || 0;
		var template = "<div class='trackBox {2}' onclick=track.fillTo(this,{0})>{1}</div>";
		var isAmmo = t.name === "Ammo";
		var htm = "";
		for(var i = 0,z=max;i<z;i++)
		{
			var className = current > i?"fill ":"";
			if(t.showModifiers && (i+1)%t.columns==0)
			{
				className += isAmmo?" ammo ":"";
				htm += ir.format(template,i,"-" + Math.ceil(i/t.columns),className); 
			}
			else
			{
				className += isAmmo?" ammo ":"";
				htm += ir.format(template,i,"",className); 
			} 
			if(isAmmo && i!=0 && (i+1)%10==0)
			{
				htm +="<div class='flexSpacer'></div>"
			}
			else if(isAmmo && i!=0 && (i+1)%5==0)
			{
				htm+="<span>&emsp;</span>";
			}
		}
		return htm;
	},	
	getItemRow:function(ele){
		var parent = ele;
		while(parent!=null && !parent.classList.contains("playerCharacterItem"))
		{
			parent = parent.parentNode;
			if(parent.classList==null)
			{
				parent = parent.parentNode;
			}
		}		
		if(parent==null)
		{
			return 0;
		}
		return ir.n(parent.id.substring("playerCharacterItem".length,parent.id.length));
	},
	getStatus:function(){
		var statusTypes = sr5.statusType.values;
		var htm = "";
		var template = "<div class='statusTrack flex'><span class='subtitle'>{4}</span><div class='statusBoxWrap flex'><div class='statusBox status{0} {2} hover' data-hover='{5}' data-defaultinput='{6}' onclick=track.toggleStatus(this,\"{0}\")>{1}</div>{3}</div></div>";
		for(var i =0,z=statusTypes.length;i<z;i++)
		{
			var t = statusTypes[i];
			var img = "<img class='statusIcon' src='"+sr5.iconPath+t.imgSrc+"'>";
			var input = "";
			if(t.useInput)
			{
				input = "<div class='inputWrap' style='display:none;'><input type='number' value='"+t.inputDefault+"' style='width:6rem;text-align:center;' onclick='this.select()'><label class='inputLabel'>"+t.inputLabel+"</label></div>";
			}
			var hover = t.description;
			if(t.resist.length>0)
			{
				hover += "<br>Resist: " + t.resist;
			}
			if(t.remove.length>0)
			{
				hover += "<br>Remove: " + t.remove;
			}
			htm += ir.format(template,t.name,img,"",input,t.text,hover,t.inputDefault);
		}
		return htm;
	},
	save:function(ele,count)
	{
		
		if(ele.classList.contains("physical"))
		{
			var characterRow = track.getCharacterRow(ele);
			if(characterRow==0)
			{
				return;
			}
			count = ir.n(count) + 1;
			sr5.ajaxAsync({fn:"updatePhysical",characterRow:characterRow,count:count})
			var char = sr5.characters.get(characterRow);
			if(char!=null)
			{
				char.PhysicalCurrent = count;
			}
		}
		if(ele.classList.contains("stun"))
		{
			var characterRow = track.getCharacterRow(ele);
			if(characterRow==0)
			{
				return;
			}
			count = ir.n(count) + 1;
			sr5.ajaxAsync({fn:"updateStun",characterRow:characterRow,count:count})
			var char = sr5.characters.get(characterRow);
			if(char!=null)
			{
				char.StunCurrent = count;
			}
		}
		if(ele.classList.contains("Ammo"))
		{
			var weaponRow = track.getItemRow(ele);
			if(weaponRow==0)
			{
				return;
			}
			count = ir.n(count) + 1;
			sr5.ajaxAsync({fn:"updateAmmo",weaponRow:weaponRow,count:count})
		}

		if(ele.classList.contains("Drone"))
		{
			var droneRow = track.getItemRow(ele);
			if(droneRow==0)
			{
				return;
			}
			count = ir.n(count) + 1;
			sr5.ajaxAsync({fn:"updateDrone",droneRow:droneRow,count:count})
		}
		if(ele.classList.contains("Vehicle"))
		{
			var vehicleRow = track.getItemRow(ele);
			if(vehicleRow==0)
			{
				return;
			}
			count = ir.n(count) + 1;
			sr5.ajaxAsync({fn:"updateVehicle",vehicleRow:vehicleRow,count:count})
		}
	},
	toggleStatus:function(ele,name){
		var sibling = ele.nextElementSibling;
		if(ele.classList.contains("fill"))
		{
			ele.classList.remove("fill");
			if(sibling!=null)
			{
				ir.hide(sibling);
			}
		}
		else
		{
			ele.classList.add("fill");
			if(sibling!=null)
			{
				ir.show(sibling);
				sibling.firstElementChild.value = ele.dataset.defaultinput;
			}
		}
	},
	zz_track:0	
};