var pickNPCPop = {
	callback:null,
	id:"pickNPCPop",
	npcs:null,
	searchArg:"",
	searchTimeout:null,
	applySearch:function(content){
		if(pickNPCPop.searchArg.length<3)
		{
			return content;
		}
		return content.replace(new RegExp(pickNPCPop.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
	},
	build:function(){
		var self = pickNPCPop;
		var list = self.npcs.values;	
		var htm="";		
		var lastPR = -2;
		var template = "<div class='section {7} shadow hover' data-hover='View Character Sheet'  onclick='pickNPCPop.pick({5})'>"
			 + "<div class='flex' style='align-items:center;justify-content:flex-end;'>"
			 + "<div class='thumbWrap'>{11}</div>"
			 + "<div style='display: flex;justify-content: space-between;width: 100%;flex: 1;flex-wrap: wrap;align-items: center;'>"
			 + "<div style='text-align: left;padding-left: 0.5rem;'>"
			 + "<div class='title'><b>{0}</b></div>"
			 + "<div class='subtitle'><b>{1}</b></div>"
			 + "<div style='text-transform:uppercase;'><i style='font-size:1rem;'>{8}</i></div>"
			 + "<div >{12}</div>"
			 + "</div>"
			 + "<div>{6}</div>"
			 + "<div style='flex:2;display: flex;justify-content: flex-end;'>"
			 + "<table class='tableExtra'>"
			 + "<tr><td></td><td style='white-space:nowrap;'><b>Initiative: </b>{9}</td></tr>"
			 + "<tr><td></td><td style='white-space:nowrap;'><b>Condition: </b>{10}</td></tr>"
			 + "</table></div>"
			 + "</div>"
			 + "<div class='' style='text-align:center;float:right;'>{2}</div>"
			 + "</div>"
			 + "<div class='source'>{5}</div>"
			 + "</div>";
		for(var i =0,z=list.length;i<z;i++)
		{
			var p = list[i];
			if(self.filter(p))
			{
				continue;
			}
			var metatype = sr5.metatypes.get(p.Metatype).Name;
			var sex = sr5.sex.get(p.Sex).Name;
			if(lastPR!=p.ProfessionalRating)
			{
				htm += "<div class='divider shadow'>"+sr5.professionalRating.get(p.ProfessionalRating).text+"</div>"
			}
			htm += ir.format(template,
					self.applySearch(p.Name),
					sex + " " + self.applySearch(metatype),
					sr5.getCharacterStatsTable(p),
					p.Nuyen + sr5.nuyen,
					p.Karma,
					p.Row,
					"",
					p.Type.toLowerCase(),
					self.applySearch(p.Misc),
					p.Initiative + " + "+p.InitiativeDice+"D6",
					sr5.getCharacterMaxPhysical(p) + "/"+sr5.getCharacterMaxStun(p),
					view.getPortrait(p),
					"PR:" + p.ProfessionalRating);

			lastPR = p.ProfessionalRating;
		}
		ir.set(self.id + "ThumbContainer",htm);	
		window.setTimeout(function(){
			ir.get(self.id + "ThumbContainer").classList.add("show");
		},300);
	},
	changePRFilter:function(){
		var self = pickNPCPop;
		self.build();
		self.toggleClearSearchBtn();
		
	},
	clear:function(){
		var self = pickNPCPop;
		ir.set(self.id+"SearchInput","");
		self.searchArg = "";
		clearTimeout(self.searchTimeout);	
		ir.hide(self.id + "ClearSearchBtn");
		ir.set(self.id + "ProfessionalRating",-1)
		if(self.searchKeyup)
		{
			self.searchTimeout = window.setTimeout(function(){pickNPCPop.searchKeyup(pickNPCPop.searchArg)}, 100);	
		}	
	},
	close:function(){
		var self = pickNPCPop;
		popup(self.id);
	},
	filter:function(s)
	{
		var self = pickNPCPop;
		var pr = ir.vn(self.id+"ProfessionalRating");
		if(self.searchArg.length<3 && pr==-1)
		{
			return false;
		}
		if(pr>-1 && s.ProfessionalRating != pr)
		{
			return true;
		}
		if(s.Misc.toLowerCase().indexOf(self.searchArg)>-1)
		{
			return false;
		}
		if(s.Name.toLowerCase().indexOf(self.searchArg)>-1)
		{
			return false;
		}
		return true;			
	},
	keyup:function(e)
	{		
		var self = pickNPCPop;
		if (typeof e == 'undefined' && window.event)
		{
			e = window.event;
		}
		if (e == null)
		{
			ir.log("Cannot process key press codes!!!");
			return;
		}
		clearTimeout(self.searchTimeout);
		if (e.keyCode!=13)//enter
		{
			self.searchArg = ir.ltrim(ir.v(self.id + "SearchInput"));
			self.searchArg = ir.rtrim(self.searchArg).toLowerCase();
			ir.show(self.id + "ClearSearchBtn",self.searchArg.length>0);
			if(self.searchKeyup)
			{
				self.searchTimeout = window.setTimeout(function(){self.searchKeyup(self.searchArg)}, 800);	
			}					
		}		
	},
	pick:function(row)
	{
		var self = pickNPCPop;
		if(self.callback!=null)
		{
			var array = [];
			var npc = self.npcs.get(row);
			npc.Quantity = 1;
			array.push(npc);
			self.callback(array);
		}
		self.close();
	},
	searchKeyup:function(arg){
		var self = pickNPCPop;
		self.build();
	},
	selectNPCs:function(){
		var self = pickNPCPop;
		if(self.npcs==null)
		{
			var callback = function(res){
				if(res.ok)
				{
					self.npcs = new KeyedArray("Row",res.list);
					self.build();
				}				
			};
			sr5.ajaxAsync({fn:"selectNPCs"},callback);
		}		
	},
	show:function(callback){
		var self = pickNPCPop;
		self.callback = callback;
		var pop = ir.get(self.id);
		self.selectNPCs();
		popup(pop);
	},
	toggleClearSearchBtn:function(){
		var self = pickNPCPop;
		var pr = ir.vn(self.id+"ProfessionalRating");
		ir.show(self.id + "ClearSearchBtn",self.searchArg.length>0 || pr>-1);
	},
	zz_pickMapPop:0		
};