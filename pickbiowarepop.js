"use strict";
var pickBiowarePop = {
		callback:null,
		characterRow:0,
		id:"pickBiowarePop",
		gradeOptions:"",
		grades:new KeyedArray("name"),
		list:new KeyedArray("Row"),
		searchArg:"",
		searchTimeout:null,
		afterChange:function(){
			var self = pickBiowarePop;
			if(self.callback!=null)
			{
				self.callback(self.characterList.values);
			}
		},
		applySearch:function(content){
			var self = pickBiowarePop;
			if(self.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(self.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildTable:function(){
			var self = pickBiowarePop;
			var list = self.list.values;
			var htm = "";
			var top = "";
			var bottom = "";
			var header = "<thead style='position:sticky;top:0;z-index:20;'><tr>"
						  + "<td class='tdl'>Bioware</td>"
						  + "<td class='tdc' style='width:20%;'>Essence</td>"
						  + "<td class='tdc'></td>"
						  + "</tr></thead>"
						  + "<tbody>";
			var rowTemplate = "<tr id='" + self.id + "BiowareRow{2}'>"
						  + "<td class='tdl clickable' onclick='return pickBiowarePop.showDetail({2})'>{0}</td>"
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
							a.Essence,
							a.Row);
			}			
			ir.set(self.id +"Table",header + htm + "</body>");
		},
		clear:function(){
			var self = pickBiowarePop;
			ir.set(self.id+"SearchInput","");
			self.searchArg = "";
			clearTimeout(self.searchTimeout);	
			ir.hide(self.id + "ClearSearchBtn");
			if(self.searchKeyup)
			{
				self.searchTimeout = window.setTimeout(function(){pickBiowarePop.searchKeyup(pickBiowarePop.searchArg)}, 100);	
			}	
		},
		close:function(){
			var self = pickBiowarePop;
			popup(self.id);
		},
		
		filter:function(s)
		{
			var self = pickBiowarePop;
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
		keyup:function(e)
		{		
			var self = pickBiowarePop;
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
			var self = pickBiowarePop;
			self.buildTable();
		},
		select:function(row){
			var self = pickBiowarePop;
			var s = self.list.get(row);
			if(s!=null && self.callback!=null)
			{
				self.callback(s);
			}
			
		},
		selectBioware:function(){
			var self = pickBiowarePop;
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
				sr5.ajaxAsync({fn:"selectBioware"},callback);
			}
			else
			{
				self.buildTable();
			}			
		},
		show:function(){
			var self = pickBiowarePop;
			self.selectBioware();
			var pop = ir.get(self.id);
			popup(pop);
			if(pop.classList.contains("show") && !sr5.isMobile)
			{
				ir.focus(self.id + "SearchInput");
			}
		},
		showDetail:function(row){
			var self = pickBiowarePop;
			descriptionPop.show(self.list.get(row));
			return false;
		},
		toggleTable:function(name){
			var self = pickBiowarePop;
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
		zz_pickBiowarePop:0
};