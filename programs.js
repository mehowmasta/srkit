"use strict";
var model={
	programs:[],
	zz_model:0	
};
var view = {
		searchArg:"",
		aaOnLoad:function(){
			view.buildPrograms();
			sr5.doneLoading();
		},
		applySearch:function(content){
			if(view.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(view.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildPrograms:function()
		{
			var container = ir.get("programList");
			var arr = model.programs;
			var prevType="";
			var htm = "";
			for (var i=0,z=arr.length;i<z;i++)
			{
				var s = arr[i];
				if(view.filterProgram(s))
				{
					continue;
				}
				if(prevType != s.Type)
				{
					if(i!=0)
					{
						htm += "</div>"
					}
					htm += "<div class='divider shadow'>"+s.Type +" Programs</div>"
						+ "<div class='flex' style='flex-wrap:wrap;'>";
					prevType = s.Type;
				}
				htm += sr5.getSection(s,view.applySearch,view.searchArg.length>2);
			}
			container.innerHTML = htm + "</div>";
		},	
		filterProgram:function(s)
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
			else if(s.Type.toLowerCase().indexOf(view.searchArg)>-1)
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
				view.buildPrograms();
			}
			
		},	
		toggleProgram:function(id)
		{
			var program=ir.get("desc"+id);
			if(program.classList.contains("show"))
			{
				program.classList.remove("show");
			}
			else
			{
				program.classList.add("show");
			}
		},
	zz_view:0
};