"use strict";
var model = {
		character:null,
		transferCharacter:function(toUser){
			var callback = function(res)
			{
				if(res.ok)
				{
					model.character.Transfer=toUser;
					view.afterTransfer();
				}
			};
			sr5.ajaxAsync({fn:"transferCharacter",characterRow:model.character.Row,toUser:toUser},callback);
		},
		updateCharacterSetting:function(togglePanel){
			var callback = function(res)
			{
				if(res.ok)
				{
					model.characterSetting = res.setting;
				}
			};
			sr5.ajaxAsync({fn:"updateCharacterSetting",characterRow:model.character.Row,togglePanel:togglePanel},callback);
		},
	zz_model:0	
};
var view = {
	prefix:"characterDetail",
	aaOnLoad:function(){
		var lastTab = sesGet(view.prefix + model.character.Row + "tab","Personal");
		view.pickTab(lastTab);
		ir.show("btnJoinTeam",model.character.Row>0);
		view.initFriendSelect();
		view.initTogglePanel();
		view.initTransfer();
		view.initAttachments();
		view.toggleProfessionalRating();
		ir.disable("tabAbilities",model.character.Row==0);
		ir.disable("tabEquipment",model.character.Row==0);
		var typeTag = ir.get("characterTypeTag");
		ir.set("characterTypeTagName",model.character.Name);
		typeTag.classList.add("show");
		if(model.character.Portrait && model.character.Portrait.Row>0)
		{
			view.afterAddPortrait(model.character.Portrait);
		}
		else
		{
			view.removePortrait();
		}
		view.buildAdeptPower(model.character.AdeptPower.values);
		view.buildArmor(model.character.Armor.values);
		view.buildBioware(model.character.Bioware.values);
		view.buildContact(model.character.Contact.values);
		view.buildCyberdeck(model.character.Cyberdeck.values);
		view.buildCyberware(model.character.Cyberware.values);
		view.buildDrone(model.character.Drone.values);
		view.buildGear(model.character.Gear.values);
		view.buildKnowledge(model.character.Knowledge.values);
		view.buildPortrait(model.character.Portrait);
		view.buildQuality(model.character.Quality.values);
		view.buildSkill(model.character.Skill.values);
		view.buildSpell(model.character.Spell.values);
		view.buildVehicle(model.character.Vehicle.values);
		view.buildWeapon(model.character.Weapon.values);
		view.buildTeams();
		view.changeMagic();
		sr5.doneLoading();
	},
	addContact:function(){
		var blankContact = ir.copy(sr5.blankContact);
		blankContact.CharacterRow = model.character.Row;
		contactPop.show(blankContact,view.afterAdd);
	},
	addKnowledge:function(){
		var blankKnowledge = ir.copy(detailPop.blankCharacterKnowledge);
		blankKnowledge.CharacterRow = model.character.Row;
		detailPop.characterRow = model.character.Row;
		detailPop.show(blankKnowledge,view.afterAdd);
	},
	addPortrait:function(){
		pickPortraitPop.callback = view.afterAddPortrait;
		pickPortraitPop.show();
	},
	afterAdd:function(obj){
		var table = sr5.getTableName(obj);
		if(table==="Cyberware" && obj.Type==="Bioware")
		{
			table = "Bioware";
		}
		else if(table==="CharacterContact")
		{
			table = "Contact";
		}
		else if(table==="CharacterKnowledge")
		{
			table = "Knowledge";
		}
		model["character"][table]["add"](obj);
		view["build"+table]();	
	},
	afterAddContact:function(contact){
		
	},
	afterAddPortrait:function(imageRec){
		ir.set("ctlPortrait",imageRec.Row);
		view.buildPortrait(imageRec);
		var image = ir.get(portraitImg);
		image.src = sr5.getImagePath(imageRec,false);
		ir.hide("addPortraitBtn");
		ir.show("portrait");
		ir.get("personal").classList.add("flexStart");
	},
	afterEditContact:function(contact){
		
	},
	afterEditDetail:function(obj){
		var table = sr5.getTableName(obj);
		if(table === "Cyberware" && obj.Type==="Bioware")
		{
			table = "Bioware";
		}
		else if(table==="CharacterContact")
		{
			table = "Contact";
			obj.ItemRow = obj.Row;
		}
		else if(table==="CharacterKnowledge")
		{
			table = "Knowledge";
		}
		if(obj.Delete)
		{
			if(obj._t==="tSkill")
			{
				obj.Rating=0;
				model["character"][table]["set"](obj);
			}
			else
			{
				model["character"][table]["remove"](obj.ItemRow);			
			}
		}
		else
		{
			model["character"][table]["set"](obj);
		}
		view["build"+table]();		
	},
	afterJoinTeam:function(res){
		if(res.ok)
		{
			Status.success("Joined Team: "+ res.teamName ,5000);
			model.groups = res.groups;
			view.buildTeams();
			ir.set("groupKey","");
			view.toggleJoinTeam();
		}
	},
	afterLeaveTeam:function(res){
		if(res.ok)
		{
			Status.success("Left Team" ,5000);
			model.groups = res.groups;
			view.buildTeams();
		}
	},
	afterTransfer:function(){
		view.initTransfer();
	},
	buildAdeptPower:function(powers){
		if(powers==null)
		{
			powers = model.character.AdeptPower.values;
		}
		ir.set("adeptPowerDetail",sr5.getCharacterAdeptPower(powers,"view.showDetail",view.prefix));
	},
	buildArmor:function(armor){
		if(armor==null)
		{
			armor = model.character.Armor.values;
		}
		ir.set("armorDetail",sr5.getCharacterArmor(armor,"view.showDetail",view.prefix));
	},
	buildBioware:function(bioware){
		if(bioware==null)
		{
			bioware = model.character.Bioware.values;
		}
		ir.set("biowareDetail",sr5.getCharacterBioware(bioware,"view.showDetail",view.prefix));
	},
	buildContact:function(contacts)
	{
		if(contacts==null)
		{
			contacts = model.character.Contact.values;
		}
		ir.set("contactDetail",sr5.getCharacterContact(contacts,"view.showContact",view.prefix));
	},
	buildCyberdeck:function(cyberdecks){
		if(cyberdecks==null)
		{
			cyberdecks = model.character.Cyberdeck.values;
		}
		ir.set("cyberdeckDetail",sr5.getCharacterCyberdeck(cyberdecks,"view.showDetail",view.prefix));
	},
	buildCyberware:function(cyberware){
		if(cyberware==null)
		{
			cyberware = model.character.Cyberware.values;
		}
		ir.set("cyberwareDetail",sr5.getCharacterCyberware(cyberware,"view.showDetail",view.prefix));
	},
	buildDrone:function(drones){
		if(drones==null)
		{
			drones = model.character.Drone.values;
		}
		ir.set("droneDetail",sr5.getCharacterDrone(drones,"view.showDetail",view.prefix));
	},
	buildGear:function(gear){
		if(gear==null)
		{
			gear = model.character.Gear.values;
		}
		ir.set("gearDetail",sr5.getCharacterGear(gear,"view.showDetail",view.prefix));
	},
	buildKnowledge:function(knowledge)
	{
		if(knowledge==null)
		{
			knowledge = model.character.Knowledge.values;
		}
		ir.set("knowledgeDetail",sr5.getCharacterKnowledge(knowledge,"view.showDetail",view.prefix));
	},
	buildPortrait:function(portrait){
		var div = ir.get(characterTypeTagPortrait);
		if(portrait.Row>0)
		{
			div.style.backgroundImage = "url("+sr5.getImagePath(portrait,false)+")";
		}
		else
		{
			div.style.backgroundImage = "url(images/Face/image_0_"+sr5.unknownFaces[parseInt(Math.random()*sr5.unknownFaces.length)]+".jpg)";
		}	
		if(model.character.Inactive)
		{
			div.classList.add("inactive");
		}
	},
	buildQuality:function(qualities){
		if(qualities==null)
		{
			qualities = model.character.Quality.values;
		}
		ir.set("qualityDetail",sr5.getCharacterQuality(qualities,"view.showDetail",view.prefix));
	},
	buildSkill:function(skills){
		/*if(skills==null)
		{
			skills = model.character.Skill.values;
		}*/
		ir.set("skillDetail",sr5.getCharacterSkill(model.character,"view.showDetail",view.prefix));
	},
	buildSpell:function(spells){
		if(spells==null)
		{
			spells = model.character.Spell.values;
		}
		ir.set("spellDetail",sr5.getCharacterSpell(spells,"view.showDetail",view.prefix));
	},
	buildTeams:function(){
		var template = "<div class='flex' style='align-items:center;justify-content:space-between;'>"
			 + "<div>"
			 + "<div class='title'><b>{0}</b></div>"
			 + "</div>"
			 + "<div>" 
			 + "<div class='subtitle'><b>{1}</b></div>"
			 + "</div>"
			 + "<div class='spacer'></div>"
			 + "<div class='flex {2}' style='justify-content:flex-start;'>{3}</div>"
			 + "<div style='align-self:flex-start;'><button type='button' class='mini' onclick='view.leaveTeam({4});'>Leave Team</button></div>"
			 + "</div>"
			 + "</div><div class='spacer'></div>";
		var teams = model.groups;
		var container = ir.get("groupsDiv");
		container.innerHTML = "";
		for(var i =0,z=teams.length;i<z;i++)
		{
			var s = teams[i];
			container.innerHTML += ir.format(template,
					s.Name + (s.User==sr5.user.Row?"":" <span class='subtitle'>(Joined)</span>"),
					s.Inactive?"Inactive":(s.Private?"Private":"Team Key: " + s.ShareKey),
					s.Inactive?"inactive":"",
					view.getTeamPortraits(s),
					s.Row);
		}
	},
	buildVehicle:function(vehicles){
		if(vehicles==null)
		{
			vehicles = model.character.Vehicle.values;
		}
		ir.set("vehicleDetail",sr5.getCharacterVehicle(vehicles,"view.showDetail",view.prefix));
	},
	buildWeapon:function(weapons){
		if(weapons==null)
		{
			weapons = model.character.Weapon.values;
		}
		ir.set("weaponDetail",sr5.getCharacterWeapon(weapons,"view.showDetail",view.prefix));
	},
	calculateEssence:function(){
		var essenceTotal = 0;
		var cyberware = model.character.Cyberware.values;
		for(var i = 0, z= cyberware.length;i<z;i++)
		{
			var a = cyberware[i];
			if(a.Delete)
			{
				continue;
			}
			essenceTotal += sr5.getEssence(a);
		}
		var bioware = model.character.Bioware.values;
		for(var i = 0, z= bioware.length;i<z;i++)
		{
			var a = bioware[i];
			if(a.Delete)
			{
				continue;
			}
			essenceTotal += sr5.getEssence(a);
		}
		essenceTotal = 6 - essenceTotal;
		ir.set("ctlEssence",essenceTotal.toFixed(2));
		Status.info("Essence has been set to " + (essenceTotal.toFixed(2)) + "<br>"+sr5.updateReminder,6000);
	},
	cancelTransfer:function(){
		ir.set("transferSelect",0);
		view.transfer();
	},
	changeAgility:function(){
		var agility = ir.vn("ctlAgility");
		ir.set("ctlMovement",(agility*2) + " / " + (agility*4));
	},
	changeBody:function(){
		ir.set("ctlPhysicalLimit",Math.ceil(((ir.vn("ctlStrength")*2)+ir.vn("ctlBody")+ir.vn("ctlReaction"))/3));
	},
	changeCharisma:function(){
		var charisma = ir.vn("ctlCharisma");
		var willpower = ir.vn("ctlWillpower");
		ir.set("ctlSocialLimit",Math.ceil(((charisma*2)+willpower+ir.vn("ctlEssence"))/3));
		ir.set("ctlComposure",charisma + willpower);
		ir.set("ctlJudgeIntentions",charisma + ir.vn("ctlIntuition"));
	},
	changeEssence:function(){
		ir.set("ctlSocialLimit",Math.ceil(((ir.vn("ctlCharisma")*2)+ir.vn("ctlWillpower")+ir.vn("ctlEssence"))/3));
	},
	changeIntuition:function(){
		var intuition = ir.vn("ctlIntuition");
		ir.set("ctlInitiative",intuition + ir.vn("ctlReaction"));
		ir.set("ctlAstralInitiative",intuition*2);
		ir.set("ctlMentalLimit",Math.ceil(((ir.vn("ctlLogic")*2) + intuition + ir.vn("ctlWillpower"))/3));
		ir.set("ctlJudgeIntentions",ir.vn("ctlCharisma") + intuition);
	},
	changeLogic:function(){
		var logic =ir.vn("ctlLogic");
		var willpower = ir.vn("ctlWillpower");
		ir.set("ctlMentalLimit",Math.ceil(((logic*2)+ir.vn("ctlIntuition")+willpower)/3));
		ir.set("ctlMemory",logic + willpower);
	},
	changeMagic:function(){
		var magic = ir.vn("ctlMagic");
		if(magic>0)
		{
			ir.set("ctlResonance",0);
			view.changeResonance();
		}
	},
	changePortrait:function(){
		pickPortraitPop.callback = view.afterAddPortrait;
		pickPortraitPop.show();
	},
	changeReaction:function(){
		ir.set("ctlInitiative",ir.vn("ctlIntuition") + ir.vn("ctlReaction"));
		ir.set("ctlMatrixInitiative",ir.vn("ctlIntuition") + ir.vn("ctlReaction"));
		ir.set("ctlPhysicalLimit",Math.ceil(((ir.vn("ctlStrength")*2)+ir.vn("ctlBody")+ir.vn("ctlReaction"))/3));
	},
	changeResonance:function(){
		var res = ir.vn("ctlResonance");
		if(res>0)
		{
			ir.set("ctlMagic",0);
			view.changeMagic();
		}
	},
	changeStrength:function(){
		var strength = ir.vn("ctlStrength");
		ir.set("ctlPhysicalLimit",Math.ceil(((strength*2)+ir.vn("ctlBody")+ir.vn("ctlReaction"))/3));
		ir.set("ctlLiftCarry",(strength*15) + " / " + (strength*10));
	},
	changeType:function(){
		view.toggleProfessionalRating();
	},
	changeWillpower:function(){
		var charisma = ir.vn("ctlCharisma");
		var willpower = ir.vn("ctlWillpower");
		var logic =ir.vn("ctlLogic");
		ir.set("ctlMentalLimit",Math.ceil(((logic*2)+ir.vn("ctlIntuition")+willpower)/3));
		ir.set("ctlSocialLimit",Math.ceil(((charisma*2)+willpower+ir.vn("ctlEssence"))/3));
		ir.set("ctlComposure",charisma + willpower);
		ir.set("ctlMemory",logic + willpower)
	},
	deleteCharacter:function(){
		var callback = function(yes)
		{
			if(yes)
			{
				sr5.submitDelete();
			}
		};
		confirmPop.show("Are you sure you want to delete this character?",callback);
	},
	getTeamPortraits:function(group)
	{
		var htm ="";
		if(group.Characters.length==0 || group.Images.length==0)
		{
			return htm;
		}
		var template = "<div class='thumbWrap clickable' onclick='view.showTeamSheet({2})'><img src='images/Face/{0}' class='thumb'><label class='thumbLabel'>{1}</label></div>";
		var sources = group.Images.split(",");
		var names = group.Characters.split(",");
		var characterRows = group.CharacterRows.split(",");
		for(var i=0,z=sources.length;i<z;i++)
		{
			var s = sources[i];
			if(s.charAt(0)=='t')
			{
				htm += ir.format(template,s,ir.ellipsis(names[i],12),characterRows[i]);
			}
			else
			{
				htm += ir.format(template,"thumb_0_"+sr5.unknownFaces[parseInt(Math.random()*sr5.unknownFaces.length)]+".jpg",ir.ellipsis(names[i],12),characterRows[i]);
			}
			
		}
		return htm;			
	},
	initAttachments:function(){
		sr5.initPlayerAttachments(model.character);
	},
	initFriendSelect:function(){
		var selectBox = ir.get("transferSelect");
		var friends = sr5.friends.values;
		addOption(selectBox,0,"[select user]");
		for(var i=0,z=friends.length;i<z;i++)
		{
			var a =friends[i];
			addOption(selectBox,a.Row,a.Name);
		}
	},
	initTogglePanel:function(){
		var panels = document.getElementsByClassName("panelWrap");
		var whatsOn = model.characterSetting.TogglePanel;
		for(var i =0, z= panels.length;i<z;i++)
		{
			var p = panels[i];
			var id = p.id.substring("container".length,p.id.length);
			var btn = ir.get("tb"+id,true);
			if(btn==null){
				continue;
			}
			if(whatsOn.indexOf(id)>-1)
			{
				p.classList.add("show");
				btn.classList.add("on");
			}
			else
			{
				p.classList.remove("show");
				btn.classList.remove("on");
			}
		}
		return false;
	},
	initTransfer:function(){
		ir.show("btnTransfer",model.character.Row>0);
		ir.show("transferBtnWrap",model.character.Transfer==0);
		ir.show("transferRequestWrap",model.character.Transfer>0);
		if(model.character.Transfer>0)
		{
			var friend = sr5.friends.get(model.character.Transfer);
			var name = friend!=null?friend.Name + " ":"UserID: "+model.character.Transfer;
			ir.set("transferTo", "Transfer request sent to <i>" + name + "</i>, pending user's acceptance...");		
		}
	},
	joinTeam:function(){
		var key = ir.v("groupKey");
		if(key.length!=6)
		{
			return Status.error("Incorrect key length. Group keys are 6 digit alpha numeric. Found on Teams page.",6000);
		}
		sr5.ajaxAsync({fn:"joinTeam",groupKey:key,characterRow:model.character.Row},view.afterJoinTeam);
	},
	joinTeamKeydown:function(event){
		if (typeof event == 'undefined' && window.event)
		{
			event = window.event;
		}
		if (event == null)
		{
			ir.log("Cannot process key press codes!!!");
			return;
		}
		switch (event.keyCode)
		{
			case 13:// enter
				cancelEvent(event);
				view.joinTeam();
				return;
				break;
			default:
				break;
		}
	},
	leaveTeam:function(groupRow){
		var callback = function(yes)
		{
			if(yes)
			{
				sr5.ajaxAsync({fn:"leaveTeam",groupRow:groupRow,characterRow:model.character.Row},view.afterLeaveTeam);
			}
		};
		confirmPop.show("Are you sure you want to leave this team?",callback);
	},
	nameKeyup:function(e){

		if (typeof e == 'undefined' && window.event)
		{
			e = window.event;
		}
		if (e == null)
		{
			ir.log("Cannot process key press codes!!!");
			return;
		}
		ir.set("characterTypeTagName",ir.v("ctlName"));
	},
	pickAdeptPowers:function(){
		pickAdeptPowerPop.characterRow = model.character.Row;
		pickAdeptPowerPop.callback = function(newList){
				model.character.AdeptPower.clear();
				model.character.AdeptPower.add(newList);
				view.buildAdeptPower();
		};
		pickAdeptPowerPop.show(model.character.AdeptPower.values);
		return false;
	},
	pickArmor:function(){
		pickArmorPop.characterRow = model.character.Row;
		pickArmorPop.callback = function(res){
			detailPop.characterRow = model.character.Row;
			detailPop.show(res,view.afterAdd);
		};
		pickArmorPop.show();
		return false;
	},
	pickBioware:function(){
		pickBiowarePop.characterRow = model.character.Row;
		pickBiowarePop.callback = function(res){
			detailPop.characterRow = model.character.Row;
			detailPop.show(res,view.afterAdd);
		};
		pickBiowarePop.show();
		return false;
	},
	pickCyberdecks:function(){
		pickCyberdeckPop.characterRow = model.character.Row;
		pickCyberdeckPop.callback = function(res){
			detailPop.characterRow = model.character.Row;
			detailPop.show(res,view.afterAdd);
		};
		pickCyberdeckPop.show();
		return false;
	},
	pickCyberware:function(){
		pickCyberwarePop.characterRow = model.character.Row;
		pickCyberwarePop.callback = function(res){
				detailPop.characterRow = model.character.Row;
				detailPop.show(res,view.afterAdd);
			};
		pickCyberwarePop.show();
		return false;
	},
	pickDrones:function(){
		pickDronePop.characterRow = model.character.Row;
		pickDronePop.callback = function(res){
			detailPop.characterRow = model.character.Row;
			detailPop.show(res,view.afterAdd);
		};
		pickDronePop.show();
		return false;
	},
	pickGear:function(){
		pickGearPop.characterRow = model.character.Row;
		var callback = function(res){
			detailPop.characterRow = model.character.Row;
			detailPop.show(res,view.afterAdd);
		};
		pickGearPop.show(callback);
		return false;
	},
	pickQualities:function(){
		pickQualityPop.characterRow = model.character.Row;
		pickQualityPop.callback = function(newList){
				model.character.Quality.clear();
				model.character.Quality.add(newList);
				view.buildQuality();
			};
		pickQualityPop.show(model.character.Quality.values);
		return false;
	},
	pickSkills:function(){
		pickSkillPop.characterRow = model.character.Row;
		pickSkillPop.callback = function(newList){
			model.character.Skill.clear();
			model.character.Skill.add(newList);
			view.buildSkill();
		};
		pickSkillPop.show(model.character.Skill.values);
		return false;
	},
	pickSpells:function(){
		pickSpellPop.characterRow = model.character.Row;
		pickSpellPop.callback = function(newList){
			model.character.Spell.clear();
			model.character.Spell.add(newList);
			view.buildSpell();
		};
		pickSpellPop.show(model.character.Spell.values);
		return false;
	},
	pickTab:function(tabName){
		var tabs = document.getElementsByClassName("tab");
		sesSet(view.prefix + model.character.Row + "tab",tabName);
		for(var i=0,z=tabs.length;i<z;i++)
		{
			var tab = tabs[i];
			var id = tab.id.substring(3,tab.id.length);
			if(id===tabName)
			{
				tab.classList.add("selected");
				ir.show("div"+id);
			}
			else
			{
				tab.classList.remove("selected");
				ir.hide("div"+id);
			}
		}
	},
	pickVehicles:function(){
		pickVehiclePop.characterRow = model.character.Row;
		pickVehiclePop.callback = function(res){
			detailPop.characterRow = model.character.Row;
			detailPop.show(res,view.afterAdd);
		};
		pickVehiclePop.show();
		return false;
	},
	pickWeapons:function(){
		pickWeaponPop.characterRow = model.character.Row;
		pickWeaponPop.callback = function(res){
			detailPop.characterRow = model.character.Row;
			detailPop.show(res,view.afterAdd);
		};
		pickWeaponPop.show();
		return false;
	},
	removePortrait:function(){
		ir.hide("portrait");
		ir.set("portraitImg","");
		ir.set("ctlPortrait",0);
		ir.show("addPortraitBtn");
		ir.get("personal").classList.remove("flexStart");
	},
	showContact:function(type,row){
		var c = model.character.Contact.get(row);		
		contactPop.show(c,view.afterEditDetail);
	},
	showDetail:function(name,row){
		var obj = model["character"][name].get(row);
		detailPop.characterRow = model.character.Row;
		detailPop.show(obj,view.afterEditDetail);
		return false;
	},
	showSheet:function(){
		playerCharacterPop.show(model.character,true);
	},
	showTeamSheet:function(row)
	{
		row = ir.n(row);
		if(row>0)
		{
			var player = sr5.characters.get(row);
			if(player==null)
			{
				sr5.selectPlayer(row,sr5.showPlayerCharacter);
			}
			else
			{
				sr5.showPlayerCharacter(player);
			}
		}
	},
	submit:function(){
		document.forms[0].submit();
	},
	toggleJoinTeam:function()
	{
		sr5.toggleChildButtons("btnJoinTeam");
		var selector = ir.get("btnJoinTeamSelector");
		if(selector.classList.contains("open"))
		{
			ir.focus("groupKey");
		}
	},
	togglePanel:function(ele){
		var name = ele.id.substring("tb".length,ele.id.length);
		var panels = document.getElementsByClassName("panelWrap");
		var whatsOn = [];
		for(var i =0, z= panels.length;i<z;i++)
		{
			var p = panels[i];
			if(p.id.indexOf(name)>-1)
			{
				if(ele.classList.contains("on"))
				{
					p.classList.remove("show");
					ele.classList.remove("on");
				}
				else
				{
					p.classList.add("show");
					ele.classList.add("on");
				}	
			}
			if(p.classList.contains("show"))
			{
				whatsOn.push(p.id.substring("container".length,p.id.length));
			}
		}	
		model.updateCharacterSetting(whatsOn.toString());
		ele.blur();
		return false;
	},
	toggleProfessionalRating:function(){
		var prWrap = ir.get("ctlProfessionalRating",true).parentNode;
		var registerWrap = ir.get("ctlRegister",true).parentNode;
		if(ir.v("ctlType")==="PC")
		{
			ir.hide(prWrap);
			ir.hide(registerWrap);
		}
		else
		{
			ir.show(prWrap);
			ir.show(registerWrap);
		}
	},
	toggleTransfer:function()
	{
		sr5.toggleChildButtons("btnTransfer");
		var selector = ir.get("btnTransferSelector");
		if(selector.classList.contains("open"))
		{
			ir.focus("transferSelect");
		}
	},
	transfer:function(){
		model.transferCharacter(ir.vn("transferSelect"));
		var selector = ir.get("btnTransferSelector");
		if(selector.classList.contains("open"))
		{
			view.toggleTransfer();
		}
	},
	zz_view:0
};