var descriptionPop = {
		callback:null,
	id:'descriptionPop',
	close:function(){
		var self = descriptionPop;
		ir.get(self.id).classList.remove("show");
	},
	init:function(record){
		var self = descriptionPop;
		var htm = sr5.getSection(record,null,true);
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
		self.initEquip(record);
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
		var characterRow = record.CharacterRow;
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
		if(self.fromCharacterSheet ||!record.hasOwnProperty("Equipped") )
		{
			ir.get(self.id+"Container").classList.remove("hasEquip");
			return ir.hide(self.id+"Equipped");
		}
		ir.show(self.id+"Equipped");
		ir.set(self.id+"Equipped",sr5.getEquipCheckbox(record));	
		ir.get(self.id+"Container").classList.add("hasEquip");	
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
	show:function(record,callback){
		var self = descriptionPop;
		self.callback = callback;
		self.init(record);
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
	zz_descriptionPop:0
};