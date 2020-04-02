"use strict";
var model={
	weaponModifiers:[],
	zz_model:0	
};
var view = {
		searchArg:"",
		aaOnLoad:function(){
			view.buildWeaponModifiers();
			sr5.doneLoading();
		},
		applySearch:function(content){
			if(view.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(view.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildWeaponModifiers:function()
		{
			var container = ir.get("weaponModifierList");
			var arr = model.weaponModifiers;
			var htm = "<div class='divider shadow'>Weapon Modifiers</div>"
					+ "<div class='flex' style='flex-wrap:wrap;'>"
			for (var i=0,z=arr.length;i<z;i++)
			{
				var s = arr[i];
				if(view.filterWeaponModifier(s))
				{
					continue;
				}
				htm += sr5.getSectionWeaponModifier(s,view.applySearch,view.searchArg.length>2);
			}
			container.innerHTML = htm + "</div>";
		},	
		filterWeaponModifier:function(s)
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
			else if(s.Wireless.toLowerCase().indexOf(view.searchArg)>-1)
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
				view.buildWeaponModifiers();
			}
			
		},	
		sortList:function(){
			var arr = model.sortType;
			arr.push(arr.shift());
			var list = model.weaponModifiers;
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
			view.buildWeaponModifiers();
			Status.info("Sorting by " + arr[0].name,2000);
		},	
		toggleWeaponModifier:function(id)
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