var awardPop = {
		characters:new KeyedArray("Row"),
		id:"awardPop",
		type:null,
		afterSelectCharacters:function(list){
			var self = awardPop;
			for(var i =0, z=list.length;i<z;i++)
			{
				var a = list[i];
				self.characters.set(a);
			}
			self.buildCharacterList();
		},
		afterUpdate:function(res,req){
			var self = awardPop;
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
				
			}
			Status.info("")
			self.close();
		},
		buildCharacterList:function(){
			var self = awardPop;
			var container = ir.get(self.id + "CharacterList");
			container.innerHTML = "";
			var template = "<div id='awardCharacter{1}' class='awardCharacter'><div class='x hover' data-hover='Remove this character' onclick='awardPop.removeCharacter({1})'>X</div><div class='awardThumbWrap' onclick='awardPop.toggleCharacter(this,{1})'>{0}<div class='overlay'></div></div><label class='thumbLabel' >{2}</label></div>";
			var list = self.characters.values;
			for(var i =0, z=list.length;i<z;i++)
			{
				var a = list[i];
				container.innerHTML+= ir.format(template,self.getPortrait(a),a.Row,a.Name);
			}
		},
		close:function(){
			var self = awardPop;
			popup(self.id);
			self.reset();
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
		init:function(type){
			var self = awardPop;
			self.type=type;
		},
		minus:function(type){
			var self = awardPop;
			var ele = ir.get(self.id + type);
			ele.value = ir.n(ele.value) - ir.n(ele.step);
		},
		pickCharacters:function(){
			pickCharacterPop.show(null,false,awardPop.afterSelectCharacters);
		},
		plus:function(type){
			var self = awardPop;
			var ele = ir.get(self.id + type);
			ele.value = ir.n(ele.value) + ir.n(ele.step);
		},
		read:function(req){
			var self = awardPop;
			var list = self.characters.values;
			var characterRows = "";
			var splitter = "";
			for(var i =0, z=list.length;i<z;i++)
			{
				var a = list[i];
				if(a.off)
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
			req.nuyen = ir.vn(self.id+"Nuyen");
			req.karma = ir.vn(self.id+"Karma");
			return req;
		},		
		removeCharacter:function(characterRow){
			var self = awardPop;
			self.characters.remove(characterRow);
			var div = ir.get("awardCharacter"+characterRow,true);
			ir.deleteNode(div);			
		},
		show:function(type){
			var self = awardPop;
			self.init(type);
			popup(self.id);
		},
		reset:function(){
			var self = awardPop;
			ir.set(self.id+"Nuyen",0);
			ir.set(self.id+"Karma",0);
		},
		toggleCharacter:function(ele,characterRow){
			var self = awardPop;
			var c = self.characters.get(characterRow)
			if(ele.classList.contains("off"))
			{
				ele.classList.remove("off");
				c.off = true;
			}
			else
			{
				ele.classList.add("off");
				c.off = false;
			}
		},
		update:function(){
			var self = awardPop;
			var req = {fn:"award",characterRows:null,type:self.type};
			req = self.read(req);
			sr5.ajaxAsync(req,function(res){self.afterUpdate(res,req);});
		},
		zz_awardPop:0
}