var playerCharacterPop = {
	id:"playerCharacterPop",
	player:null,
	prefix:"playerCharacter",
	rollTemplate: "<div class='flex detail' style='justify-content:space-between;width:100%;' onclick='diceRollPop.show({2});'><div>&bull;{0}&emsp;<b>{2}</b></div><div> <i>[{1}]</i></div></div>",
	buildAdeptPower:function()
	{
		var self = playerCharacterPop;
		var container = ir.get(self.id +"AdeptPower"+self.player.Row);	
		ir.set(container,sr5.getCharacterAdeptPower(self.player.AdeptPower.values,"playerCharacterPop.showDetail",self.prefix));
		ir.show(self.id +"AdeptPowerRow"+self.player.Row,self.player.AdeptPower!=null && self.player.AdeptPower.size()>0);
	},	
	buildAmmoTrack:function(){
		var self = playerCharacterPop;
		ir.set(self.id+"AmmoContainer"+self.player.Row,"");
		var weapons = self.player.Weapon.values;
		var count = 0;
		for(var i = 0, z=weapons.length;i<z;i++)
		{
			var w = weapons[i];
			if(w.Equipped && w.Ammo.length>0)
			{
				var type = track.scoreBoardType.get("Ammo");
				track.showNamePlate=false;
				track.showTitle=false;
				track.showCloseButton=false;
				var div = document.createElement("DIV");
				div.className = "playerCharacterItem";
				div.id = "playerCharacterItem" + w.ItemRow;
				div.innerHTML = "<div class='subtitle'>"+ir.ellipsis(w.Name,28)+"</div>";
				div.appendChild(track.buildGrid(type,w.Name,null,sr5.getAmmoCount(w),w.CurrentAmount||0))
				ir.get(self.id+"AmmoContainer"+self.player.Row).appendChild(div);
				count++;
			}
		}
		ir.show("playerCharacterPopAmmoTrackWrap"+self.player.Row,count>0);
	},
	buildArmor:function()
	{
		var self = playerCharacterPop;
		var container = ir.get(self.id +"Armor"+self.player.Row);	
		ir.set(container,sr5.getCharacterArmor(self.player.Armor.values,"playerCharacterPop.showDetail",self.prefix));
		ir.show(self.id +"ArmorRow"+self.player.Row,self.player.Armor!=null && self.player.Armor.size()>0);
		var sum = sr5.getArmorSum(self.player.Armor.values);
		ir.set(self.id + "ArmorSum"+self.player.Row,sum + sr5.getCharacterBonus(self.player,"Armor",sum));
		self.buildDefenseRoll();
		self.buildSpellDefenseRoll();
	},	
	buildAttributes:function()
	{
		var self = playerCharacterPop;
		var container = ir.get(self.id +"Attributes"+self.player.Row);		
		container.innerHTML = sr5.getCharacterStatsTable(self.player);
	},		
	buildBioware:function()
	{
		var self = playerCharacterPop;
		var container = ir.get(self.id +"Bioware"+self.player.Row);	
		ir.set(container,sr5.getCharacterBioware(self.player.Bioware.values,"playerCharacterPop.showDetail",self.prefix));
		ir.show(self.id +"BiowareRow"+self.player.Row,self.player.Bioware!=null && self.player.Bioware.size()>0);
	},
	buildContact:function()
	{
		var self = playerCharacterPop;
		var container = ir.get(self.id +"Contact"+self.player.Row);	
		ir.set(container,sr5.getCharacterContact(self.player.Contact.values,"playerCharacterPop.showDetail",self.prefix));
		ir.show(self.id +"ContactRow"+self.player.Row,self.player.Contact!=null && self.player.Contact.size()>0);
	},
	buildCyberdeck:function()
	{
		var self = playerCharacterPop;
		var container = ir.get(self.id +"Cyberdeck"+self.player.Row);	
		ir.set(container,sr5.getCharacterCyberdeck(self.player.Cyberdeck.values,"playerCharacterPop.showDetail",self.prefix));
		ir.show(self.id +"CyberdeckRow"+self.player.Row,self.player.Cyberdeck!=null && self.player.Cyberdeck.size()>0);
		self.buildMatrixDamageTrack();
	},		
	buildCyberware:function()
	{
		var self = playerCharacterPop;
		var container = ir.get(self.id +"Cyberware"+self.player.Row);	
		ir.set(container,sr5.getCharacterCyberware(self.player.Cyberware.values,"playerCharacterPop.showDetail",self.prefix));
		ir.show(self.id +"CyberwareRow"+self.player.Row,self.player.Cyberware!=null && self.player.Cyberware.size()>0);
	},
	buildDamageTrack:function(){
		var self = playerCharacterPop;
		var type = track.scoreBoardType.get("Damage");
		var container = ir.get(self.id+"DamageTrack"+self.player.Row);
		container.innerHTML = "<div><b>Condition Monitor</b></div><div class='flexSpacer'></div>";
		track.showNamePlate=false;
		track.showTitle=false;
		track.showCloseButton=false;
		var physicalMax = Math.ceil(self.player.Body/2)+8;
		var stunMax = Math.ceil(self.player.Willpower/2)+8;	
		var physicalCurrent = self.player.PhysicalCurrent;
		var stunCurrent = self.player.StunCurrent;
		container.appendChild(track.buildGrid(type,null,null,physicalMax,stunMax,physicalCurrent,stunCurrent));
	},
	buildDefenseRoll:function(){
		var self = playerCharacterPop;
		var armor = sr5.getArmorSum(self.player.Armor.values);
		var container = ir.get(self.id +"DefenseRoll"+self.player.Row);	
		var meleeSkill = -1;
		var gymnasticsSkill = -1;
		var unarmedSkill = -1;
		var skills = self.player.Skill.values;
		for(var i =0, z=skills.length;i<z;i++)
		{
			var s = skills[i];
			if(s.Name.indexOf("Blades")>-1 || s.Name.indexOf("Clubs")>-1 || s.Name.indexOf("Melee")>-1)
			{
				meleeSkill = Math.max(meleeSkill,s.Rating);
			}
			if(s.Name.indexOf("Gymnastics")>-1)
			{
				gymnasticsSkill = Math.max(gymnasticsSkill,s.Rating);
			}

			if(s.Name.indexOf("Unarmed")>-1)
			{
				unarmedSkill = Math.max(unarmedSkill,s.Rating);
			}
		}
		ir.set(container,"");
		var template = self.rollTemplate;
		container.innerHTML+= ir.format(template,"Standard Defense","Reaction "+self.player.Reaction+ " + Intuition "+self.player.Intuition+"",self.player.Reaction+self.player.Intuition);
		container.innerHTML+= ir.format(template,"Resist - With Armor","Body "+self.player.Body+ " + Armor "+armor+"",self.player.Body+armor);
		container.innerHTML+= ir.format(template,"Resist - Armor Penetrated","Body "+self.player.Body,self.player.Body);		
		container.innerHTML+= ir.format(template,"Block","Reaction "+self.player.Reaction+ " + Intuition "+self.player.Intuition+" + Gymnastics "+gymnasticsSkill,self.player.Reaction+self.player.Intuition+gymnasticsSkill);
		container.innerHTML+= ir.format(template,"Dodge","Reaction "+self.player.Reaction+ " + Intuition "+self.player.Intuition+" + Unarmed "+unarmedSkill,self.player.Reaction+self.player.Intuition+unarmedSkill);		
		container.innerHTML+= ir.format(template,"Parry","Reaction "+self.player.Reaction+ " + Intuition "+self.player.Intuition+" + Melee "+meleeSkill,self.player.Reaction+self.player.Intuition+meleeSkill);
		container.innerHTML+= ir.format(template,"Full Defense","Reaction "+self.player.Reaction+" + Intuition "+self.player.Intuition+" + Willpower "+self.player.Willpower,self.player.Reaction+self.player.Intuition+self.player.Willpower);
	},
	buildDrone:function()
	{
		var self = playerCharacterPop;
		var container = ir.get(self.id +"Drone"+self.player.Row);	
		ir.set(container,sr5.getCharacterDrone(self.player.Drone.values,"playerCharacterPop.showDetail",self.prefix));
		ir.show(self.id +"DroneRow"+self.player.Row,self.player.Drone!=null && self.player.Drone.size()>0);
		self.buildDroneTrack();
	},	
	buildDroneTrack:function(){
		var self = playerCharacterPop;
		ir.set(self.id+"DroneContainer"+self.player.Row,"");
		var drones = self.player.Drone.values;
		var count = 0;
		for(var i = 0, z=drones.length;i<z;i++)
		{
			var w = drones[i];
			if(w.Equipped)
			{
				var type = track.scoreBoardType.get("Drone");
				track.showNamePlate=false;
				track.showTitle=false;
				track.showCloseButton=false;
				var div = document.createElement("DIV");
				div.className = "playerCharacterItem";
				div.id = "playerCharacterItem" + w.ItemRow;
				div.innerHTML = "<div class='subtitle'>"+ir.ellipsis(w.Name,28)+"</div>";
				div.appendChild(track.buildGrid(type,w.Name,null,sr5.getDroneCondition(w),w.CurrentAmount||0))
				ir.get(self.id+"DroneContainer"+self.player.Row).appendChild(div);
				count++;
			}
		}
		ir.show("playerCharacterPopDroneTrackWrap"+self.player.Row,count>0);
	},
	buildGear:function()
	{
		var self = playerCharacterPop;
		var container = ir.get(self.id +"Gear"+self.player.Row);	
		ir.set(container,sr5.getCharacterGear(self.player.Gear.values,"playerCharacterPop.showDetail",self.prefix));
		ir.show(self.id +"GearRow"+self.player.Row,self.player.Gear!=null && self.player.Gear.size()>0);
	},
	buildKnowledge:function()
	{
		var self = playerCharacterPop;
		var container = ir.get(self.id +"Knowledge"+self.player.Row);	
		ir.set(container,sr5.getCharacterKnowledge(self.player.Knowledge.values,"playerCharacterPop.showDetail",self.prefix));
		ir.show(self.id +"KnowledgeRow"+self.player.Row,self.player.Knowledge!=null && self.player.Knowledge.size()>0);
	},
	buildKnowledgeRoll:function(){
		var self = playerCharacterPop;
		var container = ir.get(self.id +"KnowledgeRoll"+self.player.Row);
		var template = self.rollTemplate;
		var knowledge = self.player.Knowledge.sort(function(a,b){
		    if(a.Name < b.Name)
		    {
		    	return -1;
		    }
		    else
		    {
		    	return 1;
		    }			    
		    return 0;
		});
		for(var i =0,z=knowledge.length;i<z;i++)
		{
			var a = knowledge[i];
			if(a.Native)
			{
				continue;
			}
			container.innerHTML+= ir.format(template,a.Name,a.Name +" " + a.Rating+" + "+a.Attribute+" "+self.player[a.Attribute],a.Rating+self.player[a.Attribute]);
		}
		ir.show(self.id + "KnowledgeRollRow"+self.player.Row,knowledge.length>0);
	},
	buildMatrixDamageTrack:function(){
		var self = playerCharacterPop;
		ir.set(self.id+"MatrixContainer"+self.player.Row,"");
		var cyberdecks = self.player.Cyberdeck.values;
		var count = 0;
		for(var i = 0, z=cyberdecks.length;i<z;i++)
		{
			var w = cyberdecks[i];
			if(w.Equipped)
			{
				var type = track.scoreBoardType.get("Device");
				track.showNamePlate=false;
				track.showTitle=false;
				track.showCloseButton=false;
				var div = document.createElement("DIV");
				div.className = "playerCharacterItem";
				div.id = "playerCharacterItem" + w.ItemRow;
				div.innerHTML = "<div class='subtitle'>"+ir.ellipsis(w.Name,28)+"</div>";
				div.appendChild(track.buildGrid(type,w.Name,null,sr5.getMatrixCondition(w),w.CurrentAmount||0))
				ir.get(self.id+"MatrixContainer"+self.player.Row).appendChild(div);
				count++;
			}
		}
		ir.show("playerCharacterPopMatrixTrackWrap"+self.player.Row,count>0);
	},
	buildNaturalRoll:function(){
		var self = playerCharacterPop;
		var armor = sr5.getArmorSum(self.player.Armor.values);
		var container = ir.get(self.id +"NaturalRoll"+self.player.Row);	
		ir.set(container,"");
		var template = self.rollTemplate;
		container.innerHTML+= ir.format(template,"Compsure","Charisma "+self.player.Charisma+ " + Willpower "+self.player.Willpower+"",self.player.Charisma+self.player.Willpower);
		container.innerHTML+= ir.format(template,"Judge Intentions","Charisma "+self.player.Charisma+ " + Intuition "+self.player.Intuition+"",self.player.Charisma+self.player.Intuition);
		container.innerHTML+= ir.format(template,"Lifting/Carrying","Body "+self.player.Body+ " + Strength "+self.player.Strength+"",self.player.Body+self.player.Strength);		
		container.innerHTML+= ir.format(template,"Memory","Logic "+self.player.Logic+ " + Willpower "+self.player.Willpower,self.player.Logic+self.player.Willpower);
	},
	buildPortrait:function(){
		var self = playerCharacterPop;
		var div = ir.get(self.id + "Portrait"+self.player.Row);
		if(self.player.Portrait.Row>0)
		{
			div.style.backgroundImage = "url("+sr5.getImagePath(self.player.Portrait,false)+")";
		}
		else
		{
			div.style.backgroundImage = "url(images/Face/image_0_"+sr5.unknownFaces[parseInt(Math.random()*sr5.unknownFaces.length)]+".jpg)";
		}		
	},
	buildQuality:function()
	{
		var self = playerCharacterPop;
		var container = ir.get(self.id +"Quality"+self.player.Row);	
		ir.set(container,sr5.getCharacterQuality(self.player.Quality.values,"playerCharacterPop.showDetail",self.prefix));
		ir.show(self.id +"QualityRow"+self.player.Row,self.player.Quality!=null && self.player.Quality.size()>0);
	},		
	buildSkill:function()
	{
		var self = playerCharacterPop;
		var container = ir.get(self.id +"Skill"+self.player.Row);	
		ir.set(container,sr5.getCharacterSkill(self.player,"playerCharacterPop.showDetail",self.prefix));
		ir.show(self.id +"SkillRow"+self.player.Row,self.player.Skill!=null && self.player.Skill.size()>0);
	},		
	buildSkillRoll:function(){
		var self = playerCharacterPop;
		var container = ir.get(self.id +"SkillRoll"+self.player.Row);
		container.innerHTML = "";
		var template = self.rollTemplate;
		var skills = self.player.Skill.values;
		var player = self.player;
		for(var i =0,z=skills.length;i<z;i++)
		{
			var a = skills[i];
			var extraText = "";
			var extraAmount = 0;
			var delim = "";
			if(a.Bonus)
			{
				var bs = a.Bonus.values;
				for(var j=0,y=bs.length;j<y;j++)
				{
					var b = bs[j];
					extraAmount += ir.n(b.Value);
					extraText += " + " + b.Name + " " + b.Value;
				}
			}
			if(extraAmount==0 && a.Rating==0)
			{
				continue;
			}
			container.innerHTML+= ir.format(template,a.Name,a.Name +" " + a.Rating+" + "+a.Attribute+" "+self.player[a.Attribute] + extraText,a.Rating+self.player[a.Attribute]+extraAmount);
		}
		ir.show(self.id + "SkillRollRow"+self.player.Row,skills.length>0);
	},
	buildSpell:function()
	{
		var self = playerCharacterPop;
		var container = ir.get(self.id +"Spell"+self.player.Row);	
		ir.set(container,sr5.getCharacterSpell(self.player.Spell.values,"playerCharacterPop.showDetail",self.prefix));
		ir.show(self.id +"SpellRow"+self.player.Row,self.player.Spell!=null && self.player.Spell.size()>0);
	},	
	buildSpellDefenseRoll:function(){
		var self = playerCharacterPop;
		var armor = sr5.getArmorSum(self.player.Armor.values);
		var container = ir.get(self.id +"SpellDefenseRoll"+self.player.Row);	
		ir.set(container,"");
		var template = self.rollTemplate;
		container.innerHTML+= ir.format(template,"Indirect Dodge","Reaction "+self.player.Reaction+" + Intuition "+self.player.Intuition+"",self.player.Reaction+self.player.Intuition);
		container.innerHTML+= ir.format(template,"Indirect Soak","Body "+self.player.Body+" + Armor "+armor+"",self.player.Body+armor);
		container.innerHTML+= ir.format(template,"Direct Soak - Mana","Willpower "+self.player.Willpower+"",self.player.Willpower);
		container.innerHTML+= ir.format(template,"Direct Soak - Physical","Body "+self.player.Body+"",self.player.Body);
		container.innerHTML+= ir.format(template,"Detection Spells","Logic "+self.player.Logic+" + Willpower "+self.player.Willpower+"",self.player.Logic+self.player.Willpower);
		//container.innerHTML+= ir.format(template,"Decrease Attribute (BOD)","Logic ("+self.player.Logic+")", "+ Willpower ("+self.player.Willpower+")",self.player.Logic+self.player.Willpower);
		container.innerHTML+= ir.format(template,"Illusion - Mana","Logic "+self.player.Logic+" + Willpower "+self.player.Willpower+"",self.player.Logic+self.player.Willpower);
		container.innerHTML+= ir.format(template,"Illusion - Physical","Logic "+self.player.Logic+" + Intuition "+self.player.Intuition+"",self.player.Logic+self.player.Intuition);
		container.innerHTML+= ir.format(template,"Manipulation - Mana","Logic "+self.player.Logic+" + Willpower "+self.player.Willpower+"",self.player.Logic+self.player.Willpower);
		container.innerHTML+= ir.format(template,"Manipulation - Physical","Body "+self.player.Body+" + Strength "+self.player.Strength+"",self.player.Body+self.player.Strength);
	},
	buildStatusTrack:function(){
		var self = playerCharacterPop;
		var type = track.scoreBoardType.get("Status");
		var container = ir.get(self.id+"StatusTrack"+self.player.Row);
		container.innerHTML = "<div><b>Status</b></div><div class='flexSpacer'></div>";
		track.showNamePlate=false;
		track.showTitle=false;
		track.showCloseButton=false;
		container.appendChild(track.buildGrid(type,null,null,0,0,0,0));
	},
	buildVehicle:function()
	{
		var self = playerCharacterPop;
		var container = ir.get(self.id +"Vehicle"+self.player.Row);	
		ir.set(container,sr5.getCharacterVehicle(self.player.Vehicle.values,"playerCharacterPop.showDetail",self.prefix));
		ir.show(self.id +"VehicleRow"+self.player.Row,self.player.Vehicle!=null && self.player.Vehicle.size()>0);
		self.buildVehicleTrack();
	},
	
	buildVehicleTrack:function(){
		var self = playerCharacterPop;
		ir.set(self.id+"VehicleContainer"+self.player.Row,"");
		var vehicles = self.player.Vehicle.values;
		var count = 0;
		for(var i = 0, z=vehicles.length;i<z;i++)
		{
			var w = vehicles[i];
			if(w.Equipped)
			{
				var type = track.scoreBoardType.get("Vehicle");
				track.showNamePlate=false;
				track.showTitle=false;
				track.showCloseButton=false;
				var div = document.createElement("DIV");
				div.className = "playerCharacterItem";
				div.id = "playerCharacterItem" + w.ItemRow;
				div.innerHTML = "<div class='subtitle'>"+ir.ellipsis(w.Name,28)+"</div>";
				div.appendChild(track.buildGrid(type,w.Name,null,sr5.getVehicleCondition(w),w.CurrentAmount||0))
				ir.get(self.id+"VehicleContainer"+self.player.Row).appendChild(div);
				count++;
			}
		}
		ir.show("playerCharacterPopVehicleTrackWrap"+self.player.Row,count>0);
	},
	buildWeapon:function()
	{
		var self = playerCharacterPop;
		var container = ir.get(self.id +"Weapon" + self.player.Row);	
		ir.set(container,sr5.getCharacterWeapon(self.player.Weapon.values,"playerCharacterPop.showDetail",self.prefix));
		ir.show(self.id +"WeaponRow"+self.player.Row,self.player.Weapon!=null && self.player.Weapon.size()>0);
		self.buildAmmoTrack();
	},
	close:function(row){
		var self = playerCharacterPop;
		//ir.get(self.id+row).classList.remove("show");
		popup(self.id+row);
	},
	edit:function(row){
		var self = playerCharacterPop;
		var player = sr5.characters.get(row);
		if(player==null)
		{
			return false;
		}
		if(player.User == sr5.user.Row)
		{
			return sr5.go("characterdetail.jsp?ctlRow="+self.player.Row);
		}
		return false;
	},
	init:function(player){
		var self = playerCharacterPop;
		self.player = player || self.player || self.blankPlayer;
		if (self.template==null) {
			self.template = ir.get(self.id+"Template").innerHTML;
		}
		if(!ir.exists(self.id + self.player.Row))
		{
			var container = ir.get(self.id + "Container");
			container.innerHTML += ir.template(self.template,player);
		}
		self.setAll();
		self.buildAdeptPower();
		self.buildArmor();
		self.buildBioware();
		self.buildContact();
		self.buildCyberdeck();
		self.buildCyberware();
		self.buildDamageTrack();
		self.buildDrone();
		self.buildGear();
		self.buildKnowledge();
		self.buildKnowledgeRoll();
		self.buildNaturalRoll();
		self.buildPortrait();
		self.buildQuality();
		self.buildSkill();
		self.buildSkillRoll();
		self.buildSpell();
		self.buildSpellDefenseRoll();
		self.buildStatusTrack();
		self.buildVehicle();
		self.buildWeapon();
		self.buildAttributes();
		ir.set(self.id + "Portrait" + player.Row,"");
		ir.set(self.id + "Initiative" + player.Row,player.Initiative + " + (" + player.InitiativeDice +"D6)");
		ir.set(self.id + "Metatype" + player.Row,self.player.Metatype.Name);
		ir.set(self.id + "MetatypeTraits" + player.Row,self.player.Metatype.Traits);
		ir.set(self.id + "Limits" + player.Row,"Physical "+self.player.PhysicalLimit+sr5.getCharacterBonus(self.player,"PhysicalLimit")+", Mental "+self.player.MentalLimit+sr5.getCharacterBonus(self.player,"MentalLimit")+", Social "+self.player.SocialLimit+sr5.getCharacterBonus(self.player,"SocialLimit"));
		ir.set(self.id + "Reputation" + player.Row,"Street Cred "+self.player.StreetCred+sr5.getCharacterBonus(self.player,"StreetCred")+", Notoriety "+self.player.Notoriety+sr5.getCharacterBonus(self.player,"Notoriety")+", Public Awareness "+self.player.PublicAwareness+sr5.getCharacterBonus(self.player,"PublicAwareness"));
		//ir.show(self.id + "DamageTrackWrap" + player.Row,player.User == sr5.user.Row);
		ir.show(self.id + "EditBtn" + player.Row,player.User == sr5.user.Row);
		ir.show(self.id + "KarmaWrap" + player.Row,player.Type === "PC");
		ir.show(self.id + "PRWrap" + player.Row,player.Type === "NPC");
	},

	pickTab:function(tabName,row){
		var self = playerCharacterPop;
		var tabs = document.getElementsByClassName("playerTab");
		for(var i=0,z=tabs.length;i<z;i++)
		{
			var tab = tabs[i];
			if(tab.id.replace(/[^0-9]+/g, '')!=row)
			{
				continue;
			}
			var id = tab.id.substring(3,tab.id.indexOf(row+""));
			if(id===tabName)
			{
				tab.classList.add("selected");
				ir.show(self.id + "Div" + id + row);
			}
			else
			{
				tab.classList.remove("selected");
				ir.hide(self.id + "Div" + id + row);
			}
		}
	},
	save:function(){
		
	},
	setAll:function(){
		var self = playerCharacterPop;
		var p = self.player
		for (var attrName in p) {
			if (p.hasOwnProperty(attrName)) {
				var attr = p[attrName];
				if (ir.exists(self.id+attrName+p.Row)) {				
					ir.set(self.id+attrName+p.Row,attr);
				}
			}
		}
	},
	show:function(player,forceRefresh){
		var self = playerCharacterPop;
		self.player = player || self.player || self.blankPlayer;
		if(self.player==null)
		{
			return;
		}
		var pop = ir.get(self.id + self.player.Row,true);
		if(pop==null || forceRefresh)
		{
			self.init(self.player);
			pop = ir.get(self.id + self.player.Row);
		}
		popup(pop);
		sr5.initHover();
	},
	showAttribute:function(characterRow,attribute){
		var player = sr5.characters.get(characterRow);
		var array = [];
		var bonuses = [];
		var att = sr5.attributeType.get(attribute);
		att._t = "tAttribute";
		array.push(att);
		if(player && player.Bonus && player.Bonus[attribute])
		{
			bonuses = player.Bonus[attribute].values;
			for(var i=0, z=bonuses.length;i<z;i++)
			{
				var b = bonuses[i];
				b._t=b.Table;
				array.push(player[sr5.getTableName(b)].get(b.ItemRow));
			}
		}
		descriptionPop.showBonus(array,bonuses,null);
	},
	showBonus:function(characterRow,attribute){
		var player = sr5.characters.get(characterRow);
		var array = [];
		var bonuses = [];
		if(player && player.Bonus && player.Bonus[attribute])
		{
			bonuses = player.Bonus[attribute].values;
			for(var i=0, z=bonuses.length;i<z;i++)
			{
				var b = bonuses[i];
				b._t=b.Table;
				array.push(player[sr5.getTableName(b)].get(b.ItemRow));
			}
		}
		descriptionPop.showBonus(array,bonuses,null);
	},
	showDetail:function(name,row,characterRow){
		var player = sr5.characters.get(characterRow);
		if(player == null)
		{
			return;
		}
		if(name==="CharacterContact")
		{
			name="Contact";
		}
		var a = player[name].get(row);
		if(a!=null)
		{	
			if(a.Bonus && a.Bonus.size)
			{
				var bs = a.Bonus.values;
				var records = [];
				records.push(a);
				for(var i = 0, z= bs.length;i<z;i++)
				{
					var b = bs[i];
					b._t = b.Table;
					records.push(player[sr5.getTableName(b)].get(b.ItemRow))
				}
				return descriptionPop.showBonus(records,a.Bonus.values,null);
			}
			return descriptionPop.show(a);
		}
		return false;
	},
	zz_searchPop:0
		
};