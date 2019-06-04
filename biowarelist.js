"use strict";
var model={
	bioware:[],
	zz_model:0	
};
var view = {
		searchArg:"",
		aaOnLoad:function(){
			view.buildBioware();
			sr5.doneLoading();
		},
		applySearch:function(content){
			if(view.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(view.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildBioware:function()
		{
			var container = ir.get("biowareList");
			var arr = model.bioware;
			var prevPart="";
			var htm = "";
			for (var i=0,z=arr.length;i<z;i++)
			{
				var s = arr[i];
				if(view.filterBioware(s))
				{
					continue;
				}
				if(prevPart != s.Part)
				{
					if(i!=0)
					{
						htm += "</div>"
					}
					htm += "<div class='divider shadow'>"+s.Part+" Bioware</div>"
						+ "<div class='flex' style='flex-wrap:wrap;'>";
					prevPart = s.Part;
				}
				s._t="tBioware";
				htm += sr5.getSection(s,view.applySearch,view.searchArg.length>2);
			}
			container.innerHTML = htm + "</div>";
		},	
		filterBioware:function(s)
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
			else if(s.Part.toLowerCase().indexOf(view.searchArg)>-1)
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
				view.buildBioware();
			}
			
		},
		toggleBioware:function(id)
		{
			var bioware=ir.get("desc"+id);
			if(bioware.classList.contains("show"))
			{
				bioware.classList.remove("show");
			}
			else
			{
				bioware.classList.add("show");
			}
		},
	zz_view:0
};