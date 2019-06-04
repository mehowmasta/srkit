var pickMapObjectPop = {
	id:'pickMapObjectPop',
	initialized:false,
	build:function(){
		var self = pickMapObjectPop;
		if(self.intialized)
		{
			return;
		}
		var template = "<button type='button' class='mainBtn hover' data-hover='{0}' onclick='pickMapObjectPop.pick(\"{0}\")'><img src='{1}' class='medIcon'><br>{0}</button>";
		var mapObjectTypes = sr5.mapObjectType.values;
		var htm="";
		for(var i=0,z=mapObjectTypes.length;i<z;i++)
		{
			var a = mapObjectTypes[i];
			if(!a.hasPicker)
			{
				continue;
			}
			htm += ir.format(template,a.name,sr5.iconPath + a.imgSrc);
		}
		ir.set(self.id+ "Container",htm);
		self.initialize=true;
	},
	close:function(){
		var self = pickMapObjectPop;
		popup(self.id);
	},
	pick:function(type){
		var self = pickMapObjectPop;
		if(self.callback!=null)
		{
			self.callback(type);
			self.close();
		}
	},
	show:function(callback){
		var self = pickMapObjectPop;
		self.callback = callback;
		self.build();
		popup(self.id);
	},
	zz_pickMapObjectPop:0
};