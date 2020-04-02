"use strict";
var model={
	awardCharacters:new KeyedArray("Row"),
	boards:new KeyedArray("id"),
	currentMap:null,
	groups:null,
	groupCharacters:[],
	imageElement:null,
	initBlocks:new KeyedArray("id"),
	initBlockCounter:0,
	mapData:[],
	maps:[],
	passCounter:1,
	turnCounter:1,
	selectGroupCharacters:function(groupRow,callback){
		if(model.groupCharacters[groupRow]!=null)
		{
			callback(model.groupCharacters[groupRow]);
			return;
		}		
		var innerCallback = function(res){
			if(res.ok)
			{
				var list = model.groupCharacters[groupRow] = res.list;
				callback(list);
			}
		};
		sr5.ajaxAsync({fn:"selectGroupCharacters",group:groupRow},innerCallback);
	},
	selectMap:function(imageRow,callback){
		if(model.maps[imageRow]!=null)
		{
			model.currentMap = model.maps[imageRow];
			srMap.currentMapRow = model.currentMap.Row;
			ir.set("ctlZoom",model.currentMap.Zoom);
			callback(model.currentMap);
			return;
		}		
		var innerCallback = function(res){
			if(res.ok)
			{
				model.currentMap = model.maps[imageRow] = res.map;
				srMap.currentMapRow = model.currentMap.Row;
				ir.set("ctlZoom",model.currentMap.Zoom);
				callback(model.currentMap);
			}
		};
		sr5.ajaxAsync({fn:"selectMap",row:imageRow},innerCallback);
	},
	selectMapData:function(mapRow,callback){
		if(model.mapData[mapRow]!=null)
		{
			model.currentMapData = model.mapData[mapRow];
			srMap.currentMapData = model.currentMapData;
			callback();
			return;
		}	
		var innerCallback= function(res){
			if(res.ok)
			{
				model.currentMapData = model.mapData[mapRow] = new KeyedArray("Row",res.mapData);
				srMap.currentMapData = model.currentMapData;
				callback();
			}
		};
		sr5.ajaxAsync({fn:"selectMapData",mapRow:mapRow},innerCallback);
	},
	selectPlayer:function(row,callback){
		sr5.selectPlayer(row,callback);
	},
	updateMap:function(){
		var callback = function(res){
			if(res.ok)
			{
				Status.success(ir.format("Map {0} saved.",model.currentMap.Name),4000);
			}
		};
		var map = model.currentMap;
		map.Zoom = ir.vn("ctlZoom");
		model.maps[map.Row] = map;
		sr5.ajaxAsync({fn:"updateMap",row:map.Row,zoom:map.Zoom},callback);
	},
	zz_model:0	
};
var view = {
		currentBlock:null,
		inCombat:false,
		interval:null,
		TRACK_BOX_WIDTH:"3rem",
		aaOnLoad:function(){
			view.addResizeEvent();
			view.buildScoreBoardTypeButtons();
			view.buildAddInitGroupButtons();
			sr5.doneLoading();
		},
		addAwardCharacter:function(){
			pickCharacterPop.setFilter("PC");
			pickCharacterPop.show(null,false,view.afterAwardSelectCharacters);
		},
		addAwardItem:function(){
			Status.info("coming soon...");
		},
		addCard:function(character){
			/*
			var container = ir.get("charactersContainer");	
			var image = view.getPortrait(character);
			var template = "<div class='cardContainer' style='display:flex;'>"
							+ "<div class='imgWrap'>{0}</div>"
						 + "</div>";
			container.innerHTML += ir.format(template,image);
			*/
			
		},		
		addGrid:function(){
			view.toggleTypeButtons();
		},
		addGroup:function(list){
			for(var i =0,z=list.length;i<z;i++)
			{
				var a = list[i];
				for(var j =1,y=a.Quantity;j<=y;j++)
				{
					view.addInitiative(a,a.Name + (a.Quantity>1?" (" + j +")":""));
					view.addCard(a);
					if(a.Type.toLowerCase()!=="pc")
					{
						var initBlock = ir.get("initBlock"+(model.initBlockCounter-1));
						initBlock.dataset.initiative = a.Initiative;
						initBlock.dataset.initiativeDice = a.InitiativeDice;
						var initValue = initBlock.getElementsByClassName("initiative")[0];	
						initValue.value = sr5.getRoll(a.Initiative,a.InitiativeDice);
					}
				}
			}
			view.toggleAddButtons();
			sr5.initHover();
		},
		addInit:function(){
			view.toggleAddButtons();
		},
		addInitiative:function(character,name) //type,name,image,characterRow)
		{
			var type = character.Type;
			var image = view.getPortrait(character);			
			if(name == null){
				name = "";
			}
			var container = ir.get("initiativeContainer");
			var template = "<div class='x' onclick='view.closeInitiative(this)'>X</div>"
						 + "<div class='flex' style='justify-content:flex-start;align-items:inherit;'>"
						 + "<div style='flex:0;display:flex;justify-content:flex-start;max-height:11rem;align-items:center;'><div>{1}</div><div class='flex' style='flex-direction:column;justify-content:flex-start;'>";
			
			if(character.Row != null && character.Row>0)
			{
				template += "<button type='button' class='mini hover' data-hover='Character Sheet' style='cursor:pointer;' onclick='view.openCharacterSheet("+character.Row+")'><img src='"+sr5.iconPath+"passport.svg' class='smallIcon'></button>";
			}			 
			template += "<button type='button' class='mini toggle hover' data-hover='Toggle Damage Track'  style='cursor:pointer;' onclick='view.toggleDamageGrid(this)'>"
					 + "<img src='"+sr5.iconPath+"stethoscope.svg' class='smallIcon'>" 
					 + "</button>"
					 + "<button type='button' class='mini toggle hover' data-hover='Toggle Status Track'  style='cursor:pointer;' onclick='view.toggleStatusGrid(this)'>"
					 + "<img src='"+sr5.iconPath+"customer.svg' class='smallIcon'>" 
					 + "</button>"
					 + "</div>"
					 + "</div>"
					 + "<div class='flex' style='flex:20;justify-content:flex-start;align-items:flex-start;'>"
					 + "<div class='initInputWrap flex' style='flex:4;justify-content:flex-start;'>"
					 + "<div class='inputWrap'><input type='text' value='{0}'><label class='inputLabel'>Name</label></div>"
					 + "<div class='selectWrap'>"
					 + "<select style='width:15rem'>"
					 + "<option>Physical</option>"
					 + "<option>Astral</option>";
			if(type != "critter")
			{
				template += "<option>Matrix AR</option>"
						 + "<option>Matrix VR cold-sim</option>"
						 + "<option>Matrix VR hot-sim</option>"
						 + "<option>Rigging AR</option>";
			}								 
			template += "</select><label class='inputLabel'>Type</label></div>"
							 + "<div class='inputWrap'><input type='number' tabindex='1' class='initiative' value='0' onchange='view.onInitChange(this)' onclick='this.select()' style='width:5rem;text-align:center;'><label class='inputLabel'>Initiative</label></div></div>";

			template += "</div><div class='spacer'></div>"
			template += "<div class='initGridWrap flex' style='flex:3;flex-basis:30%;display:none;'></div>";
			template += "<div class='initGridWrap flex' style='flex:6;flex-basis:40%;display:none;'></div><div class='spacer'></div>";
			
			template += "<div style='flex: 1;text-align: right;white-space: nowrap;padding:1rem;align-self:flex-end;'>"
								 + "<button type='button' class='mini initTurnBtns hover' data-hover='Delay characters turn' style='display:none;' onclick='view.delayTurn()'>Delay</button>"
								 + "<button type='button' class='mini initTurnBtns hover' data-hover='End characters turn' style='display:none;' onclick='view.endTurn()'>End</button>"
								 + "<button type='button' class='mini initDefenseBtns hover' data-hover='Reduce initiative by 5<br>Dodge,<br>Parry,<br>Block,<br>Interrupt...'  onclick='view.minusFive(this)'>-5</button>"
								 + "<button type='button' class='mini initDefenseBtns hover' data-hover='Reduce initiative by 10<br>Full Defense...' onclick='view.minusTen(this)'>-10</button>"
							 + "</div>"
						 + "</div>";
			var div = document.createElement("DIV");
			div.className = "initBlock " + type.toLowerCase();
			div.id = "initBlock"+model.initBlockCounter++;
			div.innerHTML = ir.format(template,name,image);
			model.initBlocks.add(div);
			container.appendChild(div);
			var gridWrap = div.getElementsByClassName("initGridWrap");
			var trackType = track.scoreBoardType.get("Damage");
			track.showNamePlate=false;
			track.showCloseButton=false;
			track.showTitle=true;
			var physicalMax = character.Row>0?Math.ceil(character.Body/2)+8:9;
			var stunMax = character.Row>0?Math.ceil(character.Willpower/2)+8:9;
			gridWrap[0].appendChild(track.buildGrid(trackType,name,view.closeGridCallback,physicalMax,stunMax,character.PhysicalCurrent,character.StunCurrent))
			trackType = track.scoreBoardType.get("Status");
			gridWrap[1].appendChild(track.buildGrid(trackType,""));			
			view.toggleInitButtons();
			if(type==="PC" && character.Row>0)
			{
				var charRowArray = [];
				charRowArray.push(character)
				view.afterAwardSelectCharacters(charRowArray);
			}
		},
		addResizeEvent:function(){
			if(window.attachEvent) {
			    window.attachEvent('onresize', function() {
			        view.drawGrid();
			    });
			}
			else if(window.addEventListener) {
			    window.addEventListener('resize', function() {
			        view.drawGrid();
			    });
			}
		},
		afterAwardSelectCharacters:function(list){
			for(var i =0, z=list.length;i<z;i++)
			{
				var a = list[i];
				model.awardCharacters.set(a);
			}
			ir.disable("awardSendBtn",model.awardCharacters.size()<1);
			view.buildAwardCharacterList();
		},
		afterSendAward:function(res,req){
			var errors = [];
			if(res.errors)
			{
				if (res.errors.splice)
				{
					errors = errors.concat(res.errors);
				}
				else
				{
					errors.push(res.errors);
				}
				if (errors.length>0)
				{
					Status.error(errors.join("<br>"),7000);
				}
			}
			if(res.ok)
			{
				view.resetAward();
				var msg= "";
				if(res.karma>0)
				{
					msg+= res.karma + " Karma ";
				}
				if(msg.length>0 && res.nuyen>0)
				{
					msg+= "and "
				}
				if(res.nuyen>0)
				{
					msg+= res.nuyen + sr5.nuyen;
				}
				msg+= " sent to " + res.names.replace(/,([^,]*)$/, ' and ' + '$1').replace(/"/g,'').replace(/,/g,', ');
				Status.success(msg,9000);
				var rows = req.characterRows.split(sr5.splitter);
				for(var i=0,z=rows.length;i<z;i++)
				{
					var r = ir.n(rows[i]);
					var c = sr5.characters.get(r);
					if(c!=null)
					{
						c.Nuyen += res.nuyen;
						c.Karma += res.karma;
						c.ForceRefresh=true;
					}
				}
			}
		},
		autoRollInitiative:function(){
			var blocks = model.initBlocks.values;
			for(var i =0,z=blocks.length;i<z;i++)
			{
				var b = blocks[i];
				if(b.dataset.initiative != null)
				{
					view.setBlockInitiative(b,sr5.getRoll(b.dataset.initiative ,b.dataset.initiativeDice));
				}
			}	
		},
		buildAddInitGroupButtons:function(){
			var groups = model.groups.values;
			var container = ir.get("addInitBtnSelector");
			var template = "<button class='childBtn hover' type='button' data-hover='Add your team \"{1}\"' onclick='view.pickGroup(\"{0}\")'>{1}</button>";
			var htm = "";
			for(var i=0,z=groups.length;i<z;i++)	
			{
				var t = groups[i];
				htm += ir.format(template,t.Row,t.Name);
			}
			container.innerHTML += htm;
		},
		buildAwardCharacterList:function(){
			var container = ir.get("awardCharacterList");
			container.innerHTML = "";
			var template = "<div id='awardCharacter{1}' class='awardCharacter'><div class='x hover' data-hover='Remove this character' onclick='view.removeAwardCharacter({1})'>X</div><div class='awardThumbWrap' onclick='view.toggleAwardCharacter(this,{1})'>{0}<div class='overlay'></div></div><label class='thumbLabel' >{2}</label></div>";
			var list = model.awardCharacters.values;
			for(var i =0, z=list.length;i<z;i++)
			{
				var a = list[i];
				/*
				if(a.Type !== "PC")
				{
					continue;
				}
				*/
				container.innerHTML+= ir.format(template,view.getPortrait(a),a.Row,a.Name);
			}
		},
		buildInitiative:function(blocks){
			var container = ir.get("initiativeContainer");
			container.innerHTML = "";
			for(var i =0, z=blocks.length;i<z;i++)
			{
				container.appendChild(blocks[i]);
			}
		},
		buildMap:function(map){
			var container = ir.get("mapDiv");
			var image = document.createElement("IMG");
			image.id = "fullImage"+map.Row;
			image.className = "fullImage";
			image.src = sr5.getImagePath(map,false);
			var zoom = Math.min(3,ir.vn("ctlZoom"));
			if(zoom>1)
			{
				container.classList.remove("flex");
			}
			else
			{
				container.classList.add("flex");
			}
			image.style.width = (100*zoom) + "%";
			image.style.height = (100*zoom) + "%";
			container.appendChild(image);
			model.imageElement=image;
			view.drawGrid();
		},
		buildScoreBoardTypeButtons:function(){
			var types = track.scoreBoardType.values;
			var container = ir.get("addGridBtnSelector");
			var template = "<button class='childBtn' type='button' onclick='view.pickType(\"{0}\")'>{0}</button>";
			var htm = "<div class='x' style='top:-2rem;' onclick='view.toggleTypeButtons()'>X</div>";
			for(var i=0,z=types.length;i<z;i++)	
			{
				var t = types[i];
				htm += ir.format(template,t.name);
			}
			ir.set(container,htm);
		},
		changeMap:function(){
			ir.set("mapDiv","");
			var mapRow = ir.vn("ctlMaps");
			if(mapRow>0)
			{
				model.selectMap(mapRow,view.buildMap);
			}
			else
			{
				model.currentMap = null;
			}
			ir.show("colorDiv",mapRow>0);
			ir.show("ctlZoomWrap",mapRow>0);
			ir.show("saveMapBtn",mapRow>0);
			ir.show("ctlHideGrid",mapRow>0);
			
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
			
			var image = ir.get("fullImage"+model.currentMap.Row);
			image.style.width = (100*zoom) + "%";
			image.style.height = (100*zoom) + "%";
		},
		clearInitiative:function(){
			var callback = function(yes){
				if(yes)
					{
						model.initBlocks.clear();
						view.inCombat = false;
						view.buildInitiative(model.initBlocks.values);
						view.toggleInitButtons();
						view.showInitStatus();
						window.scroll({
							  top: 0, 
							  left: 0, 
							  behavior: 'smooth' 
							});
						return;
					}
			};
			confirmPop.show(ir.format("Clear initiative?",name),callback);
		},
		closeGridCallback:function(boardId){
			model.boards.remove(boardId);
		},
		closeInitiative:function(x){	
			var callback = function(yes){
				if(yes)
				{
					model.initBlocks.remove(x.parentNode.id);
					x.parentNode.parentNode.removeChild(x.parentNode);	
					if(view.inCombat && model.initBlocks.size()<2)
					{
						view.showCurrentBlock(false);
						view.endCombatCallback(true);
						return;
					}		
					if(view.currentBlock!=null && view.currentBlock.id == x.parentNode.id)
					{
						view.endTurn();
					}		
					view.toggleInitButtons();
				}
			}
			var name = x.parentNode.getElementsByTagName("INPUT")[0].value;
			confirmPop.show(ir.format("Remove <i>{0}</i> from initiative?",name),callback);
		},
		delayTurn:function(){
			view.endTurn(true);
		},
		drawGrid:function(){
			if(model.imageElement==null)
			{
				return;
			}			
			if(!model.imageElement.complete)
			{
				return window.setTimeout(view.drawGrid,100);
			}
			view.changeZoom();
			srMap.drawGrid("mapDiv",model.currentMap.Division);
			ir.get("mapDiv").classList.add("show");
			model.selectMapData(model.currentMap.Row,view.drawMapData);
		},
		drawMapData:function(){
			if(!model.imageElement.complete)
			{
				return window.setTimeout(view.drawMapData,100);
			}
			srMap.drawMapData();
		},
		endCombat:function(){
			confirmPop.show("Are you sure you want to end combat?",view.endCombatCallback);
		},
		endCombatCallback:function(yes){
			if(yes)
			{
				var blocks = model.initBlocks.values;
				for(var i =0, z=blocks.length;i<z;i++)						
				{
					var b = blocks[i];
					if(b.dataset.initiative != null)
					{
						view.setBlockInitiative(b,sr5.getRoll(b.dataset.initiative ,b.dataset.initiativeDice));
					}
					else
					{
						view.setBlockInitiative(b,0);
					}
					b.dataset.endTurn = false;
					b.dataset.delayTurn = false;
					view.showBlock(b,false);
				}
				model.passCounter=0;
				model.turnCounter=1;
				view.inCombat = false;
				view.buildInitiative(model.initBlocks.values);
				view.toggleInitButtons();
				view.showInitStatus();
				Status.info("Combat has ended.");
			};
		},
		endTurn:function(isDelay){
			var block = view.currentBlock;
			if(isDelay)
			{
				block.dataset.delayTurn = true;
			}
			else
			{
				block.dataset.endTurn = true;
			}
			view.showCurrentBlock(false);
			view.currentBlock = block = view.getNextBlock();
			if(block==null)
			{				
				return view.nextPass();
			}	
			if(view.getBlockInitiative(block)<=0)
			{
				return view.endTurn();
			}
			if(ir.isTrue(block.dataset.endTurn))
			{
				return view.endTurn();
			}			
			view.showCurrentBlock(true);
		},
		getBlockInitiative:function(block){
			return ir.n(block.getElementsByClassName("initiative")[0].value);
		},
		getNextBlock:function(){
			var blocks = model.initBlocks.sort(view.initBlockComparator);
			for(var i =0,z=blocks.length;i<z;i++)
			{
				var b = blocks[i];
				if(!ir.isTrue(b.dataset.endTurn) && b.id != view.currentBlock.id && view.getBlockInitiative(b)>0)
				{
					return b;
				}
			}
			return null;
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
		hasInitiative:function(){
			var blocks = model.initBlocks.values;
			for(var i =0,z=blocks.length;i<z;i++)
			{
				if(view.getBlockInitiative(blocks[i])>0)
				{
					return true;
				}
			}			
			return false;
		},
		initBlockComparator:function(a,b){
			var aValue = view.getBlockInitiative(a);
			var bValue = view.getBlockInitiative(b);
			if(aValue < bValue)
			{
				return 1;
			}
			else if(aValue > bValue)
			{
				return -1;
			}
			else
			{
				return 0;
			}
		},
		minus:function(btn,amount)
		{
			var block = btn.parentNode.parentNode;
			var value = ir.n(view.getBlockInitiative(block));
			 value-=amount;
			 view.setBlockInitiative(block,Math.max(0,value))
			 view.toggleInitButtons();
			 if(view.inCombat)
			{
				view.buildInitiative(model.initBlocks.sort(view.initBlockComparator));
				view.showCurrentBlock(true);
			}
		},
		minusAward:function(type){
			var ele = ir.get("award" + type);
			ele.value = Math.max(0,ir.n(ele.value) - ir.n(ele.step));
		},
		minusHoldAward:function(type){
			view.interval = setInterval(function(){
				view.minusAward(type);
			},200);
		},
		minusReleaseAward:function(type){
			clearInterval(view.interval);
		},
		minusFive:function(btn){
			 view.minus(btn,5);
		},
		minusTen:function(btn){
			 view.minus(btn,10);
		},
		nextPass:function(){
			var blocks = model.initBlocks.values;
			for(var i =0,z=blocks.length;i<z;i++)
			{
				var b = blocks[i];
				view.setBlockInitiative(b,view.getBlockInitiative(b)-10);
				b.dataset.endTurn = false;
				b.dataset.delayTurn = false;
				b.classList.remove("endTurn");
			}	
			if(view.hasInitiative())
			{
				Status.info("Initiative pass "+ model.passCounter + " complete.");
				view.startCombat();
				model.passCounter++;
			}
			else
			{
				view.nextTurn();
				view.autoRollInitiative();
			}
			view.showInitStatus();
			
		},
		nextTurn:function(){
			Status.info("Combat turn "+ model.turnCounter + " complete.<br>Roll initiative!",5000);
			model.passCounter=0;
			model.turnCounter++;
			view.inCombat = false;
			view.updateStatus();
			view.toggleInitButtons();
		},
		onInitChange:function(){
			view.toggleInitButtons();	
			view.showCurrentBlock(true);
		},
		openCharacterSheet:function(row)
		{
			var player = sr5.characters.get(row);
			if(player==null)
			{
				model.selectPlayer(row,sr5.showPlayerCharacter);
			}
			else
			{
				sr5.showPlayerCharacter(player,player.ForceRefresh);
			}
		},
		pickCharacter:function()
		{
			pickCharacterPop.show(null,false,view.addGroup);
		},
		pickDefault:function(type){
			var blankChar = ir.copy(model.blankCharacter);
			blankChar.Type = type;
			view.addInitiative(blankChar);
			view.toggleAddButtons();
			sr5.initHover();
		},
		pickGroup:function(groupRow){
			model.selectGroupCharacters(groupRow,view.addGroup);
		},
		pickNPC:function(){
			pickNPCPop.show(view.addGroup);
		},
		pickType:function(typeClicked){
			var type = track.scoreBoardType.get(typeClicked);
			track.showNamePlate=true;
			track.showCloseButton=true;
			track.showTitle=true;
			model.boards.add(track.buildGrid(type,null,view.closeGridCallback));
			view.rebuildBoard();
			view.toggleTypeButtons();
		},
		plusAward:function(type){
			var ele = ir.get("award" + type);
			ele.value = ir.n(ele.value) + ir.n(ele.step);
		},
		plusHoldAward:function(type){
			view.interval = setInterval(function(){
				view.plusAward(type);
			},200);
		},
		plusReleaseAward:function(type){
			clearInterval(view.interval);
		},
		readAward:function(req){
			var list = model.awardCharacters.values;
			var characterRows = "";
			var splitter = "";
			for(var i =0, z=list.length;i<z;i++)
			{
				var a = list[i];
				if(a.Off)
				{
					continue;
				}
				characterRows+=splitter+a.Row;
				splitter=sr5.splitter;
			}
			if(characterRows.length < 1)
			{
				Status.error("Choose Runners to send rewards to.")
				return false;
			}
			
			req.characterRows = characterRows;
			req.nuyen = ir.vn("awardNuyen");
			req.karma = ir.vn("awardKarma");
			if(req.nuyen + req.karma == 0)
			{
				Status.error("Nuyen? Karma?")
				return false;
			}
			return req;
		},	
		rebuildBoard:function(){
			var container = ir.get("boardContainer");
			var boards = model.boards.sort(view.sortBoardComparator);
			for(var i =0,z=boards.length;i<z;i++)
			{
				container.appendChild(boards[i]);
			}
			hoverPop.init();
		},		
		removeAwardCharacter:function(characterRow){
			model.awardCharacters.remove(characterRow);
			var div = ir.get("awardCharacter"+characterRow,true);
			ir.deleteNode(div);		
			ir.disable("awardSendBtn",model.awardCharacters.size()<1);	
		},
		resetAward:function(){
			ir.set("awardNuyen",0);
			ir.set("awardKarma",0);
		},
		saveMapData:function(){
			model.updateMap();
		},
		sendAward:function(){
			var req = {fn:"award",characterRows:null};
			req = view.readAward(req);
			if(req)
			{
				sr5.ajaxAsync(req,function(res){view.afterSendAward(res,req);});			
			}
		},
		setBlockInitiative:function(block,value){
			block.getElementsByClassName("initiative")[0].value = Math.max(0,value);
		},
		showBlock:function(block,show)
		{
			if(block!=null)
			{
				if(show && view.inCombat)
				{
					block.classList.add("selected");
				}
				else
				{
					block.classList.remove("selected");
				}
				if(ir.isTrue(block.dataset.endTurn))
				{
					block.classList.add("endTurn");
				}
				else
				{
					block.classList.remove("endTurn");
				}
				var btns = block.getElementsByClassName("initTurnBtns");
				for(var i =0,z=btns.length;i<z;i++)
				{
					ir.show(btns[i],show && view.inCombat);
				}
				var defenseBtns = block.getElementsByClassName("initDefenseBtns");
				for(var i =0,z= defenseBtns.length;i<z;i++)
				{
					var btn = defenseBtns[i];
					var value = ir.n(view.getBlockInitiative(block));
					ir.disable(btn,value < Math.abs(ir.n(btn.innerHTML)));
				}	
				//block.scrollIntoView();

				window.scroll({
					  top: block.offsetTop - 50, 
					  left: 0, 
					  behavior: 'smooth' 
					});
			}
		},
		showCurrentBlock:function(show){
			var block = view.currentBlock;
			view.showBlock(block,show);
		},
		showInitStatus:function(){
			ir.show("initStatus",view.inCombat || model.turnCounter>1);
			ir.set("initStatus",ir.format("Pass: {0}, Turn: {1}",model.passCounter,model.turnCounter));
		},
		sortBoardComparator:function(a,b){
			if(a.className < b.className) return -1;
			else if (a.className > b.className) return 1;
			return 0;
		},
		startCombat:function(){
			if(!view.hasInitiative())
			{
				Status.error("Initiative not set.",6000);
				return;
			}
			var initBlocks = model.initBlocks.sort(view.initBlockComparator);
			view.buildInitiative(initBlocks);
			if(!view.inCombat)
			{
				view.inCombat = true;
				model.passCounter=1;
			}
			view.toggleInitButtons();
			view.currentBlock = initBlocks[0];
			view.showCurrentBlock(true);
			view.showInitStatus();
			window.scroll({
				  top: 20, 
				  left: 0, 
				  behavior: 'smooth' 
				});
		},
		toggleAddButtons:function()
		{
			sr5.toggleChildButtons("addInitBtn");
		},
		toggleAwardCharacter:function(ele,characterRow){
			var c = model.awardCharacters.get(characterRow)
			if(ele.classList.contains("off"))
			{
				ele.classList.remove("off");
				c.Off = false;
			}
			else
			{
				ele.classList.add("off");
				c.Off = true;
			}
		},
		toggleDamageGrid:function(ele){
			var grid = ele.parentNode.parentNode.parentNode.getElementsByClassName("initGridWrap")[0];
			ele.classList[!ir.visible(grid)?"add":"remove"]("on");
			ir.show(grid,!ir.visible(grid));
		},
		toggleGrid:function(){
			var show = ir.isTrue(ir.v("hideGridChk"));
			var grids = document.getElementsByClassName("gridOverlay");
			for(var i =0, z=grids.length;i<z;i++)
			{
				var a = grids[i];
				if(!show)
				{
					a.classList.remove("hide");
				}
				else
				{
					a.classList.add("hide");
				}
			}
			//ir.show("gridOverlayFormapDiv",!show);
		},
		toggleInitButtons:function(){
			ir.show("endCombatBtn",view.inCombat || model.turnCounter>1);
			ir.show("startCombatBtn",!view.inCombat && model.initBlocks.size()>1);
			ir.set("startCombatBtn",model.turnCounter>1?"Next Turn":"Start Combat");
			var defenseBtns = document.getElementsByClassName("initDefenseBtns");
			ir.show("clearInitBtn",model.initBlocks.size()>0);
			for(var i =0,z= defenseBtns.length;i<z;i++)
			{
				var btn = defenseBtns[i];
				var block = btn.parentNode.parentNode;
				var value = ir.n(view.getBlockInitiative(block));
				ir.show(btn,view.inCombat);
				ir.disable(btn,value < Math.abs(ir.n(btn.innerHTML)));
			}			
		},
		toggleStatusGrid:function(ele){
			var grid = ele.parentNode.parentNode.parentNode.getElementsByClassName("initGridWrap")[1];
			ele.classList[!ir.visible(grid)?"add":"remove"]("on");
			ir.show(grid,!ir.visible(grid));
		},
		toggleTypeButtons:function()
		{
			sr5.toggleChildButtons("addGridBtn");
		},
		updateStatus:function(){
			var container = ir.get("initiativeContainer");
			//Acid Status effect
			var acid = container.getElementsByClassName("statusAcid");
			var disoriented = container.getElementsByClassName("statusDisoriented");
			var electric = container.getElementsByClassName("statusElectric");
			var fire = container.getElementsByClassName("statusFire");
			var fullDefense = container.getElementsByClassName("statusFullDefense");
			var recoil = container.getElementsByClassName("statusRecoil");
			var surprised = container.getElementsByClassName("statusSurprised");
			var paralysis = container.getElementsByClassName("statusParalysis");
			var nausea = container.getElementsByClassName("statusNausea");
			var minusOneEachTurn = [acid,fire,surprised,paralysis,nausea,fullDefense,electric,disoriented,recoil];
			for(var i =0,z=minusOneEachTurn.length;i<z;i++)
			{
				var array = minusOneEachTurn[i];
				for(var j =0,y=array.length;j<y;j++)
				{
					var a = array[j];
					if(a.classList.contains("fill"))
					{
						var sibling = a.nextElementSibling;
						if(sibling==null)
						{
							track.toggleStatus(a);
						}
						else
						{
							if(sibling.classList.contains("inputWrap"))
							{
								var input = sibling.children[0];
								var isRecoil = a.classList.contains("statusRecoil");//recoil is a special beast	
								if(!isRecoil)
								{
									var isFire = a.classList.contains("statusFire");//fire is the only exception						
									input.value = Math.max(0,ir.n(input.value)-(isFire?-1:1));
									if(input.value<1)
									{
										track.toggleStatus(a);
									}
								}
								else
								{
									input.value = Math.min(0,ir.n(input.value)-1);
									if(input.value>-1)
									{
										track.toggleStatus(a);
									}
								}
							}
						}
					}
				}
			}
			
		},
		updateZoom:function(){
			view.drawGrid();
			view.toggleGrid();
		},
	zz_view:0
};