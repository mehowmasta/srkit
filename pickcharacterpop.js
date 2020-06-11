"use strict";
var pickCharacterPop = {
		allowMulti:true,
		callback:null,
		id:"pickCharacterPop",
		currentList:new KeyedArray("Row"),
		list:new KeyedArray("Row"),
		searchArg:"",
		searchTimeout:null,
		characterMaxQuantity:99,
		applySearch:function(content){
			var self = pickCharacterPop;
			if(self.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(self.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildTable:function(){
			var self = pickCharacterPop;
			var list = self.list.values;
			var top = "";
			var bottom = "";
			var rowTemplate = "<tr id='" + self.id + "Row{2}' class='{6}'>"
						  + "<td class='tdl'><div style='position:relative;margin-left:1.7rem;' class='{7}'>{0}</div></td>"
						  + "<td class='tdl' style='width:99%'>{1}</td>"
						  + "<td class='tdc' style='white-space:nowrap;'>" 
						  + "<button id='" + self.id + "MinusBtn{2}' type='button' {4} class='mini' onclick='"+self.id+".minusOne({2})'>-</button>"
						  + "<button id='" + self.id + "PlusBtn{2}' type='button' {5} class='mini' onclick='"+self.id+".plusOne({2})'>+</button>"
						  +	"</td>"
						  + "<td id='" + self.id + "CharacterQuantity{2}' class='tdc' style='min-width:4rem;'>{3}</td>"
						  + "</tr>";
			for(var i =0, z=list.length;i<z;i++)
			{
				var a = list[i];
				if(self.filter(a))
				{
					continue;
				}
				var htm = "";
				var metatype = sr5.getMetaTypeName(a.Metatype);
				var sex = sr5.sex.get(a.Sex).Name;
				var cl = self.currentList.get(a.Row);
				if(cl!=null)
				{
					if(!cl.Quantity)
					{
						cl.Quantity=0;
					}
				}
				var characterQuantity = cl!=null?cl.Quantity:0;
				htm += ir.format(rowTemplate,
						self.getPortrait(a),
						"<span class='title'><b>"+self.applySearch(a.Name)+"</b></span><br>"
						+"<span class='subtitle'><b>" + sex + " " +self.applySearch(metatype)+"</b></span><br>"
						+ "<span style='text-transform:uppercase;'><i  style='font-size:1rem;'>"+a.Misc+"</i></span>",
						a.Row,
						characterQuantity,
						cl==null?"disabled":"",
						"",
						cl!=null?"selected":"",
						a.Type.toLowerCase());					
				if(cl!=null)
				{
					top += htm;
				}
				else
				{
					bottom += htm;					
				}
			}			
			ir.set(self.id +"Table","<tbody style='display:inline-table;'>" + top + bottom + "</tbody>");
		},
		clear:function(){
			var self = pickCharacterPop;
			ir.set(self.id+"SearchInput","");
			self.searchArg = "";
			clearTimeout(self.searchTimeout);	
			ir.hide(self.id + "ClearSearchBtn");
			if(self.searchKeyup)
			{
				self.searchTimeout = window.setTimeout(function(){pickCharacterPop.searchKeyup(pickCharacterPop.searchArg)}, 100);	
			}	
		},
		close:function(){
			var self = pickCharacterPop;
			self.currentList = null;
			popup(self.id);
		},
		combineLists:function(){
			var self = pickCharacterPop;
			var current = self.currentList.values;
			for(var i =0, z= current.length;i<z;i++)
			{
				var a = current[i];
				self.list.set(a);
			}
		},
		filter:function(s)
		{
			var self = pickCharacterPop;
			var metatype = sr5.getMetaTypeName(s.Metatype);
			var cl = self.currentList.get(s.Row);
			if(!isTrue(ir.v(self.id +"Filter"+s.Type)) && cl==null)
			{
				return true;
			}
			if(self.searchArg.length<3)
			{
				return false;
			}
			if(s.Name.toLowerCase().indexOf(self.searchArg)>-1)
			{
				return false;
			}
			if(metatype.toLowerCase().indexOf(self.searchArg)>-1)
			{
				return false;
			}
			return true;			
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
				return sr5.getThumbUnknown(false);
			}
		},
		keyup:function(e)
		{		
			var self = pickCharacterPop;
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
			var self = pickCharacterPop;
			var q = self.list.get(row);
			var cl = self.currentList.get(row);
			if(cl!=null)
			{
				cl.Quantity--;
				if(cl.Quantity<=0)
				{
					cl.Quantity=0;
					self.currentList.remove(row);
					ir.get(self.id + "Row" + row).classList.remove("selected");
				}
				if(ir.exists(self.id + "CharacterQuantity" + row ))
				{
					ir.set(self.id + "CharacterQuantity" + row ,cl.Quantity);
				}
				ir.disable(self.id + "MinusBtn" + row,cl.Quantity<=0);
				//ir.disable(self.id + "PlusBtn" + row,cl.Quantity>0);			
			}
		},
		plusOne:function(row){
			var self = pickCharacterPop;
			var q = self.list.get(row);
			var cl = self.currentList.get(row);
			if(cl==null)
			{
				cl = ir.copy(q);
				cl.Quantity = 1;
				self.currentList.add(cl);
				if(ir.exists(self.id + "CharacterQuantity" + row ))
				{
					ir.set(self.id + "CharacterQuantity" + row ,cl.Quantity);
				}
			}
			else
			{
				cl.Quantity++;
				self.currentList.add(cl);
				ir.set(self.id + "CharacterQuantity" + row ,cl.Quantity);
			}
			ir.disable(self.id + "MinusBtn" + row,cl.Quantity<=0);
			//ir.disable(self.id + "PlusBtn" + row,cl.Quantity>0);
			ir.get(self.id + "Row"+ row).classList.add("selected");
		},
		searchKeyup:function(arg){
			var self = pickCharacterPop;
			self.buildTable();
		},
		selectCharacters:function(){
			var self = pickCharacterPop;
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
						self.combineLists();
						self.buildTable();
					}
				};
				sr5.ajaxAsync({fn:"selectCharactersForPicker"},callback);
			}
			else
			{
				self.buildTable();
			}
			
		},
		setFilter:function(type)
		{
			var self = pickCharacterPop;
			var filters = document.getElementsByClassName(self.id + "Filter");
			for(var i=0,z=filters.length;i<z;i++)
			{
				var a = filters[i];
				var id = a.id.substring((self.id+"Filter").length,a.id.length);
				if(id.toUpperCase() === type.toUpperCase())
				{
					a.checked=true;
				}
				else
				{
					a.checked=false;
				}
			}
		},
		show:function(currentList,showBlank,callback){
			var self = pickCharacterPop;
			self.currentList= new KeyedArray("Row",currentList);
			self.showBlank = showBlank;
			self.callback=callback;
			self.selectCharacters();
			var pop = ir.get(self.id);
			popup(pop);
			if(pop.classList.contains("show") && !sr5.isMobile)
			{
				ir.focus(self.id + "SearchInput");
			}
		},
		update:function(){
			var self = pickCharacterPop;
			if(self.callback!=null)
			{
				self.callback(self.currentList.values);
			}
			self.close();
		},
		zz_pickCharacterPop:0
};