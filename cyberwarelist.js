"use strict";
var model={
	cyberware:[],
	zz_model:0	
};
var view = {
		searchArg:"",
		aaOnLoad:function(){
			view.buildCyberware();
			sr5.doneLoading();
		},
		applySearch:function(content){
			if(view.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(view.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildCyberware:function()
		{
			var container = ir.get("cyberwareList");
			var arr = model.cyberware;
			var precPart="";
			var htm = "";
			for (var i=0,z=arr.length;i<z;i++)
			{
				var s = arr[i];
				if(view.filterCyberware(s))
				{
					continue;
				}
				if(precPart != s.Part)
				{
					if(i!=0)
					{
						htm += "</div>"
					}
					htm += "<div class='divider shadow'>"+s.Part+" Cyberware</div>"
						+ "<div class='flex' style='flex-wrap:wrap;'>";
					precPart = s.Part;
				}
				htm += sr5.getSection(s,view.applySearch,view.searchArg.length>2);				
			}
			container.innerHTML = htm + "</div>";
		},	
		filterCyberware:function(s)
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
				view.buildCyberware();
			}
			
		},
		toggleCyberware:function(id)
		{
			var cyberware=ir.get("desc"+id);
			if(cyberware.classList.contains("show"))
			{
				cyberware.classList.remove("show");
			}
			else
			{
				cyberware.classList.add("show");
			}
		},
	zz_view:0
};