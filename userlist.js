"use strict";
var model={
	isUser:true,
	users:null,
	accept:function(row,callback)
	{
		var innerCallback = function(res)
		{
			if(res.ok)
			{
				var newFriend = res.user;
				var friendRec = res.friend;
				model.requests.remove(friendRec.Row);
				model.users.add(newFriend);
				if(callback!=null)
				{
					callback(res);
				}
			}
		};
		sr5.ajaxAsync({fn:"friendAccept",row:row},innerCallback);
	},
	addFriend:function(login,callback)
	{
		var innerCallback = function(res)
		{
			if(res.ok)
			{
				var user = res.user;
				var friend = res.friend;
				user.FriendFriend = friend.Friend;
				user.FriendConfirmed = friend.Confirmed;
				user.FriendRow = friend.Row;
				user.FriendUser = friend.User;
				model.requests.add(user);
				if(callback!=null)
				{
					callback(res);
				}
			}
		};
		sr5.ajaxAsync({fn:"friendRequest",login:login},innerCallback);
	},
	decline:function(row,callback)
	{
		var innerCallback = function(res)
		{
			if(res.ok)
			{
				var declinee = model.requests.get(row);
				model.requests.remove(row);
				if(callback!=null)
				{
					callback(declinee);
				}
			}
		};
		sr5.ajaxAsync({fn:"friendDecline",row:row},innerCallback);
	},
	removeFriend:function(row,callback)
	{
		var innerCallback = function(res)
		{
			if(res.ok)
			{
				var removee = model.users.get(row);
				model.users.remove(row);
				if(callback!=null)
				{
					callback(removee);
				}
			}
		};
		sr5.ajaxAsync({fn:"friendRemove",friendUserRow:row},innerCallback);
	},
	selectPlayer:function(row,callback){
		sr5.selectPlayer(row,callback);
	},
	selectUserDetails:function(userRow)
	{
		var callback = function(res){
			if(res.ok)
			{
				var user = model.users.get(userRow);
				user.list = res.list;
				view.buildCharacters(user);
				view.toggleUserDetail(userRow);
			}
		};
		sr5.ajaxAsync({fn:"selectUserCharacters",userRow:userRow},callback);
	},
	zz_model:0	
};
var view = {
		searchArg:"",
		aaOnLoad:function(){
			view.sortList();
			view.buildUsers();
			view.buildRequests();
			sr5.initHover();
			sr5.doneLoading();
		},
		accept:function(row){
			model.accept(row,view.afterAccept);
		},
		addFriend:function(){
			var login = ir.v("addUserLogin");
			model.addFriend(login,view.afterAddFriend);
			view.toggleAddUserButton();
			return false;
		},
		addUser:function(){
			if(!model.isUser)
			{
				userDetailPop.show(ir.copy(model.blankUser),view.afterAddUser);
			}
		},
		addUserKeydown:function(event){
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
					view.addFriend();
					return false;
					break;
				default:
					break;
			}
		},
		afterAccept:function(res){
			view.buildUsers();
			view.buildRequests();
			sr5.initHover();
			Status.success(res.user.Name + " has been added to the User List.");
		},
		afterAddFriend:function(res){
			view.buildRequests();
			sr5.initHover();
			Status.success(res.user.Name + " request has been sent.");
		},
		afterAddUser:function(res){
			model.users.add(res.user);
			view.buildUsers();
			sr5.initHover();
		},
		afterCancel:function(res){
			view.buildRequests();
			sr5.initHover();
			Status.success(res.Name + " request canceled.");
		},
		afterDecline:function(res){
			view.buildRequests();
			sr5.initHover();
			Status.success(res.Name + " request declined.");
		},
		afterDetail:function(res){
			if(res.deleted)
			{
				model.users.remove(res.user.Row);				
			}
			view.buildUsers();
			sr5.initHover();
		},
		afterRemove:function(res)
		{
			view.buildUsers();
			sr5.initHover();
			Status.success(res.Name + " has been removed from User List.");
		},
		applySearch:function(content){
			if(view.searchArg.length<3)
			{
				return content;
			}
			return content.replace(new RegExp(view.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
		},
		buildCharacters:function(user){
			var container = ir.get("userCharactersList"+user.Row);
			var arr = user.list;
			var htm = "";
			for (var i=0,z=arr.length;i<z;i++)
			{
				var s = arr[i];
				if(model.isUser && s.Type!=="PC")
				{
					continue;
				}
				var template = "<div style='flex:0;margin:0.25rem;position:relative;' class='hover' data-hover='{2}' onclick='view.openCharacterSheet({1})'>{0}</div>";
				htm += ir.format(template,
						view.getPortrait(s),
						s.Row,
						s.Name);
			}
			container.innerHTML = htm;
			sr5.initHover();
		},
		buildRequests:function()
		{
			var container = ir.get("requestList");
			var arr = model.requests.values;
			var top = "";
			var bottom = "";
			var requestCount = 0;
			var template = "<div class='section nohover shadow flex' style='justify-content:space-between;align-items:center;'>"
				 + "<div style='flex-basis:60%;'>"
				 + "<div class='title'><b>{0}</b></div>"
				 + "<div class='subtitle'><b>{1}</b></div>"
				 + "</div>"
				 + "<div class='message flex' style='justify-content: flex-end;flex:1;'>{2}</div>"
				  + "</div>";
			for (var i=0,z=arr.length;i<z;i++)
			{
				var s = arr[i];
				if(view.filterUser(s))
				{
					continue;
				}				
				var message = "";
				if(s.FriendFriend == sr5.user.Row)
				{
					message = "<div class='flex'><button class='mini' type='button' onclick='view.accept("+s.FriendRow+")'>Accept</button><button type='button' class='mini' onclick='view.decline("+s.FriendRow+")'>Decline</button></div>"
					top += ir.format(template,
							view.applySearch(s.Name),
							view.applySearch(s.EMail),
							message);
				}
				else if(s.FriendUser == sr5.user.Row)
				{
					message = "<h3 style='margin:0.5rem;'>Request is pending.</h3><div class='spacer'></div><button class='mini' type='button' onclick='view.decline("+s.FriendRow+")'>Cancel</button>"
					bottom += ir.format(template,
							view.applySearch(s.Name),
							view.applySearch(s.EMail),
							message);
				}
				else
				{
					continue;
				}				
				requestCount++;
			}
			container.innerHTML = top + bottom + "</div>";
			ir.set("requestCount",requestCount!=arr.length?requestCount+" / " + arr.length:requestCount);
		},
		buildUsers:function()
		{
			var container = ir.get("userList");
			var arr = model.users.values;
			var htm = "";
			var userCount = 0;
			for (var i=0,z=arr.length;i<z;i++)
			{
				var s = arr[i];
				if(view.filterUser(s))
				{
					continue;
				}
				var allowMessage = s.Row != sr5.user.Row;
				var template = "<div class='section shadow' onclick='view.toggleUserDetail({5})'>"
								 + (!model.isUser?
								 "<div class='userDetailBtn imgBtn hover' data-hover='Edit user details' onclick='view.showUserDetail(event,{5})'><img src='"+sr5.iconPath+"fingerprints.svg'></div>"
								 : "<div class='userDetailBtn imgBtn hover' data-hover='Remove User' onclick='view.removeFriend({5})'><img src='"+sr5.iconPath+"dead.svg'></div>" )
								 + (allowMessage?
								 "<div class='userDetailBtn imgBtn hover' style='right:4.75rem;' data-hover='Send Message' onclick='view.messageUser({5})'><img src='"+sr5.iconPath+"message.svg'></div>"
								 : "" )
								 + "<div class='title'><b>{0}</b></div>"
								 + "<div class='subtitle'><b>{1}</b></div>"
								 + (!model.isUser?"<div>&emsp;<b>Role:</b> {2}</div>":"")
								 + (!model.isUser?"<div>&emsp;<b>Created At:</b> {3}</div>":"")
								 + "<div>&emsp;<b>Last Login:</b> {6}</div>"
								 + "<div>&emsp;<b>Source Books:</b> {4}</div>"
								 + (!model.isUser?"<div>&emsp;<b>Theme:</b> {7}</div>":"")
								 + "<div class='source'>{5}</div>"
								 + "<div class='charactersWrap' id='userCharactersWrap{5}'>"
								 	+"<div class='subtitle'>Characters</div>"
								 	+ "<div class='characters flex' style='align-items:center;justify-content:flex-start;' id='userCharactersList{5}' class='flex'></div>"
							 + "</div></div>";
				htm += ir.format(template,
						view.applySearch(s.Name),
						view.applySearch(s.EMail),
						view.applySearch(s.Role),
						view.applySearch(view.getTime(s.CreatedAt)),
						view.applySearch(s.SourceBooks.replace(new RegExp(",", 'g'), ", ")),
						s.Row,
						s.LastLogin?view.getTime(s.LastLogin.substring(0,18)):"",
						s.ThemeRow);
				//content = content.replace(new RegExp(view.searchArg, 'gi'),function(v){return "<span class='highlight'>"+v+"</span>"});
				userCount++;
			}
			container.innerHTML = htm + "</div>";
			ir.set("userCount",userCount!=arr.length?userCount+" / " + arr.length:userCount);
		},
		cancel:function(row){
			model.decline(row,view.afterCancel);
		},
		decline:function(row){
			model.decline(row,view.afterDecline);
		},
		filterUser:function(s)
		{
			if(view.searchArg.length<3)
			{
				return false;
			}
			if(s.Name.toLowerCase().indexOf(view.searchArg)>-1)
			{
				return false;
			}
			else if(s.EMail!=null && s.EMail.toLowerCase().indexOf(view.searchArg)>-1)
			{
				return false;
			}
			else if(s.ShortName.toLowerCase().indexOf(view.searchArg)>-1)
			{
				return false;
			}
			else if(s.Role.toLowerCase().indexOf(view.searchArg)>-1)
			{
				return false;
			}
			else if(s.SourceBooks.toLowerCase().indexOf(view.searchArg)>-1)
			{
				return false;
			}
			return true;			
		},
		getPortrait:function(s)
		{
			if(s!=null && s.Extension && s.Extension.length>0)
			{
				var img = {Row:s.Portrait,User:s.UserImage,Extension:s.Extension,Type:"Face"};
				return sr5.getThumb(img);
			}
			else
			{
				return sr5.getThumbUnknown();
			}
		},
		getTime:function(time){
			try{
				return irdate.friendly(irdate.offsetTimeZone(new Date(time.replace(/-/g, '/'))),true);
			}
			catch(e){
				return time;
			}
		},
		messageUser:function(row){
			var user = model.users.get(row);
			var callback = function(){
				messengerPop.newThread();
				var threadId = messengerPop.lastState;
				ir.get("messagePopupAddUserToggleBtn"+threadId).click();
				ir.set("messagePopupAddUserLogin"+threadId,user.Login);
				ir.get("messagePopupAddUserBtn"+threadId).click();
				ir.focus("messagePopupText"+threadId);
			};
			if(!messengerPop.initialized)
			{
				messengerPop.initThreads(callback);
			}
			else
			{
				callback();
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
		pickTab:function(tabName){
			var tabs = document.getElementsByClassName("tab");
			for(var i=0,z=tabs.length;i<z;i++)
			{
				var tab = tabs[i];
				var id = tab.id.substring(3,tab.id.length);
				if(id===tabName)
				{
					tab.classList.add("selected");
					ir.show("div"+id);
				}
				else
				{
					tab.classList.remove("selected");
					ir.hide("div"+id);
				}
			}
		},
		removeFriend:function(row){
			var user = model.users.get(row);
			var callback=function(yes){
				if(yes)
				{
					model.removeFriend(row,view.afterRemove);
				}
			};
			confirmPop.show(ir.format("Are you sure you want to remove <i>{0}</i>  from User List?",user.Name),callback);
		},
		searchKeyup:function(arg)
		{
			if(view.searchArg != arg)
			{
				view.searchArg = arg;
				view.buildUsers();
			}
			
		},	
		showUserDetail:function(event,userRow){
			userDetailPop.show(model.users.get(userRow),view.afterDetail);
			event.stopPropagation();
			return;
		},
		sortList:function(){
			var arr = model.sortType;
			arr.push(arr.shift());
			var list = model.users.values;
			var multiplier = model.sortType[0].name === "LastLogin"?-1:1;
			list = list.sort(function(a,b){
				if(a[model.sortType[0].name] < b[model.sortType[0].name])
				{
					return -1 * multiplier;					
				}
			    if(a[model.sortType[0].name] > b[model.sortType[0].name])
		    	{
			    	return 1 * multiplier;
		    	}
			    if(a.Name < b.Name)
		    	{
			    	return -1 * multiplier;
		    	}
			    else {
			    	return 1 * multiplier;
			    }
			    return 0;
			});
			view.buildUsers();
			Status.info("Sorting by " + arr[0].name,2000);
		},	
		toggleAddUserButton:function(ele)
		{
			sr5.toggleChildButtons("addUserBtn");
			ir.set("addUserLogin","");
			if(!sr5.isMobile)
			{
				ir.focus("addUserLogin");
			}
		},
		toggleUserDetail:function(id)
		{
			var user = model.users.get(id);
			var s = ir.get("userCharactersWrap" + id);
			if(user.list != null)
			{
				if(!s.classList.contains("show"))
				{
					s.classList.add("show");
					s.parentNode.classList.add("nohover");
				}
			}
			else
			{
				return model.selectUserDetails(id);
			}
		},
	zz_view:0
};