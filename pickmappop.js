var pickMapPop = {
	callback:null,
	currentUrl:null,
	id:"pickMapPop",
	maps:null,
	searchArg:"",
	searchTimeout:null,
	build:function(){
		var self = pickMapPop;
		var list = self.maps.values;
		var template = "<div class='portriatThumbWrap show' id='pickMapPopMapWrap{1}'>"
					 + "<img src='{0}' class='portraitThumb' onclick='pickMapPop.pick(\"{1}\")'>"
					 + "<br><label>{2}</label>"
					 + "</div>";		
		var htm="";
		for(var i =0,z=list.length;i<z;i++)
		{
			var p = list[i];
			if(self.filter(p))
			{
				continue;
			}
			htm+=ir.format(template,sr5.getImagePath(p,true),p.Row,ir.ellipsis(p.Name,12))
		}
		ir.set(self.id + "ThumbContainer",htm);	
		window.setTimeout(function(){
			ir.get(self.id + "ThumbContainer").classList.add("show");
		},300);
	},
	clear:function(){
		var self = pickMapPop;
		ir.set(self.id+"SearchInput","");
		self.searchArg = "";
		clearTimeout(self.searchTimeout);	
		ir.hide(self.id + "ClearSearchBtn");
		if(self.searchKeyup)
		{
			self.searchTimeout = window.setTimeout(function(){pickMapPop.searchKeyup(pickMapPop.searchArg)}, 100);	
		}	
	},
	close:function(){
		var self = pickMapPop;
		popup(self.id);
	},
	filter:function(s)
	{
		var self = pickMapPop;
		if(self.searchArg.length<3)
		{
			return false;
		}
		if(s.Data.toLowerCase().indexOf(self.searchArg)>-1)
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
		var self = pickMapPop;
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
		var self = pickMapPop;
		if(self.callback!=null)
		{
			self.callback(self.maps.get(row));
		}
		self.close();
	},
	searchKeyup:function(arg){
		var self = pickMapPop;
		self.build();
	},
	selectMaps:function(){
		var self = pickMapPop;
		if(self.maps==null)
		{
			var callback = function(res){
				if(res.ok)
				{
					self.maps = new KeyedArray("Row",res.list);
					self.build();
				}				
			};
			sr5.ajaxAsync({fn:"selectMaps"},callback);
		}		
	},
	show:function(callback){
		var self = pickMapPop;
		self.callback = callback;
		var pop = ir.get(self.id);
		self.selectMaps();
		popup(pop);
	},
	zz_pickMapPop:0		
};