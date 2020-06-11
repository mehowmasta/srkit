var messengerPop = {
	id:"messengerPopup",
	initialized:false,
	intro:"Welcome to your Comm Link.♥A communication tool used to interact with other users in SrKit.  Create new links and connect to other users using their login credential.  Enable dice roll sharing to automatically share dice rolls with respective link.",
	lastState:0,
	messagePopupTemplate:null,
	newThreadCount:0,
	threads:null,
	addFriend:function(ele){
		var self = messengerPop;
		friendPop.callback= function(user){self.afterFriend(user,ele)};
		friendPop.show();
	},
	addMessage:function(res){
		var self = messengerPop;
		var template = "<div id='threadMessage{4}' class='threadMessageWrap {3}'><div class='flex' style='justify-content: space-between;'><div class='threadMessageUser'>{0}</div><div class='threadMessageDate'>{1}</div></div><div class='threadMessage'>{2}</div></div>";
		var container = ir.get("messagePopupMessages"+res.threadId);
		var msg = res.message.Message;
		var side = "left";
		if(sr5.user.Row == res.message.CreatedBy)
		{
			side = "right";
		}
		container.innerHTML += ir.format(template,res.fromName,self.getTime(res.fromDate),msg.replace(/\n/g, "<br />"),side,res.message.Row);
		if(!self.isThreadVisible(res.threadId))
		{
			Status.success("<i>"+res.fromName+"</i><br>"+msg.replace(/\n/g, "<br />"),30000);
		}
	},
	addMap:function(res){
		var self = messengerPop;
		var template = "<div id='threadMessage{4}' class='threadMessageWrap {3}'>"
				+ "<div class='flex' style='justify-content: space-between;'>"
				+ "<div class='threadMessageUser'>{0}</div>" 
				+ "<div class='threadMessageDate'>{1}</div>" 
				+ "</div>" 
				+ "<div class='threadMessage flex' style='justify-content:flex-start;'><div class='portraitThumb' onclick='messengerPop.previewImage({5},\"{6}\",{7},\"{8}\")'>{2}</div></div>" 
				+ "</div>";
		var container = ir.get("messagePopupMessages"+res.threadId);
		var msg = res.message.Message.split(".");
		var side = "left";
		if(sr5.user.Row == res.message.CreatedBy)
		{
			side = "right";
		}
		var image = {Type:res.message.Type,Extension:msg[1],Row:msg[0],User:res.message.CreatedBy};
		container.innerHTML += ir.format(template,res.fromName,self.getTime(res.fromDate),sr5.getThumb(image),side,res.message.Row,image.Row,image.Type,image.User,image.Extension);
		if(!self.isThreadVisible(res.threadId))
		{
			Status.success("<i>"+res.fromName+"</i><br>"+sr5.getThumb(image),30000);
		}
	},
	addNotification:function(res){
		var self = messengerPop;
		var template = "<div id='threadMessage{3}' class='threadNotificationWrap'><div class='threadNotification'>-- {2} --</div></div>";
		var container = ir.get("messagePopupMessages"+res.threadId);
		var msg = res.message.Message;
		container.innerHTML += ir.format(template,res.fromName,self.getTime(res.fromDate),msg,res.message.Row);
		if(!self.isThreadVisible(res.threadId))
		{
			Status.success("<i>"+res.fromName+"</i><br>"+msg.replace(/\n/g, "<br />"),30000);
		}
	},
	addRoll:function(res){
		var self = messengerPop;
		var template = "<div id='threadMessage{4}' class='threadMessageWrap {3}'><div class='flex' style='justify-content: space-between;'><div class='threadMessageUser'>{0}</div><div class='threadMessageDate'>{1}</div></div><div class='threadMessage'>{2}</div></div>";
		var container = ir.get("messagePopupMessages"+res.threadId);
		var msg = res.message.Message;
		var side = "left";
		if(sr5.user.Row == res.message.CreatedBy)
		{
			side = "right";
		}
		container.innerHTML += ir.format(template,res.fromName,self.getTime(res.fromDate),msg.replace(/\n/g, "<br />"),side,res.message.Row);
		if(!self.isThreadVisible(res.threadId))
		{
			Status.success("<i>"+res.fromName+"</i><br>"+msg.replace(/\n/g, "<br />"),30000);
		}
	},
	addUser:function(ele){
		var self = messengerPop;
		var threadId = ir.n(self.getPopupThreadId(ele));
		var thread = self.threads.get(threadId);
		if(thread==null)
		{
			return;
		}
		var userLogin = ir.escapeHtml(ir.v("messagePopupAddUserLogin"+threadId));
		var callback = function(res){
			if(res.ok)
			{
		    	if(threadId > 0 && thread.createdBy!=sr5.user.Row)
				{//only allow the user that created the thread to add more users
		    		return;
				}
		    	var lookup = thread.users.get(res.user.Row);
		    	if(lookup == null)
				{//if the user doesn't exist in the thread
	    			thread.users.add(res.user);
	    			self.fillUserList(threadId);
				}
		    	ir.set("messagePopupAddUserLogin"+threadId,"");
		    	self.toggleAddUserButtons(ele);
				if(!sr5.isMobile)
				{
					ir.focus("messagePopupText"+threadId);
				}
			}
	    }
		sr5.ajaxAsync({fn:"addMessageThreadUser",threadId:threadId,userLogin:userLogin},callback);
	},
	afterAddMessage:function(res){
		var self = messengerPop;
		var thread = self.threads.get(res.threadId);
		thread.messages.push(res.message);
		thread.lastMessage = res.message.Message;
		thread.lastDate = res.fromDate;
		if(res.fromRow == sr5.user.Row)
		{
			thread.lastRowSeen = res.message.Row;
		}
		self.updateThreadDiv(thread);
	},	
	afterFriend:function(user,ele){
		var self = messengerPop;
		var threadId = ir.n(self.getPopupThreadId(ele));
		ir.set("messagePopupAddUserLogin"+threadId,user.Login);
		self.addUser(ir.get("messagePopupAddUserBtn"+threadId));
	},
	afterShareMap:function(threadId,map){
		var self = messengerPop;
		self.sendMap(threadId,map);
	},
	back:function(ele){
		var self = messengerPop;
		popup("messagePopup"+self.getPopupThreadId(ele));
		popup(self.id);
		self.lastState = 0;
	},
	buildThreads:function(){
		var self = messengerPop;
		var container = ir.get(self.id+"Threads");
		container.innerHTML = "";
		var threads = self.threads.values.sort(function(a,b){return b.timeStamp-a.timeStamp});
		if(threads.length==0)
		{
			container.innerHTML = "<div style='font-family:VT323;font-size:2rem;'>0 active links</div>";
		}
		for(var i =0,z=threads.length;i<z;i++)
		{
			var t = threads[i];
			if(t.threadId<1)
			{
				continue;
			}
			container.innerHTML += self.getThreadDiv(t);
		}
	},
	close:function(){
		var self = messengerPop;
		var commlink = ir.get(self.id);
		if(commlink.classList.contains("show"))
		{
			popup(self.id);
		}
	},
	closeThread:function(ele){
		var self = messengerPop;
		popup("messagePopup"+self.getPopupThreadId(ele));
	},
	deleteThread:function(ele){
		var self = messengerPop;
		var threadId = self.getPopupThreadId(ele);
		var callback = function(yes){
			if(yes)
			{
				self.closeThread(ele);
				self.removeThreadDiv(threadId);
				sr5.ajaxAsync({fn:"deleteMessageThread",threadId:threadId},null);
				self.lastState=0;
				self.threads.remove(threadId);
			}
		};
		confirmPop.show("Are you sure you want to delete this link?",callback);
	},
	fillUserList:function(id){
		var self = messengerPop;
	    var checkedString = "";
	    var checkedComma = "";
	    var checkedCount = 0;
    	var thread = self.threads.get(id);
    	if(thread == null || thread.users == null)
		{
    		return;
		}
	    var users = thread.users.values;
	    var htm ="";
	    var template = "<div class='messageUserIcon'>{0}<label>{1}</label></div>"
	    for(var i=0, z = users.length;i<z;i++)
	    {	        
	    	var user = users[i];
	    	var x ="";
	    	if(user.Row == sr5.user.Row)
    		{
	    		ir.set("messagePopupShareRoll"+id,user.ShareRoll);
	    		continue;
    		}   
	    	if(thread.createdBy == sr5.user.Row)
    		{
		    	x = "<div class='x hover' data-hover='Remove this user from this link' onclick='messengerPop.removeUser(this,"+user.Row+")'>X</div>";
    		}
	    	if(user.Portrait>0)
    		{
	    		htm += ir.format(template,sr5.getThumb({Type:"Face",User:user.UserImage,Row:user.Portrait,Extension:user.Extension},true) + x,ir.ellipsis(user.Name,12));
    		}
	    	else
    		{
	    		htm += ir.format(template,sr5.getThumbUnknown(false)+ x,ir.ellipsis(user.Name,12));
    		}
	    }
	    ir.set("messagePopupDivUsers"+id,htm);
	},
	getPopupThreadId:function(ele){
		while(!ele.classList.contains("popup") && ele!=null)
		{
			ele = ele.parentNode;
		}
		return ele.id.substring("messagePopup".length,ele.id.length);
	},
	/**
	 * Returns a Div to place in the thread list, to show all available threads for this user 
	 */
	getThreadDiv:function(t)
	{
		var self = messengerPop;
		var template = "<div id='messengerPopupThread{3}' onclick='messengerPop.openThread({3})' class='threadWrap {4}'><div class='threadUserImages'>{5}</div> <div class='threadUsers'>{0}</div><div class='spacer'></div><div class='threadDate'>{1}</div><div class='threadMessage'>{2}</div></div>";
		var usersStr = "";
		var userImgs = "";
		var comma = "";
		var users = t.users.values;
		for(var i =0, z=users.length;i<z;i++)
		{
			var user = users[i];
			if(user!=null)
			{
				if(user.Row == sr5.user.Row)
				{// don't show logged in users name in conversation, it's obvious they are part of the conversation
					continue;
				}
				usersStr += comma + user.Name;
				if(user.Portrait>0)
	    		{
					userImgs += sr5.getThumb({Type:"Face",User:user.UserImage,Row:user.Portrait,Extension:user.Extension},true);
	    		}
		    	else
	    		{
		    		userImgs += sr5.getThumbUnknown(false);
	    		}
			}
			comma = ", ";
		}
		var seen = false;	
		var lastUserName = "";
		var lastMessage = null;
		if(t.messages && t.messages.length>0)
		{			
			seen = t.lastRowSeen>=t.messages[t.messages.length-1].Row;
			var lastMessage = t.messages[t.messages.length-1];
			var theUser = t.users.get(lastMessage.CreatedBy);
			if(theUser!=null)
			{
				var lastUserName = theUser.Name;
			}
		}
		var message = "";
		if(lastMessage!=null && lastMessage.Type === "Message")
		{
			message = lastUserName+": " + ir.ellipsis(t.lastMessage,80);
		}
		else if(lastMessage!=null && lastMessage.Type === "Map")
		{
			message = lastUserName+": [MAP]";
		}
		else
		{
			message = ir.ellipsis(t.lastMessage,60);
		}
		return ir.format(template,usersStr,self.getTime(t.lastDate),message,t.threadId,seen?"":"pulse",userImgs);
	},
	getTime:function(time){
		try{
			return irdate.friendly(irdate.offsetTimeZone(new Date(time.replace(/-/g, '/'))),true);
		}
		catch(e){
			return time;
		}
	},
	initFriends:function(){
		var self = messengerPop;
		if(!self.initialized)
		{
			var friends = sr5.friends.values;
			var container = ir.get("messagePopupAddUserList");
			for(var i=0,z=friends.length;i<z;i++)
			{
				container.innerHTML += "<option value='"+friends[i].Login+"'>";
			}
		}
	},
	initThreads:function(outerCallback){
		var self = messengerPop;
		if(self.threads==null)
		{
			var callback=function(){
				self.buildThreads();
				if(outerCallback!=null)
				{
					outerCallback();
				}
			};
			self.selectThreads(callback);
			sr5.type(self.id+"Intro",self.intro);
		}
		else
		{
			self.buildThreads();
			if(outerCallback!=null)
			{
				outerCallback();
			}
		}
	},
	isThreadExist:function(threadId){
		return ir.exists("messengerPopupThread"+threadId);
	},
	isThreadOpen:function(threadId){
		return ir.exists("messagePopup"+threadId);
	},
	isThreadVisible:function(threadId){
		return ir.exists("messagePopup"+threadId) && ir.get("messagePopup"+threadId).classList.contains("show");
	},
	keydown:function(event)
	{
		var self = messengerPop;
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
				self.sendMessage(event.target)
				return;
				break;
			case 38:// up
				return;
				break;
			case 40:// down
				return;
				break;
			default:
				break;
		}
	},	

	leaveThread:function(ele){
		var self = messengerPop;
		var threadId = self.getPopupThreadId(ele);
		var callback = function(yes){
			if(yes)
			{
				self.closeThread(ele);
				self.removeThreadDiv(threadId);
				self.removeMessageDiv(threadId);
				self.lastState=0;
				sr5.ajaxAsync({fn:"leaveMessageThread",threadId:threadId},null);
				self.threads.remove(threadId);
			}
		};
		confirmPop.show("Jack out from this link?",callback);
	},
	newThread:function(){
		var self = messengerPop;		
        self.close();
		if (self.messagePopupTemplate==null) {
			self.messagePopupTemplate = ir.get("messagePopupTemplate").innerHTML;
		}
		var container = ir.get(self.id + "MessageThreads");
		var thread = {threadId:--self.newThreadCount,users:new KeyedArray("Row",sr5.user),messages:[],createdBy:sr5.user.Row};
		if(self.threads==null)
		{
			self.threads = new KeyedArray("threadId");
		}	
		self.threads.add(thread);
		container.innerHTML += ir.template(self.messagePopupTemplate,thread);
		var pop = ir.get("messagePopup"+thread.threadId)
		ir.show("messagePopupDeleteBtn"+thread.threadId,false);
		ir.show("messagePopupLeaveBtn"+thread.threadId,false);
		ir.show("messagePopupShareRollWrap"+thread.threadId,thread.threadId>0);
		popup(pop);
		self.lastState = thread.threadId;
		return false;
	},
	openThread:function(threadId){
		var self = messengerPop;
        self.close();
		if(ir.exists("messagePopup"+threadId))
		{
			var pop = ir.get("messagePopup"+threadId);
			popup(pop);
			self.lastState = threadId;
	        self.updateThreadSeen(threadId);
			window.setTimeout(function(){ir.scrollToBottom("messagePopupMessages"+threadId);},5);
			return;
		}
		if (self.messagePopupTemplate==null) {
			self.messagePopupTemplate = ir.get("messagePopupTemplate").innerHTML;
		}
		var thread = self.threads.get(threadId);
		var container = ir.get(self.id + "MessageThreads");
		container.innerHTML += ir.template(self.messagePopupTemplate,thread);	
		ir.show("messagePopupAddUserToggleBtn"+threadId,thread.createdBy==sr5.user.Row);	
		ir.show("messagePopupDeleteBtn"+threadId,thread.createdBy==sr5.user.Row);
		ir.show("messagePopupLeaveBtn"+threadId,thread.createdBy!=sr5.user.Row);
		ir.show("messagePopupShareRollWrap"+threadId,thread.threadId>0);
        self.fillUserList(threadId);
		popup("messagePopup"+thread.threadId);
		self.lastState = thread.threadId;
		var groupMessage = "";
        for(var i=0,z=thread.messages.length;i<z;i++)
    	{
        	var m = thread.messages[i];
        	if(i < z-1)
    		{
        		var nextMessage = thread.messages[i+1];
            	if(nextMessage.CreatedBy == m.CreatedBy 
            			&& irdate.minsDiff(new Date(nextMessage.CreatedAt.replace(/-/g, '/')),new Date(m.CreatedAt.replace(/-/g, '/')))<5 
            			&& nextMessage.Type === "Message" 
            			&& m.Type === "Message")
        		{
            		groupMessage+=m.Message + "<br>";
            		continue;
        		}
    		}
        	if(groupMessage.length>0)
    		{
            	m.Message = groupMessage + m.Message;
    		}
        	var theUser = thread.users.get(m.CreatedBy);
        	var res = {threadId:threadId,message:m,fromName:(theUser!=null?theUser.Name:""),fromDate:m.CreatedAt};
        	self.parseMessage(res);
        	groupMessage="";
    	}
        sr5.initHover();
        self.updateThreadSeen(threadId);
		if(!sr5.isMobile)
		{
			ir.focus("messagePopupText"+threadId,"");
		}
		window.setTimeout(function(){ir.scrollToBottom("messagePopupMessages"+threadId);},200);
	},
	parseMessage:function(res)
	{
		var self = messengerPop;
		var m = res.message;
		if(m.Type === "Message")
		{
        	self.addMessage(res);
		}
    	else if (m.Type === "Notification")
		{
    		self.addNotification(res);
		}
    	else if (m.Type === "Roll")
		{
    		self.addRoll(res);    		
		}
    	else if (m.Type === "Map")
		{
    		self.addMap(res);
		}
	},
	previewImage:function(row,type,user,extension){
		var image = {Type:type,Extension:extension,Row:row,User:user};
		var callback = function(res){
			if(res.ok)
			{
				imagePreviewPop.show(res.map);
			}
		};
		sr5.ajaxAsync({fn:"selectSharedMap",row:image.Row,user:image.User},callback);
	},
	receiveAddUser:function(res){
		var self = messengerPop;
		if(self.isThreadExist(res.threadId))
		{
			var thread = self.threads.get(res.threadId);
			thread.users.add(res.addUser);
			res.message = res.addMessage;
			res.fromName = thread.users.get(res.message.CreatedBy).RowName;
			res.fromDate = irdate.friendly(res.message.CreatedAt,true);
			self.addNotification(res);
			self.afterAddMessage(res);
			if(self.isThreadOpen(res.threadId))
			{
    			self.fillUserList(res.threadId);
			}	
			if(self.isThreadVisible(res.threadId))
			{
		        self.updateThreadSeen(res.threadId);
			}			
		}
	},
	receiveMessage:function(res){
		var self = messengerPop;
		if(self.isThreadExist(res.threadId))
		{
			var thread = self.threads.get(res.threadId);
			if(self.isThreadOpen(res.threadId))
			{
				var lastMessage = thread.messages[thread.messages.length-1];
				var m = res.message;
				var resCopy = null;
				if(lastMessage.CreatedBy == m.CreatedBy 
						&& Math.abs(irdate.minsDiff(new Date(lastMessage.CreatedAt.replace(/-/g, '/')),new Date(m.CreatedAt.replace(/-/g, '/'))))<5 
						&& lastMessage.Type === "Message" 
						&& m.Type === "Message")
				{
					resCopy = ir.copy(res);
					var lastMessageDiv = ir.get("threadMessage"+lastMessage.Row);
					var lastMessage = lastMessageDiv.getElementsByClassName("threadMessage")[0].innerHTML;
					resCopy.message.Message = lastMessage + "<br>" + res.message.Message;
					lastMessageDiv.parentNode.removeChild(lastMessageDiv);
				}
				if(resCopy!=null)
				{
					self.addMessage(resCopy);
				}
				else
				{
					self.parseMessage(res);
					
				}
				self.afterAddMessage(res);
				window.setTimeout(function(){ir.scrollToBottom("messagePopupMessages"+res.threadId);},100);
			}
			else
			{				
				self.afterAddMessage(res);
				self.updateThreadDiv(thread);
			}
			if(res.fromRow !=sr5.user.Row)
			{
				//playMessageSound();
			}
			if(res.oldThreadId!=res.threadId && self.isThreadVisible(res.oldThreadId))
			{
				self.closeThread(ir.get("messagePopup"+res.oldThreadId));
				self.openThread(res.threadId);
			}
			else if(!self.isThreadVisible(res.threadId) && res.message.Type !== "Notification" && res.fromRow != sr5.user.Row)
			{
				self.updateChatCount(1);
			}
			else if(res.fromRow != sr5.user.Row)
			{
		        self.updateThreadSeen(res.threadId);
			}
		}
		else if(sr5.user.Row == res.fromRow && self.isThreadOpen(res.oldThreadId))
		{//this is the user that sent the message, the thread must be new so update the old thread id then add message to thread
			self.updateThreadId(res.oldThreadId,res.threadId);
			ir.show("messagePopupShareRollWrap"+res.threadId,res.threadId>0);
			if(res.message.Type === "Message")
    		{
            	self.addMessage(res);
    		}
        	else if (res.message.Type === "Notification")
    		{
        		self.addNotification(res);
    		}
        	else if (res.message.Type === "Roll")
    		{
        		self.addRoll(res);    		
    		}
			self.afterAddMessage(res);
	        self.updateThreadSeen(res.threadId);
		}
		else
		{//else the thread does not exist and it was not created by this user, ie. it's a new thread for this user
			if( res.fromRow != sr5.user.Row)
			{
				self.updateChatCount(1);
			}
			//playMessageSound();
			if(self.initialized)
			{//messenger has been initialized, ie. all the payload data has already been downloaded
				var newThread = {threadId:res.threadId,timeStamp:new Date().getTime(),messages:[],lastDate:irdate.friendly(res.message.CreatedAt,true),lastMessage:res.message.Message,users:res.users,createdBy:res.createdBy,lastRowSeen:0};
				newThread.messages.push(res.message);
				newThread.users = new KeyedArray("Row",newThread.users);
				self.threads.add(newThread);
				self.buildThreads();
			}
			else
			{
				//if the initail pay load for the messenger has not been downloaded yet, the new message will be included when the user initializes the messenger
				//so do nothing here
				if (res.message.Type === "Roll")
				{
					Status.success("<i>"+res.fromName+"</i><br>"+res.message.Message.replace(/\n/g, "<br />"),30000);
				}
			}
		}
	},
	receiveRemoveUser:function(res){
		var self = messengerPop;
		if(self.isThreadExist(res.threadId))
		{
			var thread = self.threads.get(res.threadId);
			res.message = res.removeMessage;
			res.fromName = thread.users.get(res.message.CreatedBy).Name;
			res.fromDate = irdate.friendly(res.message.CreatedAt,true);
			self.addNotification(res);
			self.afterAddMessage(res);
			thread.users.remove(res.removeUser);
			if(self.isThreadOpen(res.threadId))
			{
    			self.fillUserList(res.threadId);
			}					
			if(self.isThreadVisible(res.threadId))
			{
		        self.updateThreadSeen(res.threadId);
			}
		}
	},
	removeMessageDiv:function(threadId)
	{
		var self = messengerPop;
		var messageDiv = ir.get("messagePopup"+threadId,true);
		if(messageDiv!=null)
		{
			messageDiv.parentNode.removeChild(messageDiv);
		}		
	},
	removeThreadDiv:function(threadId)
	{
		var self = messengerPop;
		var threadDiv = ir.get("messengerPopupThread"+threadId,true);
		if(threadDiv!=null)
		{
			threadDiv.parentNode.removeChild(threadDiv);
		}		
	},	
	removeUser:function(ele,userRow){
		var self = messengerPop;
		var threadId = self.getPopupThreadId(ele);
    	var thread = self.threads.get(threadId);
    	if(thread.createdBy!=sr5.user.Row)
		{
    		return;
		}
    	var callback= function(yes){
    		if(yes)
			{
    			if(threadId>0)
    			{//if thread already exists use ajax to keep users in sync
    				sr5.ajaxAsync({fn:"removeMessageThreadUser",threadId:ir.n(threadId),userRow:ir.n(userRow)},null);
    			}   
				thread.users.remove(userRow);
				self.fillUserList(threadId);
			}
    	};
		confirmPop.show("Remove link with <i>"+thread.users.get(userRow).Name+"</i>?",callback);
		return false;
		
	},
	selectThreads:function(callback){
		var self = messengerPop;
		var innerCallback = function(res){
			if(res.ok){
				self.threads = new KeyedArray("threadId");
				self.threads.add(res.threads);
				self.initialized=true;
			}
			if(callback!=null)
			{
				callback();
			}
		};
		sr5.ajaxAsync({fn:"selectMessageThreads"},innerCallback);
	},
	sendMap:function(threadId,map)
	{
		var self = messengerPop;
		var msg = map.Row+"."+map.Extension;
		var thread = self.threads.get(threadId);
		if(thread.users.values.length < 2)
		{			
			Status.error("Must be linked to another user. Connect User to send map.",5000);
			return;
		}
		sr5.sendMessage(msg,thread,"Map");
	},
	sendMessage:function(ele){
		var self = messengerPop;
		var threadId = self.getPopupThreadId(ele);
		var msg = ir.escapeHtml(ir.v("messagePopupText"+threadId));
		if(msg.length < 1)
		{
			return;
		}
		var thread = self.threads.get(threadId);
		if(thread.users.values.length < 2)
		{			
			Status.error("Must be linked to another user. Connect User to send message.",5000);
			return;
		}
		ir.set("messagePopupText"+threadId,"");
		if(!sr5.isMobile)
		{
			ir.focus("messagePopupText"+threadId,"");
		}
		sr5.sendMessage(msg,thread,"Message");
	},
	shareMap:function(ele){
		var self = messengerPop;
		var threadId = self.getPopupThreadId(ele);
		var callback = function(map){
			self.afterShareMap(threadId,map);
		};
		pickMapPop.show(callback);
		return;
	},
	sharePortrait:function(ele){
		var self = messengerPop;
		var threadId = self.getPopupThreadId(ele);
		var callback = function(map){
			self.afterSharePortrait(threadId,portrait);
		};
		pickPortraitPop.show(callback);
		return;
	},
	show:function(){
		var self = messengerPop;
		self.initFriends();	
		self.initThreads();	
		if(self.lastState==0)
		{
			popup(self.id);
		}
		else
		{
			var popupThread = ir.get("messagePopup"+self.lastState,true);
			if(popupThread==null)
			{
				self.lastState=0;
				popup(self.id);
			}
			else
			{
				popup(popupThread);
				if(!sr5.isMobile)
				{
					ir.focus("messagePopupText"+self.lastState,"");
				}
			}
		}
	},

	toggleAddUserButtons:function(ele)
	{
		var self = messengerPop;
		var threadId = self.getPopupThreadId(ele);
		sr5.toggleChildButtons("messagePopupAddUserToggleBtn"+threadId);
		var thread = self.threads.get(threadId);
		if(ir.get("messagePopupAddUserToggleBtn"+threadId+"Selector").classList.contains("open"))
		{
			ir.get("messagePopupDivUsers"+threadId).classList.add("hide");
			ir.hide("messagePopupDeleteBtn"+threadId);
			ir.hide("messagePopupLeaveBtn"+threadId);
			ir.hide("messagePopupShareRollWrap"+threadId);
		}
		else
		{
			ir.get("messagePopupDivUsers"+threadId).classList.remove("hide");
			ir.show("messagePopupDeleteBtn"+threadId,thread.createdBy==sr5.user.Row);
			ir.show("messagePopupLeaveBtn"+threadId,thread.createdBy!=sr5.user.Row);
			ir.show("messagePopupShareRollWrap"+threadId,thread.threadId>0);
		}
		if(!sr5.isMobile)
		{
			ir.focus("messagePopupAddUserLogin"+threadId);
		}
	},
	updateChatCount:function(increment)
	{
		sr5.messageCount += increment;
		sr5.messageCount = Math.max(0,ir.n(sr5.messageCount));
		ir.set("messageCountDiv",sr5.messageCount);
		var div = ir.get("messageCountDiv");
		if(sr5.messageCount>0)
		{
			div.classList.add("show");
			div.parentNode.classList.add("pulse");
		}
		else
		{
			div.classList.remove("show");
			div.parentNode.classList.remove("pulse");
		}
	},
	updateShareRolls:function(ele){
		var self = messengerPop;
		var threadId = self.getPopupThreadId(ele);
		var thread = self.threads.get(threadId);
		var shareRoll = ir.v("messagePopupShareRoll"+threadId);
		var user = thread.users.get(sr5.user.Row);
		if(user!=null)
		{
			user.ShareRoll = shareRoll;
		}
		sr5.ajaxAsync({fn:"updateShareRoll",shareRoll:shareRoll,threadId:threadId},null);
	},
	updateThreadDiv:function(t){
		var self = messengerPop;
		var container = ir.get(self.id+"Threads");
		if(ir.exists(self.id+"Thread"+t.threadId))
		{
			var threadDiv = ir.get(self.id+"Thread"+t.threadId);
			threadDiv.parentNode.removeChild(threadDiv);
			container.innerHTML = self.getThreadDiv(t) + container.innerHTML;
		}
		else if (self.threads==null)
		{
			self.initThreads();
		}
		else
		{
			self.threads.add(t);
			container.innerHTML = self.getThreadDiv(t) + container.innerHTML;
		}
	},
	updateThreadId:function(oldThreadId,newThreadId)
	{
		var self = messengerPop;
		var thread = self.threads.get(oldThreadId);
		thread.threadId = newThreadId;
		popup("messagePopup"+oldThreadId);
		ir.get("messagePopup"+oldThreadId).id = "messagePopup"+newThreadId;
		//ir.get("messagePopupAddUserSelector"+oldThreadId).id = "messagePopupAddUserSelector"+newThreadId;
		ir.get("messagePopupAddUserBtn"+oldThreadId).id = "messagePopupAddUserBtn"+newThreadId;
		ir.get("messagePopupAddFriendBtn"+oldThreadId).id = "messagePopupAddFriendBtn"+newThreadId;
		ir.get("messagePopupAddUserToggleBtn"+oldThreadId).id = "messagePopupAddUserToggleBtn"+newThreadId;
		ir.get("messagePopupDivUsers"+oldThreadId).id = "messagePopupDivUsers"+newThreadId;
		ir.get("messagePopupAddUserLogin"+oldThreadId).id = "messagePopupAddUserLogin"+newThreadId;
		ir.get("messagePopupMessages"+oldThreadId).id = "messagePopupMessages"+newThreadId;
		ir.get("messagePopupLeaveBtn"+oldThreadId).id = "messagePopupLeaveBtn"+newThreadId;
		ir.get("messagePopupDeleteBtn"+oldThreadId).id = "messagePopupDeleteBtn"+newThreadId;
		ir.get("messagePopupText"+oldThreadId).id = "messagePopupText"+newThreadId;
		ir.get("messagePopupShareRoll"+oldThreadId).id = "messagePopupShareRoll"+newThreadId;
		ir.get("messagePopupShareRollWrap"+oldThreadId).id = "messagePopupShareRollWrap"+newThreadId;
		ir.get("messagePopupShareRollLabel"+oldThreadId).setAttribute("for","messagePopupShareRoll"+newThreadId);
		ir.get("messagePopupShareRollLabel"+oldThreadId).id = "messagePopupShareRollLabel"+newThreadId;
		popup("messagePopup"+newThreadId);
	},
	updateThreadSeen:function(threadId){
		var self = messengerPop;
		var callback = function(res)
		{
			if(res.ok)
			{
				sr5.messageCount = res.messageCount;
				self.updateChatCount(0);
			}
		};
		var thread = self.threads.get(threadId);
		thread.lastRowSeen = thread.messages[thread.messages.length-1].Row;
		var threadDiv = ir.get("messengerPopupThread"+threadId);
		if(threadDiv!=null)
		{
			threadDiv.classList.remove("pulse");
		}
		sr5.ajaxAsync({fn:"updateMessageSeen",threadId:threadId},callback);
	},
	userKeyup:function(event)
	{
		var self = messengerPop;
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
				self.addUser(event.target)
				return;
				break;
			default:
				break;
		}
	},	
	zz_messengerPop:0	
};