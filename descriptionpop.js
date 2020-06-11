var descriptionPop = {
	callback:null,
	hasAmmoType:false,
	hasEquip:false,
	hasFireMode:false,
	id:'descriptionPop',
	close:function(){
		var self = descriptionPop;
		ir.get(self.id).classList.remove("show");
	},
	init:function(record){
		var self = descriptionPop;
		var characterRow = record.CharacterRow;
		var htm = sr5.getSection(record,null,true);
		if(record.CurrentAmmoRow && record.CurrentAmmoRow>0)
		{
			var char = sr5.characters.get(characterRow);
			var ammo = char.Gear.get(record.CurrentAmmoRow);
			htm+= sr5.getSection(ammo,null,true);
		}
		if(record.Attachments && record.Attachments.size()>0)
		{
			var values = record.Attachments.values;
			for(var i = 0,z=values.length;i<z;i++)
			{
				var a = values[i];
				htm += sr5.getSection(a,null,true)
			}
		}
		if(record.Programs && record.Programs.size()>0)
		{
			var values = record.Programs.values;
			for(var i = 0,z=values.length;i<z;i++)
			{
				var a = values[i];
				htm += sr5.getSection(a,null,true)
			}
		}
		ir.set(self.id+"Container",htm);
		if(record.Portrait && record.Portrait>0)
		{
			var callback = function(imageRec){
				self.initPortrait(record.Row,imageRec);
			};
			sr5.selectImage(record.Portrait,callback);
		}
		if(characterRow)
		{
			var char = sr5.characters.get(characterRow);
			if(char !=null)
			{
				if(char.User != sr5.user.Row)
				{
					return ir.hide(self.id+"Equipped");
				}
			}
		}
		self.initEquip(record);
		self.initFireMode(record);
		self.initAmmoType(record);
		self.showEquipSection(self.hasEquip || self.hasFireMode || self.hasAmmoType);
	},
	initAmmoType:function(record)
	{		
		var self = descriptionPop;		
		if(self.fromCharacterSheet || !record.hasOwnProperty("CurrentAmmoRow") || (!record.Ammo && record.Ammo.length==0 && record.Type!=="Bow"))
		{
			self.hasAmmoType=false;
			ir.set(self.id+"AmmoType","");	
			return
		}
		self.hasAmmoType=true;
		ir.set(self.id+"AmmoType",sr5.getAmmoTypeSelect(record,self.id));	
		ir.set(self.id+"AmmoTypeSelWeapon"+record.ItemRow, record.CurrentAmmoRow);
	},
	initBonus:function(bonuses){
		var self = descriptionPop;
		var htm = "";
		var comma = "";
		for(var i =0 ,z=bonuses.length;i<z;i++)
		{
			var b = bonuses[i];
			htm += comma + (b.Value>0?"+":"") + ir.n(b.Value) + " " + b.Name;
			comma += ", "; 
		}
		ir.set(self.id+"Bonus",htm);
	},
	initEquip:function(record)
	{		
		var self = descriptionPop;
		
		if(self.fromCharacterSheet ||!record.hasOwnProperty("Equipped") )
		{
			self.hasEquip=false;
			return
		}
		self.hasEquip=true;
		ir.set(self.id+"Equip",sr5.getEquipCheckbox(record));		
	},
	initFireMode:function(record)
	{		
		var self = descriptionPop;		
		if(self.fromCharacterSheet || !record.hasOwnProperty("CurrentFireMode") || !record.Modes || record.Modes.length==0)
		{
			self.hasFireMode=false;
			ir.set(self.id+"FireMode","");	
			return
		}
		self.hasFireMode=true;
		ir.set(self.id+"FireMode",sr5.getFireModeRadio(record));		
	},
	initMulti:function(records){
		var self = descriptionPop;
		var htm = "";
		for(var i=0,z=records.length;i<z;i++)
		{
			var record = records[i];
			htm += sr5.getSection(record,null,true);
		}
		ir.set(self.id+"Container",htm);
		ir.show(self.id+"Equipped",false);
	},
	initPortrait:function(row,imageRec){
		var div = ir.get("sectionImage"+row,true);
		if(div!=null)
		{
			ir.set(div,sr5.getThumb(imageRec));
		}
	},
	initNotes:function(records){
		var self = descriptionPop;
		var htm = "";
		var comma = "";
		for(var i =0 ,z=records.length;i<z;i++)
		{
			var b = records[i];
			if(b.Note && b.Note.length>0)
			{
				htm += comma + b.Note;
				comma += ", "; 
			}
		}
		ir.set(self.id+"Note",htm);
	},
	show:function(record,callback){
		var self = descriptionPop;
		self.callback = callback;
		self.init(record);
		self.initNotes([record])
		ir.hide(self.id + "Bonus");	
		var pop = ir.get(self.id);
		if(pop.classList.contains("show"))
		{
			pop.classList.remove("show");
		}
		else
		{
			pop.classList.add("show");
		}
	},
	showBonus:function(records,bonuses,callback){
		var self = descriptionPop;
		self.callback = callback;
		self.initBonus(bonuses);
		self.initMulti(records);
		self.initNotes(records);
		ir.show(self.id + "Bonus");
		var pop = ir.get(self.id);
		if(pop.classList.contains("show"))
		{
			pop.classList.remove("show");
		}
		else
		{
			pop.classList.add("show");
		}
	},
	showEquipSection:function(showIt)
	{
		var self = descriptionPop;
		if(showIt)
		{
			ir.show(self.id+"Equipped");
			ir.get(self.id+"Container").classList.add("hasEquip");
		}
		else
		{
			ir.hide(self.id+"Equipped");
			ir.get(self.id+"Container").classList.remove("hasEquip");
		}
	},
	zz_descriptionPop:0
};