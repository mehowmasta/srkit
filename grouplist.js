"use strict";
var model={
		groups:[],
	zz_model:0	
};
var view = {
		searchArg:"",
		aaOnLoad:function(){
			view.buildGroups();
			sr5.doneLoading();
		},
		addGroup:function(){
			sr5.go("groupdetail.jsp?ctlRow=0");
		},
		applySearch:function(content){
			if(view.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(view.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildGroups:function(){
			var container = ir.get("groupList");
			var arr = model.groups;
			var top = "";
			var bottom = "";
			for (var i=0,z=arr.length;i<z;i++)
			{
				var s = arr[i];
				if(view.filterGroups(s))
				{
					continue;
				}
				var htm = "";
				var template = "<div class='section '  onclick='sr5.go(\"groupdetail.jsp?ctlRow={4}\")'>"
							 + "<div class='flex' style='align-items:center;justify-content:space-between;'>"
							 + "<div>"
							 + "<div class='title'><b>{0}</b></div>"
							 + "</div>"
							 + "<div>" 
							 + "<div class='subtitle'><b>{1}</b></div>"
							 + "</div>"
							 + "<div class='spacer' >"
							 + "<div> <b>Characters: </b>{2}</div>"
							 + "</div>"
							 + "<div class='flex {3}' style='justify-content:flex-start;'>{5}</div>"
							 + "</div>"
							 + "<div class='source'>{4}</div>"
							 + "</div></div>";
				htm = ir.format(template,
						view.applySearch(s.Name) + (s.User==sr5.user.Row?"":" <span class='subtitle'>(Joined)</span>"),
						s.Inactive?"Inactive":(s.Private?"Private":"Team Key: " + s.ShareKey),
						view.applySearch(s.Characters.replace(new RegExp(",", 'g'), ", ")),
						s.Inactive?"inactive":"",
						s.Row,
						view.getPortraits(s));
				if(s.Inactive)
				{
					bottom += htm;
				}
				else
				{
					top += htm
				}
				//content = content.replace(new RegExp(view.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
			}
			container.innerHTML = top + bottom + "</div>";
		},
		filterGroups:function(s)
		{
			if(view.searchArg.length<3)
			{
				return false;
			}
			if(s.Name.toLowerCase().indexOf(view.searchArg)>-1)
			{
				return false;
			}
			if(s.Characters.toLowerCase().indexOf(view.searchArg)>-1)
			{
				return false;
			}
			return true;			
		},
		getPortraits:function(group)
		{
			var htm ="";
			if(group.Characters.length==0 || group.Images.length==0)
			{
				return htm;
			}
			var template = "<div class='thumbWrap'><img src='images/Face/{0}' class='thumb'><label class='thumbLabel'>{1}</label></div>";
			var sources = group.Images.split(",");
			var names = group.Characters.split(",");
			for(var i=0,z=sources.length;i<z;i++)
			{
				var s = sources[i];
				if(s.charAt(0)=='t')
				{
					htm += ir.format(template,s,ir.ellipsis(names[i],12));
				}
				else
				{
					htm += ir.format(template,"thumb_0_"+sr5.unknownFaces[parseInt(Math.random()*sr5.unknownFaces.length)]+".jpg",ir.ellipsis(names[i],12));
				}
				
			}
			return htm;			
		},		
		searchKeyup:function(arg)
		{
			if(view.searchArg != arg)
			{
				view.searchArg = arg;
				view.buildGroups();
			}
			
		},
	zz_view:0
};