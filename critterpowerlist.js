"use strict";
var model={
	sortType:[],
	critterPowers:[],
	zz_model:0	
};
var view = {
		searchArg:"",
		aaOnLoad:function(){
			view.buildCritterPowers();
			sr5.doneLoading();
		},
		applySearch:function(content){
			if(view.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(view.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildCritterPowers:function()
		{
			var container = ir.get("critterPowerList");
			var arr = model.critterPowers;
			var prevCategory="";
			var htm = "";
			for (var i=0,z=arr.length;i<z;i++)
			{
				var s = arr[i];
				if(view.filterCritterPower(s))
				{
					continue;
				}
				if(prevCategory != s[model.sortType[0].name])
				{
					if(i!=0)
					{
						htm += "</div>"
					}
					htm += "<div class='divider shadow'>"+s[model.sortType[0].name]+" " +model.sortType[0].suffix + "</div>"
						+ "<div class='flex' style='flex-wrap:wrap;'>";
					prevCategory = s[model.sortType[0].name];
				}
				htm += sr5.getSection(s,view.applySearch,view.searchArg.length>2);
			}
			container.innerHTML = htm + "</div>";
		},	
		filterCritterPower:function(s)
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
				view.buildCritterPowers();
			}
			
		},	
		sortList:function(){
			var arr = model.sortType;
			arr.push(arr.shift());
			var list = model.critterPowers;
			list = list.sort(function(a,b){
				if(a[model.sortType[0].name] < b[model.sortType[0].name])
				{
					return -1;					
				}
			    if(a[model.sortType[0].name] > b[model.sortType[0].name])
		    	{
			    	return 1;
		    	}
			    if(a.Name < b.Name)
			    {
			    	return -1;
			    }
			    else
			    {
			    	return 1;
			    }			    
			    return 0;
			});
			view.buildCritterPowers();
			Status.info("Sorting by " + arr[0].name,2000);
		},	
		toggleCritterPower:function(id)
		{
			var critterPower=ir.get("desc"+id);
			if(critterPower.classList.contains("show"))
			{
				critterPower.classList.remove("show");
			}
			else
			{
				critterPower.classList.add("show");
			}
		},
	zz_view:0
};