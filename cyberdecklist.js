"use strict";
var model={
	cyberdecks:[],
	zz_model:0	
};
var view = {
		searchArg:"",
		aaOnLoad:function(){
			view.buildCyberdecks();
			sr5.doneLoading();
		},
		applySearch:function(content){
			if(view.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(view.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildCyberdecks:function()
		{
			var container = ir.get("cyberdeckList");
			var arr = model.cyberdecks;
			var htm = "";
			for (var i=0,z=arr.length;i<z;i++)
			{
				var s = arr[i];
				if(view.filterCyberdeck(s))
				{
					continue;
				}
				htm += sr5.getSection(s,view.applySearch,view.searchArg.length>2);
			}
			container.innerHTML = htm + "</div>";
		},	
		filterCyberdeck:function(s)
		{
			if(view.searchArg.length<3)
			{
				return false;
			}
			if(s.Description.toLowerCase().indexOf(view.searchArg)>-1)
			{
				return false;
			}
			else if(s.Name.toLowerCase().indexOf(view.searchArg)>-1)
			{
				return false;
			}
			return true;			
		},
		searchKeyup:function(arg)
		{
			if(view.searchArg != arg)
			{
				view.searchArg = arg;
				view.buildCyberdecks();
			}
			
		},	
		toggleCyberdeck:function(id)
		{
			var w=ir.get("desc"+id);
			if(w.classList.contains("show"))
			{
				w.classList.remove("show");
			}
			else
			{
				w.classList.add("show");
			}
		},
	zz_view:0
};