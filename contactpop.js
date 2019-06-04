var contactPop = {
		callback:null,
		characterRow:0,
		currentContact:null,
		id:"contactPop",
		initialized:false,
		addPortrait:function(){
			pickPortraitPop.callback = contactPop.afterAddPortrait;
			pickPortraitPop.show();
		},
		afterAddPortrait:function(imageRec){
			var self = contactPop;
			ir.set(self.id + "Portrait",imageRec.Row);
			var image = ir.get(self.id+"PortraitImg");
			image.src = sr5.getImagePath(imageRec,false);
			ir.hide(self.id+"AddPortraitBtn");
			ir.show(self.id + "PortraitDiv");
			ir.get(self.id + "FieldDiv").classList.add("flexStart");
		},
		changePortrait:function(){
			pickPortraitPop.callback = contactPop.afterAddPortrait;
			pickPortraitPop.show();
		},
		close:function(){
			var self = contactPop;
			self.currentContact = null;
			self.callback = null;
			popup(self.id);
		},
		connectionChange:function(amount){
			var self = contactPop;
			var newConnection =Math.min(Math.max(1,ir.vn(self.id+"Connection") + amount),12);
			ir.set(self.id+"Connection",newConnection);
			ir.disable(self.id+"ConnectionPlus",newConnection>=12);
			ir.disable(self.id+"ConnectionMinus",newConnection<=1);
		},
		init:function(){
			var self = contactPop;
			var rec = self.currentContact;
			if(!self.initialized)
			{
				self.initTypes();
			}
			self.setAll();
			var isNew = rec.Row==0;		
			if(rec.Portrait && rec.Portrait>0)
			{
				sr5.selectImage(rec.Portrait,self.afterAddPortrait);
			}
			else
			{
				self.removePortrait();
			}
			self.loyaltyChange(0);
			self.connectionChange(0);
			ir.show(self.id + "RemoveBtn",!isNew);
			ir.show(self.id + "UpdateBtn",!isNew);
			ir.show(self.id + "AddBtn",isNew);
		},
		initTypes:function(){
			var self = contactPop;
			var sex = sr5.sex.values;
			var sexSelect = ir.get(self.id + "Sex");
			for(var i =0,z=sex.length;i<z;i++)
			{
				addOption(sexSelect,sex[i].Row,sex[i].Name);
			}
			var meta = sr5.metatypes.values;
			var metaSelect = ir.get(self.id + "MetaType");
			for(var i =0,z=meta.length;i<z;i++)
			{
				addOption(metaSelect,meta[i].Row,meta[i].Name);
			}
			self.initialized=true;				
		},
		loyaltyChange:function(amount){
			var self = contactPop;
			var newLoyalty =Math.min(Math.max(1,ir.vn(self.id+"Loyalty") + amount),6);
			ir.set(self.id+"Loyalty",newLoyalty);
			ir.disable(self.id+"LoyaltyPlus",newLoyalty>=6);
			ir.disable(self.id+"LoyaltyMinus",newLoyalty<=1);
		},
		read:function(){
			var self = contactPop;
			var hasError = false;
			var rec = self.currentContact;
			for (var attrName in rec) {
				if (rec.hasOwnProperty(attrName)) {
					var attr = rec[attrName];
					if (ir.exists(self.id+attrName)) {				
						rec[attrName] = ir.v(self.id+attrName);
					}
				}
			}
			if(rec.Name.length==0)
			{
				Status.error("Contact must have a name");
				hasError = true;
			}
			if(rec.Archetype.length==0)
			{
				Status.error("Contact missing Archetype");
				hasError = true;
			}
			if(rec.Loyalty < 1)
			{
				Status.error("Loyalty must be at least 1");
				hasError = true;
			}
			if(rec.Loyalty > 6)
			{
				Status.error("Loyalty must be less than 6");
				hasError = true;
			}
			if(rec.Connection < 1)
			{
				Status.error("Connection must be at least 1");
				hasError = true;
			}
			if(rec.Connection > 12)
			{
				Status.error("Connection must be less than 12");
				hasError = true;
			}
			return !hasError;
		},
		remove:function(){
			var self = contactPop;
			var callback = function(yes){
				if(yes)
				{
					self.currentContact.Delete = true;
					self.update();
				}
			};
			confirmPop.show("Remove <i>"+self.currentContact.Archetype + " " + self.currentContact.Name + "</i> from Contacts?",callback);
		},
		removePortrait:function(){
			var self = contactPop;
			ir.hide(self.id+ "PortraitDiv");
			ir.set(self.id + "PortraitImg","");
			ir.set(self.id + "Portrait",0);
			ir.show(self.id + "AddPortraitBtn");
			ir.get(self.id + "FieldDiv").classList.remove("flexStart");
		},
		setAll:function(){
			var self = contactPop;
			var rec = self.currentContact;
			for (var attrName in rec) {
				if (rec.hasOwnProperty(attrName)) {
					var attr = rec[attrName];
					if (ir.exists(self.id+attrName)) {				
						ir.set(self.id+attrName,attr);
					}
				}
			}
		},
		show:function(record,callback){
			var self = contactPop;
			self.callback=callback;
			self.currentContact = record;
			self.init();
			popup(self.id);
			ir.focus(self.id+"Name");
		},
		update:function(){
			var self = contactPop;
			if(self.read())
			{
				var callback=function(res){
					if(res.ok)
					{
						if(self.callback!=null)
						{
							self.callback(res.contact);
						}
						self.close();
					}
				};
				var rec = self.currentContact;
				rec.fn = "updateCharacterContact";
				sr5.ajaxAsync(rec,callback);
			}
		},
		zz_contactPop:0
}