"use strict";
var pickArmorPop = {
		callback:null,
		characterRow:0,
		id:"pickArmorPop",
		list:new KeyedArray("Row"),
		searchArg:"",
		searchTimeout:null,
		applySearch:function(content){
			var self = pickArmorPop;
			if(self.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(self.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildTable:function(){
			var self = pickArmorPop;
			var list = self.list.values;
			var htm = "";
			var header = "<thead style='position:sticky;top:0;z-index:20;'><tr>"
						  + "<td class='tdl'>Armor</td>"
						  + "<td class='tdl'>Environment</td>"
						  + "<td class='tdc'>Rating</td>"
						  + "<td class='tdc'></td>"
						  + "</tr></thead>"
						  + "<tbody>";
			var rowTemplate = "<tr id='" + self.id + "Row{3}'>"
						  + "<td class='tdl clickable'  onclick='return pickArmorPop.showDetail({3})'>{0}</td>"
						  + "<td class='tdl'>{1}</td>"
						  + "<td class='tdc'>{2}</td>"
						  + "<td class='tdc' style='white-space:nowrap;'>" 
						  + "<button id='" + self.id + "PlusBtn{3}' type='button' class='mini' onclick='"+self.id+".select({3})'>+</button>"
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
						self.applySearch(a.Environment),
						a.ArmorRating,
						a.Row);		
			}			
			ir.set(self.id +"Table",header + htm + "</body>");
		},
		clear:function(){
			var self = pickArmorPop;
			ir.set(self.id+"SearchInput","");
			self.searchArg = "";
			clearTimeout(self.searchTimeout);	
			ir.hide(self.id + "ClearSearchBtn");
			if(self.searchKeyup)
			{
				self.searchTimeout = window.setTimeout(function(){pickArmorPop.searchKeyup(pickArmorPop.searchArg)}, 100);	
			}	
		},
		close:function(){
			var self = pickArmorPop;
			popup(self.id);
		},		
		filter:function(s)
		{
			var self = pickArmorPop;
			if(self.searchArg.length<3)
			{
				return false;
			}
			if(s.Name.toLowerCase().indexOf(self.searchArg)>-1)
			{
				return false;
			}
			if(s.Environment.toLowerCase().indexOf(self.searchArg)>-1)
			{
				return false;
			}
			return true;			
		},
		keyup:function(e)
		{		
			var self = pickArmorPop;
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
			var self = pickArmorPop;
			self.buildTable();
		},
		select:function(row){
			var self = pickArmorPop;
			var s = self.list.get(row);
			if(s!=null && self.callback!=null)
			{
				self.callback(s);
			}			
		},
		selectArmor:function(){
			var self = pickArmorPop;
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
				sr5.ajaxAsync({fn:"selectArmor"},callback);
			}
			else
			{
				self.buildTable();
			}			
		},
		show:function(){
			var self = pickArmorPop;
			self.selectArmor();
			var pop = ir.get(self.id);
			popup(pop);
			if(pop.classList.contains("show") && !sr5.isMobile)
			{
				ir.focus(self.id + "SearchInput");
			}
		},
		showDetail:function(row){
			var self = pickArmorPop;
			descriptionPop.show(self.list.get(row));
			return false;
		},
		zz_pickArmorPop:0
};