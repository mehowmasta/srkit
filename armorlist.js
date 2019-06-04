"use strict";
var model={
	armor:[],
	zz_model:0	
};
var view = {
		searchArg:"",
		aaOnLoad:function(){
			view.buildArmor();
			sr5.doneLoading();
		},
		applySearch:function(content){
			if(view.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(view.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildArmor:function()
		{
			var container = ir.get("armorList");
			var arr = model.armor;
			var prevType="";
			var htm = "";
			for (var i=0,z=arr.length;i<z;i++)
			{
				var s = arr[i];
				if(view.filterArmor(s))
				{
					continue;
				}
				if(prevType != s.Environment)
				{
					if(i!=0)
					{
						htm += "</div>"
					}
					htm += "<div class='divider shadow'>"+s.Environment + "</div>"
						+ "<div class='flex' style='flex-wrap:wrap;'>";
					prevType = s.Environment;
				}
				htm += sr5.getSection(s,view.applySearch,view.searchArg.length>2);
			}
			container.innerHTML = htm + "</div>";
		},	
		filterArmor:function(s)
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
			else if(s.Features.toLowerCase().indexOf(view.searchArg)>-1)
			{
				return false;
			}
			else if(s.Wireless.toLowerCase().indexOf(view.searchArg)>-1)
			{
				return false;
			}
			else if(s.SpecialRules.toLowerCase().indexOf(view.searchArg)>-1)
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
				view.buildArmor();
			}
			
		},	
		toggleArmor:function(id)
		{
			var a=ir.get("desc"+id);
			if(a.classList.contains("show"))
			{
				a.classList.remove("show");
			}
			else
			{
				a.classList.add("show");
			}
		},
	zz_view:0
};