"use strict";
var model={
		portraits:null,
		deletePortrait:function(row){
			var callback = function(res){
				if(res.ok)
				{
					var portrait = ir.get("portraitWrap"+row);
					portrait.parentNode.removeChild(portrait);
					Status.success("Portrait removed.");
				}
				
			};
			sr5.ajaxAsync({fn:"deletePortrait",row:row},callback);
		},
	zz_model:0	
};
var view = {
		searchArg:"",
		aaOnLoad:function(){
			view.buildPortraits();
			sr5.doneLoading();
		},
		addPortrait:function(){
			view.toggleUploadButton();
		},
		applySearch:function(content){
			if(view.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(view.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildPortraits:function(){
			var container = ir.get("portraitList");
			var arr = model.portraits.values;
			var htm = "";
			for (var i=0,z=arr.length;i<z;i++)
			{
				var s = arr[i];
				if(view.filterPortraits(s))
				{
					continue;
				}
				/*
				 * StringBuilder b = new StringBuilder();
					String template = "<div class='portraitThumbWrap' id='portraitWrap{1}'><div class='x' style='top:-0.5rem;right:-0.1rem;' onclick='view.deletePortrait({1})'>X</div><img src='{0}' class='portraitThumb' onclick='view.showPortrait(\"{2}\")'></div>";
					for(ImageRec p : portraits)
					{
						b.append(StringKit.format(template,p.getRelativeThumb(),p.Row,p.getRelativeImage()));
					}
					set("ctlPortraits",b.toString());
				 * 
				 */
				var template = "<div class='portraitThumbWrap' id='portraitWrap{1}'>";
				if(s.User>0)
				{
					template += "<div class='x' style='top:-0.5rem;right:-0.1rem;' onclick='view.deletePortrait({1})'>X</div>"
				}				
				template += "<img src='{0}' class='portraitThumb' onclick='view.showPortrait({1})'>"
							 + "</div>";
				htm += ir.format(template,
						sr5.getImagePath(s,true),
						s.Row);
				//content = content.replace(new RegExp(view.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
			}
			container.innerHTML = htm + "</div>";
		},
		deletePortrait:function(row){
			var callback = function(yes)
			{
				if(yes)
				{
					model.deletePortrait(row);
				}
			};
			confirmPop.show("Are you sure you want to delete this portrait?",callback);		
		},
		filterPortraits:function(s)
		{
			if(view.searchArg.length<3)
			{
				return false;
			}
			if(s.Name.toLowerCase().indexOf(view.searchArg)>-1)
			{
				return false;
			}
			if(s.Data.toLowerCase().indexOf(view.searchArg)>-1)
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
				view.buildPortraits();
			}
			
		},
		setName:function(ele)
		{
			var file = ele.files[0].name;
			ir.set("mockPortraitFile",file);
			ir.set("portraitName",file.substring(0,file.lastIndexOf(".")));
		},
		showPortrait:function(imageRow)
		{
			imagePreviewPop.show(model.portraits.get(imageRow));
		},
		submit:function(){
			
		},
		toggleUploadButton:function()
		{
			sr5.toggleChildButtons("btnAddPortrait");
		},
	zz_view:0
};