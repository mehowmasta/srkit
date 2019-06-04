"use strict";
var pickCyberdeckPop = {
		callback:null,
		id:"pickCyberdeckPop",
		characterList:new KeyedArray("Row"),
		list:new KeyedArray("Row"),
		searchArg:"",
		searchTimeout:null,
		cyberdeckMaxQuantity:99,
		afterChange:function(){
			var self = pickCyberdeckPop;
			if(self.callback!=null)
			{
				self.callback(self.characterList.values);
			}
		},
		applySearch:function(content){
			var self = pickCyberdeckPop;
			if(self.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(self.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildTable:function(){
			var self = pickCyberdeckPop;
			var list = self.list.values;
			var htm = "";
			var header = "<thead style='position:sticky;top:0;z-index:20;'><tr>"
						  + "<td class='tdl'>Cyberdeck</td>"
						  + "<td class='tdc'>Device Rating</td>"
						  + "<td class='tdc'>ASDF</td>"
						  + "<td class='tdc'>Program</td>"
						  + "<td class='tdc'></td>"
						  + "</tr></thead>"
						  + "<tbody>";
			var rowTemplate = "<tr id='" + self.id + "Row{4}'>"
						  + "<td class='tdl clickable'  onclick='return pickCyberdeckPop.showDetail({4})'>{0}</td>"
						  + "<td class='tdc'>{1}</td>"
						  + "<td class='tdc' style='white-space:nowrap;'>{2}</td>"
						  + "<td class='tdc'>{3}</td>"
						  + "<td class='tdc' style='white-space:nowrap;'>" 
						  + "<button id='" + self.id + "PlusBtn{4}' class='mini' type='button' onclick='"+self.id+".select({4})'>+</button>"
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
						a.DeviceRating,
						a.AttributeArray,
						a.Program,
						a.Row);	
			}			
			ir.set(self.id +"Table",header + htm + "</body>");
		},
		clear:function(){
			var self = pickCyberdeckPop;
			ir.set(self.id+"SearchInput","");
			self.searchArg = "";
			clearTimeout(self.searchTimeout);	
			ir.hide(self.id + "ClearSearchBtn");
			if(self.searchKeyup)
			{
				self.searchTimeout = window.setTimeout(function(){pickCyberdeckPop.searchKeyup(pickCyberdeckPop.searchArg)}, 100);	
			}	
		},
		close:function(){
			var self = pickCyberdeckPop;
			popup(self.id);
		},
		
		filter:function(s)
		{
			var self = pickCyberdeckPop;
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
			var self = pickCyberdeckPop;
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
			var self = pickCyberdeckPop;
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
				ir.disable(self.id + "PlusBtn" + row,cl.Quantity>=self.cyberdeckMaxQuantity);				
			}
			self.afterChange();
		},
		plusOne:function(row){
			var self = pickCyberdeckPop;
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
			else if(cl.Quantity < self.cyberdeckMaxQuantity)
			{
				cl.Quantity++;
				if(ir.exists(self.id + "CharacterQuantity" + row ))
				{
					ir.set(self.id + "CharacterQuantity" + row ,cl.Quantity);
				}
			}
			ir.disable(self.id + "MinusBtn" + row,cl.Quantity<=0);
			ir.disable(self.id + "PlusBtn" + row,cl.Quantity>=self.cyberdeckMaxQuantity);
			ir.get(self.id + "Row"+ row).classList.add("selected");
			self.afterChange();
		},
		searchKeyup:function(arg){
			var self = pickCyberdeckPop;
			self.buildTable();
		},
		select:function(row){
			var self = pickCyberdeckPop;
			var s = self.list.get(row);
			if(s!=null && self.callback!=null)
			{
				s.Programs=new KeyedArray("ItemRow");
				self.callback(s);
			}
		},
		selectCyberdecks:function(){
			var self = pickCyberdeckPop;
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
				sr5.ajaxAsync({fn:"selectCyberdecks"},callback);
			}
			else
			{
				self.buildTable();
			}
			
		},
		show:function(){
			var self = pickCyberdeckPop;
			self.selectCyberdecks();
			var pop = ir.get(self.id);
			popup(pop);
			if(pop.classList.contains("show") && !sr5.isMobile)
			{
				ir.focus(self.id + "SearchInput");
			}
		},
		showDetail:function(row){
			var self = pickCyberdeckPop;
			descriptionPop.show(self.list.get(row));
			return false;
		},
		zz_pickCyberdeckPop:0
};