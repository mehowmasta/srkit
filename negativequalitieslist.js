"use strict";
var model={
	qualities:[],
	zz_model:0	
};
var view = {
		searchArg:"",
		aaOnLoad:function(){
			view.buildQualities();
			sr5.doneLoading();
		},
		applySearch:function(content){
			if(view.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(view.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildQualities:function()
		{
			var container = ir.get("qualityList");
			var arr = model.qualities;
			var htm = "";
			for (var i=0,z=arr.length;i<z;i++)
			{
				var s = arr[i];
				if(view.filterQuality(s))
				{
					continue;
				}
				htm += sr5.getSection(s,view.applySearch,view.searchArg.length>2);
			}
			container.innerHTML = htm;
		},	
		filterQuality:function(s)
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
			else if(s.Prerequisites.toLowerCase().indexOf(view.searchArg)>-1)
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
				view.buildQualities();
			}
			
		},
		toggleQuality:function(id)
		{
			var quality=ir.get("desc"+id);
			if(quality.classList.contains("show"))
			{
				quality.classList.remove("show");
			}
			else
			{
				quality.classList.add("show");
			}
		},
	zz_view:0
};