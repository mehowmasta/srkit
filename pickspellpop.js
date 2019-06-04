"use strict";
var pickSpellPop = {
		callback:null,
		characterRow:0,
		currentList:null,
		id:"pickSpellPop",
		list:new KeyedArray("Row"),
		searchArg:"",
		searchTimeout:null,
		tempItemRow:-1,
		afterUpdate:function(res){
			var self = pickSpellPop;
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
				Status.info("Spells have been updated.",5000);
			}
		},
		applySearch:function(content){
			var self = pickSpellPop;
			if(self.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(self.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildTable:function(){
			var self = pickSpellPop;
			var list = self.list.values;
			var htm = "";
			var top = "";
			var bottom = "";
			var header = "<thead style='position:sticky;top:0;z-index:20;'><tr>"
						  + "<td class='tdl'>Spell</td>"
						  + "<td class='tdc'>Type</td>"
						  + "<td class='tdc'>RNG</td>"
						  + "<td class='tdc'>DMG</td>"
						  + "<td class='tdc'>Dur</td>"
						  + "<td class='tdc'>DRN</td>"
						  + "<td class='tdc'></td>"
						  + "</tr></thead>"
						  + "<tbody>";
			var rowTemplate = "<tr id='" + self.id + "Row{6}' class='{9}'>"
						  + "<td class='tdl clickable' onclick='return pickSpellPop.showDetail({6})'>{0}</td>"
						  + "<td class='tdc'>{1}</td>"
						  + "<td class='tdc'>{2}</td>"
						  + "<td class='tdc'>{3}</td>"
						  + "<td class='tdc'>{4}</td>"
						  + "<td class='tdc'>{5}</td>"
						  + "<td class='tdc' style='white-space:nowrap;'>" 
						  + "<button id='" + self.id + "MinusBtn{6}' class='mini' type='button' {7} onclick='"+self.id+".minusOne({6})'>-</button>"
						  + "<button id='" + self.id + "PlusBtn{6}' class='mini' type='button' {8} onclick='"+self.id+".plusOne({6})'>+</button>"
						  +	"</td>"
						  + "</tr>";
			for(var i =0, z=list.length;i<z;i++)
			{
				var a = list[i];
				if(self.filter(a))
				{
					continue;
				}
				var cl = self.getCharacterItem(a.Row);
				htm = ir.format(rowTemplate,
						self.applySearch(a.Name),
						a.Type,
						a.Range,
						a.Damage,
						a.Duration,
						a.Drain,
						a.Row,
						cl==null || cl.Delete?"disabled":"",
						cl!=null && !cl.Delete?"disabled":"",
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
			var self = pickSpellPop;
			ir.set(self.id+"SearchInput","");
			self.searchArg = "";
			clearTimeout(self.searchTimeout);	
			ir.hide(self.id + "ClearSearchBtn");
			if(self.searchKeyup)
			{
				self.searchTimeout = window.setTimeout(function(){pickSpellPop.searchKeyup(pickSpellPop.searchArg)}, 100);	
			}	
		},
		close:function(){
			var self = pickSpellPop;
			self.currentList=null;
			self.callback=null;
			popup(self.id);
		},		
		filter:function(s)
		{
			var self = pickSpellPop;
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
		 * Spells are Unique
		 */
		getCharacterItem:function(row){
			var self = pickSpellPop;
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
			var self = pickSpellPop;
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
		/** decrements the spell and automatically updates the current list */
		minusOne:function(row){
			var self = pickSpellPop;
			var q = self.list.get(row);
			var cl = self.getCharacterItem(row);
			if(cl!=null)
			{
				cl.Delete=true;
				ir.get(self.id + "Row" + row).classList.remove("selected");
				ir.disable(self.id + "MinusBtn" + row,true);
				ir.disable(self.id + "PlusBtn" + row,false);				
			}
		},
		plusOne:function(row){
			var self = pickSpellPop;
			var q = self.list.get(row);
			var cl = self.getCharacterItem(row);
			if(cl==null)
			{
				cl = ir.copy(q);
				cl.CharacterRow = self.characterRow;
				cl.Delete = false;
				cl.ItemRow = self.tempItemRow--;
				self.currentList.add(cl);
			}
			else
			{
				cl.Delete = false;
			}
			ir.disable(self.id + "MinusBtn" + row,false);
			ir.disable(self.id + "PlusBtn" + row,true);
			ir.get(self.id + "Row"+ row).classList.add("selected");
		},
		searchKeyup:function(arg){
			var self = pickSpellPop;
			self.buildTable();
		},
		selectSpells:function(){
			var self = pickSpellPop;
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
				sr5.ajaxAsync({fn:"selectSpells"},callback);
			}
			else
			{
				self.buildTable();
			}
			
		},
		show:function(currentList){
			var self = pickSpellPop;
			self.currentList = new KeyedArray("ItemRow",ir.copy(currentList));
			self.selectSpells();
			var pop = ir.get(self.id);
			popup(pop);
			if(pop.classList.contains("show") && !sr5.isMobile)
			{
				ir.focus(self.id + "SearchInput");
			}
		},
		showDetail:function(row){
			var self = pickSpellPop;
			descriptionPop.show(self.list.get(row));
			return false;
		},
		update:function(){
			var self = pickSpellPop;
			sr5.updateCharacterSpell(self.characterRow,self.currentList.values,self.afterUpdate);
		},
		zz_pickSpellPop:0
};