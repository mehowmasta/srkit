var searchPop = {
	id:"searchPop",
	searchArg:null,
	searchTimeout:null,
	clear:function(){
		var self = searchPop;
		ir.set("searchInput","");
		searchPop.searchArg = "";
		clearTimeout(searchPop.searchTimeout);
		if(view.searchKeyup)
		{
			searchPop.searchTimeout = window.setTimeout(function(){view.searchKeyup(searchPop.searchArg)}, 100);	
		}	
		ir.hide("clearSearchBtn");
	},
	close:function(){
		var self = searchPop;
		ir.get(self.id).classList.remove("show");
	},
	keyup:function(e)
	{		
		if (typeof e == 'undefined' && window.event)
		{
			e = window.event;
		}
		if (e == null)
		{
			ir.log("Cannot process key press codes!!!");
			return;
		}
		clearTimeout(searchPop.searchTimeout);
		if (e.keyCode!=13)//enter
		{
			searchPop.searchArg = ir.ltrim(ir.v("searchInput"));
			searchPop.searchArg = ir.rtrim(searchPop.searchArg).toLowerCase();
			ir.show("clearSearchBtn",searchPop.searchArg.length>0);
			if(view.searchKeyup)
			{
				searchPop.searchTimeout = window.setTimeout(function(){view.searchKeyup(searchPop.searchArg)}, 800);	
			}					
		}		
	},
	show:function(){
		var self = searchPop;
		var pop = ir.get(self.id);
		if(pop.classList.contains("show"))
		{
			self.close();
		}
		else
		{
			pop.classList.add("show");
			ir.focus("searchInput");
		}
	},
	zz_searchPop:0
		
};