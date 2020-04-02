"use strict";
var pickWeaponModifierPop = {
		callback:null,
		characterRow:0,
		id:"pickWeaponModifierPop",
		grades:new KeyedArray("name"),
		list:new KeyedArray("Row"),
		parentRecord:null,
		searchArg:"",
		searchTimeout:null,
		afterChange:function(){
			var self = pickWeaponModifierPop;
			if(self.callback!=null)
			{
				self.callback(self.characterList.values);
			}
		},
		applySearch:function(content){
			var self = pickWeaponModifierPop;
			if(self.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(self.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildTable:function(){
			var self = pickWeaponModifierPop;
			var list = self.list.values;
			var htm = "";
			var top = "";
			var bottom = "";
			var header = "<thead style='position:sticky;top:0;z-index:20;'><tr>"
						  + "<td class='tdl'>Attachment</td>"
						  + "<td class='tdc'>Mount</td>"
						  + "<td class='tdc'></td>"
						  + "</tr></thead>"
						  + "<tbody>";
			var rowTemplate = "<tr id='" + self.id + "WeaponModifierRow{2}'>"
						  + "<td class='tdl clickable' onclick='return pickWeaponModifierPop.showDetail({2})'>{0}</td>"
						  + "<td class='tdc'>{1}</td>"
						  + "<td class='tdc' style='white-space:nowrap;'>" 
						  + "<button type='button' class='mini' onclick='"+self.id+".select({2})'>+</button>"
						  +	"</td>"
						  + "</tr>";
			for(var i =0, z=list.length;i<z;i++)
			{
				var a = list[i];
				if(self.filter(a))
				{
					continue;
				}
				htm += ir.format(rowTemplate,
							self.applySearch(a.Name),
							a.Mount.length>0?self.applySearch(a.Mount):"-",
							a.Row);
			}			
			ir.set(self.id +"Table",header + htm + "</body>");
		},
		clear:function(){
			var self = pickWeaponModifierPop;
			ir.set(self.id+"SearchInput","");
			self.searchArg = "";
			clearTimeout(self.searchTimeout);	
			ir.hide(self.id + "ClearSearchBtn");
			if(self.searchKeyup)
			{
				self.searchTimeout = window.setTimeout(function(){pickWeaponModifierPop.searchKeyup(pickWeaponModifierPop.searchArg)}, 100);	
			}	
		},
		close:function(){
			var self = pickWeaponModifierPop;
			self.parentRecord = null;
			popup(self.id);
		},
		
		filter:function(s)
		{
			var self = pickWeaponModifierPop;
			var parent = self.parentRecord;
			if(self.searchArg.length<3)
			{
				return false;
			}
			if(s.Name.toLowerCase().indexOf(self.searchArg)>-1)
			{
				return false;
			}
			if(s.Mount.toLowerCase().indexOf(self.searchArg)>-1)
			{
				return false;
			}
			return true;			
		},
		keyup:function(e)
		{		
			var self = pickWeaponModifierPop;
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
		searchKeyup:function(arg){
			var self = pickWeaponModifierPop;
			self.buildTable();
		},
		select:function(row){
			var self = pickWeaponModifierPop;
			var s = self.list.get(row);
			if(s!=null && self.callback!=null)
			{
				self.callback(s);
			}
			
		},
		selectWeaponModifiers:function(){
			var self = pickWeaponModifierPop;
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
				sr5.ajaxAsync({fn:"selectWeaponModifiers"},callback);
			}
			else
			{
				self.buildTable();
			}			
		},
		show:function(parentRecord){
			var self = pickWeaponModifierPop;
			self.parentRecord = parentRecord;
			self.selectWeaponModifiers();
			var pop = ir.get(self.id);
			popup(pop)
			if(pop.classList.contains("show") && !sr5.isMobile)
			{
				ir.focus(self.id + "SearchInput");
			}
		},
		showDetail:function(row){
			var self = pickWeaponModifierPop;
			descriptionPop.show(self.list.get(row));
			return false;
		},
		toggleTable:function(name){
			var self = pickWeaponModifierPop;
			var pop = ir.get(self.id);
			var tables = pop.getElementsByClassName("tableWrap");
			for(var i=0,z=tables.length;i<z;i++)
			{
				var t = tables[i];
				if(t.id.indexOf(name)>-1)
				{
					ir.show(t);
				}
				else
				{
					ir.hide(t);
				}				
			}
		},
		zz_pickWeaponModifierPop:0
};