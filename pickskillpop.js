"use strict";
var pickSkillPop = {
		callback:null,
		characterRow:0,
		currentList:null,
		id:"pickSkillPop",
		list:new KeyedArray("Row"),
		searchArg:"",
		searchTimeout:null,
		skillMaxRating:14,
		tempItemRow:-1,
		afterUpdate:function(res){
			var self = pickSkillPop;
			if(res.ok)
			{
				if(res.newRows && res.newRows.length>0)
				{
					sr5.applyNewRows(res.newRows,self.currentList);
				}
				if(self.callback!=null)
				{
					self.callback(self.currentList.values);
				}
				self.close();
				Status.info("Skills have been updated.",5000);
			}
		},
		applySearch:function(content){
			var self = pickSkillPop;
			if(self.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(self.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildTable:function(){
			var self = pickSkillPop;
			var list = self.list.values;
			var top = "";
			var bottom = "";
			var header = "<thead style='position:sticky;top:0;z-index:20;'><tr>"
						  + "<td class='tdl'>Skill</td>"
						  + "<td class='tdl'>Group</td>"
						  + "<td class='tdc'></td>"
						  + "<td class='tdc'>Character Rating</td>"
						  + "</tr></thead>"
						  + "<tbody>";
			var rowTemplate = "<tr id='" + self.id + "SkillRow{2}' class='{6}'>"
						  + "<td class='tdl clickable' onclick='return pickSkillPop.showDetail({2})'>{0}</td>"
						  + "<td class='tdl'>{1}</td>"
						  + "<td class='tdc' style='white-space:nowrap;'>" 
						  + "<button id='" + self.id + "MinusBtn{2}' class='mini' type='button' {4} onclick='"+self.id+".minusOne({2})'>-</button>"
						  + "<button id='" + self.id + "PlusBtn{2}' class='mini' type='button' {5} onclick='"+self.id+".plusOne({2})'>+</button>"
						  +	"</td>"
						  + "<td id='" + self.id + "CharacterRating{2}' class='tdc'>{3}</td>"
						  + "</tr>";
			for(var i =0, z=list.length;i<z;i++)
			{
				var a = list[i];
				if(self.filter(a))
				{
					continue;
				}
				var cl = self.getCharacterItem(a.Row);
				var characterRating = cl!=null?cl.Rating:0;
				var htm = ir.format(rowTemplate,
							self.applySearch(a.Name),
							self.applySearch(a.Group),
							a.Row,
							characterRating,
							characterRating==0 || cl.Delete?"disabled":"",
							characterRating>0 && !cl.Delete && cl.Rating>=self.skillMaxRating?"disabled":"",
							characterRating>0 && !cl.Delete?"selected":"");	
				if(characterRating>0 && !cl.Delete)
				{
					top += htm;
				}
				else
				{
					bottom += htm;					
				}
			}			
			ir.set(self.id +"Table",header + top + bottom + "</tbody>");
		},
		clear:function(){
			var self = pickSkillPop;
			ir.set(self.id+"SearchInput","");
			self.searchArg = "";
			clearTimeout(self.searchTimeout);	
			ir.hide(self.id + "ClearSearchBtn");
			if(self.searchKeyup)
			{
				self.searchTimeout = window.setTimeout(function(){pickSkillPop.searchKeyup(pickSkillPop.searchArg)}, 100);	
			}	
		},
		close:function(){
			var self = pickSkillPop;
			self.currentList=null;
			self.callback=null;
			popup(self.id);
		},		
		filter:function(s)
		{
			var self = pickSkillPop;
			if(self.searchArg.length<3)
			{
				return false;
			}
			if(s.Name.toLowerCase().indexOf(self.searchArg)>-1)
			{
				return false;
			}
			if(s.Group.toLowerCase().indexOf(self.searchArg)>-1)
			{
				return false;
			}
			return true;			
		},
		/**
		 * Skills are Unique a character cannot have duplicates
		 */
		getCharacterItem:function(row){
			var self = pickSkillPop;
			var array = self.currentList.values;
			for(var i=0,z=array.length;i<z;i++)
			{
				var a = array[i];
				if(a.Row == row)
				{
					return a;
				}
			}
			return null;
		},
		keyup:function(e)
		{		
			var self = pickSkillPop;
			if (typeof e == 'undefined' && window.event)
			{
				e = window.event;
			}
			if (e == null)
			{
				ir.log("Cannot process key press codes!!!");
				return;
			}
			clearTimeout(self.searchTimeout);
			if (e.keyCode!=13)//enter
			{
				self.searchArg = ir.ltrim(ir.v(self.id + "SearchInput"));
				self.searchArg = ir.rtrim(self.searchArg).toLowerCase();
				ir.show(self.id + "ClearSearchBtn",self.searchArg.length>0);
				if(self.searchKeyup)
				{
					self.searchTimeout = window.setTimeout(function(){self.searchKeyup(self.searchArg)}, 800);	
				}					
			}		
		},
		minusOne:function(row){
			var self = pickSkillPop;
			var q = self.list.get(row);
			var cl = self.getCharacterItem(row);
			if(cl!=null)
			{
				cl.Rating--;
				if(cl.Rating<=0)
				{
					cl.Rating=0;
					cl.Delete = true;
					ir.get(self.id + "SkillRow" + row).classList.remove("selected");
				}
				if(ir.exists(self.id + "CharacterRating" + row ))
				{
					ir.set(self.id + "CharacterRating" + row ,cl.Rating);
				}
				ir.disable(self.id + "MinusBtn" + row,cl.Rating<=0);
				ir.disable(self.id + "PlusBtn" + row,cl.Rating>=self.skillMaxRating);				
			}
		},
		plusOne:function(row){
			var self = pickSkillPop;
			var q = self.list.get(row);
			var cl = self.getCharacterItem(row);
			if(cl==null)
			{
				cl = ir.copy(q);
				cl.Rating = 1;
				cl.Special = "";
				cl.Delete = false;
				cl.ItemRow = self.tempItemRow--;
				self.currentList.add(cl);
				if(ir.exists(self.id + "CharacterRating" + row ))
				{
					ir.set(self.id + "CharacterRating" + row ,cl.Rating);
				}
			}
			else if(cl.Rating < self.skillMaxRating)
			{
				cl.Rating++;
				cl.Delete = false;
				if(ir.exists(self.id + "CharacterRating" + row ))
				{
					ir.set(self.id + "CharacterRating" + row ,cl.Rating);
				}
			}
			ir.disable(self.id + "MinusBtn" + row,cl.Rating<=0);
			ir.disable(self.id + "PlusBtn" + row,cl.Rating>=self.skillMaxRating);
			ir.get(self.id + "SkillRow"+ row).classList.add("selected");
		},
		searchKeyup:function(arg){
			var self = pickSkillPop;
			self.buildTable();
		},
		selectSkills:function(){
			var self = pickSkillPop;
			var list = self.list;
			if(list.values.length==0)
			{
				var callback = function(res){
					if(res.ok)
					{
						self.list.add(res.list.sort(function(a,b){
						    if(a.Name < b.Name)
						    {
						    	return -1;
						    }
						    else
						    {
						    	return 1;
						    }			    
						    return 0;
						}));
						self.buildTable();
					}
				};
				sr5.ajaxAsync({fn:"selectSkills"},callback);
			}
			else
			{
				self.buildTable();
			}
			
		},
		show:function(currentList){
			var self = pickSkillPop;
			self.currentList = new KeyedArray("Row",ir.copy(currentList));
			self.selectSkills();
			var pop = ir.get(self.id);
			popup(pop);
			if(pop.classList.contains("show") && !sr5.isMobile)
			{
				ir.focus(self.id + "SearchInput");
			}
		},
		showDetail:function(row){
			var self = pickSkillPop;
			descriptionPop.show(self.list.get(row));
			return false;
		},
		update:function(){
			var self = pickSkillPop;
			var value = "";
			var delim = "";
			var array = self.currentList.values;
			for(var i=0,z=array.length;i<z;i++)
			{
				var q = array[i];
				if(q.ItemRow <= 0 && q.Delete)
				{
					continue;
				}
				value += delim + q.ItemRow + sr5.splitter + q.Row + sr5.splitter + q.Rating + sr5.splitter + (q.Special || "") + sr5.splitter + (q.Delete || false);
				delim= sr5.delimiter;
			}
			sr5.ajaxAsync({fn:"updateCharacterSkill",characterRow:self.characterRow,updateString:value},self.afterUpdate);
		},
		zz_pickSkillPop:0
};