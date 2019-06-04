var journalPop = {
		blankEntry:null,
		currentEntry:null,
		journalData:null,
		id:"journalPop",
		buildJournalEntries:function(){
			var self = journalPop;
			var htm = "";
			var template = "<div id='journalListEntry{0}' onclick='return journalPop.showEntry({0})' class='journalEntry flex'>" 
						 + "<div class='threadUsers'>{1}</div>"
						 + "<div class='threadDate'>{2}</div>"
						 + "<div class='spacer'></div>"
						 + "<div>{3}</div>"
						 + "</div>";
			var entries = self.journalData.values;
			for(var i =0,z=entries.length;i<z;i++)
			{
				var entry = entries[i];
				htm += ir.format(template,entry.Row,entry.Title,irdate.friendly(entry.Time,true),ir.ellipsis(entry.Text,200));
			}
			ir.set(self.id+"List",htm);
		},
		cancelEntry:function(){
			var self = journalPop;
			self.toggleEdit(false);
		},
		close:function(){
			var self = journalPop;
			popup(self.id);
		},
		deleteEntry:function(){
			var self = journalPop;
			var c = self.currentEntry;
			c.deleteEntry = true;
		},
		init:function(){
			var self = journalPop;
			
		},
		newEntry:function(){
			var self = journalPop;
			self.toggleEdit(true);
		},
		readEntry:function(){
			var self = journalPop;
			var c = self.currentEntry;
			if(c.deleteEntry)
			{
				return true;
			}
			var hasError = false;
			c.Title = ir.escapeHtml(ir.v(self.id+"Title"));
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
			c.Time = ir.v(self.id+"Time");
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
					}
				};
				sr5.ajaxAsync({fn:"selectJournalData",row:sr5.user.Row},callback);
			}
		},
		show:function(){
			var self = journalPop;
			self.selectJournals();
			popup(self.id);
		},
		showEntry:function(){
			
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
			}
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
					
					}
				};
				sr5.ajaxAsync(c,callback);
			}
		},
		zz_journalPop:0
}