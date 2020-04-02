var detailModPop = {
	callback:null,
	characterRow:0,
	currentRecord:null,
	grade:null,
	id:'detailModPop',
	tempItemRow:-1,
	add:function(){
		var self = detailModPop;
		self.currentRecord.ItemRow = self.tempItemRow--;
		self.update();
	},
	afterPickAttachment:function(){
		var self = detailModPop;
		self.buildAttachments();
	},
	afterUpdate:function(res){
	},
	close:function(){
		var self = detailModPop;
		self.currentRecord=null;
		self.currentPrograms=null;
		popup(self.id);
	},
	gradeChange:function(){
		
	},
	init:function(){
		var self = detailModPop;		
		ir.set(self.id+"Section",sr5.getSection(self.currentRecord,null,true));
		self.initNewRecord();
		self.initTitle();
		self.initQuantity();
		self.initRating();
		self.initGrade();
		self.initMount();
		self.initButtons();
	},
	initButtons:function(){
		var self = detailModPop;	
		var canUpdate = (self.blankCharacterRecord.hasOwnProperty("Equipped")
					 || self.blankCharacterRecord.hasOwnProperty("Grade")
					 || self.blankCharacterRecord.hasOwnProperty("Mounted")
					 || self.blankCharacterRecord.hasOwnProperty("Quantity")
					 || (self.blankCharacterRecord.hasOwnProperty("Level")  && (self.currentRecord.Max>1 || false))
					 || (self.blankCharacterRecord.hasOwnProperty("Rating") && (self.currentRecord.MaxRating>1||false))
					 || self.blankCharacterRecord.hasOwnProperty("SkillRow") )
		ir.show(self.id + "RemoveBtn",!self.isNew);
		ir.show(self.id + "UpdateBtn",!self.isNew && canUpdate);
		ir.show(self.id + "AddBtn",self.isNew);
		ir.disable(self.id+"RatingPlus",ir.vn(self.id+"Rating")>=(self.currentRecord.MaxRating || 20));
		ir.disable(self.id+"RatingMinus",ir.vn(self.id+"Rating")<=(self.currentRecord.MinRating || 1));
		//ir.disable(self.id+"QuantityPlus",ir.vn(self.id+"Quantity")>=(99));
		//ir.disable(self.id+"QuantityMinus",ir.vn(self.id+"Quantity")<=(1));
	},
	initGrade:function(){
		var self = detailModPop;
		if(self.blankCharacterRecord.hasOwnProperty("Grade"))
		{
			ir.set(self.id+"Grade",self.parentRecord.Grade);
			ir.show(self.id+"GradeWrap");
		}
		else
		{
			ir.set(self.id+"Grade","Standard");
			ir.show(self.id+"GradeWrap",false);
		}		
	},
	initMount:function(){
		var self = detailModPop;
		var slots =["Barrel", "Internal", "Side", "Stock", "Top", "Under"];
		if(self.blankCharacterRecord.hasOwnProperty("Mounted") && self.currentRecord.Mount.length>0)
		{
			var available = self.currentRecord.Mount.replace(/ /g,"").split(",");
			if(self.currentRecord.Mount== "Any")
			{
				available=["Barrel", "Internal", "Side", "Stock", "Top", "Under"];
			}
			
			for(var i=0, z=slots.length;i<z;i++)
			{
				ir.show("detailModPopMountType" +slots[i],available.indexOf(slots[i])>-1);
			}
			ir.set(self.id+"Mount",self.currentRecord.Mounted || available[0]);
			ir.show(self.id+"MountWrap");
		}
		else
		{
			ir.set(self.id+"Mount","");
			ir.show(self.id+"MountWrap",false);
			for(var i=0, z=slots.length;i<z;i++)
			{
				ir.show("detailModPopMountType" +slots[i]);
			}
		}		
	},
	initNewRecord:function(){
		var self = detailModPop;
		var record = self.currentRecord;
		self.isNew=false;
		if(!record.hasOwnProperty("ItemRow"))
		{
			record.ItemRow=0;
			self.isNew=true;
		}
	},
	initQuantity:function(){
		var self = detailModPop;
		/* Not sure how quantity and mods work yet
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
		*/
		ir.set(self.id+"Quantity",1);
		ir.hide(self.id+"QuantityWrap");
	},
	initRating:function(){
		var self = detailModPop;
		var record = self.currentRecord;
		if(self.blankCharacterRecord.hasOwnProperty("Rating"))
		{
			if(record.hasOwnProperty("MaxRating") && (record.MaxRating<=1 || (record.MinRating || 1)==record.MaxRating)){
				ir.set(self.id+"Rating",1);
				ir.hide(self.id+"RatingWrap");
			}
			else
			{
				ir.set(self.id+"Rating",record.Rating || 1);
				ir.show(self.id+"RatingWrap",self.blankCharacterRecord.hasOwnProperty("Rating"));
			}
		}
		else
		{
			ir.set(self.id+"Rating",1);
			ir.hide(self.id+"RatingWrap");
		}
	},
	initTitle:function(){
		var self = detailModPop;
		var titlePrefix = "Edit ";
		if(self.isNew)
		{
			titlePrefix = "Add ";
		}
		ir.set(self.id+"Title",titlePrefix + self.parentRecordType + " Attachment");
	},
	quantityChange:function(amount){
		var self = detailModPop;
		var newRating =Math.min(Math.max(1,ir.vn(self.id+"Quantity") + amount),99);
		ir.set(self.id+"Quantity",newRating);
		ir.disable(self.id+"QuantityPlus",newRating>=99);
		ir.disable(self.id+"QuantityMinus",newRating<=1);
	},
	ratingChange:function(amount){
		var self = detailModPop;
		var newRating = Math.min(Math.max(self.currentRecord.MinRating || 1,ir.vn(self.id+"Rating") + amount),self.currentRecord.MaxRating || 20);
		ir.set(self.id+"Rating",newRating);
		ir.disable(self.id+"RatingPlus",newRating>=(self.currentRecord.MaxRating || 20));
		ir.disable(self.id+"RatingMinus",newRating<=(self.currentRecord.MinRating || 1));
	},
	read:function(){
		var self = detailModPop;
		var blank = self.blankCharacterRecord;
		var current = self.currentRecord;
		if(current.Delete)
		{
			return true;
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
		if(blank.hasOwnProperty("Mounted"))
		{
			current.Mounted = ir.v(self.id+"Mount");
			var parent = self.parentRecord;
			if(parent.Attachments==null)
			{
				parent.Attachments = new KeyedArray("ItemRow");
			}			
			var attachments = parent.Attachments.values;
			var callback = function(yes,a){
				if(yes)
				{
					a.Delete=true;
					a.Mounted="";
					self.update();
				}
			};
			for(var i =0, z=attachments.length;i<z;i++)
			{
				var a = attachments[i];
				if(a.Mounted !== "NA" && a.Mounted==current.Mounted && a.ItemRow!=current.ItemRow)
				{
					confirmPop.show("<span class='over'>" + a.Mounted+ "</span> already has an Attachment.<br>Replace <i>" + a.Name + " (<span class='over'>" + a.Mounted +"</span>)</i> with <i>" + current.Name + " (<span class='over'>" + current.Mounted +"</span>)</i>?",function(yes){callback(yes,a);});
					return false;
					
				}
				
			}
		}
		return true;
	},
	remove:function(){
		var self = detailModPop;
		var callback = function(yes){
			if(yes)
			{
				self.currentRecord.Delete = true;

				if(self.currentRecord.hasOwnProperty("Mounted"))
				{
					self.currentRecord.Mounted="";
				}
				self.update();
			}
		};
		confirmPop.show("Remove <i>" + self.currentRecord.Name + "</i> from <i>" + self.parentRecord.Name + "</i>?",callback);
	},
	show:function(record,parentRecord,callback){
		var self = detailModPop;
		self.currentRecord = ir.copy(record);
		self.parentRecord = parentRecord;
		self.callback = callback;
		self.parentRecordType = self.parentRecord._t.substring(1,record._t.length);
		if(self.parentRecordType==="Cyberware")
		{
			self.blankCharacterRecord = ir.copy(self.blankCyberwareAttachment);
		}
		else if(self.parentRecordType==="Weapon")
		{
			self.blankCharacterRecord = ir.copy(self.blankWeaponModifier);
		}
		self.init();
		popup(ir.get(self.id));
	},
	/** detailModPop.update updates a single record and returns that rcord to the callback */
	update:function(){
		var self = detailModPop;
		if(self.read())
		{
			if(self.callback!=null)
			{
				self.callback(self.currentRecord);
				self.close();
			}
		}
	},
	zz_detailModPop:0
};