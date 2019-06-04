"use strict";
var pickDronePop = {
		callback:null,
		id:"pickDronePop",
		characterList:new KeyedArray("Row"),
		list:new KeyedArray("Row"),
		searchArg:"",
		searchTimeout:null,
		droneMaxQuantity:99,
		afterChange:function(){
			var self = pickDronePop;
			if(self.callback!=null)
			{
				self.callback(self.characterList.values);
			}
		},
		applySearch:function(content){
			var self = pickDronePop;
			if(self.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(self.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildTable:function(){
			var self = pickDronePop;
			var list = self.list.values;
			var top = "";
			var bottom = "";
			var header = "<thead style='position:sticky;top:0;z-index:20;'><tr>"
						  + "<td class='tdl'>Drone</td>"
						  + "<td class='tdl'>Type</td>"
						  + "<td class='tdl'>Size</td>"
						  + "<td class='tdc'></td>"
						  + "</tr></thead>"
						  + "<tbody>";
			var rowTemplate = "<tr id='" + self.id + "Row{3}'>"
						  + "<td class='tdl clickable'  onclick='return pickDronePop.showDetail({3})'>{0}</td>"
						  + "<td class='tdl'>{1}</td>"
						  + "<td class='tdl'>{2}</td>"
						  + "<td class='tdc' style='white-space:nowrap;'>" 
						  + "<button id='" + self.id + "PlusBtn{3}' class='mini' type='button' onclick='"+self.id+".select({3})'>+</button>"
						  +	"</td>"
						  + "</tr>";
			for(var i =0, z=list.length;i<z;i++)
			{
				var a = list[i];
				if(self.filter(a))
				{
					continue;
				}
				var cl = self.characterList.get(a.Row);

				var characterQuantity = cl!=null?cl.Quantity:0;
				var	htm = ir.format(rowTemplate,
							self.applySearch(a.Name),
							self.applySearch(a.Type),
							self.applySearch(a.Size),
							a.Row);	
				if(cl!=null)
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
			var self = pickDronePop;
			ir.set(self.id+"SearchInput","");
			self.searchArg = "";
			clearTimeout(self.searchTimeout);	
			ir.hide(self.id + "ClearSearchBtn");
			if(self.searchKeyup)
			{
				self.searchTimeout = window.setTimeout(function(){pickDronePop.searchKeyup(pickDronePop.searchArg)}, 100);	
			}	
		},
		close:function(){
			var self = pickDronePop;
			popup(self.id);
		},
		
		filter:function(s)
		{
			var self = pickDronePop;
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
			return true;			
		},
		keyup:function(e)
		{		
			var self = pickDronePop;
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
			var self = pickDronePop;
			var q = self.list.get(row);
			var cl = self.characterList.get(row);
			if(cl!=null)
			{
				cl.Quantity--;
				if(cl.Quantity<=0)
				{
					cl.Quantity=0;
					self.characterList.remove(row);
					ir.get(self.id + "Row" + row).classList.remove("selected");
				}
				if(ir.exists(self.id + "CharacterQuantity" + row ))
				{
					ir.set(self.id + "CharacterQuantity" + row ,cl.Quantity);
				}
				ir.disable(self.id + "MinusBtn" + row,cl.Quantity<=0);
				ir.disable(self.id + "PlusBtn" + row,cl.Quantity>=self.droneMaxQuantity);				
			}
			self.afterChange();
		},
		plusOne:function(row){
			var self = pickDronePop;
			var q = self.list.get(row);
			var cl = self.characterList.get(row);
			if(cl==null)
			{
				cl = ir.copy(q);
				cl.Quantity = 1;
				self.characterList.add(cl);
				if(ir.exists(self.id + "CharacterQuantity" + row ))
				{
					ir.set(self.id + "CharacterQuantity" + row ,cl.Quantity);
				}
			}
			else if(cl.Quantity < self.droneMaxQuantity)
			{
				cl.Quantity++;
				if(ir.exists(self.id + "CharacterQuantity" + row ))
				{
					ir.set(self.id + "CharacterQuantity" + row ,cl.Quantity);
				}
			}
			ir.disable(self.id + "MinusBtn" + row,cl.Quantity<=0);
			ir.disable(self.id + "PlusBtn" + row,cl.Quantity>=self.droneMaxQuantity);
			ir.get(self.id + "Row"+ row).classList.add("selected");
			self.afterChange();
		},
		searchKeyup:function(arg){
			var self = pickDronePop;
			self.buildTable();
		},
		select:function(row){
			var self = pickDronePop;
			var s = self.list.get(row);
			if(s!=null && self.callback!=null)
			{
				s.Quantity=1;
				self.callback(s);
			}
			
		},
		selectDrones:function(){
			var self = pickDronePop;
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
				sr5.ajaxAsync({fn:"selectDrones"},callback);
			}
			else
			{
				self.buildTable();
			}
			
		},
		show:function(){
			var self = pickDronePop;
			self.selectDrones();
			var pop = ir.get(self.id);
			popup(pop);
			if(pop.classList.contains("show") && !sr5.isMobile)
			{
				ir.focus(self.id + "SearchInput");
			}
		},
		showDetail:function(row){
			var self = pickDronePop;
			descriptionPop.show(self.list.get(row));
			return false;
		},
		zz_pickDronePop:0
};