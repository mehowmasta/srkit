"use strict";
var diceRollPop = {
		history:[],
		historyIndex:0,
		maxDice:100,
	id:'diceRollPop',
	imgTemplate:"<img class='die {1}' src='{0}.svg'>",
	lastRoll:null,
	buildDice:function(rolls,className,isEdge,noFly){
		var htm="";
		if(rolls)
		{
			for(var i=0,z=rolls.length;i<z;i++)
			{
				htm+= ir.format(diceRollPop.imgTemplate,sr5.iconPath + (isEdge?"rdie":"die") + rolls[i], (noFly?"":"fly ") + className + " " + rolls[i]);
			}
		}
		return htm;
	},
	buildResults:function(roll)
	{
		var htm = "";
		var glitch = false;
		var critical = false;
		var totalHits = roll.hits.length;
		var totalGlitch = roll.glitch.length;	
		var noPush=true;
		var count = roll.count + roll.edgeRating;
		if(roll.edgeHits)
		{
			totalHits+=roll.edgeHits.length;
		}
		if(roll.edgeGlitch)
		{
			totalGlitch+=roll.edgeGlitch.length;
		}
		if(roll.pushHits)
		{
			totalHits+=roll.pushHits.length;
		}
		if(roll.pushGlitch)
		{
			totalGlitch+=roll.pushGlitch.length;
		}
		htm += "<div class='hitsDiv'><label id='labelHits'></label></div>";
		if((roll.hits.length>0 || roll.edgeHits.length>0))
		{
			htm += diceRollPop.buildDice(roll.hits,"hits",false,roll.secondRoll);
			htm += diceRollPop.buildDice(roll.edgeHits,"hits",true);
		}		
		if((roll.nothing.length>0 || roll.edgeNothing.length>0))
		{
			htm += "<div class='nothingDiv'></div>";
			htm += diceRollPop.buildDice(roll.nothing,"",false,roll.secondRoll);
			htm += diceRollPop.buildDice(roll.edgeNothing,"",true);
		}		
		if((roll.glitch.length>0 || roll.edgeGlitch.length>0) && totalGlitch>=Math.ceil(count/2))
		{
			glitch = true;
			critical = roll.hits.length==0;
			htm += "<div class='glitchDiv'><label id='labelGlitch'>"+(critical?"critical ":"")+"glitch!</label></div>";
			htm += diceRollPop.buildDice(roll.glitch,"glitch",false,roll.secondRoll);
			htm += diceRollPop.buildDice(roll.edgeGlitch,"glitch",true);
		}
		else
		{
			if((roll.nothing.length==0 && roll.edgeNothing.length==0))
			{
				htm += "<div class='edgeNothing'></div>";
			}
			
			htm += diceRollPop.buildDice(roll.glitch,"",false);
			htm += diceRollPop.buildDice(roll.edgeGlitch,"",true);
		}
		if(roll.pushHits || roll.pushGlitch || roll.pushNothing)
		{	
			noPush=false;
			htm += "<div class='pushDiv'><label id='labelHits'>Push the Limit"+(roll.secondRoll?" (after roll)":"")+"</label></div>";
			htm += diceRollPop.buildDice(roll.pushHits,"hits",true);
			htm += diceRollPop.buildDice(roll.pushNothing,"",true);
			htm += diceRollPop.buildDice(roll.pushGlitch,critical?"glitch":"",true);
		}
		htm += "<div class='sumDiv'><label id='labelSum'></label></div>";
		ir.set("diceRolls",htm);
		window.setTimeout(function(){ir.set("labelHits","hits "+ totalHits+ " " + (roll.secondRoll&&noPush?"(second chance)":""))},700);
		window.setTimeout(function(){ir.set("labelSum","sum "+ roll.sum + "")},700);
		sr5.ajaxAsync({fn:"shareRoll",sum:roll.sum,hits:totalHits,count:count,glitch:glitch,critical:critical},null);
	},
	clear:function(){
		ir.set("diceRolls","");
		diceRollPop.lastRoll = null;
		ir.disable("rollPushTheLimitBtn",false);
		ir.disable("rollSecondChanceBtn",true);
		ir.hide("diceRollBtnClear");
	},
	close:function(){
		var self = diceRollPop;
		popup(self.id);
	},

	edgeRatingKeydown:function(event){
		if (typeof event == 'undefined' && window.event)
		{
			event = window.event;
		}
		if (event == null)
		{
			ir.log("Cannot process key press codes!!!");
			return;
		}
		switch (event.keyCode)
		{
			case 13:// enter
				cancelEvent(event);
				return;
				break;
			default:
				break;
		}
	},
	minusOne:function(){
		ir.set("diceCount",Math.max(1,ir.vn("diceCount")-1));
	},

	nextRoll:function(){
		var self = diceRollPop;
		var index = ++self.historyIndex;
		if(index>=self.history.length-1)
		{
			index = self.history.length-1;			
		}
		var roll = self.history[index];
		if(roll)
		{
			diceRollPop.buildResults(roll);	
			ir.set("diceCount",roll.count);
			ir.show("diceRollBtnClear");
		}
		ir.disable("diceRollBtnPrev",index<=0);
		ir.disable("diceRollBtnNext",index>=self.history.length-1);
	},
	plusOne:function(){
		var self = diceRollPop;
		ir.set("diceCount",Math.min(self.maxDice,ir.vn("diceCount")+1));
	},
	previousRoll:function(){
		var self = diceRollPop;
		var index = --self.historyIndex;
		if(index<0)
		{
			self.historyIndex=0;			
		}
		var roll = self.history[index];
		if(roll)
		{
			diceRollPop.buildResults(roll);	
			ir.set("diceCount",roll.count);
			ir.show("diceRollBtnClear");
		}
		ir.disable("diceRollBtnPrev",index<=0);
		ir.disable("diceRollBtnNext",index>=self.history.length-1);
	},
	pushTheLimit:function(){
		var roll = ir.copy(diceRollPop.lastRoll);
		var edgeRating = ir.vn("edgeRating");
		var count = ir.vn("diceCount");
		var hits = [];
		var edgeHits = [];
		var pushHits = [];
		var nothing = [];
		var edgeNothing = [];
		var pushNothing = [];
		var glitch = [];
		var edgeGlitch = [];
		var pushGlitch = [];
		var sum = 0;
		var sixes = 0;
		var sixesSum = 0;
		if(roll==null)
		{//push the limit before dice roll
			for(var i=0,z=count;i<z;i++)
			{
				var r = parseInt(Math.random()*6) + 1;
				if(r>4)
				{
					if(r==6)
					{
						sixes++;
					}
					hits.push(r);
				}
				else if(r==1)
				{
					glitch.push(r);
				}
				else
				{
					nothing.push(r);
				}	
				sum+=r;
			}
			for(var i=0,z=edgeRating;i<z;i++)
			{
				var r = parseInt(Math.random()*6) + 1;
				if(r>4)
				{
					if(r==6)
					{
						sixes++;
					}
					edgeHits.push(r);
				}
				else if(r==1)
				{
					edgeGlitch.push(r);
				}
				else
				{
					edgeNothing.push(r);
				}	
				sum+=r;
			}
		}
		else
		{//push the limit after dice roll

			for(var i=0,z=edgeRating;i<z;i++)
			{
				var r = parseInt(Math.random()*6) + 1;
				if(r>4)
				{
					if(r==6)
					{
						sixes++;
					}
					edgeHits.push(r);
				}
				else if(r==1)
				{
					edgeGlitch.push(r);
				}
				else
				{
					edgeNothing.push(r);
				}	
				roll.sum+=r;
			}
		}
		for(var i=0,z=sixes;i<z;i++)
		{
			var r = parseInt(Math.random()*6) + 1;
			if(r>4)
			{
				pushHits.push(r);
			}
			else if(r==1)
			{
				pushGlitch.push(r);
			}
			else
			{
				pushNothing.push(r);
			}	
			sixesSum+=r;
		}
		if(roll!=null)
		{// continue for push limit after roll
			roll.edgeHits = edgeHits;
			roll.edgeNothing = edgeNothing;
			roll.edgeGlitch = edgeGlitch;
			roll.pushHits = pushHits;
			roll.pushNothing = pushNothing;
			roll.pushGlitch = pushGlitch;
			roll.edgeRating = edgeRating;
			roll.sum+=sixesSum;
			roll.secondRoll=true;
		}
		else
		{
			roll = {count:count+edgeRating,hits:hits,nothing:nothing,glitch:glitch,sum:sum+sixesSum,edgeHits:edgeHits,edgeGlitch:edgeGlitch,edgeNothing:edgeNothing,pushHits:pushHits,pushGlitch:pushGlitch,pushNothing:pushNothing};
		}
		diceRollPop.buildResults(roll);	
		diceRollPop.saveRoll(roll);
		ir.show("diceRollBtnClear");
		ir.disable("rollPushTheLimitBtn",true);
		ir.disable("rollSecondChanceBtn",true);
		return roll;
	},
	roll:function(){
		var container = ir.get("diceRolls");
		var count = ir.vn("diceCount");
		if(count>100)
		{
			count=100;
			ir.set("diceCount",count);
			Status.info("Max amount of dice to roll is 100");
		}
		var hits=[];
		var nothing=[];
		var glitch=[];
		var sum=0;
		var roll ={};
		for(var i=0,z=count;i<z;i++)
		{
			var r = parseInt(Math.random()*6) + 1;
			if(r>4)
			{
				hits.push(r);
			}
			else if(r==1)
			{
				glitch.push(r);
			}
			else
			{
				nothing.push(r);
			}	
			sum+=r;
		}
		roll = {count:count,hits:hits,nothing:nothing,glitch:glitch,sum:sum,edgeHits:[],edgeGlitch:[],edgeNothing:[],edgeRating:0};
		diceRollPop.buildResults(roll);		
		diceRollPop.saveRoll(roll);
		ir.show("diceRollBtnClear");
		ir.disable("rollPushTheLimitBtn",false);
		ir.disable("rollSecondChanceBtn",false);
		return roll;
	},
	saveRoll:function(roll){

		diceRollPop.lastRoll = roll;
		diceRollPop.history.push(roll);
		diceRollPop.historyIndex = diceRollPop.history.length-1;
		ir.disable("diceRollBtnPrev",diceRollPop.history.length<1);
		ir.disable("diceRollBtnNext",true);
	},
	secondChance:function(){
		var self = diceRollPop;
		if(self.lastRoll==null)
		{
			return;
		}
		var roll = ir.copy(self.lastRoll);
		var edgeHits = [];
		var edgeNothing = [];
		var edgeGlitch = [];
		var sum = 0;
		var reroll = roll.nothing.length + roll.glitch.length;
		for(var i=0,z=reroll;i<z;i++)
		{
			var r = parseInt(Math.random()*6) + 1;
			if(r>4)
			{
				edgeHits.push(r);
			}
			else if(r==1)
			{
				edgeGlitch.push(r);
			}
			else
			{
				edgeNothing.push(r);
			}	
			sum+=r;
		}
		for(var i =0, z= roll.hits.length;i<z;i++)
		{
			sum += roll.hits[i];
		}
		roll.edgeRating = 0;
		roll.edgeHits = edgeHits;
		roll.edgeNothing = edgeNothing;
		roll.edgeGlitch = edgeGlitch;
		roll.nothing=[];
		roll.glitch=[];
		roll.secondRoll=true;
		roll.sum=sum;
		diceRollPop.buildResults(roll);		
		diceRollPop.saveRoll(roll);
		ir.show("diceRollBtnClear");
		ir.disable("rollPushTheLimitBtn",true);
		ir.disable("rollSecondChanceBtn",true);
		return roll;
	},
	show:function(roll){
		var self = diceRollPop;
		if(roll)
		{
			ir.set("diceCount",roll);
			self.clear();
			//window.setTimeout(self.roll,250);
		}
		var pop = ir.get(self.id);
		popup(pop);
	},
	showInfo:function(){
		diceRollInfoPop.show();
	},
	toggleEdge:function()
	{
		sr5.toggleChildButtons("btnEdge");
		var selector = ir.get("btnEdgeSelector");
		if(selector.classList.contains("open"))
		{
			ir.focus("edgeRating");
		}
	},
	zz_diceRollPop:0
};