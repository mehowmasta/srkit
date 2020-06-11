"use strict";
var pickProgramPop = {
		callback:null,
		characterRow:0,
		id:"pickProgramPop",
		programList:null,
		list:new KeyedArray("Row"),
		searchArg:"",
		searchTimeout:null,
		programMaxQuantity:0,
		tempItemRow:-1,
		applySearch:function(content){
			var self = pickProgramPop;
			if(self.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(self.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildTable:function(){
			var self = pickProgramPop;
			var list = self.list.values;
			var htm = "";
			var top = "";
			var bottom = "";
			var header = "<thead style='position:sticky;top:0;z-index:20;'><tr>"
						  + "<td class='tdl' style='width:80%;'>Name</td>"
						  + "<td class='tdl' style='width:30%;'>Type</td>"
						  + "<td class='tdc'></td>"
						  + "</tr></thead>"
						  + "<tbody>";
			var rowTemplate = "<tr id='" + self.id + "Row{2}' class='{5}'>"
						  + "<td class='tdl clickable'  onclick='return pickProgramPop.showDetail({2})'>{0}</td>"
						  + "<td class='tdl'>{1}</td>"
						  + "<td class='tdc' style='white-space:nowrap;'>" 
						  + "<button id='" + self.id + "MinusBtn{2}' class='mini' type='button' {3} onclick='"+self.id+".minusOne({2})'>-</button>"
						  + "<button id='" + self.id + "PlusBtn{2}' class='programPlusBtn mini' {4} type='button' onclick='"+self.id+".plusOne({2})'>+</button>"
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
						self.applySearch(a.Type),
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
			self.checkProgramQuantity();
		},
		checkProgramQuantity:function(){
			var self = pickProgramPop;
			if(self.programMaxQuantity>0)
			{
				var btns = document.getElementsByClassName("programPlusBtn");
				if(self.getSelectedCount() >= self.programMaxQuantity)
				{//disable all plus buttons
					for(var i=0,z=btns.length;i<z;i++)
					{
						ir.disable(btns[i]);
					}
				}
				else
				{//enable all plus buttons except the ones that have been selected already
					for(var i=0,z=btns.length;i<z;i++)
					{
						var row = btns[i].id.substring((self.id+"PlusBtn").length,btns[i].id.length);
						var pl = self.getCharacterItem(row);
						ir.disable(btns[i],pl!=null && !pl.Delete);
					}
				}
			}
			
		},
		clear:function(){
			var self = pickProgramPop;
			ir.set(self.id+"SearchInput","");
			self.searchArg = "";
			clearTimeout(self.searchTimeout);	
			ir.hide(self.id + "ClearSearchBtn");
			if(self.searchKeyup)
			{
				self.searchTimeout = window.setTimeout(function(){pickProgramPop.searchKeyup(pickProgramPop.searchArg)}, 100);	
			}	
		},
		close:function(){
			var self = pickProgramPop;
			popup(self.id);
		},
		
		filter:function(s)
		{
			var self = pickProgramPop;
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
		/**
		 * Programs are Unique to a cyberdeck
		 */
		getCharacterItem:function(row){
			var self = pickProgramPop;
			var array = self.programList.values;
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
		getSelectedCount:function(){
			var self = pickProgramPop;
			var array = self.programList.values;
			var count = 0;
			for(var i=0,z=array.length;i<z;i++)
			{
				var a = array[i];
				if(!a.Delete)
				{
					count++;
				}
			}
			return count;
		},
		keyup:function(e)
		{		
			var self = pickProgramPop;
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
			var self = pickProgramPop;
			var q = self.list.get(row);
			var cl = self.getCharacterItem(row);
			if(cl!=null)
			{
				cl.Delete=true;
				ir.get(self.id + "Row" + row).classList.remove("selected");
				ir.disable(self.id + "MinusBtn" + row,true);
				ir.disable(self.id + "PlusBtn" + row,false);				
			}
			self.checkProgramQuantity();
		},
		plusOne:function(row){
			var self = pickProgramPop;
			var q = self.list.get(row);
			var cl = self.getCharacterItem(row);
			if(cl==null)
			{
				cl = ir.copy(q);
				cl.CharacterRow = self.characterRow;
				cl.Delete = false;
				cl.ItemRow = self.tempItemRow--;
				self.programList.add(cl);
			}
			else
			{
				cl.Delete = false;
			}
			ir.disable(self.id + "MinusBtn" + row,false);
			ir.disable(self.id + "PlusBtn" + row,true);
			ir.get(self.id + "Row"+ row).classList.add("selected");
			self.checkProgramQuantity();
		},
		searchKeyup:function(arg){
			var self = pickProgramPop;
			self.buildTable();
		},
		selectPrograms:function(){
			var self = pickProgramPop;
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
				sr5.ajaxAsync({fn:"selectPrograms"},callback);
			}
			else
			{
				self.buildTable();
			}
			
		},
		show:function(parentRecord,programList,callback){
			var self = pickProgramPop;
			self.callback=callback;
			self.programList=new KeyedArray("ItemRow",ir.copy(programList));
			self.parentRecord = parentRecord;
			if(self.parentRecord != null)
			{
				self.programMaxQuantity = self.parentRecord.Program;
			}
			self.selectPrograms();
			var pop = ir.get(self.id);
			popup(pop);
			if(pop.classList.contains("show") && !sr5.isMobile)
			{
				ir.focus(self.id + "SearchInput");
			}
		},
		showDetail:function(row){
			var self = pickProgramPop;
			descriptionPop.show(self.list.get(row));
			return false;
		},
		update:function(){
			var self = pickProgramPop;
			if(self.callback!=null)
			{
				self.callback(self.programList.values);
			}
			self.close();
		},
		zz_pickProgramPop:0
};