'use strict';
var friendPop = {
		friends:null,
		friendTemplate:'<div class="section " style="max-width:inherit;" onclick="friendPop.pick({0})">'
					+ '<div class="title"><b>{1}</b></div>'
					+ '<div class="subtitle"><b>{2}</b></div>'
					+ '</div>',
		id:'friendPop',
		build:function(){
			var self = friendPop;
			var friends = sr5.friends.values;
			var htm="";
			for(var i=0,z=friends.length;i<z;i++)
			{
				var a = friends[i];
				htm += ir.format(self.friendTemplate,
						a.Row,
						a.Name,
						a.EMail
				);
			}
			ir.get(self.id +"List").innerHTML = htm;
		},
		close:function(){
			var self = friendPop;
			popup(self.id);
		},
		pick:function(userRow){
			var self = friendPop;
			var user = sr5.friends.get(userRow);
			if(user!=null && self.callback != null)
			{
				self.callback(user);
			}
			self.close();			
		},
		show:function(){
			var self = friendPop;
			self.build();
			popup(self.id);
		},
		zz_friendPop:0
		
};