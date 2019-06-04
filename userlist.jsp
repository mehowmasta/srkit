<%@include file="dochdr.jspf"%>
<script src="userlist.js?s=<%=stamp%>" TYPE="text/javascript"></script>
<script>
	model.blankUser = <%= new UserRec() %>;
	model.users = new KeyedArray("Row",<%=_bean.get("Users")%>);
	model.requests = new KeyedArray("FriendRow",<%=_bean.get("Requests")%>);
	model.sortType = <%=_bean.get("SortType") %>;
</script>
<style>
.charactersWrap{
padding:0.5rem;
	display:none;
}
.charactersWrap.show{
	display:block;
}
.userDetailBtn{
	position: absolute;
	top: 0.75rem;
	right: 0.75rem;
}
#divUsers, #divRequests {
	max-height: calc(100vh - 14rem);
	overflow-y: auto;
}
#divRequests .section{
	min-height:8rem;
}
#divUsers .subtitle{
	font-size:1.2rem;
}
#divUsers .section, #divRequests .section{
width: 100%;
max-width: 100%;
}
</style>
<body onload='view.aaOnLoad()'>
	<%=_bean.startForm()%>	
	<div id='usersTabs' class='flex' style='padding-top:1rem;justify-content:flex-end;overflow:hidden;'>
		<div class='tabFillLeft'></div>
		<div id='tabUsers' class='tab selected' onclick='view.pickTab("Users");'>Users</div>
		<div id='tabRequests' class='tab' onclick='view.pickTab("Requests");'>Requests</div>
	</div>
	<div class='container' id='divUsers'>
		<div class='flex' id='userList'></div>
	</div>
	<div class='container' id='divRequests' style='display:none;'>
		<div class='flex' id='requestList'></div>
	</div>
	<div class='flex'>
		<label id='userCount' style='position:absolute;right:0.75rem;'></label>
		<label id='requestCount' style='position:absolute;right:0.75rem;display:none;'></label>
		<% if(_bean.isSysAdmin()) {%>
			<button type='button' onclick='view.addUser()'>Create Account</button>
		<% }  else if (!_bean.isGuest()){ %>
			<div class="flex" style="align-items: center;flex-wrap:wrap;">
					<button style="" class="parentBtn hover" data-hover="Add User" id="addUserBtn" type="button" tabindex="1" onclick="view.toggleAddUserButton(this)">Add User</button>	
					<div class="childBtnContainer flex" id="addUserBtnSelector" style="justify-content:flex-start;align-items:center;max-width:35rem;">
						<div class="x" style="top:0rem;right:-2rem;" onclick="view.toggleAddUserButton(this)">X</div>					
						<div class="inputWrap childBtn">
							<input type="email" id="addUserLogin" name="" onfocus="this.select()" onkeydown='view.addUserKeydown(event)'><label class="inputLabel">User Login</label>
						</div>
						<button type="button" class="childBtn" style="max-height:3.75rem" onclick="return view.addFriend(this)">Add</button>
					</div>
				</div>
		<% } %>
	</div>
	<%=_bean.endForm() %>
	
	<% if(_bean.isSysAdmin()) {%>
		<%@include file="userdetailpop.jspf"%>
		<script>model.isUser=false;</script>
	<% } else { %>
	<script>model.isUser=true;</script>
	<% } %>
	<%@include file="docftr.jspf"%>