"use strict";
var model={
		hasFriendRequest:0,
		hasTransferRequest:0,
	zz_model:0	
};
var view = {
		aaOnLoad:function(){
			sr5.doneLoading();
			if(model.hasFriendRequest)
			{
				Status.info("You have a pending friend request, <a href='userlist.jsp'>click here</a>.",15000)
			}
			if(model.hasTransferRequest)
			{
				Status.info("You have a character transfer request pending, <a href='characterlist.jsp'>click here</a>.",15000)
			}
		},
	zz_view:0
};