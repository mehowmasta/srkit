"use strict";
var model={
	characters:[],
	metatype:null,
	sex:null,
	types:null,
	zz_model:0	
};
var view = {
		searchArg:"",
		aaOnLoad:function(){
			view.buildAddTypeButtons();
			view.buildCharacters();
			hoverPop.init();
			sr5.doneLoading();
		},
		acceptTransfer:function(characterRow){
			var callback= function(res){
				if(res.ok)
				{
					sr5.go("characterdetail.jsp?ctlRow="+characterRow);
					return false;
				}
			};
			sr5.ajaxAsync({fn:"acceptTransfer",characterRow:characterRow},callback);
		},
		addCharacter:function(){
			view.toggleAddTypeButtons();
		},
		addImport:function(){
			view.toggleImportButton();
			ir.get("importFile").click();
		},
		applySearch:function(content){
			if(view.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(view.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildAddTypeButtons:function(){
			var types = model.types.values;
			var container = ir.get("btnAddSelector");
			var template = "<button class='childBtn' type='button' {1} onclick='view.pickType(\"{0}\")'>{0}</button>";
			var htm = "<div class='x' style='top:-2rem;' onclick='view.toggleAddTypeButtons()'>X</div>";
			for(var i=0,z=types.length;i<z;i++)	
			{
				var t = types[i];
				htm += ir.format(template,t.Name,t.Name=="Critter"?"disabled":"");
			}
			ir.set(container,htm);
		},
		buildCharacters:function()
		{
			var container = ir.get("characterList");
			var arr = model.characters;
			var top = "";
			var bottom = "";
			var transfer = "";
			var count = 0;
			for (var i=0,z=arr.length;i<z;i++)
			{
				var s = arr[i];
				var htm;
				if(view.filterCharacter(s))
				{
					continue;
				}
				var metatype = model.metatypes.get(s.Metatype).Name;
				var sex = model.sex.get(s.Sex).Name;
				var isPc = s.Type.toLowerCase() === "pc";
				var onclick = "sr5.go(\"characterdetail.jsp?ctlRow="+s.Row+"\")";
				var isTransfer = s.Transfer==sr5.user.Row;
				var transferBtns ="";
				if(isTransfer)
				{
					ir.show("transferDiv");
					ir.show("characterListTitle");
					transferBtns = "</div><div class='spacer'><div class='flex'><h3 style='margin:0.5rem;'>This character has been transferred to you...&emsp;</h3><button class='mini' type='button' onclick='view.acceptTransfer("+s.Row+")'>Accept</button><button type='button' class='mini' onclick='view.declineTransfer("+s.Row+")'>Decline</button></div></div><div>";
					onclick="return false";
				}
				var template = "<div class='section {7} shadow hover' data-hover='Edit Character' onclick='{13}'>"
							 + "<div class='flex' style='align-items:center;justify-content:flex-start;'>"
							 + "<div class='{12}'>{11}</div>"
							 + "<div style='display: flex;justify-content: space-between;width: 100%;flex: 1;flex-wrap: wrap;align-items: center;'>"
							 + "<div>"
							 + "<div class='title'><b>{0}</b></div>"
							 + "<div class='subtitle'><b>{1}</b></div>"
							 + "<div style='text-transform:uppercase;'><i style='font-size:1rem;'>{8}</i></div>"
							 + "</div>"
							 + "<div>{6}</div>"
							 + "<div style='flex:2;display: flex;justify-content: flex-end;'>"
							 + "<table class='tableExtra'>"
							 + "<tr><td>" + (false?"<b>Nuyen: </b>{3}":"")+ "</td><td><b>Initiative: </b>{9}</td></tr>"
							 + "<tr><td>" + (false?"<b>Karma: </b>{4}":"")+ "</td><td><b>Condition: </b>{10}</td></tr>"
							 + "</table></div>"
							 + "</div>"
							 + "<div class='' style='text-align:center;'>{2}</div>"
							 + "</div>"
							 + "<div class='source'>{5}</div>"
							 + "</div>";
				htm = ir.format(template,
						view.applySearch(s.Name),
						sex + " " + view.applySearch(metatype),
						sr5.getCharacterStatsTable(s) + transferBtns,
						s.Nuyen + sr5.nuyen,
						s.Karma,
						s.Row,
						"",
						s.Type.toLowerCase() + (isTransfer?" nohover ": ""),
						view.applySearch(s.Misc),
						s.Initiative + " + "+s.InitiativeDice+"D6",
						sr5.getCharacterMaxPhysical(s) + "/"+sr5.getCharacterMaxStun(s),
						view.getPortrait(s),
						s.Inactive?"inactive":"",
						onclick);
				//content = content.replace(new RegExp(view.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
				if(isTransfer)
				{
					ir.get("transferList").innerHTML += htm;
				}				
				else if(s.Inactive)
				{
					bottom += htm;
				}
				
				else
				{
					top += htm
				}
				count++;
			}
			container.innerHTML = transfer + top + bottom + "</div>";
			ir.set("characterCount",count!=arr.length?count+" / " + arr.length:count);
		},	
		declineTransfer:function(characterRow){
			var callback= function(res){
				if(res.ok)
				{
					sr5.go("characterlist.jsp");
					return false;
				}
			};
			sr5.ajaxAsync({fn:"declineTransfer",characterRow:characterRow},callback);
		},
		filterCharacter:function(s)
		{
			if(view.searchArg.length<3)
			{
				return false;
			}
			if(s.Name.toLowerCase().indexOf(view.searchArg)>-1)
			{
				return false;
			}

			if(s.Misc.toLowerCase().indexOf(view.searchArg)>-1)
			{
				return false;
			}
			var metatype = model.metatypes.get(s.Metatype).Name;
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
		pickType:function(typeClicked){
			var type = model.types.get(typeClicked);
			view.toggleAddTypeButtons();
			sr5.go("characterdetail.jsp?ctlRow=0&ctlType="+typeClicked);
		},
		searchKeyup:function(arg)
		{
			if(view.searchArg != arg)
			{
				view.searchArg = arg;
				view.buildCharacters();
			}
			
		},	
		setImportName:function(ele)
		{
			var file = ele.files[0];
			var name = file.name;
			var fileSize = file.size / 1024 / 1024;
			if(fileSize > 3)
			{
				ele.value='';
				ir.set("mockImportFile","");
				Status.error(ir.format("File {0} is too large. Max import size is 3MB.",name),7000);
				return false;
			}
			else if(name.toLowerCase().indexOf(".json")==-1)
			{
				ele.value='';
				ir.set("mockImportFile","");
				Status.error("Import file must be a JSON file.");
				return false;
			}			
			ir.set("mockImportFile",name);
		},
		toggleCharacter:function(id)
		{
			var s=ir.get("desc"+id);
			if(s.classList.contains("show"))
			{
				s.classList.remove("show");
			}
			else
			{
				s.classList.add("show");
			}
		},

		toggleAddTypeButtons:function(){
			sr5.toggleChildButtons("btnAdd");
			if(ir.get("btnAddSelector").classList.contains("open"))
			{
				if(ir.exists("btnImport"))
				{
					ir.hide("btnImport");
				}
			}
			else
			{
				if(ir.exists("btnImport"))
				{
					ir.show("btnImport");
				}
			}
		},
		toggleImportButton:function(){
			sr5.toggleChildButtons("btnImport");
			if(ir.get("btnImportSelector").classList.contains("open"))
			{
				ir.hide("btnAdd");
			}
			else
			{
				ir.show("btnAdd");
			}
		},
	zz_view:0
};