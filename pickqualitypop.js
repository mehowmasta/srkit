"use strict";
var pickQualityPop = {
		callback:null,
		characterRow:0,
		currentList:null,
		id:"pickQualityPop",
		list:new KeyedArray("Row"),
		searchArg:"",
		searchTimeout:null,
		tempItemRow:-1,
		afterUpdate:function(res){
			var self = pickQualityPop;
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
				Status.info("Qualities have been updated.",5000);
			}
		},
		applySearch:function(content){
			var self = pickQualityPop;
			if(self.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(self.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildTable:function(){
			var self = pickQualityPop;
			var list = self.list.values;
			var top = "";
			var bottom = "";
			var header = "<thead style='position:sticky;top:0;z-index:20;'><tr>"
						  + "<td class='tdl'>Quality</td>"
						  + "<td class='tdc'>Karma</td>"
						  + "<td class='tdc'>P/N</td>"
						  + "<td class='tdc'></td>"
						  + "<td class='tdc'>Rating</td>"
						  + "</tr></thead>"
						  + "<tbody>";
			var rowTemplate = "<tr id='" + self.id + "QualityRow{4}' class='{8}'>"
						  + "<td class='tdl clickable'  onclick='return pickQualityPop.showDetail({4})'>{0}</td>"
						  + "<td class='tdc'>{1}</td>"
						  + "<td class='tdc'>{2}</td>"
						  + "<td class='tdc' style='white-space:nowrap;'>" 
						  + "<button id='" + self.id + "MinusBtn{4}' class='mini' type='button' {6} onclick='"+self.id+".minusOne({4})'>-</button>"
						  + "<button id='" + self.id + "PlusBtn{4}' class='mini' type='button' {7} onclick='"+self.id+".plusOne({4})'>+</button>"
						  +	"</td>"
						  + "<td id='" + self.id + "CharacterRating{4}' class='tdc'>{5}</td>"
						  + "</tr>";
			for(var i =0, z=list.length;i<z;i++)
			{
				var a = list[i];
				if(self.filter(a))
				{
					continue;
				}
				var cl = self.getCharacterItem(a.Row);
				var characterRating = cl!=null?cl.Rating:0;
				var htm = ir.format(rowTemplate,
							self.applySearch(a.Name),
							a.Karma,
							a.Type.substring(0,1),
							a.MaxRating,
							a.Row,
							characterRating + " / "+a.MaxRating,
							cl==null || cl.Delete?"disabled":"",
							cl!=null && !cl.Delete && cl.Rating>=a.MaxRating?"disabled":"",
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
			var self = pickQualityPop;
			ir.set(self.id+"SearchInput","");
			self.searchArg = "";
			clearTimeout(self.searchTimeout);	
			ir.hide(self.id + "ClearSearchBtn");
			if(self.searchKeyup)
			{
				self.searchTimeout = window.setTimeout(function(){pickQualityPop.searchKeyup(pickQualityPop.searchArg)}, 100);	
			}	
		},
		close:function(){
			var self = pickQualityPop;
			self.currentList=null;
			self.callback=null;
			popup(self.id);
		},		
		filter:function(s)
		{
			var self = pickQualityPop;
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
		 * Qualities are Unique a character cannot have duplicates
		 */
		getCharacterItem:function(row){
			var self = pickQualityPop;
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
			var self = pickQualityPop;
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
			var self = pickQualityPop;
			var q = self.list.get(row);
			var cl = self.getCharacterItem(row);
			if(cl!=null)
			{
				cl.Rating--;
				if(cl.Rating<=0)
				{
					cl.Rating=0;
					cl.Delete=true;
					ir.get(self.id + "QualityRow" + row).classList.remove("selected");
				}
				if(ir.exists(self.id + "CharacterRating" + row ))
				{
					ir.set(self.id + "CharacterRating" + row ,cl.Rating + " / "+q.MaxRating);
				}
				ir.disable(self.id + "MinusBtn" + row,cl.Rating<=0);
				ir.disable(self.id + "PlusBtn" + row,cl.Rating>=q.MaxRating);				
			}
		},
		plusOne:function(row){
			var self = pickQualityPop;
			var q = self.list.get(row);
			var cl = self.getCharacterItem(row);
			if(cl==null)
			{
				cl = ir.copy(q);
				cl.Rating = 1;
				cl.Delete = false;
				cl.CharacterRow = self.characterRow;
				cl.ItemRow = self.tempItemRow--;
				self.currentList.add(cl);
				if(ir.exists(self.id + "CharacterRating" + row ))
				{
					ir.set(self.id + "CharacterRating" + row ,cl.Rating+ " / "+q.MaxRating);
				}
			}
			else if(cl.Rating < q.MaxRating)
			{
				cl.Rating++;
				cl.Delete = false;
				if(ir.exists(self.id + "CharacterRating" + row ))
				{
					ir.set(self.id + "CharacterRating" + row ,cl.Rating+ " / "+q.MaxRating);
				}
			}
			ir.disable(self.id + "MinusBtn" + row,cl.Rating<=0);
			ir.disable(self.id + "PlusBtn" + row,cl.Rating>=q.MaxRating);
			ir.get(self.id + "QualityRow"+ row).classList.add("selected");
		},
		searchKeyup:function(arg){
			var self = pickQualityPop;
			self.buildTable();
		},
		selectQualities:function(){
			var self = pickQualityPop;
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
				sr5.ajaxAsync({fn:"selectQualities"},callback);
			}
			else
			{
				self.buildTable();
			}
			
		},
		show:function(currentList){
			var self = pickQualityPop;
			self.currentList = new KeyedArray("ItemRow",ir.copy(currentList));
			self.selectQualities();
			var pop = ir.get(self.id);
			popup(pop);
			if(pop.classList.contains("show") && !sr5.isMobile)
			{
				ir.focus(self.id + "SearchInput");
			}
		},
		showDetail:function(row){
			var self = pickQualityPop;
			descriptionPop.show(self.list.get(row));
			return false;
		},
		update:function(){
			var self = pickQualityPop;			
			sr5.updateCharacterQuality(self.characterRow,self.currentList.values,self.afterUpdate);
		},
		zz_pickQualityPop:0
};