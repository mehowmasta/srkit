"use strict";
var model={
		maps:[],
	zz_model:0	
};
var view = {
		searchArg:"",
		aaOnLoad:function(){
			view.buildMaps();
			sr5.doneLoading();
		},
		addMap:function(){
			view.toggleUploadButton();
		},
		applySearch:function(content){
			if(view.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(view.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildMaps:function(){
			var container = ir.get("mapList");
			var arr = model.maps;
			var htm = "";
			for (var i=0,z=arr.length;i<z;i++)
			{
				var s = arr[i];
				if(view.filterMaps(s))
				{
					continue;
				}
				var template = "<div class='section {2}'  onclick='sr5.go(\"mapdetail.jsp?ctlRow={3}\")'>"
							 + "<div class='flex' style='align-items:center;justify-content:space-between;'>"
							 + "<div class='title'><b>{0}</b></div>"
							 + "<div class='flex' style='justify-content:flex-start;'>{1}</div>"
							 + "<div class='source'>{3}</div>"
							 + "</div>"
							 + "</div>";
				htm += ir.format(template,
						view.applySearch(s.Name),
						sr5.getThumb(s),
						s.Inactive?"inactive":"shadow",
						s.Row);
				//content = content.replace(new RegExp(view.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
			}
			container.innerHTML = htm + "</div>";
		},
		filterMaps:function(s)
		{
			if(view.searchArg.length<3)
			{
				return false;
			}
			if(s.Name.toLowerCase().indexOf(view.searchArg)>-1)
			{
				return false;
			}
			if(s.FileName.toLowerCase().indexOf(view.searchArg)>-1)
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
				view.buildMaps();
			}
			
		},
		setName:function(ele)
		{
			var file = ele.files[0].name;
			ir.set("mockMapFile",file);
			ir.set("mapName",file.substring(0,file.lastIndexOf(".")));
		},
		
		submit:function(){
			
		},
		toggleUploadButton:function()
		{
			sr5.toggleChildButtons("btnAdd");
		},
	zz_view:0
};