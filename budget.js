"use strict";
var filter = {		
		from:null,
		id:"filterPopup",
		to:null,
		apply:function(){
			filter.from = ir.v("filterPopupFrom");
			filter.to = ir.v("filterPopupTo");
			view.setDateRange();
			model.selectList();
			filter.cancel();
		},
		cancel:function(){
			var self = filter;
			var pop = ir.get(self.id)
			if(pop.classList.contains("show"))
			{
				popup(pop);
			}
		},
		initCategories:function(){
			var container = ir.get("filterPopupViewCategoryContainer",true);
			if(container!=null)
			{
				var template = "<div class='checkWrap' id='filterPopupViewCategoryWrap{0}'>"
							  +	"<input type='checkbox' class='categoryChk' checked='true' id='filterPopupViewCategory{0}' tabindex='1'>"
							  +	"<label class='hover' for='filterPopupViewCategory{0}' data-hover='Toggle this category'>{1}</label>"
						  	+ "</div>";
				var categories = model.category;
				container.innerHTML = "<div class='checkWrap' id='filterPopupViewCategoryWrapAll'>"
									  +	"<input type='checkbox' checked='true' id='filterPopupViewCategoryAll' onchange='filter.toggleAllCategories()' tabindex='1'>"
									  +	"<label class='hover' for='filterPopupViewCategoryAll' data-hover='Toggle all'>[All]</label>"
								  	+ "</div>";
				for(var i=0, z=categories.length;i<z;i++)
				{
					var a=categories[i];
					container.innerHTML += ir.format(template,a.name,a.text);
				}
			}
		},
		initSharers:function(){
			var container = ir.get("filterPopupViewShareContainer",true);
			if(container!=null)
			{
				var template = "<div class='checkWrap' id='filterPopupViewShareWrap{0}'>"
							  +	"<input type='checkbox' checked='true' id='filterPopupViewShare{0}' tabindex='1'>"
							  +	"<label class='hover' for='filterPopupViewShare{0}' data-hover='Toggle this users entries'>{1}</label>"
						  	+ "</div>";
				container.innerHTML = ir.format(template,sr5.user.Row,sr5.user.Name);
				var sharers = model.sharers.values;
				for(var i=0, z=sharers.length;i<z;i++)
				{
					var a=sharers[i];
					container.innerHTML += ir.format(template,a.Row,a.ShortName||a.Name);
				}
			}
		},
		show:function(){
			var self = filter;
			popup(ir.get(self.id));
		},
		toggleAllCategories:function(){
			var categories = document.getElementsByClassName("categoryChk");
			var checked = ir.v("filterPopupViewCategoryAll");
			for(var i =0, z=categories.length;i<z;i++)
			{
				var a = categories[i];
				ir.set(a,checked);
			}
		},
	zz_filter:0
};
var model={
		budgetBlank:null,
		category:null,
		currentEntry:null,
		list:null,
		sharers:null,
		hasSharers:function(){
			return model.sharers!=null && model.sharers.size()>0;
		},
		selectList:function(){
			var callback = function(res){
				if(res.ok)
				{
					model.list.clear();
					model.list.add(res.list);
					view.buildList();
				}
			};
			sr5.ajaxAsync({fn:"selectBudgetList",from:filter.from,to:filter.to},callback);
		},
	zz_model:0	
};
var view = {
		prefix:"budget_",
		showP:true,
		
		aaOnLoad:function(){
			model.currentEntry = ir.copy(model.budgetBlank);
			filter.initSharers();
			filter.initCategories();
			sr5.dateRangerInit("filterPopupRangeContainer","filterPopupFrom","filterPopupTo")
			view.buildCategory();
			view.buildList();
			ir.set("filterPopupFrom",filter.from);
	    	ir.set("filterPopupTo",filter.to);
	    	view.setDateRange();
	    	sr5.doneLoading();
			ir.focus("ctlName");
		},
		addEntry:function(){
			if(model.currentEntry.Row>0)
			{
				return;
			}
			view.updateEntry();
		},
		afterChangeCategory:function(){
			ir.focus("ctlAmount");
		},
		afterUpdate:function(res){
			if(res.ok)
			{
				var b = res.budget;
				if(b.Delete)
				{
					model.list.remove(b.Row);
					Status.error(view.getDescription(b) + " entry deleted.");
				}
				else
				{
					var newEntry = model.list.get(b.Row)==null;			
					model.list.set(b);
					Status.success(view.getDescription(b) + " entry "+(newEntry?"added":"updated")+".");
				}				
				view.buildList();
				view.newEntry();
				view.pickTab("List");
				return;
			}			
		},
		buildCategory:function(){
			var select = ir.get("ctlCategory");
			clearSelectBox(select);
			var cat = model.category;
			var group = addOptionGroup(select, "Expense");
			var prevGroup = true;
			for(var i=0,z=cat.length;i<z;i++)
			{
				var a = cat[i];
				if(prevGroup!=a.isExpense)
				{
					group = addOptionGroup(select, "Income");
					prevGroup = a.isExpense;
				}
				var opt = document.createElement("OPTION");
				opt.value = a.name;
				opt.text = a.text;
				group.appendChild(opt);
			}
		},
		buildList:function(){
			var header = "<label>Date</label><label>Category</label><label class='right'>Expense</label><label class='right'>Income</label>";
			var template = "<div class='entry'>{0}</div><div class='entry'>{1}</div><div class='entry right'>{2}</div><div class='entry right'>{3}</div>";
			
			var entries = model.list.sort(view.sortList);
			var expenseSum = 0;
			var incomeSum = 0;
			var grandTotal = 0;
			var numMonthsShowing = 0;
			var prevMonth = "V#^&";
			var container = ir.get("budgetList");
			var first = true;
			container.innerHTML = header;
			for(var i=0, z=entries.length; i < z; i++)
			{
				var a = entries[i];
				if(model.hasSharers())
				{
					if(!ir.v("filterPopupViewShare"+a.User))
					{
						continue;
					}
				}
				if(!ir.v("filterPopupViewCategory"+a.Category))
				{
					continue;
				}
				var date = new Date(a.Time);
				date.setHours(date.getHours()+8);
				if(!first && prevMonth != date.getMonth())
				{
					var diff = incomeSum - expenseSum;
					grandTotal += diff;
					container.innerHTML += "<div class='subspacer'></div>"
										+ "<div></div>"
										+ "<div class='entry'>Total</div>"
										+ "<div class='entry right'>"+ir.decimal(expenseSum*-1,2) + " " + sr5.nuyen+"</div>"
										+ "<div class='entry right'>"+ir.decimal(incomeSum,2) + " " + sr5.nuyen+"</div>"
										+ "<div></div>"
										+ "<div></div>"
										+ "<div class='entry grandtotal minus right'>"+(diff<0?ir.decimal(diff,2) + " " + sr5.nuyen:"")+"</div>"
										+ "<div class='entry grandtotal plus right'>"+(diff>=0?ir.decimal(diff,2) + " " + sr5.nuyen:"")+"</div>";
					expenseSum = 0;
					incomeSum = 0;
					numMonthsShowing++;
				}
				var link  = "<a href='#' onclick='view.clickEntry("+a.Row+")'>"+a.Category.replace(/_/g," ")+"</a>";
				if(a.User != sr5.user.Row)
				{
					link = a.Category.replace(/_/g," ");
				}
				container.innerHTML += (ir.format(template,
						formatDate(a.Time,irdate.format),
						link + " <i>"+a.Note+"</i>",
						(a.Type==="Expense"?ir.decimal(a.Amount*-1,2) + " " + sr5.nuyen:""),
						(a.Type==="Income"?ir.decimal(a.Amount,2) + " " + sr5.nuyen:"")));
				first=false;
				expenseSum += a.Type==="Expense"?a.Amount:0;
				incomeSum += a.Type==="Income"?a.Amount:0;
				prevMonth = date.getMonth();
			}
			var diff = incomeSum - expenseSum;
			grandTotal += diff;
			container.innerHTML += "<div class='subspacer'></div>"
								+ "<div></div>"
								+ "<div class='entry' >Total</div>"
								+ "<div class='entry right'>"+ir.decimal(expenseSum*-1,2) + " " + sr5.nuyen+"</div>"
								+ "<div class='entry right'>"+ir.decimal(incomeSum,2) + " " + sr5.nuyen+"</div>"
								+ "<div></div>"
								+ "<div></div>"
								+ "<div class='entry grandtotal minus right'>"+(diff<0?ir.decimal(diff,2) + " " + sr5.nuyen:"")+"</div>"
								+ "<div class='entry grandtotal plus right'>"+(diff>=0?ir.decimal(diff,2) + " " + sr5.nuyen:"")+"</div>";
			if(numMonthsShowing>0)
			{
				container.innerHTML += "<div class='subspacer'></div>"
					+ "<div></div>"
					+ "<div class='entry' >Grand Total</div>"
					+ "<div class='entry grandtotal minus right'>"+(grandTotal<0?ir.decimal(grandTotal,2) + " " + sr5.nuyen:"")+"</div>"
					+ "<div class='entry grandtotal plus right'>"+(grandTotal>=0?ir.decimal(grandTotal,2) + " " + sr5.nuyen:"")+"</div>";
			}
		},
		clickEntry:function(row){
			var entry = model.list.get(row);
			if(entry==null)
			{
				Status.error("Failed to find entry.");
				return;
			}
			else
			{
				model.currentEntry = entry;
				view.setEntry(model.currentEntry);
				view.pickTab("Detail");
				view.showHideButtons();
			}
		},
		deleteEntry:function(){
			if(model.currentEntry==null)
			{
				return;
			}
			var callback=function(yes)
			{
				if(yes)
				{
					var entry = model.currentEntry;
					entry.Delete = true;
					entry.fn = "updateBudget";
					sr5.ajaxAsync(entry,view.afterUpdate);
					return;
				}				
			}
			confirmPop.show("Delete "+ view.getDescription(model.currentEntry)+"?",callback);
		},
		getDescription:function(b)
		{
			return formatDate(b.Time,irdate.format) + " " + b.Category.replace(/_/g," ") +  " $" + ir.decimal(b.Amount,2);
		},
		newEntry:function(){
			model.currentEntry = ir.copy(model.budgetBlank);
			view.setEntry(model.currentEntry);
			ir.set("ctlTime",formatDate(new Date(),irdate.format));
			view.showHideButtons();
			ir.focus("ctlAmount");
		},
		pickTab:function(tabName){
			var tabs = document.getElementsByClassName("tab");
			sesSet(view.prefix + sr5.user.Row + "tab",tabName);
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
		readEntry:function(){
			var hasError=false;
			var entry = model.currentEntry;
			entry.Row = ir.vn("ctlRow");
			entry.Amount = ir.vn("ctlAmount");
			if(entry.Amount==null || entry.Amount<=0)
			{
				hasError=true;
				Status.error("Enter a valid Amount.")
			}
			entry.Note = ir.escapeHtml(ir.v("ctlNote"));
			/*
			entry.Name = ir.v("ctlName");
			if(entry.Name==null || entry.Name.length<1)
			{
				hasError = true;
				Status.error("Enter a Name.")
			}
			*/
			entry.Time = ir.v("ctlTime");
			//entry.Type = ir.v("ctlType");
			entry.Category = ir.v("ctlCategory");
			return !hasError;
		},
		setDateRange:function(){
			ir.set("ctlTime",formatDate(new Date(),irdate.format));
			ir.set("dateRange",filter.from + " - " + filter.to);
		},
		setEntry:function(entry)
		{
			ir.set("ctlRow",entry.Row);
			ir.set("ctlNote",entry.Note);
			//ir.set("ctlName",entry.Name);
			ir.set("ctlTime",formatDate(entry.Time,irdate.format));
			//ir.set("ctlType",entry.Type);
			ir.set("ctlAmount",entry.Amount);
			ir.set("ctlCategory",entry.Category);
		},
		showHideButtons:function(){
			var entry = model.currentEntry;
			ir.show("newEntryBtn",entry.Row>0);
			ir.show("addEntryBtn",entry.Row==0);
			ir.show("deleteEntryBtn",entry.Row>0);
			ir.show("updateEntryBtn",entry.Row>0);
		},
		sortList:function(a,b){
			if(a.ts < b.ts)
			{
				return 1;					
			}
		    if(a.ts > b.ts)
	    	{
		    	return -1;
	    	}
		    if(a.Row < b.Row)
		    {
		    	return 1;
		    }
		    else
		    {
		    	return -1;
		    }	
		    return 0;
		},
		updateEntry:function(){
			if(view.readEntry())
			{
				var res = model.currentEntry;
				res.fn = "updateBudget";
				sr5.ajaxAsync(res,view.afterUpdate);
			}
		},
	zz_view:0
};