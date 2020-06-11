var detailPop = {
	attachmentList:null,
	callback:null,
	characterRow:0,
	currentAttachments:null,
	currentPrograms:null,
	grade:null,
	id:'detailPop',
	suppressEquip:false,
	tempItemRow:-1,
	add:function(){
		var self = detailPop;
		self.currentRecord.ItemRow = self.tempItemRow--;
		self.update();
	},
	addMod:function(){
		var self = detailPop;

		if(self.currentRecordType === "Weapon")
		{
			pickWeaponModifierPop.attachmentList = self.currentAttachments;
			pickWeaponModifierPop.callback = self.afterPickWeaponModifier;
			pickWeaponModifierPop.show(self.currentRecord);
		}
		else
		{
			pickCyberwareAttachmentPop.attachmentList = self.currentAttachments;
			pickCyberwareAttachmentPop.callback = self.afterPickAttachment;
			pickCyberwareAttachmentPop.show(self.currentRecord);
		}		
	},
	addProgram:function(){
		var self = detailPop;
		pickProgramPop.show(self.currentRecord,self.currentPrograms.values,self.afterPickProgram);
	},
	afterAttachmentUpdate:function(attachmentRec){
		var self = detailPop;
		self.currentAttachments.set(attachmentRec);
		if(sr5.isShown(pickCyberwareAttachmentPop.id))
		{
			popup(pickCyberwareAttachmentPop.id);
		}
		self.buildAttachments();
	},
	afterWeaponModifierUpdate:function(attachmentRec){
		var self = detailPop;
		self.currentAttachments.set(attachmentRec);
		if(sr5.isShown(pickWeaponModifierPop.id))
		{
			popup(pickWeaponModifierPop.id);
		}
		self.buildWeaponModifiers();
	},
	afterPickAttachment:function(record){
		var self = detailPop;
		detailModPop.show(record,self.currentRecord,detailPop.afterAttachmentUpdate);
	},
	afterPickProgram:function(programList){
		var self = detailPop;
		self.currentPrograms.clear();
		self.currentPrograms.add(programList);
		self.buildPrograms();
	},
	afterPickWeaponModifier:function(record){
		var self = detailPop;
		detailModPop.show(record,self.currentRecord,detailPop.afterWeaponModifierUpdate);
	},
	afterUpdate:function(res){
		var self = detailPop;
		if(res.ok)
		{
			if(res.newRows && res.newRows.length>0)
			{
				sr5.applyNewRows(res.newRows,new KeyedArray("ItemRow",self.currentRecord));
				if(self.currentRecordType==="CharacterKnowledge")
				{
					self.afterUpdateKnowledge(self.currentRecord,res.skill);
				}
			}
			var type = self.currentRecordType
			if(type.indexOf("Character")>-1)
			{
				type = type.substring("Character".length,type.length);
			}
			if(self.currentRecord.Delete)
			{
				Status.info(type + ": " + self.currentRecord.Name + " has been removed.",5000);
			}
			else
			{
				Status.info(type + ": " + self.currentRecord.Name + " has been updated.",5000);
			}
			if(self.currentPrograms != null && self.currentPrograms.size)
			{
				if(self.currentPrograms.size()>0)
				{
					return self.updatePrograms();
				}
			}
			if(self.currentAttachments != null && self.currentAttachments.size)
			{
				if(self.currentAttachments.size()>0)
				{
					if(self.currentRecordType=== "Weapon")
					{
						return self.updateWeaponModifier();
					}					
					return self.updateAttachments();
				}
			}
			if(self.currentRecordType==="CharacterKnowledge")
			{
			
			}
			if(self.callback!=null)
			{
				self.callback(self.currentRecord);
			}
			self.close();
		}
	},
	afterUpdateAttachments:function(res){
		var self = detailPop;
		if(res.ok)
		{
			if(res.newRows && res.newRows.length>0)
			{
				sr5.applyNewRows(res.newRows,self.currentAttachments);
			}
			self.currentRecord.Attachments = self.currentAttachments;
		}
		if(self.callback!=null)
		{
			self.callback(self.currentRecord);
		}
		self.close();
	},
	afterUpdateKnowledge:function(rec,skill)
	{
		rec.Description = skill.Description;
		rec.Source = skill.Source;
		rec.Attribute = skill.Attribute;
		rec.Row = skill.Row;
	},
	afterUpdatePrograms:function(res){
		var self = detailPop;
		if(res.ok)
		{
			if(res.newRows && res.newRows.length>0)
			{
				sr5.applyNewRows(res.newRows,self.currentPrograms);
			}
			self.currentRecord.Programs = self.currentPrograms;
			
		}
		if(self.callback!=null)
		{
			self.callback(self.currentRecord);
		}
		self.close();
	},
	afterUpdateWeaponModifier:function(res){
		var self = detailPop;
		if(res.ok)
		{
			if(res.newRows && res.newRows.length>0)
			{
				sr5.applyNewRows(res.newRows,self.currentAttachments);
			}
			self.currentRecord.Attachments = self.currentAttachments;
		}
		if(self.callback!=null)
		{
			self.callback(self.currentRecord);
		}
		self.close();
	},
	buildAttachments:function(){
		var self = detailPop;	
		if(self.currentAttachments == null)
		{
			ir.set(self.id+"ModList","");
			return;
		}		
		var attachments = self.currentAttachments.values;
		var capacitySum = 0;
		var htm = "";
		var template = "<div class='detail' id='detailPopAttachment{0}' onclick='detailPop.showAttachmentDetail({0})'>{1}</div>";
		if(attachments!=null && attachments.length>0)
		{
			for(var i=0,z=attachments.length;i<z;i++)
			{
				var p = attachments[i];
				if(p.Delete)
				{
					continue;
				}
				capacitySum += sr5.getCapacity(p);
				htm += ir.format(template,p.ItemRow,sr5.getCyberware(p));
			}
		}
		var capacityClass="";
		if(capacitySum>self.currentRecord.Capacity)
		{
			capacityClass="over";
		}
		ir.set(self.id+"ModListSums", "Capacity:<span class='"+capacityClass+"'> "+capacitySum+"</span>/"+ self.currentRecord.Capacity);
		ir.set(self.id+"ModList",htm);
	},
	buildPrograms:function(){
		var self = detailPop;	
		var programs = self.currentPrograms.values;
		var htm = "";
		var template = "<div class='detail' id='detailPopProgram{0}' onclick='detailPop.showProgramDescription({0})'>•{1}</div>";
		if(programs!=null && programs.length>0)
		{
			for(var i=0,z=programs.length;i<z;i++)
			{
				var p = programs[i];
				if(p.Delete)
				{
					continue;
				}
				htm += ir.format(template,p.ItemRow,p.Name + " ["+p.Type+"]");
			}
		}
		ir.set(self.id+"ProgramList",htm);
	},
	buildWeaponModifiers:function(){
		var self = detailPop;	
		var weapon = self.currentRecord;
		if(self.currentAttachments == null)
		{
			ir.set(self.id+"ModList","");
			return;
		}	
		var slots =["Barrel", "Internal", "Side", "Stock", "Top", "Under"];
		var availableSlots = self.currentRecord.Mounts.replace(/ /g,"").split(",");
		if(self.currentRecord.Mount== "Any")
		{
			available=["Barrel", "Internal", "Side", "Stock", "Top", "Under"];
		}
		var usedSlots = [];
		var attachments = self.currentAttachments.values;
		var capacitySum = 0;
		var htm = "";
		var template = "<div class='detail' id='detailPopAttachment{0}' onclick='detailPop.showAttachmentDetail({0})'>{1}</div>";
		if(attachments!=null && attachments.length>0)
		{
			for(var i=0,z=attachments.length;i<z;i++)
			{
				var p = attachments[i];
				if(p.Delete)
				{
					continue;
				}
				if(!usedSlots.indexOf(p.Mounted)>-1)
				{
					usedSlots.push(p.Mounted);
				}
				htm += ir.format(template,p.ItemRow,sr5.getWeaponModifier(p));
			}
		}
		var modListSum = "";
		var comma = "";
		for(var i=0,z=slots.length;i<z;i++)
		{
			var a = slots[i];
			var available = availableSlots.indexOf(a)>-1;
			var used = usedSlots.indexOf(a)>-1;
			modListSum +=  comma + "<span class='"+ (!available?"disabled":"") + (used?" over ":"")+"'>"+a+"</span>"
			comma = ", ";
		}
		ir.set(self.id+"ModListSums", modListSum.length>0? "[" + modListSum + "]":"");
		ir.set(self.id+"ModList",htm);
	},
	close:function(){
		var self = detailPop;
		self.currentRecord=null;
		self.currentAttachments=null;
		self.currentPrograms=null;
		popup(self.id);
	},
	gradeChange:function(){
		var self = detailPop;
		if(self.currentRecord.Attachments)
		{
			var attachments = self.currentRecord.Attachments.values;
			for(var i=0,z=attachments.length;i<z;i++)
			{
				var p = attachments[i];
				if(p.Delete)
				{
					continue;
				}
				p.Grade = ir.v(self.id+"Grade");
			}
			self.buildAttachments();
		}
		
	},
	init:function(){
		var self = detailPop;		
		ir.set(self.id+"Section",sr5.getSection(self.currentRecord,null,true));
		self.initNewRecord();
		self.initTitle();
		self.initNote();
		self.initQuantity();
		self.initRating();
		self.initLevel();
		self.initEquip();
		self.initKnowledge();
		self.initGrade();
		self.initSpecialization();
		self.initModification();
		self.initPrograms();
		self.initButtons();
	},
	initAmmoType:function(){
		
	},
	initButtons:function(){
		var self = detailPop;	
		var canUpdate = (self.blankCharacterRecord.hasOwnProperty("Equipped")
					 || self.blankCharacterRecord.hasOwnProperty("Grade")
					 || self.blankCharacterRecord.hasOwnProperty("Quantity")
					 || (self.blankCharacterRecord.hasOwnProperty("Level")  && (self.currentRecord.Max>1 || false))
					 || (self.blankCharacterRecord.hasOwnProperty("Note"))
					 || (self.blankCharacterRecord.hasOwnProperty("Rating") && (self.currentRecord.MaxRating>1||false))
					 || self.blankCharacterRecord.hasOwnProperty("SkillRow") 
					 || self.currentRecordType === "CharacterKnowledge" )
		ir.show(self.id + "RemoveBtn",!self.isNew);
		ir.show(self.id + "UpdateBtn",!self.isNew && canUpdate);
		ir.show(self.id + "AddBtn",self.isNew);
		ir.disable(self.id+"RatingPlus",ir.vn(self.id+"Rating")>=(self.currentRecord.MaxRating || 20));
		ir.disable(self.id+"RatingMinus",ir.vn(self.id+"Rating")<=(self.currentRecord.MinRating || 1));
		ir.disable(self.id+"LevelPlus",ir.vn(self.id+"Level")>=(self.currentRecord.Max || 20));
		ir.disable(self.id+"LevelMinus",ir.vn(self.id+"Level")<=1);
		ir.disable(self.id+"QuantityPlus",ir.vn(self.id+"Quantity")>=(99));
		ir.disable(self.id+"QuantityMinus",ir.vn(self.id+"Quantity")<=(1));
	},
	initEquip:function(){
		var self = detailPop;
		if(self.blankCharacterRecord.hasOwnProperty("Equipped"))
		{
			ir.set(self.id+"Equip",self.currentRecord.Equipped);
			ir.show(self.id+"EquipWrap");
		}
		else
		{
			ir.set(self.id+"Equip",false);
			ir.show(self.id+"EquipWrap",false);
		}
	},
	initGrade:function(){
		var self = detailPop;
		if(self.blankCharacterRecord.hasOwnProperty("Grade"))
		{
			ir.set(self.id+"Grade",self.currentRecord.Grade || "Standard");
			ir.show(self.id+"GradeWrap");
		}
		else
		{
			ir.set(self.id+"Grade","Standard");
			ir.show(self.id+"GradeWrap",false);
		}		
	},
	initKnowledge:function(){
		var self = detailPop;
		var isKnowledge = self.currentRecordType === "CharacterKnowledge";
		if(isKnowledge)
		{
			if(self.blankCharacterRecord.hasOwnProperty("Type"))
			{
				ir.set(self.id+"KnowledgeType",self.currentRecord.Type || "Academic");
				ir.show(self.id+"KnowledgeNativeWrap",self.currentRecord.Type==="Language");
			}
			if(self.blankCharacterRecord.hasOwnProperty("Name"))
			{
				ir.set(self.id+"KnowledgeName",self.currentRecord.Name);
			}
			if(self.blankCharacterRecord.hasOwnProperty("Native"))
			{
				ir.set(self.id+"KnowledgeNative",self.currentRecord.Native);
			}
			ir.show(self.id + "KnowledgeTypeWrap",self.currentRecord.ItemRow==0);
			ir.show(self.id + "KnowledgeNameWrap");
			self.knowledgeChange(ir.v(self.id + "KnowledgeType"));
		}
		else
		{
			ir.hide(self.id + "KnowledgeTypeWrap");
			ir.hide(self.id + "KnowledgeNameWrap");
		}

		
	},
	initLevel:function(){
		var self = detailPop;
		var record = self.currentRecord;
		if(self.blankCharacterRecord.hasOwnProperty("Level"))
		{
			if(record.hasOwnProperty("Max") && record.Max==1){
				ir.set(self.id+"Level",1);
				ir.hide(self.id+"LevelWrap");
			}
			else
			{
				ir.set(self.id+"Level",record.Level);
				ir.show(self.id+"LevelWrap",self.blankCharacterRecord.hasOwnProperty("Level"));
			}
		}
		else
		{
			ir.set(self.id+"Level",1);
			ir.hide(self.id+"LevelWrap");
		}
	},
	initModification:function(){
		var self = detailPop;
		var record = self.currentRecord;;
		if(self.blankCharacterRecord.hasOwnProperty("Parent") && record.Container)
		{
			if(record.Attachments)
			{
				self.currentAttachments = new KeyedArray("ItemRow",record.Attachments.values);
			}
			else
			{
				self.currentAttachments = new KeyedArray("ItemRow");
			}
			if(self.currentRecordType === "Weapon")
			{
				self.buildWeaponModifiers();
			}
			else
			{
				self.buildAttachments();
			}
			ir.show(self.id+"ModWrap");
		}
		else
		{
			self.currentAttachments = null;
			ir.set(self.id+"ModList","");
			ir.show(self.id+"ModWrap",false);
		}
	},
	initNewRecord:function(){
		var self = detailPop;
		var record = self.currentRecord;
		self.isNew=false;
		if(!record.hasOwnProperty("ItemRow"))
		{
			record.ItemRow=0;
			self.isNew=true;
		}
	},
	initNote:function(){
		var self = detailPop;
		var record = self.currentRecord;
		if(self.blankCharacterRecord.hasOwnProperty("Note"))
		{
			ir.set(self.id+"Note",record.Note || "");
			ir.show(self.id+"NoteWrap",true);
			if(self.currentRecord.hasOwnProperty("Options"))
			{
				var options = self.currentRecord.Options.split(",");
				var list = ir.get(self.id+"NoteList");
				list.innerHTML = "";
				for(var i=0,z=options.length;i<z;i++)
				{
					list.innerHTML+="<option value='"+options[i]+"'>";
				}
			}
		}
		else
		{
			ir.set(self.id+"Note","");
			ir.set(self.id+"NoteList","");
			ir.hide(self.id+"NoteWrap");
		}
	},
	initPrograms:function(){
		var self = detailPop;
		var record = self.currentRecord;
		if(record.hasOwnProperty("Programs"))
		{
			self.currentPrograms = new KeyedArray("ItemRow",record.Programs.values);
			self.buildPrograms();
			ir.show(self.id+"ProgramWrap");
		}
		else
		{
			self.currentPrograms = null;
			ir.set(self.id+"ProgramList","");
			ir.show(self.id+"ProgramWrap",false);
		}
	},
	initQuantity:function(){
		var self = detailPop;
		var record = self.currentRecord;
		if(self.blankCharacterRecord.hasOwnProperty("Quantity"))
		{
			ir.set(self.id+"Quantity",record.Quantity || 1);
			ir.show(self.id+"QuantityWrap",self.blankCharacterRecord.hasOwnProperty("Quantity"));
		}
		else
		{
			ir.set(self.id+"Quantity",1);
			ir.hide(self.id+"QuantityWrap");
		}
	},
	initRating:function(){
		var self = detailPop;
		var record = self.currentRecord;
		if(self.blankCharacterRecord.hasOwnProperty("Rating"))
		{
			if(record.hasOwnProperty("MaxRating") && (record.MinRating==record.MaxRating)){
				ir.set(self.id+"Rating",record.MaxRating);
				ir.hide(self.id+"RatingWrap");
			}
			else
			{
				ir.set(self.id+"Rating",record.Rating || record.MinRating || 1);
				ir.show(self.id+"RatingWrap",self.blankCharacterRecord.hasOwnProperty("Rating"));
			}
		}
		else
		{
			ir.set(self.id+"Rating",1);
			ir.hide(self.id+"RatingWrap");
		}
	},
	initSpecialization:function(){
		var self = detailPop;
		var record = self.currentRecord;
		if(self.blankCharacterRecord.hasOwnProperty("Special"))
		{
			ir.set(self.id+"Special",record.Special || "");
			ir.show(self.id+"SpecialWrap",true);
			var specializations = self.currentRecord.Specialization.split(",");
			var list = ir.get(self.id+"SpecialList");
			list.innerHTML = "";
			for(var i=0,z=specializations.length;i<z;i++)
			{
				list.innerHTML+="<option value='"+specializations[i]+"'>";
			}
		}
		else
		{
			ir.set(self.id+"Special","");
			ir.set(self.id+"SpecialList","");
			ir.hide(self.id+"SpecialWrap");
		}
	},
	initTitle:function(){
		var self = detailPop;
		var titlePrefix = "Edit ";
		if(self.isNew)
		{
			titlePrefix = "Add ";
		}
		var type = self.currentRecordType
		if(type.indexOf("Character")>-1)
		{
			type = type.substring("Character".length,type.length);
		}
		ir.set(self.id+"Title",titlePrefix + type);
	},
	knowledgeChange:function(value){
		var self = detailPop;
		var dataList = ir.get(self.id + "KnowledgeNameList");
		var data = sr5.knowledgeType.get(value);
		if(data!=null)
		{
			var defaults = data.defaults.split(",");
			var htm = "";
			var template = "<option value='{0}'></option>";
			for(var i = 0, z=defaults.length;i<z;i++)
			{
				htm += ir.format(template,defaults[i]);
			}
			dataList.innerHTML = htm;
		}
		if(self.currentRecord.Type===value)
		{
			ir.set(self.id + "KnowledgeName",self.currentRecord.Name);
		}
		else
		{
			ir.set(self.id + "KnowledgeName","");
		}
		ir.show(self.id + "KnowledgeNativeWrap",value==="Language");
		if(value!=="Language" && !ir.visible(self.id + "RatingWrap"))
		{
			ir.set(self.id + "KnowledgeNative",false);
			ir.show(self.id + "RatingWrap");
		}
	},
	knowledgeNativeChange:function(checked){
		var self = detailPop;
		ir.show(self.id +"RatingWrap",!checked);
	},
	levelChange:function(amount){
		var self = detailPop;
		var newLevel = Math.min(Math.max(self.currentRecord.MinLevel || 1,ir.vn(self.id+"Level") + amount),self.currentRecord.MaxLevel || 20);
		ir.set(self.id+"Level",newLevel);
		ir.disable(self.id+"LevelPlus",newLevel>=(self.currentRecord.MaxLevel || 99));
		ir.disable(self.id+"LevelMinus",newLevel<=(self.currentRecord.MinLevel || 1));
	},

	quantityChange:function(amount){
		var self = detailPop;
		var newRating =Math.min(Math.max(1,ir.vn(self.id+"Quantity") + amount),99);
		ir.set(self.id+"Quantity",newRating);
		ir.disable(self.id+"QuantityPlus",newRating>=99);
		ir.disable(self.id+"QuantityMinus",newRating<=1);
	},
	ratingChange:function(amount){
		var self = detailPop;
		var newRating = Math.min(Math.max(self.currentRecord.MinRating || 1,ir.vn(self.id+"Rating") + amount),self.currentRecord.MaxRating || 20);
		ir.set(self.id+"Rating",newRating);
		ir.disable(self.id+"RatingPlus",newRating>=(self.currentRecord.MaxRating || 20));
		ir.disable(self.id+"RatingMinus",newRating<=(self.currentRecord.MinRating || 1));
	},
	read:function(){
		var self = detailPop;
		var blank = self.blankCharacterRecord;
		var current = self.currentRecord;
		var isKnowledge = self.currentRecordType === "CharacterKnowledge";
		var hasError=false;
		if(blank.hasOwnProperty("Equipped"))
		{
			current.Equipped = ir.v(self.id+"Equip");			
		}
		if(blank.hasOwnProperty("Rating"))
		{
			current.Rating = ir.vn(self.id+"Rating");
		}
		if(blank.hasOwnProperty("Quantity"))
		{
			current.Quantity = ir.vn(self.id+"Quantity");
		}
		if(blank.hasOwnProperty("Grade"))
		{
			current.Grade = ir.v(self.id+"Grade");
		}
		if(blank.hasOwnProperty("Note"))
		{
			current.Note = ir.escapeHtml(ir.v(self.id+"Note"));
			if(current.Note.length==0 && current.RequireText)
			{
				Status.error("Extra note is required. Re-read the description.");
				hasError = true;
			}
		}
		if(blank.hasOwnProperty("Special"))
		{
			current.Special = ir.escapeHtml(ir.v(self.id+"Special"));
		}
		if(blank.hasOwnProperty("Level"))
		{
			current.Level = ir.vn(self.id+"Level");
		}
		if(isKnowledge)
		{
			if(blank.hasOwnProperty("Name"))
			{
				current.Name = ir.v(self.id+"KnowledgeName");
				if(current.Name.length==0)
				{
					Status.error("Knowledge must have a name.");
					hasError = true;
				}
			}
			if(blank.hasOwnProperty("Type"))
			{
				current.Type = ir.v(self.id+"KnowledgeType");
				if(current.Type.length==0)
				{
					Status.error("Knowledge must have a type.");
					hasError = true;
				}
			}
			if(blank.hasOwnProperty("Native"))
			{
				current.Native = ir.v(self.id+"KnowledgeNative");
			}
		}
		return !hasError;
	},
	remove:function(){
		var self = detailPop;
		var callback = function(yes){
			if(yes)
			{
				self.currentRecord.Delete = true;
				self.update();
			}
		};
		confirmPop.show("Remove <i>" + self.currentRecord.Name + "</i> from Character?",callback);
	},
	show:function(record,callback){
		var self = detailPop;
		self.currentRecord = ir.copy(record);
		self.callback = callback;
		self.currentRecordType = record._t.substring(1,record._t.length);
		if(self.currentRecordType==="Cyberware" && self.currentRecord.Type==="Bioware")
		{
			self.currentRecordType = "Bioware";
		}
		self.blankCharacterRecord = ir.copy(self["blank"+self.currentRecordType]);
		self.init();
		popup(ir.get(self.id));
	},
	showAttachmentDetail:function(row){
		var self = detailPop;
		var record = self.currentAttachments.get(row);
		var callback = detailPop.afterAttachmentUpdate;
		if(self.currentRecordType==="Weapon")
		{
			callback = detailPop.afterWeaponModifierUpdate;
		}
		detailModPop.show(record,self.currentRecord,callback);
	},
	showProgramDescription:function(row){
		var self = detailPop;
		descriptionPop.show(self.currentPrograms.get(row));
		return false;
	},
	/** detailPop.update updates a single record, but we still put that record in an array so we can use a universal update function */
	update:function(){
		var self = detailPop;
		if(self.read())
		{
			var array = [];
			array.push(self.currentRecord);
			var type = self.currentRecordType
			if(type.indexOf("Character")>-1)
			{
				type = type.substring("Character".length,type.length);
			}
			sr5["updateCharacter" + type](self.characterRow,array,self.afterUpdate);
		}
	},
	updateAttachments:function(){
		var self = detailPop;
		sr5["updateCharacter"+self.currentRecordType+"Attachment"](self.characterRow,self.currentRecord.ItemRow,self.currentAttachments.values,self.afterUpdateAttachments);
	},
	updatePrograms:function(){
		var self = detailPop;
		sr5["updateCharacter"+self.currentRecordType+"Program"](self.characterRow,self.currentRecord.ItemRow,self.currentPrograms.values,self.afterUpdatePrograms);
	},
	updateWeaponModifier:function(){
		var self = detailPop;
		sr5["updateCharacter"+self.currentRecordType+"Modifier"](self.characterRow,self.currentRecord.ItemRow,self.currentAttachments.values,self.afterUpdateWeaponModifier);
	},
	zz_detailPop:0
};