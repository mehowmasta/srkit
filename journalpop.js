var journalPop = {
		blankEntry:null,
		currentEntry:null,
		journalData:null,
		titles:{},
		types:{},
		id:"journalPop",
		addCharacter:function(){
			var self = journalPop;
			if(pickCharacterPop)
			{
				pickCharacterPop.show([],false,journalPop.afterAddCharacter);				
			}
		},
		afterAddCharacter:function(character)
		{
			var self = journalPop;
			var container = ir.get("")
		},
		buildJournalEntries:function(){
			var self = journalPop;
			var htm = "";
			var archived = "";
			var prevCategory="";
			var template = "<div id='journalListEntry{0}' onclick='return journalPop.pickEntry({0})' class='journalEntry flex {4}'>" 
						 + "<div class='threadUsers'>{1}</div>"
						 + "<div class='threadUsers'>{2}</div>"
						 + "<div class='spacer'></div>"
						 + "<div class='threadMessage' style=''>{3}</div>"
						 + "</div>";
			var entries = self.journalData.values;
			for(var i =0,z=entries.length;i<z;i++)
			{
				var entry = entries[i];
				if(entry.deleteEntry)
				{
					continue;
				}
				if(prevCategory != entry[self.sortType[0].name].toUpperCase().trim() && self.sortType[0].name != "Time")
				{
					htm += "<div class='subtitle'>"+entry[self.sortType[0].name]+" " +self.sortType[0].suffix + "</div>";
					prevCategory = entry[self.sortType[0].name].toUpperCase().trim();
				}
				htm += ir.format(template
						,entry.Row
						,self.sortType[0].name=="Title"?entry.Type:entry.Title
						,irdate.friendly(entry.Time,true)
						,ir.ellipsis(entry.Text,60)
						,entry.Archive?"archived":"");
				self.titles[entry.Title.trim()]=1;
				self.types[entry.Type.trim()]=1;
			}
			ir.set(self.id+"List",htm + archived);
			self.buildTitleDataList();
			self.buildTypeDataList();
		},
		buildTitleDataList:function(){
			var self = journalPop;
			var list = ir.get(self.id + "TitleDataList");
			list.innerHTML = "";
			for (var attr in self.titles)
			{
				list.innerHTML += "<option>"+attr+"</option>";
			}
		},
		buildTypeDataList:function(){
			var self = journalPop;
			var list = ir.get(self.id + "TypeDataList");
			list.innerHTML = "";
			for (var attr in self.types)
			{
				list.innerHTML += "<option>"+attr+"</option>";
			}
		},
		cancelEntry:function(){
			var self = journalPop;
			self.toggleEdit(false);
		},
		changeSort:function(){
			var self = journalPop;
			var arr = self.sortType;
			arr.push(arr.shift());
			self.sortList();
			self.buildJournalEntries();
			Status.info("Sorting journal entries by " + self.sortType[0].name,4000);
		},
		close:function(){
			var self = journalPop;
			self.closeList();
			popup(self.id);
		},
		closeList:function(){
			var self = journalPop;
			var wrap = ir.get(self.id+"ListWrap");
			wrap.classList.remove("open");
		},
		deleteEntry:function(){
			var self = journalPop;
			var c = self.currentEntry;
			var callback = function(yes){
				if(yes)
				{
					c.deleteEntry = true;
					self.updateEntry();
				}
			};
			confirmPop.show("Are you sure you want to delete <i>" +irdate.friendly(c.Time,true) + " " + c.Type + ": " + c.Title + "</i>?",callback)
		},
		editEntry:function(){
			var self = journalPop;
			self.toggleEdit(true);
		},
		init:function(){
			var self = journalPop;
			
		},
		nextEntry:function(){
			var self = journalPop;
			if(self.currentIndex>=self.journalData.size()-1)
			{
				self.currentIndex = self.journalData.size()-1;
				return;				
			}
			var entry = self.journalData.getAt(self.currentIndex+1);
			self.showEntry(entry);
			return;
		},
		newEntry:function(){
			var self = journalPop;
			self.currentEntry = ir.copy(self.blankEntry);
			self.currentEntry.Time = new Date();
			self.setValues(self.currentEntry);
			self.toggleEdit(true);
			self.closeList();
		},
		pickEntry:function(journalRow){
			var self = journalPop;
			self.showEntry(self.journalData.get(journalRow));
			self.closeList();
		},
		prevEntry:function(){
			var self = journalPop;
			if(self.currentIndex <= 0)
			{
				self.currentIndex = 0;
				return;				
			}
			var entry = self.journalData.getAt(self.currentIndex-1);
			self.showEntry(entry);
			return;
		},
		readEntry:function(){
			var self = journalPop;
			var c = self.currentEntry;
			if(c.deleteEntry)
			{
				return true;
			}
			var hasError = false;
			c.Title = ir.escapeHtml(ir.v(self.id+"TitleText"));
			if(c.Title.length==0)
			{
				Status.error("Title is required.");
				hasError = true;
			}
			c.Text = ir.escapeHtml(ir.v(self.id+"Text"));
			if(c.Text.length==0)
			{
				Status.error("Text is required.");
				hasError = true;
			}
			c.Type = ir.escapeHtml(ir.v(self.id+"Type"));
			if(c.Type.length==0)
			{
				Status.error("Type of entry is required.");
				hasError = true;
			}
			c.Time = ir.v(self.id+"Time");
			c.Archive = ir.v(self.id+"Archive");
			return !hasError;
		},
		selectJournals:function(){
			var self = journalPop;
			if(self.journalData==null)
			{
				var callback=function(res){
					if(res.ok)
					{
						self.journalData= new KeyedArray("Row",res.data);
						self.buildJournalEntries();
						if(self.journalData.size()>0)
						{
							self.showEntry(self.journalData.getAt(0));						
						}
						else
						{
							self.newEntry();
						}
					}
				};
				sr5.ajaxAsync({fn:"selectJournalData",row:sr5.user.Row},callback);
			}
		},
		setIndex:function(index){
			var self = journalPop;
			self.currentIndex = index;
			ir.disable(self.id + "NextBtn",self.currentIndex>=self.journalData.size()-1);
			ir.disable(self.id + "PrevBtn",self.currentIndex<=0);
		},
		setValues:function(entry){
			var self = journalPop;
			var template = "<div class='journalHeader flex'>"
						 + "<div class='journalType'><i>{0}</i></div>"
						 + "<div class='journalTime'>{1}</div>"
						 + "<div class='journalTitle'>{2}</div>"
						 + "</div>"
						 + "<p id='journalEntryText' class='journalText'>{3}</p>";
			ir.set(self.id+"TitleText",ir.unescapeHtml(entry.Title) );
			ir.set(self.id+"Text",ir.unescapeHtml(entry.Text));
			ir.set(self.id+"Time",formatDate(entry.Time,irdate.formatTime));
			ir.set(self.id+"Type",ir.unescapeHtml(entry.Type));
			ir.set(self.id+"Archive",entry.Archive);
			ir.set(self.id+"EntryWrap",ir.format(template
					,entry.Type
					,irdate.friendly(entry.Time,true)
					,entry.Title + (entry.Archive?"<div class='archived' style='float:right;'>Archived</div>":"")
					,entry.Text));
			//sr5.type("journalEntryText",entry.Text);
		},
		show:function(){
			var self = journalPop;
			self.selectJournals();
			popup(self.id);
		},
		showEntry:function(journalEntry){
			var self = journalPop;
			self.setIndex(self.journalData.indexOf(journalEntry.Row));
			self.currentEntry = journalEntry;
			self.setValues(self.currentEntry);
			self.toggleEdit(false);
		},
		sortList:function(){
			var self = journalPop;
			self.journalData.sortInPlace(function(a,b){
				if(self.sortType[0].name!=="Time")
				{
					if(a[self.sortType[0].name].toUpperCase().trim() < b[self.sortType[0].name].toUpperCase().trim())
					{
						return -1;					
					}
				    if(a[self.sortType[0].name].toUpperCase().trim() > b[self.sortType[0].name].toUpperCase().trim())
			    	{
				    	return 1;
			    	}
				}	
			    if(new Date(a.Time) < new Date(b.Time))
			    {
			    	return 1;
			    }
			    else
			    {
			    	return -1;
			    }	
			    return 0;
			});
			self.setIndex(self.journalData.indexOf(self.currentEntry.Row));
		},	
		toggleEdit:function(showEdit){
			var self = journalPop;
			if(showEdit)
			{
				ir.get(self.id + "Entry").classList.remove("open");
				ir.get(self.id + "EntryEdit").classList.add("open");
			}
			else
			{
				ir.get(self.id + "EntryEdit").classList.remove("open");
				ir.get(self.id + "Entry").classList.add("open");
				if(self.currentEntry.Row==0 && self.journalData.size()>0)
				{
					self.showEntry(self.journalData.getAt(0));
				}
			}
			ir.show(self.id + "DeleteBtn",self.currentEntry.Row>0);
			ir.show(self.id + "EditBtn",self.currentEntry.Row>0);
			ir.show(self.id + "SortBtn",self.journalData.size()>1);
		},
		toggleList:function(){
			var self = journalPop;
			var wrap = ir.get(self.id+"ListWrap");
			if(wrap.classList.contains("open"))
			{
				wrap.classList.remove("open");
			}
			else
			{
				wrap.classList.add("open");
			}
			ir.show(self.id + "SortBtn",self.journalData.size()>1);
		},
		updateEntry:function(){
			var self = journalPop;
			if(self.readEntry())
			{
				var c = self.currentEntry;
				c.fn = "updateJournal";
				var callback = function(res){
					if(res.ok)
					{
						self.journalData.set(res.journal);
						self.sortList();
						self.buildJournalEntries();
						if(res.journal.deleteEntry)
						{
							self.journalData.remove(res.journal.Row);
							var entry = self.journalData.getAt(0);
							if(entry==null)
							{
								self.newEntry();							
							}
							else
							{
								self.pickEntry(entry.Row);	
							}
							
						}
						else
						{
							self.pickEntry(res.journal.Row);						
						}
					}
				};
				sr5.ajaxAsync(c,callback);
			}
		},
		zz_journalPop:0
}