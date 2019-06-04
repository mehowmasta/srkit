"use strict";
var pickGearPop = {
		callback:null,
		characterRow:0,
		id:"pickGearPop",
		list:new KeyedArray("Row"),
		searchArg:"",
		searchTimeout:null,
		applySearch:function(content){
			var self = pickGearPop;
			if(self.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(self.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildTable:function(){
			var self = pickGearPop;
			var list = self.list.values;
			var htm = "";
			var header = "<thead style='position:sticky;top:0;z-index:20;'><tr>"
						  + "<td class='tdl'>Gear</td>"
						  + "<td class='tdl'>Type</td>"
						  + "<td class='tdc'>Rating</td>"
						  + "<td class='tdc'></td>"
						  + "</tr></thead>"
						  + "<tbody>";
			var rowTemplate = "<tr id='" + self.id + "Row{3}'>"
						  + "<td class='tdl clickable'  onclick='return pickGearPop.showDetail({3})'>{0}</td>"
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
						self.applySearch(a.Type + " " + a.SubType),
						self.getRating(a),
						a.Row);		
			}			
			ir.set(self.id +"Table",header + htm + "</body>");
		},
		clear:function(){
			var self = pickGearPop;
			ir.set(self.id+"SearchInput","");
			self.searchArg = "";
			clearTimeout(self.searchTimeout);	
			ir.hide(self.id + "ClearSearchBtn");
			if(self.searchKeyup)
			{
				self.searchTimeout = window.setTimeout(function(){pickGearPop.searchKeyup(pickGearPop.searchArg)}, 100);	
			}	
		},
		close:function(){
			var self = pickGearPop;
			popup(self.id);
		},		
		filter:function(s)
		{
			var self = pickGearPop;
			if(self.searchArg.length<3)
			{
				return false;
			}
			if(s.Name.toLowerCase().indexOf(self.searchArg)>-1)
			{
				return false;
			}
			if(s.Type.toLowerCase().indexOf(self.searchArg)>-1)
			{
				return false;
			}
			if(s.SubType.toLowerCase().indexOf(self.searchArg)>-1)
			{
				return false;
			}
			return true;			
		},
		getRating:function(a)
		{
			if(a.MinRating != a. MaxRating)
			{
				return a.MinRating + " - "  + a. MaxRating;
			}
			else if (a.MaxRating > 1)
			{
				return a.MaxRating;
			}
			else
			{
				return "";
			}
		},
		keyup:function(e)
		{		
			var self = pickGearPop;
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
			var self = pickGearPop;
			self.buildTable();
		},
		select:function(row){
			var self = pickGearPop;
			var s = self.list.get(row);
			if(s!=null && self.callback!=null)
			{
				self.callback(s);
			}			
		},
		selectGear:function(){
			var self = pickGearPop;
			var list = self.list;
			if(list.values.length==0)
			{
				var callback = function(res){
					if(res.ok)
					{
						self.list.add(res.list.sort(function(a,b){
							if(a.Type < b.Type)
							{
								return -1;
							}
							else if(a.Type > b.Type)
							{
								return 1;
							}
							else
							{
								if(a.Name < b.Name)
							    {
							    	return -1;
							    }
							    else if (a.Name > b.Name)
							    {
							    	return 1;
							    }	
							    else
						    	{
							    	return 0;
						    	}
							}
						}));
						self.buildTable();
					}
				};
				sr5.ajaxAsync({fn:"selectGear"},callback);
			}
			else
			{
				self.buildTable();
			}			
		},
		show:function(callback){
			var self = pickGearPop;
			self.callback = callback;
			self.selectGear();
			var pop = ir.get(self.id);
			popup(pop);
			if(pop.classList.contains("show") && !sr5.isMobile)
			{
				ir.focus(self.id + "SearchInput");
			}
		},
		showDetail:function(row){
			var self = pickGearPop;
			descriptionPop.show(self.list.get(row));
			return false;
		},
		zz_pickGearPop:0
};