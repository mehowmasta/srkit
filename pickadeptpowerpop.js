"use strict";
var pickAdeptPowerPop = {
		adeptPowerMaxRating:13,
		callback:null,
		characterRow:0,
		currentList:null,
		id:"pickAdeptPowerPop",
		list:new KeyedArray("Row"),
		searchArg:"",
		searchTimeout:null,
		tempItemRow:-1,
		afterUpdate:function(res){
			var self = pickAdeptPowerPop;
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
				Status.info("Adept Powers have been updated.",5000);
			}
		},
		applySearch:function(content){
			var self = pickAdeptPowerPop;
			if(self.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(self.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildTable:function(){
			var self = pickAdeptPowerPop;
			var list = self.list.values;
			var htm = "";
			var top = "";
			var bottom = "";
			var header = "<thead style='position:sticky;top:0;z-index:20;'><tr>"
						  + "<td class='tdl'>Adept Power</td>"
						  + "<td class='tdc'>Max Level</td>"
						  + "<td class='tdc'></td>"
						  + "<td class='tdc'>Character Level</td>"
						  + "</tr></thead>"
						  + "<tbody>";
			var rowTemplate = "<tr id='" + self.id + "AdeptPowerRow{2}' class='{6}'>"
						  + "<td class='tdl clickable' onclick='return pickAdeptPowerPop.showDetail({2})'>{0}</td>"
						  + "<td class='tdc'>{1}</td>"
						  + "<td class='tdc' style='white-space:nowrap;'>" 
						  + "<button id='" + self.id + "MinusBtn{2}' class='mini' type='button' {4} onclick='"+self.id+".minusOne({2})'>-</button>"
						  + "<button id='" + self.id + "PlusBtn{2}' class='mini' type='button' {5} onclick='"+self.id+".plusOne({2})'>+</button>"
						  +	"</td>"
						  + "<td id='" + self.id + "CharacterLevel{2}' class='tdc'>{3}</td>"
						  + "</tr>";
			for(var i =0, z=list.length;i<z;i++)
			{
				var a = list[i];
				if(self.filter(a))
				{
					continue;
				}
				var cl = self.getCharacterItem(a.Row);
				var characterLevel = cl!=null?cl.Level:0;
				htm = ir.format(rowTemplate,
						self.applySearch(a.Name),
						a.Max==99?"":a.Max,
						a.Row,
						characterLevel,
						cl==null || cl.Delete?"disabled":"",
						cl!=null && !cl.Delete && characterLevel>=a.Max?"disabled":"",
						cl!=null && !cl.Delete?"selected":"");
				if(cl!=null && !cl.Delete)
				{
					top += htm;
				}
				else
				{
					bottom += htm;					
				}
			}			
			ir.set(self.id +"Table",header + top + bottom + "</body>");
		},
		clear:function(){
			var self = pickAdeptPowerPop;
			ir.set(self.id+"SearchInput","");
			self.searchArg = "";
			clearTimeout(self.searchTimeout);	
			ir.hide(self.id + "ClearSearchBtn");
			if(self.searchKeyup)
			{
				self.searchTimeout = window.setTimeout(function(){pickAdeptPowerPop.searchKeyup(pickAdeptPowerPop.searchArg)}, 100);	
			}	
		},
		close:function(){
			var self = pickAdeptPowerPop;
			popup(self.id);
		},
		
		filter:function(s)
		{
			var self = pickAdeptPowerPop;
			if(self.searchArg.length<3)
			{
				return false;
			}
			if(s.Name.toLowerCase().indexOf(self.searchArg)>-1)
			{
				return false;
			}
			return true;			
		},
		/**
		 * AdeptPowers are Unique a character cannot have duplicates
		 */
		getCharacterItem:function(row){
			var self = pickAdeptPowerPop;
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
			var self = pickAdeptPowerPop;
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
			var self = pickAdeptPowerPop;
			var q = self.list.get(row);
			var cl = self.getCharacterItem(row);
			if(cl!=null)
			{
				cl.Level--;
				if(cl.Level<=0)
				{
					cl.Level=0;
					cl.Delete=true;
					ir.get(self.id + "AdeptPowerRow" + row).classList.remove("selected");
				}
				if(ir.exists(self.id + "CharacterLevel" + row ))
				{
					ir.set(self.id + "CharacterLevel" + row ,cl.Level);
				}
				ir.disable(self.id + "MinusBtn" + row,cl.Level<=0);
				ir.disable(self.id + "PlusBtn" + row,cl.Level>=q.Max);				
			}
			self.afterChange();
		},
		plusOne:function(row){
			var self = pickAdeptPowerPop;
			var q = self.list.get(row);
			var cl = self.getCharacterItem(row);
			if(cl==null)
			{
				cl = ir.copy(q);
				cl.Level = 1;
				cl.Delete = false;
				cl.CharacterRow = self.characterRow;
				cl.ItemRow = self.tempItemRow--;
				self.currentList.add(cl);
				if(ir.exists(self.id + "CharacterLevel" + row ))
				{
					ir.set(self.id + "CharacterLevel" + row ,cl.Level);
				}
			}
			else if(cl.Level < q.Max)
			{
				cl.Level++;
				cl.Delete = false;
				if(ir.exists(self.id + "CharacterLevel" + row ))
				{
					ir.set(self.id + "CharacterLevel" + row ,cl.Level);
				}
			}
			ir.disable(self.id + "MinusBtn" + row,cl.Level<=0);
			ir.disable(self.id + "PlusBtn" + row,cl.Level>=q.Max);
			ir.get(self.id + "AdeptPowerRow"+ row).classList.add("selected");
			self.afterChange();
		},
		searchKeyup:function(arg){
			var self = pickAdeptPowerPop;
			self.buildTable();
		},
		selectAdeptPowers:function(){
			var self = pickAdeptPowerPop;
			var list = self.list;
			if(list.values.length==0)
			{
				var callback = function(res){
					if(res.ok)
					{
						self.list.add(res.list);
						self.buildTable();
					}
				};
				sr5.ajaxAsync({fn:"selectAdeptPowers"},callback);
			}
			else
			{
				self.buildTable();
			}
			
		},
		show:function(currentList){
			var self = pickAdeptPowerPop;
			self.currentList = new KeyedArray("ItemRow",ir.copy(currentList));
			self.selectAdeptPowers();
			var pop = ir.get(self.id);
			popup(pop);
			if(pop.classList.contains("show") && !sr5.isMobile)
			{
				ir.focus(self.id + "SearchInput");
			}
		},
		showDetail:function(row){
			var self = pickAdeptPowerPop;
			descriptionPop.show(self.list.get(row));
			return false;
		},
		update:function(){
			var self = pickAdeptPowerPop;			
			sr5.updateCharacterAdeptPower(self.characterRow,self.currentList.values,self.afterUpdate);
		},
		zz_pickAdeptPowerPop:0
};