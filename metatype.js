"use strict";
var model={
	races:[],
	zz_model:0	
};
var view = {
		showEssence:false,
		showInitiative:false,
		aaOnLoad:function(){
			view.buildRaces();
			sr5.doneLoading();
		},

		addHeader: function(table,hasMagic)
		{
			var t = table		
			var header = table.createTHead();
			var tr = header.insertRow(0);
			var td = tr.insertCell(-1);
			td.className = "tdl";
			td.innerHTML = "Name";
			td = tr.insertCell(-1);
			td.className = "tdc";
			td.innerHTML = "Bod";
			td = tr.insertCell(-1);
			td.className = "tdc";
			td.innerHTML = "Agi";
			td = tr.insertCell(-1);
			td.className = "tdc";
			td.innerHTML = "Rea";
			td = tr.insertCell(-1);
			td.className = "tdc";
			td.innerHTML = "Str";
			td = tr.insertCell(-1);
			td.className = "tdc";
			td.innerHTML = "Wil";
			td = tr.insertCell(-1);
			td.className = "tdc";
			td.innerHTML = "Log";
			td = tr.insertCell(-1);
			td.className = "tdc";
			td.innerHTML = "Int";
			td = tr.insertCell(-1);
			td.className = "tdc";
			td.innerHTML = "Cha";
			td = tr.insertCell(-1);
			td.className = "tdc";
			td.innerHTML = "Edg";
			if(view.showEssence)
			{
				td = tr.insertCell(-1);
				td.className = "tdc";
				td.innerHTML = "Ess";
			}
			if(hasMagic)
			{
				td = tr.insertCell(-1);
				td.className = "tdc";
				td.innerHTML = "Mag";
			}
			if(view.showInitiative)
			{
				td = tr.insertCell(-1);
				td.className = "tdc";
				td.innerHTML = "Ini";
			}
		},
		addRow: function(a,table,hasMagic)
		{
			var t = table		
			var tr = t.insertRow(-1);
			tr.id =  table.id + "Row" + a.Row;
			var td = tr.insertCell(-1);
			td.className = "tdl";
			td.innerHTML = a.Name;
			td = tr.insertCell(-1);
			td.className = "tdc";
			td.innerHTML = a.Body;
			td = tr.insertCell(-1);
			td.className = "tdc";
			td.innerHTML = a.Agility;
			td = tr.insertCell(-1);
			td.className = "tdc";
			td.innerHTML = a.Reaction;
			td = tr.insertCell(-1);
			td.className = "tdc";
			td.innerHTML = a.Strength;
			td = tr.insertCell(-1);
			td.className = "tdc";
			td.innerHTML = a.Willpower;
			td = tr.insertCell(-1);
			td.className = "tdc";
			td.innerHTML = a.Logic;
			td = tr.insertCell(-1);
			td.className = "tdc";
			td.innerHTML = a.Intuition;
			td = tr.insertCell(-1);
			td.className = "tdc";
			td.innerHTML = a.Charisma;
			td = tr.insertCell(-1);
			td.className = "tdc";
			td.innerHTML = a.Edge;

			if(view.showEssence)
			{
				td = tr.insertCell(-1);
				td.className = "tdc";
				td.innerHTML = a.Essence;
			}
			if(a.Magic>0)
			{
				td = tr.insertCell(-1);
				td.className = "tdc";
				td.innerHTML = a.Magic;
			}

			if(view.showInitiative)
			{
				td = tr.insertCell(-1);
				td.className = "tdc";
				td.innerHTML = a.Initiative;
			}
		},
		addTrait:function(a,table){
			var t = table		
			var tr = t.insertRow(-1);
			var td = tr.insertCell(-1);
			var colSpan = a.Magic>0?11:10;
			if(view.showEssence)
			{
				colSpan++;
			}
			if(view.showInitiative)
			{
				colSpan++;
			}
			td.className = "tdl";
			td.colSpan= colSpan;
			td.innerHTML = "<b>" + a.Name + " Racial:</b> " + a.Traits;
		},
		buildRaces:function()
		{
			var races = model.races;
			var tbl1 = ir.get("metaTypeTable");
			var tbl2 = ir.get("metaSapientTable");
			var tbl3 = ir.get("metaSapient2Table");
			var tbl4 = ir.get("shapeShifterTable");
			for(var i=0,z=races.length;i<z;i++)
			{
				var race = races[i];
				if(race.ShapeShifter)
				{
					view.addRow(race,tbl4);
					view.addTrait(race,tbl4);
					ir.show("shapeShifterWrap");
				}
				else if(race.Magic>0)
				{
					view.addRow(race,tbl3);
					view.addTrait(race,tbl3);
					ir.show("metaSapient2Wrap");
				}
				else if(race.Source.indexOf("Core")==-1)
				{
					view.addRow(race,tbl2);
					view.addTrait(race,tbl2);
					ir.show("metaSapientWrap");
				}
				else
				{
					view.addRow(race,tbl1);
					view.addTrait(race,tbl1);
					ir.show("metaTypeWrap");
				}
			}
			view.addHeader(tbl1);
			view.addHeader(tbl2);
			view.addHeader(tbl3,true);
			view.addHeader(tbl4,true);
			
		},
	zz_view:0
};