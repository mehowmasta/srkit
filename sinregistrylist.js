"use strict";
var model={
	characters:[],
	selectPlayer:function(row,callback){
		sr5.selectPlayer(row,callback);
	},
	zz_model:0	
};
var view = {
		searchArg:"",
		aaOnLoad:function(){
			view.buildCharacters();
			hoverPop.init();
			sr5.doneLoading();
		},
		applySearch:function(content){
			if(view.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(view.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildCharacters:function()
		{
			var container = ir.get("characterList");
			var arr = model.characters;
			var top = "";
			var bottom = "";
			var count = 0;
			var lastPR = -2;
			if(arr.length==0)
			{
				var htm = "<div class='title' id='sinRegistryEmpty' style='margin-top:8rem;'></div>";
				container.innerHTML = htm;
				sr5.type(ir.get("sinRegistryEmpty")," Failed to establish connection to Global SIN Registry.");		
				return;
			}
			for (var i=0,z=arr.length;i<z;i++)
			{
				var htm = "";
				var s = arr[i];
				if(view.filterCharacter(s))
				{
					continue;
				}
				var metatype = sr5.metatypes.get(s.Metatype).Name;
				var sex = sr5.sex.get(s.Sex).Name;
				var template = "<div class='section {7} shadow hover' data-hover='View Character Sheet'  onclick='view.openCharacterSheet({5})'>"
							 + "<div class='flex' style='align-items:center;justify-content:flex-end;'>"
							 + "<div class='thumbWrap'>{11}</div>"
							 + "<div style='display: flex;justify-content: space-between;width: 100%;flex: 1;flex-wrap: wrap;align-items: center;'>"
							 + "<div>"
							 + "<div class='title'><b>{0}</b></div>"
							 + "<div class='subtitle'><b>{1}</b></div>"
							 + "<div style='text-transform:uppercase;'><i style='font-size:1rem;'>{8}</i></div>"
							 + "<div >{12}</div>"
							 + "</div>"
							 + "<div>{6}</div>"
							 + "<div style='flex:2;display: flex;justify-content: flex-end;'>"
							 + "<table class='tableExtra'>"
							 + "<tr><td></td><td><b>Initiative: </b>{9}</td></tr>"
							 + "<tr><td></td><td><b>Condition: </b>{10}</td></tr>"
							 + "</table></div>"
							 + "</div>"
							 + "<div class='' style='text-align:center;float:right;'>{2}</div>"
							 + "</div>"
							 + "<div class='source'>{5}</div>"
							 + "</div>";
				if(lastPR!=s.ProfessionalRating)
				{
					htm += "<div class='divider shadow'>"+sr5.professionalRating.get(s.ProfessionalRating).text+"</div>"
				}
				htm += ir.format(template,
						view.applySearch(s.Name),
						sex + " " + view.applySearch(metatype),
						sr5.getCharacterStatsTable(s,true),
						s.Nuyen + sr5.nuyen,
						s.Karma,
						s.Row,
						"",
						s.Type.toLowerCase(),
						view.applySearch(s.Misc),
						s.Initiative + " + "+s.InitiativeDice+"D6",
						sr5.getCharacterMaxPhysical(s) + "/"+sr5.getCharacterMaxStun(s),
						view.getPortrait(s),
						(s.ProfessionalRating==-1?"PR: NA":"PR: "+ s.ProfessionalRating));
				//content = content.replace(new RegExp(view.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
				if(s.ProfessionalRating==-1)
				{
					bottom += htm;
				}
				else
				{
					top += htm
				}
				lastPR = s.ProfessionalRating;
				count++;
			}
			container.innerHTML = top + bottom + "</div>";
			ir.set("characterCount",count!=arr.length?count+" / " + arr.length:count);
		},	
		changePRFilter:function(){
			view.buildCharacters();			
		},
		filterCharacter:function(s)
		{

			var pr = ir.vn("professionalRating");
			if(view.searchArg.length<3 && pr==-1)
			{
				return false;
			}
			if(pr>-1 && s.ProfessionalRating != pr)
			{
				return true;
			}
			if(s.Name.toLowerCase().indexOf(view.searchArg)>-1)
			{
				return false;
			}

			if(s.Misc.toLowerCase().indexOf(view.searchArg)>-1)
			{
				return false;
			}
			var metatype = sr5.metatypes.get(s.Metatype).Name;
			if(metatype.toLowerCase().indexOf(view.searchArg)>-1)
			{
				return false;
			}
			return true;			
		},
		getPortrait:function(s)
		{
			if(s.Extension.length>0)
			{
				var img = {Row:s.Portrait,User:s.UserImage,Extension:s.Extension,Type:"Face"};
				return sr5.getThumb(img);
			}
			else
			{
				return sr5.getThumbUnknown();
			}
		},	
		openCharacterSheet:function(row)
		{
			var player = sr5.characters.get(row);
			if(player==null)
			{
				model.selectPlayer(row,sr5.showPlayerCharacter);
			}
			else
			{
				sr5.showPlayerCharacter(player);
			}
		},
		searchKeyup:function(arg)
		{
			if(view.searchArg != arg)
			{
				view.searchArg = arg;
				view.buildCharacters();
			}
			
		},	
	zz_view:0
};