"use strict";
var sr5 = {
		blankPlayer:null,
		characters:new KeyedArray("Row"),
		deleteButton:"deleteButton2075",
		delimiter:null,
		iconPath:"",
		images:new KeyedArray("Row"),
		lastAjaxRequest:null,
		lastDefaultFace:522,
		messageCount:0,
		nuyen:"¥",
		pageErrors:null,		
		pageStatus:null,
		player:null,
		splitter:null,
		star:"&#9733;",
		statusType:null,
		unknownFaces:[218,219,240,241,270,271,304,305,337,338,373,374,410,411,430,431,459,460,489,490],
		updateReminder:"Click UPDATE to save changes.",
		webSocketClient:null,
		ajaxAsync : function(obj, callBack,supressError)
		{
			var sep = "?";
			var url = "ajax.jsp";
			for (var attr in obj)
			{
				if (obj.hasOwnProperty(attr))
				{
					url += sep + attr + "=" + encodeURIComponent(obj[attr]);
					sep = "&";
				}
			}
			url += sep + "frPg=" + ir.pageName();
			url += sep + "subTime=" + new Date().getTime();
			try
			{
				var req = new XMLHttpRequest();
				req.onreadystatechange = function()
				{
					if(req.readyState == 4)
					{
						if(req.status == 200)
						{
							sr5.lastAjaxResponse = "sr5.ajaxAsync:" + req.responseText;
							if (callBack != null)
							{
								var response = ir.evalObj(req.responseText);
								sr5.checkResponse(response);
								try
								{
									callBack(response);
								}
								catch (exc)
								{
									if(supressError != true)
									{
										Status.error("sr5.ajaxAsync:" + url + ":callback error:" + exc,7000);		
									}							
								}
							}
						}
						else
						{
							if(supressError != true)
							{
								Status.error("sr5.ajaxAsync:Unexpected error submitting request.",7000);
							}
						}
					}
				};
				sr5.lastAjaxRequest = "sr5.ajaxAsync:" + url;
				req.open("GET", url, true);
				req.send(null);
				return;
			}
			catch (e)
			{
				if(supressError != true)
				{
					ir.error("sr5.ajaxAsync:u=" + url + ":" + e);
				}
			}
		},	
		ajaxAsyncUpload : function(data,callback,progress)		
		{					
			var xhr;
			if (window.XMLHttpRequest)
			{
				xhr = new XMLHttpRequest();
			}
			else if (window.ActiveXObject)
			{
				xhr = new ActiveXObject("Microsoft.XMLHTTP");
			}
			if(progress != null)
			{
					xhr.onprogress = progress;
					xhr.upload.addEventListener("progress",xhr.onprogress,false);			
			}
			/*
			if(abortBtnId != null)
			{
				abortBtn = ir.get(abortBtnId)
				if(abortBtn)
				{
					abortBtn.onclick = function(){
						xhr.abort();
						callback({ok:0,error:"Upload canceled by user."});
						return;
					};
				}
			}
			*/
			xhr.open("POST", "upload.jsp", true);
			//
			xhr.setRequestHeader("charset", "utf-8");		
			xhr.onloadend = function (e) 
			{
				var res = ir.evalObj(xhr.responseText);
				if(callback!=null)
				{
					callback(res);
				}
			};			
			xhr.send(data);
		},
		applyNewRows:function(newRowsString,keyedArray){
			var newArray = newRowsString.split(sr5.delimiter);
			for(var i =0, z=newArray.length;i<z;i++)
			{
				var rows = newArray[i].split(sr5.splitter)
				var rec = keyedArray.get(rows[0]);
				if(rec!=null)
				{
					keyedArray.remove(rows[0]);
					rec.ItemRow = ir.n(rows[1]);
					keyedArray.add(rec);
				}
			}
		},
		changeThumbUnknown:function(ele){
			if(pickPortraitPop)
			{
				pickPortraitPop.callback = function(p){
					ele.src = sr5.getImagePath(p,true);
				};
				pickPortraitPop.show();
			}			
		},
		checkResponse:function(res)
		{
			var errors=[];
			if (res.hasOwnProperty("security") && res.security>0)
			{
				errors.push("Unauthorized!");
				sr5.go("index.jsp");
			}
			else if (res.exc)
			{
				errors.push("Unexpected error in request: " + res.exc);
			}
			else if (res.ee)
			{
				if (res.ee.splice)
				{
					errors = errors.concat(res.ee);
				}
				else
				{
					errors.push(res.ee);
				}
			}
			if (errors.length>0)
			{
				Status.error(errors.join("<br>"),7000);
			}
			return errors.length==0;
		},	
		doneLoading:function(){
			var form = ir.get("f1",true);
			if(form!=null)
			{
				//window.setTimeout(function(){form.classList.remove("loading")},5);
				window.setTimeout(function(){form.classList.remove("loading")},980);
			}
		},
		equip:function(ele)
		{
			var characterRow = ir.n(ele.dataset.characterrow);
			var char = sr5.characters.get(characterRow);
			var row = ir.n(ele.dataset.row);
			var itemRow = ir.n(ele.dataset.itemrow);
			var type = ele.dataset.type;
			var equipped =ele.checked;
			var callback = function(res){
				if(res.ok){
					if(char!=null)
					{
						var a = char[type].get(itemRow);
						if(a!=null)
						{
							a.Equipped = equipped;
						}
						ir.set(playerCharacterPop.prefix+type+characterRow+"-"+itemRow,sr5["get"+type](a));
						playerCharacterPop["build"+type]();
					}
					if(model!=null && model.character!=null && model["character"][type]!=null && model.character.Row == characterRow)
					{
						var a = model["character"][type].get(itemRow);
						if(a!=null)
						{
							a.Equipped = equipped;
							ir.set(view.prefix+type+characterRow+"-"+itemRow,sr5["get"+type](a));
						}
					}
				}
			};
			sr5.ajaxAsync({fn:"updateEquip",type:type,itemRow:itemRow,equipped:equipped},callback);
		},
		getAdeptPower:function(s)
		{
			return "&bull;"
				+ s.Name 
				+ (s.Level>1 ? " [" + s.Level + "]" : "");
		},
		getAmmoCount:function(w){
			if(w.Ammo.indexOf("/")>-1 || w.Ammo.toLowerCase().indexOf("or")>-1)
			{
				return ir.n(w.Ammo.substring(0,3).replace(/\D+/g, ''));
			}
			return ir.n(w.Ammo.replace(/\D+/g, ''));
		},
		getArmor:function(s){

			return (s.Equipped ? sr5.star : "&bull;")
				+ s.Name
				+ " - "
				+ s.Environment
				+ " [" 
				+ "<span class='noWrap'>AR: " + s.ArmorRating+ "</span>"
				+"]"
				+ (s.Quantity >1? " x"+s.Quantity:"");
		},
		getArmorSum:function(armorArray){
			var sum = 0;
			for(var i = 0 ,z=armorArray.length;i<z;i++)
			{
				var a = armorArray[i];
				if(a.Equipped)
				{
					sum+= ir.n(a.ArmorRating);
				}
			}
			return sum;
		},
		getBioware:function(s){
			return "&bull;"
				+ s.Name
				+ (s.MaxRating>1?" ["+ s.Rating +"]":"")
				+ (s.Grade !== "Standard"?(" - " + s.Grade ): "");
		},
		getCapacity:function(obj){
			if(obj.Capacity.toLowerCase().indexOf("rating")>-1)
			{
				var multiplier = ir.n(obj.Capacity.replace(/\D+/g, ''));
				if(multiplier>0)
				{
					return obj.Rating * multiplier;
				}
				else
				{
					return obj.Rating;
				}
			}
			return ir.n(obj.Capacity.replace(/\D+/g, ''));			
		},
		getCharacterAdeptPower:function(powers,onclick,idPrefix)
		{
			var htm = "";
			var sum = 0;
			for(var i =0,z=powers.length;i<z;i++)
			{
				var s = powers[i];
				if(s.Delete)
				{
					continue;
				}
				var type = s._t.substring(1,s._t.length);
				htm += "<div class='detail' id='"+idPrefix+type+s.CharacterRow+"-"+s.ItemRow+"' "
					+ (onclick?" onclick='"+onclick+"(\""+type+"\","+s.ItemRow+","+s.CharacterRow+")'":"")
					+ ">" 
					+ sr5.getAdeptPower(s)
					+ "</div>";
			}
			return htm;
		},
		getCharacterArmor:function(armor,onclick,idPrefix)
		{
			var htm = "";
			var sum = 0;
			for(var i =0,z=armor.length;i<z;i++)
			{
				var s = armor[i];
				if(s.Delete)
				{
					continue;
				}
				var type = s._t.substring(1,s._t.length);
				htm += "<div class='detail' id='"+idPrefix+type+s.CharacterRow+"-"+s.ItemRow+"' "
					+ (onclick? " onclick='"+onclick+"(\""+type+"\","+s.ItemRow+","+s.CharacterRow+")'":"")
					+ ">"
					+ sr5.getArmor(s)
					+ "</div>";
			}
			return htm;
		},
		getCharacterBioware:function(bioware,onclick,idPrefix)
		{
			var htm = "";
			for(var i =0,z=bioware.length;i<z;i++)
			{
				var s = bioware[i];
				if(s.Delete)
				{
					continue;
				}
				var type = s._t.substring(1,s._t.length);
				htm += "<div class='detail' id='"+idPrefix+type+s.CharacterRow+"-"+s.ItemRow+"' "
					+ (onclick?" onclick='"+onclick+"(\"Bioware\","+s.ItemRow+","+s.CharacterRow+")'":"")
					+ ">"
					+ sr5.getBioware(s)
					+ "</div>";
			}
			return htm;
		},
		getCharacterBonus:function(c,attribute,baseOverride/*TODO*/)
		{
			if(c.Bonus && c.Bonus[attribute])
			{
				var base = baseOverride!=null?baseOverride:c[attribute];
				var array = c.Bonus[attribute].values;
				var sum = 0;
				var hoverText = "";
				var breakline = "";
				for(var i =0, z=array.length;i<z;i++)
				{
					
					var a = array[i];
					sum += a.Value;
					hoverText += breakline + (a.Value>0?"+":"") + ir.n(a.Value) + " " + a.Name;
					breakline = "<br>";
				}
				return " (<span onclick='playerCharacterPop.showBonus("+c.Row+",\""+attribute+"\")' class='bonus hover' data-hover='"+hoverText+"'>"+(ir.n(base)+ir.n(sum))+"</span>)";
			}
			else
			{
				return "";
			}
		},
		getCharacterContact:function(contacts,onclick,idPrefix)
		{
			var htm = "";
			for(var i =0,z=contacts.length;i<z;i++)
			{
				var s = contacts[i];
				if(s.Delete)
				{
					continue;
				}
				var type = s._t.substring(1,s._t.length);
				htm += "<div class='detail' id='"+idPrefix+type+s.CharacterRow+"-"+s.Row+"' "
					+ (onclick? " onclick='"+onclick+"(\""+type+"\","+s.Row+","+s.CharacterRow+")'" :"")
					+ ">"
					+ sr5.getContact(s)
					+ "</div>";
			}
			return htm;
		},
		getCharacterCyberdeck:function(cyberdecks,onclick,idPrefix)
		{
			var htm = "";
			for(var i =0,z=cyberdecks.length;i<z;i++)
			{
				var s = cyberdecks[i];
				if(s.Delete)
				{
					continue;
				}
				var type = s._t.substring(1,s._t.length);
				htm += "<div class='detail' id='"+idPrefix+type+s.CharacterRow+"-"+s.ItemRow+"' "
					+ (onclick? " onclick='"+onclick+"(\""+type+"\","+s.ItemRow+","+s.CharacterRow+")'" :"")
					+ ">"
					+ sr5.getCyberdeck(s)
					+ "</div>";
			}
			return htm;
		},	
		getCharacterCyberware:function(cyberware,onclick,idPrefix)
		{
			var htm = "";
			for(var i =0,z=cyberware.length;i<z;i++)
			{
				var s = cyberware[i];
				if(s.Delete)
				{
					continue;
				}
				var type = s._t.substring(1,s._t.length);
				htm += "<div class='detail' id='"+idPrefix+type+s.CharacterRow+"-"+s.ItemRow+"' "
					+ (onclick?"onclick='"+onclick+"(\""+type+"\","+s.ItemRow+","+s.CharacterRow+")'":"")
					+ ">"
					+ sr5.getCyberware(s)
					+ "</div>";
			}
			return htm;
		},	
		getCharacterDrone:function(drones,onclick,idPrefix)
		{
			var htm = "";
			for(var i =0,z=drones.length;i<z;i++)
			{
				var s = drones[i];
				if(s.Delete)
				{
					continue;
				}
				var type = s._t.substring(1,s._t.length);
				htm += "<div class='detail' id='"+idPrefix+type+s.CharacterRow+"-"+s.ItemRow+"' "
					+ (onclick?"onclick='"+onclick+"(\""+type+"\","+s.ItemRow+","+s.CharacterRow+")'":"")
					+ ">" 
					+ sr5.getDrone(s)
					+ "</div>";
			}
			return htm;
		},
		getCharacterGear:function(gear,onclick,idPrefix)
		{
			var htm = "";
			for(var i =0,z=gear.length;i<z;i++)
			{
				var s = gear[i];
				if(s.Delete)
				{
					continue;
				}
				var type = s._t.substring(1,s._t.length);
				htm += "<div class='detail' id='"+idPrefix+type+s.CharacterRow+"-"+s.ItemRow+"' "
					+ (onclick?"onclick='"+onclick+"(\""+type+"\","+s.ItemRow+","+s.CharacterRow+")'":"")
					+ ">" 
					+ sr5.getGear(s)
					+ "</div>";
			}
			return htm;
		},
		getCharacterKnowledge:function(knowledge,onclick,idPrefix)
		{
			var htm = "";
			var htmBottom = "";
			for(var i =0,z=knowledge.length;i<z;i++)
			{
				var s = knowledge[i];
				if(s.Delete)
				{
					continue;
				}
				var type = "Knowledge";
				var isLanguage = s.Type==="Language";
				if(isLanguage)
				{
					htmBottom += "<div class='detail' id='"+idPrefix+type+s.CharacterRow+"-"+s.ItemRow+"' "
						+ (onclick? " onclick='"+onclick+"(\""+type+"\","+s.ItemRow+","+s.CharacterRow+")'" :"")
						+ ">"
						+ sr5.getKnowledge(s)
						+ "</div>";
				}
				else
				{
					htm += "<div class='detail' id='"+idPrefix+type+s.CharacterRow+"-"+s.ItemRow+"' "
					+ (onclick? " onclick='"+onclick+"(\""+type+"\","+s.ItemRow+","+s.CharacterRow+")'" :"")
					+ ">"
					+ sr5.getKnowledge(s)
					+ "</div>";
				}				
			}
			return htm + htmBottom;
		},
		getCharacterMaxPhysical:function(char){
			return Math.ceil(char.Body/2)+8;
		},
		getCharacterMaxStun:function(char){
			return Math.ceil(char.Willpower/2)+8;	
		},
		getCharacterQuality:function(qualities,onclick,idPrefix)
		{
			var htm = "";
			for(var i =0,z=qualities.length;i<z;i++)
			{
				var s = qualities[i];
				if(s.Delete)
				{
					continue;
				}
				var type = s._t.substring(1,s._t.length);
				htm += "<div class='detail' id='"+idPrefix+type+s.CharacterRow+"-"+s.ItemRow+"' "
					+ (onclick?"onclick='"+onclick+"(\""+type+"\","+s.ItemRow+","+s.CharacterRow+")'":"")
					+ ">" 
					+ sr5.getQuality(s)
					+ "</div>";
			}
			return htm;
		},
		getCharacterSkill:function(character,onclick,idPrefix)
		{
			var skills = character.Skill.values;
			var htm = "";
			for(var i =0,z=skills.length;i<z;i++)
			{
				var s = skills[i];
				if(s.Delete)
				{
					s.Rating=0;
					continue;
				}
				if(s.Rating==0 && (!s.Bonus || s.Bonus.values.length==0))
				{
					continue;
				}
				var type = s._t.substring(1,s._t.length);
				htm += "<div class='detail' id='"+idPrefix+type+s.CharacterRow+"-"+s.Row+"' "
					+ (onclick?"onclick='"+onclick+"(\""+type+"\","+s.Row+","+s.CharacterRow+")'":"")
					+ ">"
					+ sr5.getSkill(s)
					+ "</div>";
			}
			return htm;
		},
		getCharacterSpell:function(spells,onclick,idPrefix)
		{
			var htm = "";
			for(var i =0,z=spells.length;i<z;i++)
			{
				var s = spells[i];
				if(s.Delete)
				{
					continue;
				}
				var type = s._t.substring(1,s._t.length);
					htm += "<div class='detail' id='"+idPrefix+type+s.CharacterRow+"-"+s.ItemRow+"' "
					+ (onclick?"onclick='"+onclick+"(\""+type+"\","+s.ItemRow+","+s.CharacterRow+")'":"")
					+ ">" 
					+ sr5.getSpell(s)
					+ "</div>";
			}
			return htm;
		},
		getCharacterStat:function(player,attName,baseValue,baseOverride)
		{
			var base = baseOverride!=null?baseOverride:player[attName];
			var hoverText = "Base " + base + "<br>";
			var bonusText = "";
			if(player.Bonus && player.Bonus[attName])
			{
				var array = player.Bonus[attName].values;
				var sum = 0;
				var breakline = "";
				for(var i =0, z=array.length;i<z;i++)
				{					
					var a = array[i];
					sum += a.Value;
					hoverText += breakline + (a.Value>0?"+":"") + ir.n(a.Value) + " " + a.Name;
					breakline = "<br>";
				}
				bonusText = "(" + (ir.n(base)+ir.n(sum))+")";
			}
			return "<span onclick='playerCharacterPop.showAttribute("+player.Row+",\""+attName+"\")' class='attribute hover' data-hover='"+hoverText+"'>" + player[attName] + bonusText + "</span>";
		},
		getCharacterStatsTable:function(char){
			var p = char;
			var htm = "";
			var template = "<div class='pctableWrap'>"
				 		  + "<table id='playerCharacterAttributesTable' class='pctable show'>"
				 		  + "<thead><tr>"
						  + "<td class='tdc'>B<span class='long'>OD</span></td>"
						  + "<td class='tdc'>A<span class='long'>GI</span></td>"
						  + "<td class='tdc'>R<span class='long'>EA</span></td>"
						  + "<td class='tdc'>S<span class='long'>TR</span></td>"
						  + "<td class='tdc'>W<span class='long'>IL</span></td>"
						  + "<td class='tdc'>L<span class='long'>OG</span></td>"
						  + "<td class='tdc'>I<span class='long'>NT</span></td>"
						  + "<td class='tdc'>C<span class='long'>HA</span></td>"
						  + "<td class='tdc'>E<span class='long'>D</span>G</td>"
						  + "<td class='tdc'>ES<span class='long'>S</span></td>"
						  + "<td class='tdc {10}'>M<span class='long'>AG</span></td>"
						  + "<td class='tdc {12}'>R<span class='long'>E</span>S</td>"
						  + "</tr></thead>"
						 + "<tbody><tr>"
						  + "<td class='tdc'>{0}</td>"
						  + "<td class='tdc'>{1}</td>"
						  + "<td class='tdc'>{2}</td>"
						  + "<td class='tdc'>{3}</td>"
						  + "<td class='tdc'>{4}</td>"
						  + "<td class='tdc'>{5}</td>"
						  + "<td class='tdc'>{6}</td>"
						  + "<td class='tdc'>{7}</td>"
						  + "<td class='tdc'>{8}</td>"
						  + "<td class='tdc'>{9}</td>"
						  + "<td class='tdc {10}'>{11}</td>"
						  + "<td class='tdc {12}'>{13}</td>"
						  + "</tr></tbody></table></div>";
			htm += ir.format(template,
					sr5.getCharacterStat(p,"Body",p.Body),
					sr5.getCharacterStat(p,"Agility",p.Agility),
					sr5.getCharacterStat(p,"Reaction",p.Reaction),
					sr5.getCharacterStat(p,"Strength",p.Strength),
					sr5.getCharacterStat(p,"Willpower",p.Willpower),
					sr5.getCharacterStat(p,"Logic",p.Logic),
					sr5.getCharacterStat(p,"Intuition",p.Intuition),
					sr5.getCharacterStat(p,"Charisma",p.Charisma),
					sr5.getCharacterStat(p,"Edge",p.Edge),
					sr5.getCharacterStat(p,"Essence",p.Essence),
					p.Magic>0?"":"hide",sr5.getCharacterStat(p,"Magic",p.Magic),
					p.Resonance>0?"":"hide",sr5.getCharacterStat(p,"Resonance",p.Resonance));
			return htm;
		},
		getCharacterVehicle:function(vehicles,onclick,idPrefix)
		{
			var htm = "";
			for(var i =0,z=vehicles.length;i<z;i++)
			{
				var s = vehicles[i];
				if(s.Delete)
				{
					continue;
				}
				var type = s._t.substring(1,s._t.length);
				htm += "<div class='detail' id='"+idPrefix+type+s.CharacterRow+"-"+s.ItemRow+"' "
					+ (onclick?"onclick='"+onclick+"(\""+type+"\","+s.ItemRow+","+s.CharacterRow+")'":"")
					+ ">" 
					+ sr5.getVehicle(s)
					+ "</div>";
			}
			return htm;
		},
		getCharacterWeapon:function(weapons,onclick,idPrefix)
		{
			var htm = "";
			for(var i =0,z=weapons.length;i<z;i++)
			{
				var s = weapons[i];
				if(s.Delete)
				{
					continue;
				}
				var type = s._t.substring(1,s._t.length);
				htm += "<div class='detail' id='"+idPrefix+type+s.CharacterRow+"-"+s.ItemRow+"' "
					+ (onclick?"onclick='"+onclick+"(\""+type+"\","+s.ItemRow+","+s.CharacterRow+")'":"")
					+ ">" 
					+ sr5.getWeapon(s)
					+ "</div>";
			}
			return htm;
		},
		getContact:function(s){
			return "&bull;"
				+ s.Archetype
				+ " "
				+ s.Name
				+ " (Connection "+ s.Connection 
				+ ", Loyalty " + s.Loyalty
				+ ")";
		},
		getCyberdeck:function(s){
			var internal = "";
			if(s.Programs !=null && s.Programs.values!=null && s.Programs.values>0)
			{
				var programs = s.Programs.values;
				var comma = "";
				for(var i =0,z=programs.length;i<z;i++)
				{
					var a = programs[i];
					if(a.Delete)
					{
						continue;
					}
					internal += comma + a.Name;
					comma =", ";
				}
			}
			return (s.Equipped ? sr5.star : "&bull;")
				+ s.Name + " [" 
				+ "<span class='noWrap'>DR: " + s.DeviceRating+ "</span>"
				+ ", <span class='noWrap'>ASDF: " + s.AttributeArray+ "</span>"
				+ ", <span class='noWrap'>Programs: " + s.Program+ "</span>"
				+"]"
				+ (internal.length>0?" ("+internal+")":"")
				+ (s.Quantity >1? " x"+s.Quantity:"");
		},
		getCyberware:function(s){
			var internal = "";
			if(s.Attachments !=null && s.Attachments.values!=null)
			{
				var attachments = s.Attachments.values;
				var comma = "";
				for(var i =0,z=attachments.length;i<z;i++)
				{
					var a = attachments[i];
					if(a.Delete)
					{
						continue;
					}
					internal += comma + a.Name + (a.MaxRating>1?" " + a.Rating:"");
					comma =", ";
				}
			}
			return "&bull;"
				+ s.Name
				+ (s.MaxRating>1?" ["+ s.Rating +"]":"")
				+ (internal.length>0?" ("+internal+")":"")
				+ (s.Grade !== "Standard"?" - " + s.Grade : "");
		},
		getDrone:function(s){
			return (s.Equipped ? sr5.star : "&bull;")
				+ s.Name 
				+ " - "
				+ s.Size + " [" 
				+ "<span class='noWrap'>Handling: " + s.Handling + "</span>"
				+ ", <span class='noWrap'>Speed: " + s.Speed+ "</span>"
				+ ", <span class='noWrap'>Accel: " + s.Acceleration+ "</span>"
				+ ", <span class='noWrap'>Bod: " + s.Body+ "</span>"
				+ ", <span class='noWrap'>Arm: " + s.Armor+ "</span>"
				+ ", <span class='noWrap'>Pilot: " + s.Pilot+ "</span>"
				+ ", <span class='noWrap'>Sensor: " + s.Sensor+ "</span>"
				+"]"
				+ (s.Quantity >1? " x"+s.Quantity:"");
		},
		getDroneCondition:function(drone){
			return 6 + Math.ceil(ir.n(drone.Body.substring(0,1))/2);
		},
		getEquipCheckbox:function(record){
			var type = record._t.substring(1,record._t.length);
			var checked = record.Equipped?"checked='checked'":"";
			return "<div class='checkWrap'>"
				+ "<input name='equipChk"+type+record.ItemRow+"' id='equipChk"+type+record.ItemRow+"' "+checked+" data-row='"+record.Row+"' data-characterrow='"+record.CharacterRow+"' data-itemrow='"+record.ItemRow+"' data-type='"+type+"' tabindex='1' type='checkbox' onclick='sr5.equip(this)'>"
				+ "<label for='equipChk"+type+record.ItemRow+"'>"
				+ "Equip"
				+ "</label></div>";
		},
		getEssence:function(obj){
			var grade = sr5.cyberwareGrade.get(obj.Grade);
			if(obj.Essence.toLowerCase().indexOf("rating")>-1)
			{
				var multiplier = ir.n(obj.Essence.replace(/[^0-9.,]+/g, ''));
				if(multiplier>0)
				{
					return ir.n((obj.Rating * multiplier * grade.essenceMultiplier).toFixed(2));
				}
				else
				{
					return ir.n((obj.Rating * grade.essenceMultiplier).toFixed(2));
				}
			}
			return ir.n((obj.Essence.replace(/[^0-9.,]+/g, '') * grade.essenceMultiplier).toFixed(2));	
		},
		getGear:function(s){
			return "&bull;"
				+ s.Name 
				+ (s.MaxRating>1 ? " [R" + s.Rating + "]" : "")
				+ " "
				+ (s.Quantity >1? " x"+s.Quantity:"");
		},
		getImage:function(i){
			var template ="<img class='fullImage' src='{0}' title='{1}'>"
				return ir.format(template,sr5.getImagePath(i,false),i.Name);
		},
		getImagePath:function(i,isThumb){
			return "images/" + i.Type + "/" + (isThumb?"thumb":"image")+"_"+i.User+"_"+i.Row+"."+i.Extension;
		},
		getImageUnknown:function(){
			var template ="<img class='fullImage' src='images/Face/{0}'>"
				return ir.format(template,"image_0_"+sr5.unknownFaces[parseInt(Math.random()*sr5.unknownFaces.length) + 1]+".jpg");
		},
		getKnowledge:function(s){
			return "&bull;"
				+ s.Name
				+ " ("
				+ s.Type
				+ ") "
				+ (s.Native?"N":s.Rating);
		},
		getMatrixCondition:function(obj){			
			return 8 + Math.ceil(obj.DeviceRating/2);
		},
		getMetaTypeName:function(metatypeRow){
			var m = sr5.metatypes.get(metatypeRow);
			if(m==null)
			{
				return "Unknown";
			}
			return m.Name;
		},
		getQuality:function(s){
			return "&bull;"
				+ s.Name 
				+ (s.MaxRating>1 ? " [" + s.Rating + "]" : "");
		},
		getRecordSection:function(tableName,obj)
		{
			var type = tableName.substring(1,tableName.length);
			
			return sr5["getSection"+type](obj);
		},
		getRoll:function(base,diceCount){
			var roll = parseInt(base);
			for(var i =0,z=parseInt(diceCount);i<z;i++)
			{
				roll += parseInt(Math.random()*6) + 1;
			}	
			return roll;
		},
		getSection:function(s,searchFunction,showDesc){
			if(searchFunction==null)
			{
				searchFunction=function(x){return x;}
			}
			var table = s._t.substring(1,s._t.length);
			return sr5["getSection"+table](s,searchFunction,showDesc);
		},
		getSectionAdeptPower:function(s,searchFunction,showDesc){
			var template = "<div class='section' onclick='view.toggleAdeptPower({5})'>"
				 + "<div class='title titleUnderline'><b>{0}</b></div>"
				 + "<div class='subtitle'>{1}</div>"
				 + "<div>"
				 + "<table class='properties'><tr><td>{2}</td></tr><tr><td>{3}</td></tr></table>"
				 + "</div>"
				 + "<div class='desc {6}' id='desc{5}'>{4}</div>"
				 + "<div class='source'>{7}</div>"
				 + "</div>";
			var description = "";
			if(s.Prerequisite.length>0)
			{
				description +="<b>Prerequisite</b><br><div class='feat'>"+searchFunction(s.Prerequisite)+"</div><br>"
			}
			if(s.Drain)
			{
				description +="<b>Drain</b><br><div class='feat'>Drain per level</div><br>"
			}		
			description+= "&emsp;" + searchFunction(s.Description);
			return ir.format(template,
					searchFunction(s.Name),
					"Cost: " + s.Cost+" PP " +(s.Max>1?"per level":"") + (s.Max>1 && s.Max<99?" (MAX " +s.Max+")":""),
					"<b>Activation:</b> " + s.Activation,
					"<b>Duration:</b> " + s.Duration,
					"&emsp;" + searchFunction(description),
					s.Row,
					showDesc?"show":"",
					s.Source);
		},
		getSectionArmor:function(s,searchFunction,showDesc){
			var template = "<div class='section' onclick='view.toggleArmor({3})'>"
				 + "<div class='title titleUnderline'><b>{0}</b></div>"
				 + "<div class='subtitle'><b>{1}</b></div>"
				 + sr5.getTable(s,false,false)
				 + "<div class='desc {4}' id='desc{3}'>{2}</div>"
				 + "<div class='source'>{5}</div>"
				 + "</div>";
			var description = "";
			if(s.Features.length>0)
			{
				description +="<b>Features</b><br><div class='feat'>"+searchFunction(s.Features)+"</div><br>"
			}
			if(s.SpecialRules.length>0)
			{
				description +="<b>Special Rules</b><br><div class='feat'>"+searchFunction(s.SpecialRules)+"</div><br>"
			}
			if(s.Wireless.length>0)
			{
				description +="<b>Wireless Bonus</b><br><div class='feat'>"+searchFunction(s.Wireless)+"</div><br>"
			}			
			description+= "&emsp;" + searchFunction(s.Description);
			return ir.format(template,
					searchFunction(s.Name),
					s.Cost,
					description,
					s.Row,
					showDesc?"show":"",
					s.Source);
		},
		getSectionAttribute:function(s,searchFunction,showDesc){
			var template = "<div class='section' onclick=''>"
			 	+ "<div class='title titleUnderline'><b>{0}</b></div>"
			 	+ "<div class='subtitle'>{1}</div>"
				+ "<div class='desc {3}'>{2}</div>"
				+ "</div>";
			return ir.format(template,
					searchFunction(s.Name + " (" + s.Abbreviation + ")"),
					s.Type,
					"&emsp;" + searchFunction(s.Description),
					"show");
		},
		getSectionBioware:function(s,searchFunction,showDesc){
			var template = "<div class='section' onclick='view.toggleBioware({6})'>"
			 	+ "<div class='title titleUnderline'><b>{0}</b></div>"
			 	+ "<div class='subtitle'>{1}</div>"
			 	+ sr5.getTable(s,false,false)
			 	+ "<div class='desc {7}' id='desc{6}'>{5}</div>"
			 	+ "<div class='source'>{8}</div>"
			 + "</div>";
			var description = searchFunction(s.Description);
			if(s.Wireless.length>0)
			{
				description +="<br><br><b>Wireless Bonus</b><br><div class='feat'>"+searchFunction(s.Wireless)+"</div>";
			}
			return ir.format(template,
					searchFunction(s.Name),
					s.Cost,
					s.Essence,
					s.Capacity,
					s.Availability,
					(s.MaxRating>1?"<b>MAX RATING: </b>" + s.MaxRating +"<br><br>":"") + "&emsp;" + description,
					s.Row,
					showDesc?"show":"",
					s.Source);
		},
		getSectionCharacterContact:function(s,searchFunction,showDesc)
		{
			var template = "<div class='section' onclick='view.toggleContact({8})'>"
				 + "<div class='title titleUnderline'><b>{0}</b></div>"
				 + "<div class='subtitle'>{1}</div>"
				 + "<div class='flex'>"
				 + "<div id='sectionImage{8}' style='flex-basis:0%;'></div>"
				 + "<div class='properties flex' style='justify-content:space-between;padding-left:1rem;align-items:flex-start;flex:1;'>"
				 + "<div>{2}</div><div class='spacer'></div><div>{3}</div><div class='spacer'></div><div>{4}</div><div class='spacer'></div><div>{5}</div><div class='spacer'></div><div>{6}</div>"
				 + "</div>"
				 + "</div>"
				 + "<div class='desc {9}' id='desc{8}'>{7}</div>"
				 + "</div>";
			return ir.format(template,
					searchFunction(s.Name),
					"(" + searchFunction(s.Archetype)+")",
					(s.Age>0?"<b>Age:</b> " + s.Age:""),
					"<b>Sex:</b> " + sr5.sex.get(s.Sex).Name,
					"<b>MetaType:</b> " + sr5.getMetaTypeName(s.MetaType),
					"<b>Connection:</b> " + s.Connection,
					"<b>Loyalty:</b> " + s.Loyalty,
					"&emsp;" + searchFunction(s.Note),
					s.Row,
					showDesc?"show":"");
		},

		getSectionCharacterKnowledge:function(s,searchFunction,showDesc)
		{
			if(s.Row==0)
			{
				return "";
			}
			var template = "<div class='section' onclick='view.toggleKnowledge({2})'>"
			 	+ "<div class='title titleUnderline'><b>{0}</b></div>"
				+ "<div class='desc {3}' id='desc{2}'>{1}</div>"
				+ "<div class='source'>{4}</div>"
				+ "</div>";
			return ir.format(template,
					searchFunction(s.Type),
					"&emsp;" + searchFunction(s.Description),
					s.Row,
					showDesc?"show":"",
					s.Source);
		},
		getSectionCritterPower:function(s,searchFunction,showDesc){
			var template = "<div class='section' onclick='view.toggleCritterPower({8})'>"
				 + "<div class='title titleUnderline'><b>{0}</b></div>"
				 + "<div class='subtitle'>{1}</div>"
				 + "<div>"
				 + "<table class='properties'><tr><td>{2}</td><td>{3}</td><td>{4}</td></tr><tr><td>{5}</td><td>{6}</td><td></td></tr></table>"
				 + "</div>"
				 + "<div class='desc {9}' id='desc{8}'>{7}</div>"
				 + "<div class='source'>{10}</div>"
				 + "</div>";
			return ir.format(template,
					searchFunction(s.Name),
					"(" + searchFunction(s.PowerType)+")",
					"<b>Type:</b> " + s.SpellType,
					"<b>Range:</b> " + s.Range,
					"",
					"<b>Action:</b> " + s.Action,
					"<b>Duration:</b> " + s.Duration,
					"&emsp;" + searchFunction(s.Description),
					s.Row,
					showDesc?"show":"",
					s.Source);
		},
		getSectionCyberdeck:function(s,searchFunction,showDesc){
			var template = "<div class='section' onclick='view.toggleCyberdeck({3})'>"
				 + "<div class='title titleUnderline'><b>{0}</b></div>"
				 + "<div class='subtitle'><b>{1}</b></div>"
				 + sr5.getTable(s,false,false)
				 + "<div class='desc {4}' id='desc{3}'>{2}</div>"
				 + "<div class='source'>{5}</div>"
				 + "</div>";
			var description = (s.Specialty?"&emsp;<b>Specialty</b><br><br>":"") + "&emsp;" + searchFunction(s.Description);
			return ir.format(template,
					searchFunction(s.Name),
					s.Cost,
					description,
					s.Row,
					showDesc?"show":"",
					s.Source);
		},
		getSectionCyberware:function(s,searchFunction,showDesc){
			var template = "<div class='section' onclick='view.toggleCyberware({6})'>"
			 	+ "<div class='title titleUnderline'><b>{0}</b></div>"
			 	+ "<div class='subtitle'>{1}</div>"
			 	+ sr5.getTable(s,false,false)
			 	+ "<div class='desc {7}' id='desc{6}'>{5}</div>"
			 	+ "<div class='source'>{8}</div>"
			    + "</div>";
			var description = (s.MaxRating>1?"<b>MAX RATING: </b>" + s.MaxRating +"<br><br>":"") + "&emsp;" + searchFunction(s.Description);
			if(s.Wireless.length>0)
			{
				description +="<br><br><b>Wireless Bonus</b><br><div class='feat'>"+searchFunction(s.Wireless)+"</div>";
			}
			return ir.format(template,
					searchFunction(s.Name),
					s.Cost,
					s.Essence,
					s.Capacity,
					s.Availability,
					description,
					s.Row,
					showDesc?"show":"",
					s.Source
					);
		},
		getSectionDrone:function(s,searchFunction,showDesc){
			var template = "<div class='section' onclick='view.toggleDrone({3})'>"
				 + "<div class='title titleUnderline'><b>{0}</b></div>"
				 + "<div class='subtitle'><b>{1}</b></div>"
				 + sr5.getTable(s,false,false)
				 + "<div class='desc {4}' id='desc{3}'>{2}</div>"
				 + "<div class='source'>{5}</div>"
				 + "</div>";
			var description = "";
			if(s.Type.length>0)
			{
				description +="<b>Type</b><br><div class='feat'>"+searchFunction(s.Type)+"</div><br>"
			}
			if(s.Style.length>0)
			{
				description +="<b>Style</b><br><div class='feat'>"+searchFunction(s.Style)+"</div><br>"
			}		
			description += "&emsp;" + searchFunction(s.Description);
			return ir.format(template,
					searchFunction(s.Name + " ("+s.Size+")"),
					s.Cost,
					description,
					s.Row,
					showDesc?"show":"",
					s.Source);
		},
		getSectionGear:function(s,searchFunction,showDesc){
			var template = "<div class='section' onclick='view.toggleGear({3})'>"
				 + "<div class='title titleUnderline'><b>{0}</b></div>"
				 + "<div class='subtitle'><b>{1}</b></div>"
				 + sr5.getTable(s,false,false)
				 + "<div class='desc {4}' id='desc{3}'>{2}</div>"
				 + "<div class='source'>{5}</div>"
				 + "</div>";
			var description = "";
			if(s.Wireless.length>0)
			{
				description +="<b>Wireless Bonus</b><br><div class='feat'>"+searchFunction(s.Wireless)+"</div><br>"
			}
			description+= "&emsp;" + searchFunction(s.Description);
			return ir.format(template,
					searchFunction(s.Name),
					s.Cost,
					description,
					s.Row,
					showDesc?"show":"",
					s.Source);
		},
		getSectionMatrixAction:function(s,searchFunction,showDesc){
			var template = "<div class='section' onclick='view.toggleAction({5})'>"
				 + "<div class='title titleUnderline'><b>{0}</b></div>"
				 + "<div class='subtitle'>{1}</div>"
				 + "<div><b>Marks Required:</b> {2}</div>"
				 + "<div><b>Test:</b> {3}</div>"
				 + "<div class='desc {6}' id='desc{5}'>{4}</div>"
				 + "<div class='source'>{7}</div>"
				 + "</div>";
			return ir.format(template,
					searchFunction(s.Name),
					"("+searchFunction(s.Action)+" Action)",
					s.Marks,
					searchFunction(s.Test),
					"&emsp;" + searchFunction(s.Description),
					s.Row,
					showDesc?"show":"",
					s.Source);
		},
		getSectionMentorSpirit:function(s,searchFunction,showDesc){
			var template = "<div class='section' onclick='view.toggleSpirit({7})'>"
				 + "<div class='title titleUnderline'><b>{0}</b></div>"
				 + "<div class='subtitle'>{5}</div>"
				 + "<div class='bold caps' style='margin-top:1rem;'>Advantages</div>"
				 + "<div>&emsp;<b>All:</b> {1}</div>"
				 + "<div>&emsp;<b>Magician:</b> {2}</div>"
				 + "<div>&emsp;<b>Adept:</b> {3}</div>"
				 + "<div class='desc {8}' id='desc{7}'>"
				 + "<div class='bold caps' style='margin-top:1rem;'>Disadvantages</div>"
				 + "<div>&emsp;{4}</div>"
				 + "<br>{6}</div>"
				 + "<div class='source'>{9}</div>"
				 + "</div>";
			return ir.format(template,
					searchFunction(s.Name),
					searchFunction(s.AdvantageAll),
					searchFunction(s.AdvantageMagician),
					searchFunction(s.AdvantageAdept),
					searchFunction(s.Disadvantage),
					(s.SimilarArchetypes.length>0?"("+s.SimilarArchetypes+")":""),
					"&emsp;" + searchFunction(s.Description),
					s.Row,
					showDesc?"show":"",
					s.Source);
		},
		getSectionProgram:function(s,searchFunction,showDesc){
			var template = "<div class='section' onclick='view.toggleProgram({3})'>"
				 + "<div class='title titleUnderline'><b>{0}</b></div>"
				 + "<div class='subtitle'>{1}</div>"
				 + "<div class='desc {4}' id='desc{3}'>{2}</div>"
				 + "<div class='source'>{5}</div>"
				 + "</div>";
			return ir.format(template,
					searchFunction(s.Name),
					"("+searchFunction(s.Type)+")",
					searchFunction(s.Description),
					s.Row,
					showDesc?"show":"",
					s.Source);
		},
		getSectionQuality:function(s,searchFunction,showDesc){
			var template = "<div class='section' onclick='view.toggleQuality({5})'>"
			 	+ "<div class='title titleUnderline'><b>{0}</b></div>"
			 	+ "<div class='subtitle'>{1}</div>"
			 	+ "<div class='info'>{3}</div>"
			 	+ "<div class='info'>{4}</div>"
				+ "<div class='desc {6}' id='desc{5}'>{2}</div>"
				+ "<div class='source'>{7}</div>"
				+ "</div>";
			return ir.format(template,
					searchFunction(s.Name),
					(s.Type==="Negative"?"BONUS: ":"COST: ")+s.Karma+"",
					"&emsp;" + searchFunction(s.Description),
					s.MaxRating>1?"MAX RATING: " + s.MaxRating:"",
					s.Prerequisites.length>0?s.Prerequisites:"",
					s.Row,
					showDesc?"show":"",
					s.Source);
		},
		getSectionSkill:function(s,searchFunction,showDesc){
			var template = "<div class='section' onclick='view.toggleSkill({6})'>"
			 	+ "<div class='title titleUnderline'><b>{0}</b></div>"
			 	+ "<div class='subtitle'>{1}</div>"
			 	 + "<div>"
				 + "<table class='properties'><tr><td>{3}</td><td>{4}</td></tr><tr><td colspan='2' style='white-space:initial;' >{5}</td></tr></table>"
				 + "<div class='desc {7}' id='desc{6}'>{2}</div>"
				 + "<div class='source'>{8}</div>"
				 + "</div>"								
				 + "</div>";
			return ir.format(template,
				searchFunction(s.Name),
				"("+searchFunction(s.Attribute)+")",
				"&emsp;" + searchFunction(s.Description),
				"<b>Default:</b> " + (s.Default?"Yes":"No"),
				"<b>Skill Group:</b> " + (s.Group.length==0?"None":searchFunction(s.Group)),
				"<b>Specialization:</b> " + (s.Specialization.length==0?"None":searchFunction(s.Specialization)),
				s.Row,
				showDesc?"show":"",
				"Core");
		},
		getSectionSpell:function(s, searchFunction,showDesc){
			var template = "<div class='section' onclick='view.toggleSpell({8})'>"
				 + "<div class='title titleUnderline'><b>{0}</b></div>"
				 + "<div class='subtitle'>{1}</div>"
				 + "<div>"
				 + "<table class='properties'><tr><td>{2}</td><td>{3}</td><td>{4}</td></tr><tr><td>{5}</td><td>{6}</td><td></td></tr></table>"
				 + "</div>"
				 + "<div class='desc {9}' id='desc{8}'>{7}</div>"
				 + "<div class='source'>{10}</div>"
				 + "</div>";
			return ir.format(template,
					searchFunction(s.Name),
					s.Effects.length>0?"("+searchFunction(s.Effects)+")":"",
					"<b>Type:</b> " + s.Type,
					"<b>Range:</b> " + s.Range,
					(s.Damage.length>0?"<b>Damage:</b> " + s.Damage:""),
					"<b>Duration:</b> " + s.Duration,
					"<b>Drain:</b> " + s.Drain,
					"&emsp;" + searchFunction(s.Description),
					s.Row,
					showDesc?"show":"",
					s.Source);
		},
		getSectionVehicle:function(s, searchFunction,showDesc){
			var template = "<div class='section' onclick='view.toggleVehicle({3})'>"
				 + "<div class='title titleUnderline'><b>{0}</b></div>"
				 + "<div class='subtitle'><b>{1}</b></div>"
				 + sr5.getTable(s,false,false)
				 + "<div class='desc {4}' id='desc{3}'>{2}</div>"
				 + "<div class='source'>{5}</div>"
				 + "</div>";
			return ir.format(template,
					searchFunction(s.Name),
					s.Cost,
					searchFunction(s.Description),
					s.Row,
					showDesc?"show":"",
					s.Source);
		},
		getSectionWeapon:function(s, searchFunction,showDesc){
			var template = "<div class='section' onclick='view.toggleWeapon({3})'>"
				 + "<div class='title titleUnderline'><b>{0}</b></div>"
				 + "<div class='subtitle'><b>{1}</b></div>"
				 + sr5.getTable(s,false,false)
				 + "<div class='desc {4}' id='desc{3}'>{2}</div>"
				 + "<div class='source'>{5}</div>"
				 + "</div>";
			var description =  "";
			if(s.Modifiers.length>0)
			{
				description +="<b>Modifiers</b><br><div class='feat'>"+searchFunction(s.Modifiers)+"</div><br>"
			}
			if(s.StandardUpgrades.length>0)
			{
				description +="<b>Standard Upgrades</b><br><div class='feat'>"+searchFunction(s.StandardUpgrades)+"</div><br>"
			}
			if(s.Wireless.length>0)
			{
				description +="<b>Wireless Bonus</b><br><div class='feat'>"+searchFunction(s.Wireless)+"</div><br>"
			}			
			description += "&emsp;" + searchFunction(s.Description);
			return  ir.format(template,
					searchFunction(s.Name),
					s.Cost,
					description,
					s.Row,
					showDesc?"show":"",
					s.Source);
		},
		getSkill:function(s){
			var bonus = 0;
			var hoverText = "";
			if(s.Bonus && s.Bonus.values.length>0)
			{
				var bs = s.Bonus.values;
				var breakline = "";
				for(var i =0, z=bs.length;i<z;i++)
				{
					var b = bs[i];
					bonus += b.Value;
					hoverText += breakline + (b.Value>0?"+":"") + ir.n(b.Value) + " " + b.Name;
					breakline = "<br>";
				}
			}
			return "<span class='hover' data-hover='"+hoverText+"' >&bull;"
				+ s.Name 
				+ " " + (bonus>0? "<b>" + (ir.n(s.Rating) + ir.n(bonus)) +"</b>" : s.Rating)
				+ (s.Special.length>0?" (+2 "+s.Special+")":"")
				+ "</span>";
		},
		getSpell:function(s){
			return "&bull;"
				+ s.Name 
				+ " - "
				+ s.Type
				+ " [" 
				+ "<span class='noWrap'>Range: " + s.Range+ "</span>"
				+ (s.Damage.length>0?", <span class='noWrap'>Damage: " + s.Damage+ "</span>":"")
				+ ", <span class='noWrap'>Duration: " + s.Duration+ "</span>"
				+ ", <span class='noWrap'>Drain: " + s.Drain+ "</span>"
				+ "]";
		},
		getTable:function(s,showName,showCost){
			var table = sr5.getTableName(s);
			if(sr5["getTable"+table])
			{
				return "<div class='tableWrap'>" + sr5["getTable"+table](s,showName,showCost) + "</div>";
			}
			return "";
		},
		getTableArmor:function(s,showName,showCost){
			var template = "<table class='table show'>"
						 + "<thead><tr>"
						  + "<td class='tdl {0}'>NAME</td>"
						  + "<td class='tdc'>ARMOR</td>"
						  + "<td class='tdc'>CAPACITY</td>"
						  + "<td class='tdc'>AVAIL</td>"
						  + "<td class='tdr {5}'>COST</td>"
						  + "</tr></thead>"
						 + "<tbody><tr>"
						  + "<td class='tdl {0}'>{1}</td>"
						  + "<td class='tdc'>{2}</td>"
						  + "<td class='tdc'>{3}</td>"
						  + "<td class='tdc'>{4}</td>"
						  + "<td class='tdr {5}'>{6}</td>"
						  + "</tr></tbody>"
						 + "</table>";
			return ir.format(template,
					(showName?"":"hide"),s.Name,
					s.ArmorRating,
					s.Capacity,
					s.Availability,
					(showCost?"":"hide"),s.Cost);
					
		},
		getTableCyberdeck:function(s,showName,showCost){
			var template = "<table class='table show'>"
						 + "<thead><tr>"
						  + "<td class='tdl {0}'>NAME</td>"
						  + "<td class='tdc'>DR</td>"
						  + "<td class='tdc'>Asdf</td>"
						  + "<td class='tdc'>Prog</td>"
						  + "<td class='tdc'>Avail</td>"
						  + "<td class='tdr {6}'>COST</td>"
						  + "</tr></thead>"
						 + "<tbody><tr>"
						  + "<td class='tdl {0}'>{1}</td>"
						  + "<td class='tdc'>{2}</td>"
						  + "<td class='tdc'>{3}</td>"
						  + "<td class='tdc'>{4}</td>"
						  + "<td class='tdc'>{5}</td>"
						  + "<td class='tdr {6}'>{7}</td>"
						  + "</tr></tbody>"
						 + "</table>";
			return  ir.format(template,
					(showName?"":"hide"),s.Name,
					s.DeviceRating,
					s.AttributeArray,
					s.Program,
					s.Availability,
					(showCost?"":"hide"),s.Cost);
		},
		getTableCyberware:function(s,showName,showCost){
			var template = "<table class='table show'>"
						 + "<thead><tr>"
						  + "<td class='tdl {0}'>NAME</td>"
						  + "<td class='tdc '>ESS</td>"
						  + "<td class='tdc '>CAPACITY</td>"
						  + "<td class='tdc '>AVAIL</td>"
						  + "<td class='tdr {5}'>COST</td>"
						  + "</tr></thead>"
						 + "<tbody><tr>"
						  + "<td class='tdl {0}'>{1}</td>"
						  + "<td class='tdc'>{2}</td>"
						  + "<td class='tdc'>{3}</td>"
						  + "<td class='tdc'>{4}</td>"
						  + "<td class='tdr {5}'>{6}</td>"
						  + "</tr></tbody>"
						 + "</table>";
			return  ir.format(template,
					(showName?"":"hide"),s.Name,
					s.Essence,
					s.Capacity,
					s.Availability,
					(showCost?"":"hide"),s.Cost);
		},
		getTableDrone:function(s,showName,showCost)
		{
			var template = "<table class='table show'>"
					 + "<thead><tr>"
					  + "<td class='tdl {0}'>NAME</td>"
					  + "<td class='tdc'>Han</td>"
					  + "<td class='tdc'>Spd</td>"
					  + "<td class='tdc'>Acel</td>"
					  + "<td class='tdc'>Bod</td>"
					  + "<td class='tdc'>Arm</td>"
					  + "<td class='tdc'>Plt</td>"
					  + "<td class='tdc'>Sen</td>"
					  + "<td class='tdc'>AVAIL</td>"
					  + "<td class='tdr {10}'>COST</td>"
					  + "</tr></thead>"
					 + "<tbody><tr>"
					  + "<td class='tdl {0}'>{1}</td>"
					  + "<td class='tdc'>{2}</td>"
					  + "<td class='tdc'>{3}</td>"
					  + "<td class='tdc'>{4}</td>"
					  + "<td class='tdc'>{5}</td>"
					  + "<td class='tdc'>{6}</td>"
					  + "<td class='tdc'>{7}</td>"
					  + "<td class='tdc'>{8}</td>"
					  + "<td class='tdc'>{9}</td>"
					  + "<td class='tdr {10}'>{11}</td>"
					  + "</tr></tbody>"
					 + "</table>";
			return ir.format(template,
					(showName?"":"hide"),s.Name,
					s.Handling,
					s.Speed,
					s.Acceleration,
					s.Body,					
					s.Armor,
					s.Pilot,	
					s.Sensor,
					s.Availability,
					(showCost?"":"hide"),s.Cost);
		},
		getTableGear:function(s,showName,showCost)
		{
			var template = "<table class='table show'>"
					 + "<thead><tr>"
					  + "<td class='tdl {0}'>NAME</td>"
					  + "<td class='tdc {2}'>Capacity</td>"
					  + "<td class='tdc {4}'>Max Value</td>"
					  + "<td class='tdc {6}'>Rating</td>"
					  + "<td class='tdc {8}'>Data processing</td>"
					  + "<td class='tdc {10}'>Firewall</td>"
					  + "<td class='tdc {12}'>Vector</td>"
					  + "<td class='tdc {14}'>Speed</td>"
					  + "<td class='tdc {16}'>Penetration</td>"
					  + "<td class='tdc {18}'>Power</td>"
					  + "<td class='tdc {20}'>Effects</td>"
					  + "<td class='tdc {22}'>Damage</td>"
					  + "<td class='tdc {24}'>Damage Mod</td>"
					  + "<td class='tdc {26}'>AP</td>"
					  + "<td class='tdc {28}'>Blast</td>"
					  + "<td class='tdc'>AVAIL</td>"
					  + "<td class='tdr {31}'>COST</td>"
					  + "</tr></thead>"
					 + "<tbody><tr>"
					  + "<td class='tdl {0}'>{1}</td>"
					  + "<td class='tdc {2}'>{3}</td>"
					  + "<td class='tdc {4}'>{5}</td>"
					  + "<td class='tdc {6}'>{7}</td>"
					  + "<td class='tdc {8}'>{9}</td>"
					  + "<td class='tdc {10}'>{11}</td>"
					  + "<td class='tdc {12}'>{13}</td>"
					  + "<td class='tdc {14}'>{15}</td>"
					  + "<td class='tdc {16}'>{17}</td>"
					  + "<td class='tdc {18}'>{19}</td>"
					  + "<td class='tdc {20}'>{21}</td>"
					  + "<td class='tdc {22}'>{23}</td>"
					  + "<td class='tdc {24}'>{25}</td>"
					  + "<td class='tdc {26}'>{27}</td>"
					  + "<td class='tdc {28}'>{29}</td>"
					  + "<td class='tdc'>{30}</td>"
					  + "<td class='tdr {31}'>{32}</td>"
					  + "</tr></tbody>"
					 + "</table>";
			return ir.format(template,
					(showName?"":"hide"),s.Name,
					(s.Capacity.length>0?"":"hide"),s.Capacity,
					(s.Max>0?"":"hide"),s.Max,
					(s.Rating>0?"":"hide"),s.Rating,
					(s.DataProcessing>0?"":"hide"),s.DataProcessing,
					(s.Firewall>0?"":"hide"),s.Firewall,
					(s.Vector.length>0?"":"hide"),s.Vector,
					(s.Speed.length>0?"":"hide"),s.Speed,
					(s.Penetration.length>0?"":"hide"),s.Penetration,
					(s.Power.length>0?"":"hide"),s.Power,
					(s.Effects.length>0?"":"hide"),s.Effects,
					(s.DamageValue.length>0?"":"hide"),s.DamageValue,
					(s.DamageMod.length>0?"":"hide"),s.DamageMod,
					(s.ArmorPenetration.length>0?"":"hide"),s.ArmorPenetration,
					(s.Blast.length>0?"":"hide"),s.Blast,
					(s.Availability.length==0?"-":s.Availability),
					(showCost?"":"hide"),s.Cost);
		},
		getTableName:function(s)
		{//all database table names have a prefix of 't' and every object holds the table name in property '_t'
			if(s.hasOwnProperty("_t"))
			{
				return s._t.substring(1,s._t.length);
			}
			return "unknown";
		},
		getTableVehicle:function(s,showName,showCost)
		{
			var template = "<table class='table show'>"
						 + "<thead><tr>"
						  + "<td class='tdl {0}'>NAME</td>"
						 + "<td class='tdc'>HANDL</td>"
						 + "<td class='tdc'>SPEED</td>"
						 + "<td class='tdc'>ACCEL</td>"
						 + "<td class='tdc'>BODY</td>"
						 + "<td class='tdc'>ARM</td>"
						 + "<td class='tdc'>PILOT</td>"
						 + "<td class='tdc'>Sens</td>"
						 + "<td class='tdc'>SEATS</td>"
						 + "<td class='tdc'>AVAIL</td>"
						  + "<td class='tdr {11}'>COST</td>"
						 + "</tr></thead>"
						 + "<tbody><tr>"
						  + "<td class='tdl {0}'>{1}</td>"
						 + "<td class='tdc'>{2}</td>"
						 + "<td class='tdc'>{3}</td>"
						 + "<td class='tdc'>{4}</td>"
						 + "<td class='tdc'>{5}</td>"
						 + "<td class='tdc'>{6}</td>"
						 + "<td class='tdc'>{7}</td>"
						 + "<td class='tdc'>{8}</td>"
						 + "<td class='tdc'>{9}</td>"
						 + "<td class='tdc'>{10}</td>"
						  + "<td class='tdr {11}'>{12}</td>"
						 + "</tr></tbody>"
						 + "</table>";
			return ir.format(template,
					(showName?"":"hide"),s.Name,
					s.Handling,
					s.Speed,
					s.Acceleration,
					s.Body,
					s.Armor,					
					s.Pilot,
					s.Sensor,	
					s.Seats,
					s.Availability,
					(showCost?"":"hide"),s.Cost);
		},
		getTableWeapon:function(s,showName,showCost){
			var template = "<table class='table show'>"
						 + "<thead><tr>"
						 + "<td class='tdl {0}'>NAME</td>"
						 + "<td class='tdc {2}'>ACC</td>"
						 + "<td class='tdc {4}'>REACH</td>"
						 + "<td class='tdc {6}'>DV</td>"
						 + "<td class='tdc {8}'>AP</td>"
						 + "<td class='tdc {10}'>MODES</td>"
						 + "<td class='tdc {12}'>RC</td>"
						 + "<td class='tdc {14}'>AMMO</td>"
						 + "<td class='tdc {16}'>AVAIL</td>"
						 + "<td class='tdr {18}'>COST</td>"
						 + "</tr></thead>"
						 + "<tbody><tr>"
						 + "<td class='tdl {0}'>{1}</td>"
						 + "<td class='tdc {2}'>{3}</td>"
						 + "<td class='tdc {4}'>{5}</td>"
						 + "<td class='tdc {6}'>{7}</td>"
						 + "<td class='tdc {8}'>{9}</td>"
						 + "<td class='tdc {10}'>{11}</td>"
						 + "<td class='tdc {12}'>{13}</td>"
						 + "<td class='tdc {14}'>{15}</td>"
						 + "<td class='tdc {16}'>{17}</td>"
						 + "<td class='tdr {18}'>{19}</td>"
						 + "</tr></tbody>"
						 + "</table>";
			return  ir.format(template,
					(showName?"":"hide"),s.Name,
					(s.Accuracy.length>0?"":"hide"),s.Accuracy,
					(s.Reach.length>0?"":"hide"),s.Reach,
					(s.DamageValue.length>0?"":"hide"),s.DamageValue,
					(s.ArmorPenetration.length>0?"":"hide"),s.ArmorPenetration,					
					(s.Modes.length>0?"":"hide"),s.Modes,
					(s.RecoilCompensation.length>0?"":"hide"),s.RecoilCompensation,	
					(s.Ammo.length>0?"":"hide"),s.Ammo,
					(s.Availability.length>0?"":"hide"),s.Availability,
					(showCost?"":"hide"),s.Cost);
		},
		getThumb:function(i){
			var template ="<img class='thumb' src='{0}'>"
			return ir.format(template,sr5.getImagePath(i,true));
		},
		getThumbUnknown:function(allowPick){
			var template ="<img style='cursor:pointer;' class='thumb' src='images/Face/{0}' " + (allowPick?" onclick='sr5.changeThumbUnknown(this)' ":"") + ">"
				return ir.format(template,"thumb_0_"+sr5.unknownFaces[parseInt(Math.random()*sr5.unknownFaces.length)]+".jpg");
		},
		getVehicle:function(s){
			return (s.Equipped ? sr5.star : "&bull;")
				+ s.Name 
				+ " - "
				+ s.Type + " [" 
				+ "<span class='noWrap'>Handling: " + s.Handling + "</span>"
				+ ", <span class='noWrap'>Speed: " + s.Speed+ "</span>"
				+ ", <span class='noWrap'>Accel: " + s.Acceleration+ "</span>"
				+ ", <span class='noWrap'>Bod: " + s.Body+ "</span>"
				+ ", <span class='noWrap'>Arm: " + s.Armor+ "</span>"
				+ ", <span class='noWrap'>Pilot: " + s.Pilot+ "</span>"
				+ ", <span class='noWrap'>Sensor: " + s.Sensor+ "</span>"
				+ ", <span class='noWrap'>Seats: " + s.Seats+ "</span>"
				+"]"
				+ (s.Quantity >1? " x"+s.Quantity:"");
		},

		getVehicleCondition:function(veh){
			return 8 + Math.ceil(veh.Body/2);
		},
		getWeapon:function(s){
			return  (s.Equipped ? sr5.star : "&bull;")
				+ s.Name 
				+ " - "
				+ s.Type
				+ " [" 
				+ (s.Reach.length>0? "<span class='noWrap'>Reach: " + s.Reach+ ", </span>":"")
				+ (s.Modes.length>0 ? "<span class='noWrap'>Mode: " + s.Modes+ ", </span>": "")
				+ "<span class='noWrap'>Acc: " + s.Accuracy+ "</span>"
				+ ", <span class='noWrap'>DV: " + s.DamageValue+ "</span>"
				+ (s.ArmorPenetration.length>0?", <span class='noWrap'>AP: " + s.ArmorPenetration+ "</span>":"")
				+ (s.Ammo.length>0?", <span class='noWrap'>Ammo: " + s.Ammo+ "</span>":"")
				+"]"
				+ (s.Quantity >1? " x"+s.Quantity:"");
		},
		go:function(url){
			var callback = function(yes){
				if(yes)
				{
					ir.get("navMenu").classList.remove("show");
					if(ir.visible("popupBackground"))
					{
						ir.hide("popupBackground");
					}
					sr5.load();
					window.setTimeout(function(){ir.go(url)},1);
				}
			};
			if(location.href.indexOf("gmboard.jsp")>-1)
			{
				
				confirmPop.show("Are you sure you want to leave the GM Board?",callback);
			}
			else
			{
				callback(true);
			}
		},
		initHover:function(){
			if(!sr5.isMobile && hoverPop!=null)
			{
				hoverPop.init();
			}
		},
		initParagraphs:function(){
			var wraps = document.getElementsByClassName("pWrap");
			for(var i =0,z=wraps.length;i<z;i++)
			{
				var wrap = wraps[i];
				var title = wrap.getElementsByClassName("pTitle");
				var p = wrap.getElementsByTagName("P");
				if(view.showP)
				{
					p[0].classList.add("show");
					p[0].classList.add("shadow");
				}				
				title[0].dataset.link = p[0].id + "";
				title[0].onclick=function(){sr5.show(this.dataset.link)};
			}
		},
		initNavInfoBtn:function(){
			if(ir.exists("navInfoBtn"))
			{
				ir.show("navInfoBtn",ir.exists("infoPopup"));
			}
		},
		initPlayer:function(){
			if(playerCharacterPop.init && sr5.user.PlayerCharacter>0)
			{
				playerCharacterPop.blankPlayer = sr5.blankPlayer;
				playerCharacterPop.player = sr5.characters.get(sr5.user.PlayerCharacter);
				sr5.player = playerCharacterPop.player;
				sr5.initPlayerAttachments(sr5.player);
				sr5.initPlayerSkillBonus(sr5.player);
				playerCharacterPop.init(sr5.player);
			}
		},
		initPlayerAttachments(char){
			//Programs
			var decks = char.Cyberdeck.values;
			for(var i =0, z=decks.length;i<z;i++)
			{
				decks[i].Programs = new KeyedArray("ItemRow");
			}
			if(char.Program.size()>0)
			{
				var programs = char.Program.values;
				for(var i =0, z=programs.length;i<z;i++)
				{
					var p = programs[i];
					var deck = char.Cyberdeck.get(p.ParentRow);
					if(deck!=null)
					{
						deck.Programs.add(p);
					}
				}
			}
			//CyberwareAttachments
			var cyberware = char.Cyberware.values;
			for(var i =0, z=cyberware.length;i<z;i++)
			{
				cyberware[i].Attachments = new KeyedArray("ItemRow");
			}
			if(char.CyberwareAttachment.size()>0)
			{
				var cyberAtt = char.CyberwareAttachment.values;
				for(var i =0, z=cyberAtt.length;i<z;i++)
				{
					var p = cyberAtt[i];
					var cyber = char.Cyberware.get(p.ParentRow);
					if(cyber!=null)
					{
						cyber.Attachments.add(p);
					}
				}
			}
		},
		initPlayerSkillBonus:function(player){
			var skills = player.Skill.values;
			for(var i =0, z=skills.length;i<z;i++)
			{
				var s = skills[i];
				if(player.SkillBonus && player.SkillBonus[s.Name.replace(/ /g,'')])
				{
					s.Bonus = player.SkillBonus[s.Name.replace(/ /g,'')];
				}
			}
		},
		initPopups:function(){
			var popups= document.getElementsByClassName("popup");
			for(var i =0,z=popups.length;i<z;i++)
			{
				var popup = popups[i];
				if(popup.classList.contains("hover"))
				{
					continue;
				}
				var header = popup.getElementsByClassName("popupHeader")[0];
				if(sr5.isMobile)
				{
					popup.classList.add("mobile");
				}
				if(header==null)
				{
					var div = document.createElement("DIV");
					div.className="popupHeader";
					if(popup.childNodes[0])
					{
						popup.insertBefore(div, popup.childNodes[0]);
					}
					else
					{
						popup.appendChild(div);
					}
					header = div;
				}
				var x = header.getElementsByClassName("x")[0];
				if(!x)
				{
					x = document.createElement("DIV");
					x.className="x";
					x.dataset.grandparentId=popup.id;
					x.onclick=function(){irPop.showHide(this.dataset.grandparentId)};
					x.innerHTML="X";
					header.appendChild(x);
				}
			}
		},
		initTables:function(){
			var wraps = document.getElementsByClassName("tableWrap");
			for(var i =0,z=wraps.length;i<z;i++)
			{
				var wrap = wraps[i];
				var title = wrap.getElementsByClassName("tableTitle");
				var table = wrap.getElementsByClassName("table");
				if(view.showTable)
				{
					table[0].classList.add("show");
					table[0].classList.add("shadow");
				}
				if(title[0]!=null && table[0]!=null)
				{
					title[0].dataset.link = table[0].id + "";
					title[0].onclick=function(){sr5.show(this.dataset.link)};
				}
			}
		},
		initWebSocket:function (){
			var address = "";
			var port = "";
			var source = "";
			var protocol = "";
			if(sr5.isLocalhost())
			{
				address = "localhost";
				port = 8080;
				source = "/sr/webSock";
				protocol ="ws";
			}
			else
			{
				address = "srkit.ca";
				port = 443;
				source = "/sr/webSock";
				protocol ="wss";
			}
			var client = sr5.webSocketClient = new WebSocketClient(protocol, address, port, source);
			client.connect();
		},
		isLocalhost:function(){
			return location.href.indexOf("127.0.0") > -1 || location.href.indexOf("localhost") > -1;
		},
		isShown:function(idOrEle){
			var obj = ir.get(idOrEle);
			return obj.classList.contains("show");
		},
		load:function(){
			var form = ir.get("f1",true);
			if(form!=null)
			{
				form.classList.add("load");
			}
		},
		onScroll:function(){
			var top = window.scrollY;
			var quickBtns = ir.get("navQuickBtns");
			if(top>50)
			{
				quickBtns.classList.add("fixed");
			}
			else
			{
				quickBtns.classList.remove("fixed");
			}
		},
		selectImage:function(row,callback){
			if(row>0)
			{
				var image = sr5.images.get(row);
				if(image!=null)
				{
					return callback(image);
				}
				var innerCallback = function(res){
					if(res.ok)
					{
						sr5.images.add(res.image);
						if(callback!=null)
						{
							callback(res.image);
						}
					}
				};
				sr5.ajaxAsync({fn:"selectImage",row:row},innerCallback);
			}
		},
		selectJournalData:function(callback){
			var innerCallback = function(res){
				if(res.ok && callback!=null)
				{
					callback(res);
				}
			};			
			sr5.ajaxAsync({fn:"selectJournalData",row:sr5.user.Row},innerCallback);
		},
		selectPlayer:function(row,callback){
			if(row<1)
			{
				return;
			}
			var innerCallback = function(res){
				if(res.ok)
				{
					sr5.initPlayerAttachments(res.player);
					sr5.initPlayerSkillBonus(res.player);
					sr5.characters.add(res.player);
					if(callback!=null)
					{
						callback(res.player);
					}
				}
			};
			sr5.ajaxAsync({fn:"selectPlayer",row:row},innerCallback);
		},
		sendMessage:function(msg,data,type){
			var innerCallback = function(res){
				if(res.ok){
					//add some sort of capture here 
				}
			};
			var users = data.users.sort(function(a,b){return a.Row - b.Row;})
			var userArray = "";
			var comma="";
			for(var i=0,z=users.length;i<z;i++)
			{
				userArray +=comma+ users[i].Row;
				comma = sr5.splitter;
			}
			sr5.ajaxAsync({fn:"sendMessage",message:msg,users:userArray,threadId:data.threadId,type:type},innerCallback);
		},
		show:function(id){
			var p = ir.get(id,true);
			var sibling = p.nextElementSibling;
			if(p==null)
			{
				return;
			}
			if(p.classList.contains("show"))
			{
				p.classList.remove("show");

				if(sibling!=null)
				{
					sibling.classList.remove("show");
				}
			}
			else
			{
				p.classList.add("show");
				if(sibling!=null)
				{
					sibling.classList.add("show");
				}
			}
			if(!ir.isInView(p))
			{
				ir.scrollIntoView(p,p.parentElement);
			}			
		},
		showDetail:function(name,record)
		{
			Status.info(record.Description);
		},
		showInfo:function(){
			var pop = document.getElementsByClassName("popup info")[0];
			popup(pop);
		},
		showJournal:function(){
			if(sr5.journalData==null)
			{
				var callback = function(res){
					if(res.ok)
					{
						journalPop.show();
					}
				};
				sr5.selectJournalData(callback);				
			}
			else if(journalPop)
			{
				journalPop.show();
			}
			
		},
		showPlayerCharacter:function(player){
			if(playerCharacterPop.show)
			{
				if(player!=null)
				{
					playerCharacterPop.show(player);
				}
				else
				{
					playerCharacterPop.show(sr5.characters.get(sr5.user.PlayerCharacter));
				}
			}
		},
		sortList:function(){
			if(view.sortList)
			{
				view.sortList();
			}			
		},
		submitDelete:function(){
			var submit = document.createElement("input");
			submit.type="submit";
			submit.style.display="none";
			submit.id=sr5.deleteButton;
			submit.setAttribute("name",sr5.deleteButton);
			document.forms[0].appendChild(submit);
			submit.click();
		},
		toggleChildButtons:function(parentId){
			var a =ir.get(parentId);
			var childContainer = ir.get(parentId+"Selector");
			var typeButtons = childContainer.getElementsByClassName("childBtn");
			for(var i=0,z=typeButtons.length;i<z;i++)	
			{
				var t = typeButtons[i];
				if(t.classList.contains("open"))
				{
					a.classList.remove("open");
					t.classList.remove("open");
					t.parentNode.classList.remove("open");
				}
				else
				{
					a.classList.add("open");
					t.classList.add("open");
					t.parentNode.classList.add("open");
				}
			}
		},
		top:function(){
			window.scrollTo({
			    top: 0,
			    behavior: "smooth"
			});
		},
		/**
		 * new idea to give the illusion that text is being typed
		 */
		type: function(id,text)
		{
			if(text==null || text.length == 0)
			{
				return false;
			}	
		    var element = (typeof (id) == "string" ? document.getElementById(id) : id);
		    var letter = text.substring(0,1);
		    if(letter == sr5.splitter)
	    	{
		    	letter = "<br />"
	    	}
		    element.innerHTML += letter;
		    if(text.length<=1)
			{
		    	return false;
			}
		    return window.setTimeout(function(){sr5.type(id,text.substring(1,text.length));},Math.floor(Math.random() * 80) + 20);  // returns a random integer from 20-100);
		},
		updateCharacterAdeptPower:function(characterRow,array,callback){
			var value = "";
			var delim = "";
			for(var i=0,z=array.length;i<z;i++)
			{
				var a = array[i];
				if(a.ItemRow <= 0 && a.Delete)
				{
					continue;
				}
				value += delim + a.ItemRow + sr5.splitter + a.Row + sr5.splitter + a.Level + sr5.splitter + (a.Delete || false);
				delim= sr5.delimiter;
			}
			sr5.ajaxAsync({fn:"updateCharacterAdeptPower",characterRow:characterRow,updateString:value},callback);
		},
		updateCharacterArmor:function(characterRow,array,callback){
			var value = "";
			var delim = "";
			for(var i=0,z=array.length;i<z;i++)
			{
				var a = array[i];
				if(a.ItemRow <= 0 && a.Delete)
				{
					continue;
				}
				value += delim + a.ItemRow + sr5.splitter + a.Row + sr5.splitter + a.Quantity + sr5.splitter + a.Equipped + sr5.splitter + (a.Delete || false);
				delim= sr5.delimiter;
			}
			sr5.ajaxAsync({fn:"updateCharacterArmor",characterRow:characterRow,updateString:value},callback);
		},
		updateCharacterBioware:function(characterRow,array,callback){
			var value = "";
			var delim = "";
			for(var i=0,z=array.length;i<z;i++)
			{
				var a = array[i];
				if(a.ItemRow <= 0 && a.Delete)
				{
					continue;
				}
				value += delim + a.ItemRow + sr5.splitter + a.Row + sr5.splitter + a.Rating + sr5.splitter + a.Grade + sr5.splitter + (a.Delete || false);
				delim= sr5.delimiter;
			}
			sr5.ajaxAsync({fn:"updateCharacterBioware",characterRow:characterRow,updateString:value},callback);
		},
		updateCharacterCyberdeck:function(characterRow,array,callback){
			var value = "";
			var delim = "";
			for(var i=0,z=array.length;i<z;i++)
			{
				var a = array[i];
				if(a.ItemRow <= 0 && a.Delete)
				{
					continue;
				}
				value += delim + a.ItemRow + sr5.splitter + a.Row + sr5.splitter + a.Quantity + sr5.splitter + a.Equipped + sr5.splitter + (a.Delete || false);
				delim= sr5.delimiter;
			}
			sr5.ajaxAsync({fn:"updateCharacterCyberdeck",characterRow:characterRow,updateString:value},callback);
		},
		updateCharacterCyberdeckProgram:function(characterRow,parentRow,array,callback){
			var value = "";
			var delim = "";
			for(var i=0,z=array.length;i<z;i++)
			{
				var a = array[i];
				if(a.ItemRow <= 0 && a.Delete)
				{
					continue;
				}
				value += delim + a.ItemRow + sr5.splitter + a.Row + sr5.splitter + (a.Delete || false);
				delim= sr5.delimiter;
			}
			sr5.ajaxAsync({fn:"updateCharacterCyberdeckProgram",characterRow:characterRow,parentRow:parentRow,updateString:value},callback);
		},
		updateCharacterCyberware:function(characterRow,array,callback){
			var value = "";
			var delim = "";
			for(var i=0,z=array.length;i<z;i++)
			{
				var a = array[i];
				if(a.ItemRow <= 0 && a.Delete)
				{
					continue;
				}
				value += delim + a.ItemRow + sr5.splitter + a.Row + sr5.splitter + a.Rating + sr5.splitter + a.Grade + sr5.splitter + (a.Delete || false);
				delim= sr5.delimiter;
			}
			sr5.ajaxAsync({fn:"updateCharacterCyberware",characterRow:characterRow,updateString:value},callback);
		},
		updateCharacterCyberwareAttachment:function(characterRow,parentRow,array,callback){
			var value = "";
			var delim = "";
			for(var i=0,z=array.length;i<z;i++)
			{
				var a = array[i];
				if(a.ItemRow <= 0 && a.Delete)
				{
					continue;
				}
				value += delim + a.ItemRow + sr5.splitter + a.Row + sr5.splitter + a.Rating + sr5.splitter + a.Grade + sr5.splitter + (a.Delete || false);
				delim= sr5.delimiter;
			}
			sr5.ajaxAsync({fn:"updateCharacterCyberwareAttachment",characterRow:characterRow,parentRow:parentRow,updateString:value},callback);
		},
		updateCharacterDrone:function(characterRow,array,callback){
			var value = "";
			var delim = "";
			for(var i=0,z=array.length;i<z;i++)
			{
				var a = array[i];
				if(a.ItemRow <= 0 && a.Delete)
				{
					continue;
				}
				value += delim + a.ItemRow + sr5.splitter + a.Row + sr5.splitter + a.Quantity + sr5.splitter + a.Equipped + sr5.splitter + (a.Delete || false);
				delim= sr5.delimiter;
			}
			sr5.ajaxAsync({fn:"updateCharacterDrone",characterRow:characterRow,updateString:value},callback);
		},
		updateCharacterGear:function(characterRow,array,callback){
			var value = "";
			var delim = "";
			for(var i=0,z=array.length;i<z;i++)
			{
				var a = array[i];
				if(a.ItemRow <= 0 && a.Delete)
				{
					continue;
				}
				value += delim + a.ItemRow + sr5.splitter + a.Row + sr5.splitter + a.Quantity + sr5.splitter + a.Rating + sr5.splitter + (a.Delete || false);
				delim= sr5.delimiter;
			}
			sr5.ajaxAsync({fn:"updateCharacterGear",characterRow:characterRow,updateString:value},callback);
		},

		updateCharacterKnowledge:function(characterRow,array,callback){
			var value = "";
			var delim = "";
			for(var i=0,z=array.length;i<z;i++)
			{
				var a = array[i];
				if(a.ItemRow <= 0 && a.Delete)
				{
					continue;
				}
				value += delim + a.ItemRow + sr5.splitter + a.Rating + sr5.splitter + a.Name + sr5.splitter + a.Type+ sr5.splitter + a.Native + sr5.splitter + (a.Delete || false);
				delim= sr5.delimiter;
			}
			sr5.ajaxAsync({fn:"updateCharacterKnowledge",characterRow:characterRow,updateString:value},callback);
		},
		updateCharacterQuality:function(characterRow,array,callback){
			var value = "";
			var delim = "";
			for(var i=0,z=array.length;i<z;i++)
			{
				var a = array[i];
				if(a.ItemRow <= 0 && a.Delete)
				{
					continue;
				}
				value += delim + a.ItemRow + sr5.splitter + a.Row + sr5.splitter + a.Rating + sr5.splitter + (a.Delete || false);
				delim= sr5.delimiter;
			}
			sr5.ajaxAsync({fn:"updateCharacterQuality",characterRow:characterRow,updateString:value},callback);
		},
		updateCharacterSkill:function(characterRow,array,callback){
			var value = "";
			var delim = "";
			for(var i=0,z=array.length;i<z;i++)
			{
				var a = array[i];
				if(a.ItemRow <= 0 && a.Delete)
				{
					continue;
				}
				value += delim + a.ItemRow + sr5.splitter + a.Row + sr5.splitter + a.Rating + sr5.splitter + a.Special + sr5.splitter + (a.Delete || false);
				delim= sr5.delimiter;
			}
			sr5.ajaxAsync({fn:"updateCharacterSkill",characterRow:characterRow,updateString:value},callback);
		},
		updateCharacterSpell:function(characterRow,array,callback)
		{
			var value = "";
			var delim = "";
			for(var i=0,z=array.length;i<z;i++)
			{
				var a = array[i];
				if(a.ItemRow <= 0 && a.Delete)
				{
					continue;
				}
				value += delim + a.ItemRow + sr5.splitter + a.Row + sr5.splitter + (a.Delete || false);
				delim= sr5.delimiter;
			}
			sr5.ajaxAsync({fn:"updateCharacterSpell",characterRow:characterRow,updateString:value},callback);
		},	
		updateCharacterVehicle:function(characterRow,array,callback){
			var value = "";
			var delim = "";
			for(var i=0,z=array.length;i<z;i++)
			{
				var a = array[i];
				if(a.ItemRow <= 0 && a.Delete)
				{
					continue;
				}
				value += delim + a.ItemRow + sr5.splitter + a.Row + sr5.splitter + a.Quantity + sr5.splitter + a.Equipped + sr5.splitter + (a.Delete || false);
				delim= sr5.delimiter;
			}
			sr5.ajaxAsync({fn:"updateCharacterVehicle",characterRow:characterRow,updateString:value},callback);
		},
		updateCharacterWeapon:function(characterRow,array,callback){
			var value = "";
			var delim = "";
			for(var i=0,z=array.length;i<z;i++)
			{
				var a = array[i];
				if(a.ItemRow <= 0 && a.Delete)
				{
					continue;
				}
				value += delim + a.ItemRow + sr5.splitter + a.Row + sr5.splitter + a.Quantity + sr5.splitter + a.Equipped + sr5.splitter + (a.Delete || false);
				delim= sr5.delimiter;
			}
			sr5.ajaxAsync({fn:"updateCharacterWeapon",characterRow:characterRow,updateString:value},callback);
		},	
		uploadFile:function(fileField,type,callback){
			var fileField = ir.get(fileField);
			var files = fileField.files;
			var formData = new FormData();
			formData.append("fn","uploadFile");
			formData.append("file",files[0]);
			formData.append("type",type);
			formData.append("ok",1);
			//ir.get("progressWrap").classList.add("show");
			view.uploadFile(formData,callback,sr5.uploadProgress);		
			return false;
		},
		
		uploadProgress:function(e)
		{			
			if (e.lengthComputable) 
			{
				var perc = parseInt((e.loaded/e.total)*100);
		        ir.log(perc);
		    }
		},
		zz_sr5:0		
};