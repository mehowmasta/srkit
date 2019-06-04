"use strict";
var model={
	sortType:[],
	adeptPowers:[],
	zz_model:0	
};
var view = {
		searchArg:"",
		aaOnLoad:function(){
			view.buildAdeptPowers();
			sr5.doneLoading();
		},
		applySearch:function(content){
			if(view.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(view.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildAdeptPowers:function()
		{
			var container = ir.get("adeptPowerList");
			var arr = model.adeptPowers;
			var prevCategory="";
			var htm = "";
			for (var i=0,z=arr.length;i<z;i++)
			{
				var s = arr[i];
				if(view.filterAdeptPower(s))
				{
					continue;
				}
				if(prevCategory != s[model.sortType[0].name])
				{
					if(i!=0)
					{
						htm += "</div>"
					}
					if(s[model.sortType[0].name] != null)
					{
						htm += "<div class='divider shadow'>"+s[model.sortType[0].name]+" " +model.sortType[0].suffix + "</div>"
							+ "<div class='flex' style='flex-wrap:wrap;'>";
					}
					else
					{
						htm += "<div class='flex' style='flex-wrap:wrap;'>";
					}
					prevCategory = s[model.sortType[0].name];
				}
				htm += sr5.getSection(s,view.applySearch,view.searchArg.length>2);
			}
			container.innerHTML = htm + "</div>";
		},	
		filterAdeptPower:function(s)
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
			else if(s.Prerequisite.toLowerCase().indexOf(view.searchArg)>-1)
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
				view.buildAdeptPowers();
			}
			
		},	
		sortList:function(){
			var arr = model.sortType;
			arr.push(arr.shift());
			var list = model.adeptPowers;
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
			view.buildAdeptPowers();
			var name = arr[0].name.length>0?arr[0].name:"Name";
			Status.info("Sorting by " + name,2000);
		},	
		toggleAdeptPower:function(id)
		{
			var adeptPower=ir.get("desc"+id);
			if(adeptPower.classList.contains("show"))
			{
				adeptPower.classList.remove("show");
			}
			else
			{
				adeptPower.classList.add("show");
			}
		},
	zz_view:0
};