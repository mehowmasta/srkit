var userDetailPop = {
	currentUser:null,
	id:"userDetailPop",
	roles:null,
	afterAdd:function(res){
		var self = userDetailPop;
		if(res.ok)
		{
			Status.info(self.currentUser.Name + " has been added.",4000);
			if(self.callback!=null)
			{
				self.callback(res);
			}
			self.close();
		}
	},
	afterDelete:function(res){
		var self = userDetailPop;
		if(res.ok)
		{
			Status.info(self.currentUser.Name + " has been deleted.",4000);
			if(self.callback!=null)
			{
				res.deleted = true;
				self.callback(res);
			}
			self.close();
		}
	},
	afterReset:function(res){
		var self = userDetailPop;
		if(res.ok)
		{
			Status.info(self.currentUser.Name + " password has been reset.",4000);
			if(self.callback!=null)
			{
				self.callback(res);
			}
			self.close();
		}
	},
	afterUpdate:function(res){
		var self = userDetailPop;
		if(res.ok)
		{
			Status.info(self.currentUser.Name + " has been update.",4000);
			if(self.callback!=null)
			{
				self.callback(res);
			}
			self.close();
		}
	},
	close:function(){
		var self = userDetailPop;
		self.currentUser=null;
		popup(self.id);
	},
	deleteUser:function(){
		var self = userDetailPop;
		var user = self.currentUser;
		if(user.Row < 1)
		{
			return;
		}
		var callback = function(yes)
		{
			if(yes)
			{
			sr5.ajaxAsync({fn:"deleteUser",row:user.Row},self.afterDelete);
			}
		}
		confirmPop.show("Are you sure you want to delete <i>"+user.Name+"</i>?",callback);
	},
	initRole:function(){
		var self = userDetailPop;
		if(self.roles==null)
		{
			var callback=function(res){
				if(res.ok)
				{
					self.roles = res.roles;
					var selectBox = ir.get(self.id + "Role");
					for(var i = 0, z = res.roles.length; i<z;i++)
					{
						var a = res.roles[i];
						addOption(selectBox,a.Row,a.Row);
					}
					if(self.currentUser!=null)
					{
						ir.set(self.id+"Role",self.currentUser.Role);
					}
				}
			};
			sr5.ajaxAsync({fn:"selectRoles"},callback);
		}
	},
	initUser:function(){
		var self = userDetailPop;
		var user = self.currentUser;
		ir.set(self.id + "Name",user.Name);
		ir.set(self.id + "Role",user.Role);
		ir.set(self.id + "EMail",user.EMail);
	},
	read:function(){
		var self = userDetailPop;
		var hasError = false;
		var user = self.currentUser;
		user.Name = ir.escapeHtml(ir.v(self.id + "Name"));
		user.Role = ir.v(self.id + "Role");
		user.EMail = ir.escapeHtml(ir.v(self.id + "EMail"));
		if(user.Name.length==0)
		{
			hasError = true;
			Status.error("Please enter a Name,",5000);
		}
		if(user.Role.length==0)
		{
			hasError = true;
			Status.error("Please select a valid Role",5000);
		}
		if(!ir.checkEmail(user.EMail))
		{
			hasError = true;
			Status.error("Please enter a valid Email address,",5000);
		}		
		return !hasError;
	},
	reset:function(){
		var self = userDetailPop;
		var user = self.currentUser;
		if(user.Row < 1)
		{
			return;
		}
		var callback = function(yes)
		{
			if(yes)
			{
				sr5.ajaxAsync({fn:"resetPassword",userRow:user.Row},self.afterReset);
			}
		}
		confirmPop.show("Are you sure you want to reset <i>"+user.Name+"</i>'s password?",callback);
	},
	show:function(user,callback){
		var self = userDetailPop;
		self.currentUser = user;
		self.callback = callback;
		self.initRole();
		self.initUser();
		ir.show(self.id + "RemoveBtn",self.currentUser.Row>0);
		ir.show(self.id + "ResetBtn",self.currentUser.Row>0);
		ir.show(self.id + "AddBtn",self.currentUser.Row==0);
		ir.show(self.id + "UpdateBtn",self.currentUser.Row>0);
		ir.set(self.id+"Title",user.Row>0?"Update " + user.Name:"Add new user");
		popup(self.id);
	},
	update:function(){
		var self = userDetailPop;
		if(self.read())
		{
			var user = self.currentUser;
			var isNew = user.Row==0;
			var callback = function(res){
				if(res.ok)
				{
					if(isNew)
					{
						self.afterAdd(res);
					}
					else
					{
						self.afterUpdate(res);
					}
				}
			};
			sr5.ajaxAsync({fn:"updateUser",row:user.Row,role:user.Role,email:user.EMail,name:user.Name},callback);
		}
		
	},
	zz_userDetailPop:0
};