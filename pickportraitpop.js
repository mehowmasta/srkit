var pickPortraitPop = {
	callback:null,
	currentUrl:null,
	id:"pickPortraitPop",
	portraits:null,
	searchArg:"",
	searchTimeout:null,
	build:function(){
		var self = pickPortraitPop;
		var list = self.portraits.values;
		var template = "<div class='portraitThumbWrap show' id='pickPortraitPopPortraitWrap{1}'>"
					 + "<img src='{0}' class='portraitThumb' onclick='pickPortraitPop.pick(\"{1}\")'>"
					 //+ "<label style='position:absolute;left: 0;bottom: 0.2rem;'>{2}</label>"
					 + "</div>";		
		var htm="";
		for(var i =0,z=list.length;i<z;i++)
		{
			var p = list[i];
			if(self.filter(p))
			{
				continue;
			}
			htm+=ir.format(template,sr5.getImagePath(p,true),p.Row,p.Name)
		}
		ir.set(self.id + "ThumbContainer",htm);	
		window.setTimeout(function(){
			ir.get(self.id + "ThumbContainer").classList.add("show");
		},300);
	},
	clear:function(){
		var self = pickPortraitPop;
		ir.set(self.id+"SearchInput","");
		self.searchArg = "";
		clearTimeout(self.searchTimeout);	
		ir.hide(self.id + "ClearSearchBtn");
		if(self.searchKeyup)
		{
			self.searchTimeout = window.setTimeout(function(){pickPortraitPop.searchKeyup(pickPortraitPop.searchArg)}, 100);	
		}	
	},
	close:function(){
		var self = pickPortraitPop;
		popup(self.id);
	},
	filter:function(s)
	{
		var self = pickPortraitPop;
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
		var self = pickPortraitPop;
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
		var self = pickPortraitPop;
		if(self.callback!=null)
		{
			self.callback(self.portraits.get(row));
		}
		self.close();
	},
	searchKeyup:function(arg){
		var self = pickPortraitPop;
		self.build();
	},
	selectPortraits:function(){
		var self = pickPortraitPop;
		if(self.portraits==null)
		{
			var callback = function(res){
				if(res.ok)
				{
					self.portraits = new KeyedArray("Row",res.list);
					self.build();
				}				
			};
			sr5.ajaxAsync({fn:"selectFaces"},callback);
		}		
	},
	show:function(){
		var self = pickPortraitPop;
		var pop = ir.get(self.id);
		self.selectPortraits();
		popup(pop);
	},
	zz_pickPortraitPop:0		
};