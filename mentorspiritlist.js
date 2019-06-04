"use strict";
var model={
	spirits:[],
	zz_model:0	
};
var view = {
		searchArg:"",
		aaOnLoad:function(){
			view.buildSpirits();
			sr5.doneLoading();
		},
		applySearch:function(content){
			if(view.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(view.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildSpirits:function()
		{
			var container = ir.get("spiritList");
			var arr = model.spirits;
			var prevCategory="";
			var htm = "";
			for (var i=0,z=arr.length;i<z;i++)
			{
				var s = arr[i];
				if(view.filterSpirit(s))
				{
					continue;
				}
				htm += sr5.getSection(s,view.applySearch,view.searchArg.length>2);
			}
			container.innerHTML = htm + "</div>";
		},	
		filterSpirit:function(s)
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
			else if(s.Disadvantage.toLowerCase().indexOf(view.searchArg)>-1)
			{
				return false;
			}
			else if(s.AdvantageAdept.toLowerCase().indexOf(view.searchArg)>-1)
			{
				return false;
			}
			else if(s.AdvantageAll.toLowerCase().indexOf(view.searchArg)>-1)
			{
				return false;
			}
			else if(s.AdvantageMagician.toLowerCase().indexOf(view.searchArg)>-1)
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
				view.buildSpirits();
			}
			
		},	
		toggleSpirit:function(id)
		{
			var s=ir.get("desc"+id);
			if(s.classList.contains("show"))
			{
				s.classList.remove("show");
			}
			else
			{
				s.classList.add("show");
			}
		},
	zz_view:0
};