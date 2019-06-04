"use strict";
var model = {
		gcRowIndex:-1,
		groupCharacters:null,
		metatype:null,
		sex:null,
		types:null,
		selectPlayer:function(row,callback){
			sr5.selectPlayer(row,callback);
		},
	zz_model:0	
};
var view = {
	aaOnLoad:function(){
		if(model.shareKey.length>0)
		{
			ir.set("groupKey","TEAM KEY: " + model.shareKey);
		}
		ir.show("charactersWrap",model.groupRow>0);
		view.buildCharacters(model.groupCharacters.values);
		sr5.doneLoading();
	},
	addCharacters:function(){
		pickCharacterPop.show(null,false,view.afterPickCharacters);
	},
	addRegistry:function(){
		pickNPCPop.show(view.afterPickCharacters);
	},
	afterPickCharacters:function(characters){
		for(var i=0, z=characters.length; i<z; i++)
		{
			var c = ir.copy(characters[i]);
			c.GroupCharacterRow = model.gcRowIndex--;
			model.groupCharacters.add(c);
			for(var j=1, y=c.Quantity;j<y;j++)
			{
				var d = ir.copy(c);
				d.GroupCharacterRow = model.gcRowIndex--;
				model.groupCharacters.add(d);
			}
		}
		view.buildCharacters(model.groupCharacters.values);
	},
	buildCharacters:function(characters){
		var container = ir.get("characterList");
		var arr = characters;
		var htm = "";
		for (var i=0,z=arr.length;i<z;i++)
		{
			var s = arr[i];
			var metatype = sr5.metatypes.get(s.Metatype).Name;
			var sex = sr5.sex.get(s.Sex).Name;
			var diffUser = sr5.user.Row != s.User;
			var name = diffUser?"<span style='color:white;text-transform: initial;'> ("+s.UserName+")</span>":"";
			var template = "<div style='position:relative;width:100%;padding-right:3rem;'>"
				 		 + "<div class='section {4} shadow' onclick='view.openCharacterSheet({3})'>"
						 + "<div class='flex' style='align-items:center;justify-content:flex-start;'>"
						 + "<div>{6}</div>"
						 + "<div>"
						 + "<div class='title'><b>{0}</b></div>"
						 + "<div class='subtitle'><b>{1}</b></div>"
						 + "<div style='text-transform:uppercase;'><i  style='font-size:1rem;'>{5}</i></div>"
						 + "</div>"
						 + "<div style='text-align:right;flex:1;'>{2}</div>"
						 + "</div>"
						 + "<div class='source'>{7}</div>"
						 + "</div>"
						 + "<div class='x'  style='position:absolute;top:0.75rem;right:0.75rem;' onclick='view.remove({7})'>X</div>"
						 + "</div>";
			htm += ir.format(template,
					s.Name + name,
					sex + " " + metatype,
					sr5.getCharacterStatsTable(s),
					s.Row,
					s.Type.toLowerCase(),
					s.Misc,
					view.getPortrait(s),
					s.GroupCharacterRow);
		}
		container.innerHTML = htm + "</div>";
		view.setCharacters(characters);
	},

	deleteGroup:function(){
		var callback = function(yes)
		{
			if(yes)
			{
				sr5.submitDelete();
			}
		};
		confirmPop.show("Are you sure you want to delete this team?",callback);
	},
	getPortrait:function(s)
	{
		if(s.Extension && s.Extension.length>0)
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
	remove:function(row)
	{
		var character = model.groupCharacters.get(row);
		if(character!=null)
		{
			var callback = function(yes){
				if(yes)
				{
					model.groupCharacters.remove(row);
					view.buildCharacters(model.groupCharacters.values);
				}
			};
			confirmPop.show("Are you sure you want to remove <i>"+ character.Name +"</i> ?",callback);
		}
	},
	setCharacters:function(characters){
		var value = "";
		var delim = "";
		for(var i=0,z=characters.length;i<z;i++)
		{
			var s = characters[i];
			value += delim + s.Row + sr5.splitter + "1";
			delim= sr5.delimiter;
		}
		ir.set("ctlCharacters",value);
	},
	zz_view:0
};