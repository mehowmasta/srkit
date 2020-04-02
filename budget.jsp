<%@include file="dochdr.jspf"%>
<style>
.subspacer {
	border-top:1px solid;
	grid-column-end: span 4;	
}
.entry, .entry a{	
font-size:1.1rem;
}
.grandtotal {
	font-weight:bold;padding-bottom: 2rem;
	font-size:1.5rem;
}
</style>
<script src="budget.js?s=<%=stamp%>" TYPE="text/javascript"></script>
<script>
	model.budgetBlank = <%= _bean.get("BudgetBlank") %>;
	model.category = <%= _bean.get("BudgetCategory") %>;
	model.list = new KeyedArray("Row",<%= _bean.get("BudgetList") %>);
	model.sharers = new KeyedArray("Row",<%= _bean.get("BudgetSharers") %>);
	filter.from = formatDate(<%= _bean.get("From") %>,irdate.format);
	filter.to = formatDate(<%= _bean.get("To") %>,irdate.format);
</script>
<body onload='view.aaOnLoad()'>
	<%=_bean.startForm()%>	
	<div id='budgetTabs' class='flex' style='padding-top:1rem;justify-content:flex-end;overflow:hidden;align-items:flex-end;'>
		<div class='tabFillLeft'></div>
		<div id='tabDetail' class='tab selected' onclick='view.pickTab("Detail");'>Entry</div>
		<div id='tabList' class='tab' onclick='view.pickTab("List");'>List</div>
	</div>	
	<div id='divDetail'>
		<div class='pageSubtitle'>Entry</div>	
		<div id='detail' class='container flex' style='flex-direction:column;align-items:flex-start;' >		
			<%=_bean.get("ctlRow") %>
			<%=_bean.get("ctlTime") %>
			<%=_bean.get("ctlCategory") %>
			<%=_bean.get("ctlAmount") %>
			<%=_bean.get("ctlNote") %>
		</div>
		<div class='spacer'></div>	
		<div class='container flex'>			
			<button id='addEntryBtn'  type='button' onclick='view.addEntry()' style='' >Add</button>
			<button id='newEntryBtn'  type='button' onclick='view.newEntry()' style='display:none;' >New</button>
			<button id='deleteEntryBtn'  type='button' onclick='view.deleteEntry()' style='display:none;' >Delete</button>
			<button id='updateEntryBtn'  type='button' onclick='view.updateEntry()' style='display:none;' >Update</button>
		</div>
	</div>
	<div id='divList' style='display:none;'>
		<div class='pageSubtitle'>Summary
			<div class='imgBtn hover' data-hover='Filter'  style='cursor:pointer;position:absolute;right:0.5rem;padding-bottom:0.2rem;' onclick='filter.show()'>
				<img src='<%=_bean.getIconPath() %>cogwheel.svg' class='medIcon'> 
			</div>
		</div>
		<div id='dateRange' class='subtitle' style='display:flex;justify-content:center;'></div>
		<div id='budgetList' style=''>
		</div>
	</div>	
	<div id='filterPopup' class='popup filter'>
		<div style='width:100%;max-height:calc(100vh - 7rem);overflow:auto;'>
			<div class='popupTitle'>Date Range</div>
			<div class='container flex'  >	
				<div class='dateWrap' id='filterPopupToWrap'>
					<input id='filterPopupTo' name='filterPopupTo' type='text'  class='jdate'>
					<label class='inputLabel'>To</label>
				</div>
				<div class='dateWrap' id='filterPopupFromWrap'>
					<input id='filterPopupFrom' name='filterPopupFrom' type='text' class='jdate'>
					<label class='inputLabel'>From</label>
				</div>
				<div class='spacer'></div>	
				<div id='filterPopupRangeContainer' style='display:grid;width:100%;grid-template-columns: repeat(auto-fill, minmax(10rem,1fr));'></div>
			</div>		
			<% if(_bean.getBool("BudgetHasSharers")) { %>			
				<div class='popupTitle'>Contributors</div>
				<div id='filterPopupViewShareContainer' style='display:grid;width:100%;grid-template-columns: repeat(auto-fill, minmax(16rem,1fr));'>
				</div>
			<% } %>
			<div class='popupTitle'>Categories</div>
			<div id='filterPopupViewCategoryContainer' style='display:grid;width:100%;grid-template-columns: repeat(auto-fill, minmax(16rem,1fr));'>
			</div>
		</div>
		<div class='container flex' style='padding:1rem;'>			
			<button id='filterPopupApply' type='button' onclick='filter.apply()'>Apply</button>
			<button id='filterPopupCancel' type='button' onclick='filter.cancel()'>Cancel</button>
		</div>
	</div>
	
	<%=_bean.endForm() %>
	<%@include file="docftr.jspf"%>