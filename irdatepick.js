/**
* irDatePickerImpl handles an array of irDatePickerInstances.
* Note that one global instance of irDatePickerImpl is declared here - irDatePicker.
*
* usage: 
*	irDatePicker.show(String textboxWithDateValueName,boolean includeTime,String dateFormat,String sLang)
*/
"use strict";
function irDatePickerImpl()
{//
	var instances = [];
	this.DefaultLanguage = "E";
	this.defaultFormat = "MMM d, yyyy";
	this.minuteIncrement = 15;
	this.addHour=function(calIdx,minus)
	{
		instances[calIdx].addHour(minus);
		return false;
	};
	this.addMinute=function(calIdx,minus)
	{
		instances[calIdx].addMinute(minus);
		return false;
	};
	this.bwdMo=function(calIdx)
	{
		instances[calIdx].bwdMo();
		return false;
	};
	this.bwdYr=function(calIdx)
	{
		instances[calIdx].bwdYr();
		return false;
	};
	this.close=function(calIdx)
	{
		instances[calIdx].close();
		return false;
	};
	this.fwdMo=function(calIdx)
	{
		instances[calIdx].fwdMo();
		return false;
	};
	this.fwdYr=function(calIdx)
	{
		instances[calIdx].fwdYr();
		return false;
	};
	this.getDate=function(calIdx)
	{
		return instances[calIdx].getDate();
	};
	this.getFormat=function(calIdx)
	{
		return instances[calIdx].getFormat();
	};
	this.isOpen=function(calIdx)
	{
		return instances[calIdx].isOpen();
	};
	this.open=function(calIdx)
	{
		instances[calIdx].open();
		return false;
	};
	this.openDate=function(calIdx)
	{
		instances[calIdx].openDate();
		return false;
	};
	this.openTime=function(calIdx)
	{
		instances[calIdx].openTime();
		return false;
	};
	this.pickDate=function(calIdx,yr,mo,day)
	{
		instances[calIdx].pickDate(yr,mo,day);
		return false;
	};
	this.pickHours=function(calIdx,hours)
	{
		instances[calIdx].pickHours(hours);
		return false;
	};
	this.pickMinutes=function(calIdx,minutes)
	{
		instances[calIdx].pickMinutes(minutes);
		return false;
	};
	this.pickPeriod=function(calIdx,period)
	{
		instances[calIdx].pickPeriod(period);
		return false;
	};
	this.reDraw=function(calIdx)
	{
		instances[calIdx].reDraw();
		return false;
	};
	this.save=function(calIdx)
	{
		instances[calIdx].save();
		return false;
	};
	this.saveTime=function(calIdx)
	{
		instances[calIdx].saveTime();
		return false;
	};
	this.setPickCallback=function(calIdx,func)
	   {
         instances[calIdx].setPickCallback(func);
	   };
	this.setLanguage=function(lang)
   {
      for (var i=0;i<instances.length;i++)
      {
         instances[i].setLanguage(lang);
      }
   };
   this.setShowTime=function(calIdx,showTime)
   {
	   instances[calIdx].setShowTime(showTime);
   };
	this.show=function(sTextBox,bTime,sFormat,sLang)
	{
		var tb = document.getElementById(sTextBox);
		if (null == tb)
		{
			alert("irdatepicker.show cannot find text box for id '" + sTextBox  +"'.");
			return;
		}
		if (sFormat==null)
		{
			sFormat = "MMM d, yyyy";
		}
		if (sLang==null)
		{
			sLang = "E";
		}
		var i = instances.length;	
		var spanId = "irDatePickerSpan" + i;
		var span=null;
		var workSpan = document.getElementById(sTextBox + "DatePick");
		if (workSpan)
		{
			spanId = workSpan.id;
			span = workSpan;
		}
		else
		{
			span = document.createElement("DIV");
			span.id = spanId;
			if (tb.nextSibling == null)
			{
				tb.parentNode.appendChild(span);
			}
			else
			{
				tb.parentNode.insertBefore(span,tb.nextSibling);
			}
		}		
		if (tb.disabled)
		{
			span.style.display="none";
		}
		span.className = "irDatePickerSpan";
		var dp = new irDatePickerInstance(i,sTextBox,spanId,bTime,sFormat,sLang);
		instances[i] = dp;
		tb.dataset.pickerIndex=i;
		dp.draw();
	};	
	this.showFace=function(calIdx,face)
	{
		instances[calIdx].showFace(face);
		return false;
	};
	////////////////////////////////////////////////////////////
	// Inner Instance Class 
	function irDatePickerInstance(myIdx,myTxt,mySpan,incTime,dFmt,sLang)
	{
		//private members
		var callback = null;
		var hourBoxId = "irDatePicker_" + myIdx + "_hh";
		var hourDiv = null;
		var hourNumbers = [12,1,2,3,4,5,6,7,8,9,10,11];
		var minuteBoxId = "irDatePicker_" + myIdx + "_mm";
		var minuteDiv = null;
		var minuteNumbers = [0,5,10,15,20,25,30,35,40,45,50,55];
		var periodBoxId = "irDatePicker_" + myIdx + "_pp";
		var periodDiv = null;
		var periodNumbers = ["am","pm"];
		var isOpen = false;
		var textBox = document.getElementById(myTxt);
		var dayAbbr = irdate.dayCodes();
		var monthAbbr = irdate.monthCodes();
		var startAngle=15;
		var showTime = incTime;
		var endAngle=-75;
		var radius = 150;
		var x1,x2,y1,y2,cx,cy = 0;
		var sectorAngleArr = [];
		var total = 0;
		var pieData = [3/2,5/3,11/6,2,1/6,1/3,1/2,2/3,5/6,1,7/6,4/3];
		var paper = null;
		var useSVGClock = true;
		var clockWidth = 360;
		//
		textBox.style.width = (isIE() ? 12 : 11.5) + "em"; 
		//public functions
		this.addHour=function(minus)
		{
			var b = document.getElementById("irDatePicker_" + myIdx + "_hh");
			if (minus)
			{
				if (b.selectedIndex>0)
				{
					b.selectedIndex--;
				}
			}
			else if (b.selectedIndex < b.options.length - 1)
			{
				b.selectedIndex++;
			}
		};
		this.addMinute=function(minus)
		{
			var b = document.getElementById("irDatePicker_" + myIdx + "_mm");
			if (minus)
			{
				if (b.selectedIndex>0)
				{
					b.selectedIndex--;
				}
			}
			else if (b.selectedIndex < b.options.length - 1)
			{
				b.selectedIndex++;
			}
		};
		this.buildHours=function(){
			sectorAngleArr = [];
			startAngle=15;
			endAngle=-75;
			x1,x2,y1,y2 = 0;
			paper = Raphael("clockHours"+myIdx,clockWidth,clockWidth);
			//CALCULATE THE ANGLES THAT EACH SECTOR SWIPES AND STORE IN AN ARRAY
			for(var i=0; i < hourNumbers.length; i++)
			{
				sectorAngleArr.push(30);
			}
			function arcDetails(arc,id) {
				arc.click(function(){return irDatePicker.pickHours(myIdx,id+1)});
				arc.data("i",id);
				arc.id="hrs"+((id+1))+"_"+myIdx;
			};
			this.drawArcs(arcDetails);
			this.drawClockNumbers(radius-20,hourNumbers);	
		};

		this.buildMinutes=function()
		{
			sectorAngleArr = [];
			startAngle=0;
			endAngle=-105;
			x1,x2,y1,y2 = 0;
			paper = Raphael("clockMinutes"+myIdx,clockWidth,clockWidth);
			//CALCULATE THE ANGLES THAT EACH SECTOR SWIPES AND STORE IN AN ARRAY
			for(var i=0; i < minuteNumbers.length; i++)
			{
				sectorAngleArr.push(30);
			}
			function arcDetails(arc,id) {
				arc.click(function(){return irDatePicker.pickMinutes(myIdx,lpad(id*5,0,2))});
				arc.data("i",id);
				arc.id="min"+((id*5))+"_"+myIdx;
			};
			this.drawArcs(arcDetails);
			this.drawClockNumbers(radius - 20,minuteNumbers);
		};
		this.buildPeriod=function()
		{
			sectorAngleArr = [];
			startAngle=0;
			endAngle=-180;
			x1,x2,y1,y2 = 0;
			radius = parseInt(radius * 0.6);
			paper = Raphael("clockPeriod"+myIdx,clockWidth,clockWidth);
			//CALCULATE THE ANGLES THAT EACH SECTOR SWIPES AND STORE IN AN ARRAY
			for(var i=0; i < periodNumbers.length; i++)
			{
				sectorAngleArr.push(180);
			}
			function arcDetails(arc,id) {
				arc.click(function(){return irDatePicker.pickPeriod(myIdx,id==0)});
				arc.data("i",id);
				arc.id="min"+((id*5))+"_"+myIdx;;
			};
			this.drawArcs(arcDetails);
			this.drawClockNumbers(radius-25,periodNumbers);
		};
		this.bwdMo=function()
		{
			if (this.showDate.getMonth() == 0)
			{
				this.showDate = new Date(this.showDate.getUTCFullYear() - 1,11,1);
			}
			else
			{
				this.showDate = new Date(this.showDate.getUTCFullYear(),this.showDate.getMonth() - 1,1);
			}
			this.draw();
		};
		this.bwdYr=function()
		{
			this.showDate = new Date(this.showDate.getUTCFullYear() - 1,this.showDate.getMonth(),1);
			this.draw();
		};
		this.close=function()
		{
			isOpen = false;
			this.draw();
		};
		this.dateString=function(dt)
		{
			var s = formatDate(dt,dFmt);
			if (showTime)
			{
				s += " " + lpad(dt.getHours(),"0",2) 
				+ ":" + lpad(dt.getMinutes(),"0",2);
			}
			return s;
		};
		this.draw = function()
		{
			if (isOpen)		
			{
				this.drawOpen();
			}
			else
			{
				this.drawClosed();
			}
		};
		this.drawArcs = function(arcDetails)
		{
			for(var i=0; i <sectorAngleArr.length; i++)
			{
				startAngle = endAngle;
				endAngle = startAngle + sectorAngleArr[i];

				x1 = parseInt(cx + radius*Math.cos(Math.PI*startAngle/180));
				y1 = parseInt(cy + radius*Math.sin(Math.PI*startAngle/180));

				x2 = parseInt(cx + radius*Math.cos(Math.PI*endAngle/180));
				y2 = parseInt(cy + radius*Math.sin(Math.PI*endAngle/180));                

				var d = "M"+cx+","+cy+" L" + x1 + "," + y1 + "  A" + radius + "," + radius + " 0 0,1 " + x2 + "," + y2 + " z"; //1 means clockwise
				//alert(d);
				var arc = paper.path(d);			
				arcDetails(arc,i);	
			}
			var circle = paper.circle(cx,cy,radius+1);
			circle.attr("stroke","black");
			circle.attr("stroke-width","1px");
		};
		this.drawClockNumbers = function(r, array)
		{
			var length = array.length;
			var angle = 360/length
			var radius = r;
			var startAngle = -90;
			for(var i = 0; i < length; i ++)
			{
				var x = parseInt(cx + radius*Math.cos(Math.PI*startAngle/180));
				var y = parseInt(cy + radius*Math.sin(Math.PI*startAngle/180));
				var text = paper.text(x,y,array[i]);
				text.id="text"+i;			
				text.attr("font-size",26);
				text.attr("font-weight","bold");
				text.attr("text-anchor","middle");	
				text.attr("font-family","Verdana");
				text.attr("fill","black");
				startAngle+=angle;	
			}
		};
		this.drawClosed=function()
		{
			textBox.style.display="";
			if (textBox.disabled)
			{
				textBox.placeholder = "disabled";
			}
			else
			{
				var s = "<a id='irDatePickerDateAnchor"+ myIdx +"' href='#' onclick='return irDatePicker.openDate(" + myIdx + ")'>"
				+ "<img class='datePickImg' border='0' src='"+sr5.iconPath+"calendar.svg'></a>";
				if (showTime)
				{			
					s += " <a id='irDatePickerTimeAnchor"+ myIdx +"' href='#' onclick='return irDatePicker.openTime(" + myIdx + ")'>"
					+ "<img class='datePickImg' border='0' src='"+sr5.iconPath+"clock.svg'></a>";
				}
				ir.set(mySpan,s);
				ir.get(mySpan).classList.remove("open");
			}
		};
		this.drawHands=function(){
			this.drawHandHour();
			this.drawHandMinute();
		};
		this.drawHandHour = function(current){
			ir.set("clockHandHr"+myIdx,"");
			var paperHourHand = Raphael("clockHandHr"+myIdx,clockWidth,clockWidth);
			var currentHour = ir.vn(hourBoxId);
			var pieIndex = hourNumbers.indexOf(currentHour);
			var r = radius * 0.6;
			var x = parseInt(cx + r*Math.cos(Math.PI*pieData[pieIndex]));
			var y = parseInt(cy + r*Math.sin(Math.PI*pieData[pieIndex]));
			var path = paperHourHand.path( "M"+cx+","+cy+" L"+x+","+y);
			//path.attr("stroke",current?"red":"black");
			path.attr("stroke-width","6px");
		};
		this.drawHandMinute=function(current){
			ir.set("clockHandMin"+myIdx,"");
			var paperMinuteHand = Raphael("clockHandMin"+myIdx,clockWidth,clockWidth);
			var currentMinute = ir.vn(minuteBoxId);
			var pieIndex = minuteNumbers.indexOf(currentMinute);
			var r = radius;
			var x = parseInt(cx + r*Math.cos(Math.PI*pieData[pieIndex]));
			var y = parseInt(cy + r*Math.sin(Math.PI*pieData[pieIndex]));
			var path = paperMinuteHand.path( "M"+cx+","+cy+" L"+x+","+y);
			//path.attr("stroke",current?"red":"black");
			path.attr("stroke-width","5px");
		};
		this.drawOpen=function()
		{
			this.openDate(true);
		};		
		this.fwdMo=function()
		{
			if (this.showDate.getMonth() == 11)
			{
				this.showDate = new Date(this.showDate.getUTCFullYear() + 1,0,1);
			}
			else
			{
				this.showDate = new Date(this.showDate.getUTCFullYear(),this.showDate.getMonth() + 1,1);
			}
			this.draw();
		};
		this.fwdYr=function()
		{
			this.showDate = new Date(this.showDate.getUTCFullYear() + 1,this.showDate.getMonth(),1);
			this.draw();
		};
		this.getDate=function()
		{
			return this.parse();
		};
		this.getFormat=function()
		{
			return dFmt;
		};

		this.hideDetails=function()
		{
			
		};	
		this.highlight=function(name){
			var sections = ir.get("clockTimeWrapper"+myIdx).getElementsByClassName("clockTime");
			var className = "underline";
			for(var i =0;  i < sections.length; i++)
			{
				var a = sections[i];
				if(a.id.indexOf(name.substring(0,1))>-1)
				{
					a.classList.add(className);
				}
				else
				{
					a.classList.remove(className);
				}
			}
		};
		this.isOpen=function()
		{
			return isOpen;
		};
		this.open=function()
		{
			if (! textBox.disabled)
			{
				isOpen = true;
				selDate = this.parse();
				this.draw();
			}
		};
		this.openDate=function(doNotPositionToSelDate)
		{
			if (! textBox.disabled)
			{
				isOpen = true;
				selDate = this.parse();
				if (! doNotPositionToSelDate)
				{
					this.showDate = new Date(selDate.getUTCFullYear(),selDate.getMonth(),1);	
				}
				textBox.style.display="none";
				var s = "<table>";
				if (textBox.value > "")
				{
					s += "<tr><td class='title' colspan='7' align='center'>&#9660; " + textBox.value + " &#9660; </td></tr>";
				}
				s += "<tr valign='top'><td><table  class='ird' cellpadding='0' cellspacing='0'  style='border:0 none;'>";
				s += "<tr>";
				s += "<td colspan='3' style='text-align:center;'>";
				s += " <a href='#' onclick='return irDatePicker.bwdMo(" + myIdx + ")'><img src='"+sr5.iconPath+"left.svg' class='datePickImg left'></a> ";
				s += monthAbbr[this.showDate.getMonth()];
				s += " <a href='#' onclick='return irDatePicker.fwdMo(" + myIdx + ")'><img src='"+sr5.iconPath+"right.svg' class='datePickImg right'></a>  ";
				s += "</td>";
				s += "<td colspan='3' style='text-align:center;'>";
				s += " <a href='#' onclick='return irDatePicker.bwdYr(" + myIdx + ")'><img src='"+sr5.iconPath+"left.svg' class='datePickImg left'></a> ";
				s += this.showDate.getUTCFullYear();
				s += " <a href='#' onclick='return irDatePicker.fwdYr(" + myIdx + ")'><img src='"+sr5.iconPath+"right.svg' class='datePickImg right'></a>  ";
				s += "</td>";
				s += "<td>" + "<a href='#' onclick='return irDatePicker.close(" + myIdx + ")' title='Cancel'>"
					+ "<img src='"+sr5.iconPath+"cancel.svg' class='datePickImg cancel'></a>" 
					+ "</td>";
				s += "</tr>";
				s += "<tr>";
				for (var i=0;i<7;i++)
				{
					s += "<td>" + dayAbbr[i] + "&nbsp;</td>";
				}
				s += "</tr>";
				var box = -1;
				var redDate=selDate;
				var d = this.showDate;
				while (d.getMonth() == this.showDate.getMonth())
				{
					var redClass = "";
					box++;
					if (box == 7)
					{
						s += "</tr><tr>";
						box = 0;
					}	
					if (d.getDate() > 1 || box >= d.getDay())
					{
						var style = " style=''";
						if (d.getUTCFullYear() == redDate.getUTCFullYear() 
							&& d.getMonth()==redDate.getMonth()
							&& d.getDate()==redDate.getDate())
						{
							redClass = "selectedDate";
						}
						s += "<td class='date "+redClass+"' onclick='return irDatePicker.pickDate(" + myIdx + "," + d.getUTCFullYear() 
							+ "," + d.getMonth() + "," + d.getDate() + ")'>&nbsp;"
							+ "<a href='#'" + style + " class='datePickDate'>" + d.getDate() + "</a>";
						d = new Date(this.showDate.getUTCFullYear(),this.showDate.getMonth(),d.getDate() + 1);
					}
					else
					{
						s += "<td>";
					}
					s += "&nbsp;</td>";
				}
				s += "</table>";
				var span = document.getElementById(mySpan);
				span.innerHTML = s;
				span.classList.add("open");
			}
		};
		this.openTime=function()
		{
			if (! textBox.disabled)
			{
				isOpen = true;
				selDate = this.parse();
				textBox.style.display="none";
				var radioMode = irDatePicker.minuteIncrement == 15 || irDatePicker.minuteIncrement==10;
				var s = "";
				
				if(useSVGClock)
				{					
					/*
					 * mn20180515 - implementing svg clock
					 */
					s = "<table cellpadding='0' cellspacing='0'>";
					s += "<tr>";
					s += "<td colspan='3' style='text-align:center;width:100%;' id='clockTimeWrapper"+myIdx+"' class='clockTimeWrapper title'>"; 
					var hours = selDate.getHours();
					var am = true;
					if(hours >= 12)
					{
						hours -= hours==12?0:12;
						am = false;
					}
					else if(hours==0)
					{
						hours=12;
					}	
					var minutes = selDate.getMinutes();
					s += "&#9660; " + irdate.mdy(selDate) + " "
					s += "<span id='"+hourBoxId+"' class='clockTime' onclick='return irDatePicker.showFace("+myIdx+",\"hour\")'>"+hours+"</span>"
					s += ":"
					s += "<span id='"+minuteBoxId+"' class='clockTime' onclick='return irDatePicker.showFace("+myIdx+",\"minute\")'>"+lpad(minutes,0,2)+"</span>"
					s += " "
					s += "<span id='"+periodBoxId+"' class='clockTime' onclick='return irDatePicker.showFace("+myIdx+",\"period\")'>"+(am?"am":"pm")+"</span>";
					s += " &#9660;"
					s += "</td>";
					s += "</tr>";
					s += "<tr valign='middle'>";
					s += "<td style='text-align:center;' colspan='3'>";
					s += "<a style='z-index:199;position:absolute;top:2rem;right:0;' href='#' onclick='return irDatePicker.close(" + myIdx + ")' title='Cancel'>"
					s += "<img src='"+sr5.iconPath+"cancel.svg' class='datePickImg cancel'></a>" 
					s += "<div id='clockWrapper"+myIdx+"'style='height:500px;width:100%;max-width:400px;min-width:240px;position:relative;margin:auto;'>";
					s += "<div id='clockHours"+myIdx+"' class='clock visible'></div>";
					s += "<div id='clockMinutes"+myIdx+"' class='clock hidden'></div>";
					s += "<div id='clockPeriod"+myIdx+"' class='clock hidden'></div>";	
					s += "<div id='clockHandMin"+myIdx+"' class='clockHand minute visible'></div>";
					s += "<div id='clockHandHr"+myIdx+"' class='clockHand hour visible'></div>";
					s += "</div>";				
					s += "</td>";
					s += "</tr>";
					s += "</table>";
					var span = document.getElementById(mySpan);
					span.innerHTML = s;
					span.classList.add("open");
					var clockWrapper = ir.get("clockWrapper"+myIdx);
					clockWidth = clockWrapper.clientWidth
					radius = parseInt(clockWidth /2);
					clockWrapper.style.height = radius*2+2+"px";
					cx = radius;
					cy = radius;
					radius -= 4;
					//ir.get("irDatePickerSpan"+myIdx).onresize = function(){irDatePicker.reDraw(myIdx);};
					this.buildMinutes();
					this.buildHours();
					this.buildPeriod();
					this.drawHands();
					this.showFace("hour");
					//ir.scrollIntoView(ir.get(mySpan));
				}
					else
					{
						s = "<table cellpadding='0' cellspacing='0' style=''>";
					if (textBox.value > "")
					{
						s += "<tr><td colspan='3' align='center'>&#9660; " + textBox.value + " &#9660; </td></tr>";
					}
					s += "<tr valign='middle'>";
					s += "<td>";
					var hourBoxSize = 6;
					s += "<select id='" + hourBoxId + "' size='" + hourBoxSize + "'>";
					var selectedHourIndex=-1;
					for (var i=0;i<24;i++)
					{
						s += "<option value='" + i +"' class='datePickHour'";
						if (i == selDate.getHours())
						{
							s += " selected ";
							selectedHourIndex = i;
						}
						var h = i == 0 ? 12 : i <= 12 ? i : i - 12;
						var ap = i < 12 ? " am" : " pm";
						s += ">" + lpad(h," ",2) + ap + "</option>";
					}
					s += "</select></td>";
					s += "<td>";
					var mins = [];
					for (var i=0;i<60;i += irDatePicker.minuteIncrement)
					{
						mins.push(i);
					}				
					var selMin = selDate.getMinutes(); 
					if (selMin % irDatePicker.minuteIncrement > 0)
					{
						mins.push(selMin);
					}
					mins.sort(function(a,b){return a-b;});
					var minCount=mins.length;
					if (radioMode)
					{	
						for (var i=0;i < Math.ceil((hourBoxSize - minCount) / 2);i++)
					    {
							s += "<br>";
					    }
						for (var i=0;i<minCount;i++)
						{
							var mm = mins[i];
							s += "<input id='"+minuteBoxId+mm+"' type='radio' name='" + minuteBoxId + "' value='" + mm +"'";
							if (mm == selMin)
							{
								s += " checked ";
							}
							s += "><label for='"+minuteBoxId+mm+"' class='datePickMinuteRb'>:" + lpad(mm,"0",2) + "</label><br>";
						}					
					}
					else
					{
						s += "<select id='" + minuteBoxId + "' size='" + hourBoxSize + "' class='datePickMinute'>";				
						for (var i=0;i<minCount;i++)
						{
							var mm = mins[i];
							s += "<option value='" + mm +"'";
							if ( mm == selMin)
							{
								s += " selected ";
							}
							s += ">" + lpad(mm,"0",2) + "</option>";
						}
						s += "</select>";
					}	
					s += "</td>";
					s += "<td>"
						+ "&emsp;<a href='#' onclick='return irDatePicker.saveTime(" + myIdx + ")'  title='Accept'>"
						+ "<img src='"+sr5.iconPath+"checkmark.svg' class='datePickImg'></a>";
					for (var i=0;i<hourBoxSize-1;i++)
					{//try to push save to top and exit to botttom
						s +="<br>";
					}
					s += "&emsp;<a href='#' onclick='return irDatePicker.close(" + myIdx + ")' title='Cancel'>"
						+ "<img src='"+sr5.iconPath+"cancel.svg' class='datePickImg'></a>";
					s += "</tr>";
					s += "</table>";
					var span = document.getElementById(mySpan);
					span.innerHTML = s;
					span.classList.add("open");
					var hourBox = document.getElementById(hourBoxId);
					if (selectedHourIndex > hourBoxSize / 2)
					{
						var topIndex = selectedHourIndex - (Math.floor(hourBoxSize / 2));
						scrollIntoView(hourBox,hourBox.options[topIndex]);
					}
					else if (selectedHourIndex==0)
					{
						scrollIntoView(hourBox,hourBox.options[8]);
					}			
				}
			}
		};
		this.parse=function()
		{
			var h=0,m=0,s=0;
			var initStr = ir.trim(textBox.value);
			var dateEnd = initStr.length;
			var firstColonAt = initStr.indexOf(":");
			if (firstColonAt > 0)
			{//it includes the time, back up to the last preceding space
				for (var i=firstColonAt-2;i>-1;i--)
				{
					if (initStr.charAt(i) == " ")
					{
						dateEnd = i;
						var t = initStr.substring(i + 1,initStr.length);
						var hms = t.split(":");
						if (hms.length > 0 && ! isNaN(hms[0]))
						{
							h = Number(hms[0]);	
						}
						if (hms.length > 1 && ! isNaN(hms[1]))
						{
							m = Number(hms[1]);	
						}
						if (hms.length > 2 && ! isNaN(hms[2]))
						{
							s = Number(hms[2]);	
						}
						break;
					}
				}
			}
			else if (showTime)
			{ 
				if (this.defaultHourMinuteNow)
				{
					var now = new Date();
					h = now.getHours();
					m = now.getMinutes();
				}
			}
			if(!showTime)
			{
				h=0;
				m=0;
				s=0;
			}
			
			var dateStr = initStr.substr(0,dateEnd);
			var d = getDateFromFormat(dateStr,dFmt);
			if (d == null)
			{
				d = new Date();
			}
			return new Date(d.getUTCFullYear(),d.getMonth(),d.getDate(),h,m,s);
		};
		this.pickDate=function(yr,mo,day)
		{
			var h = selDate.getHours();
			var m = selDate.getMinutes();
			var s = selDate.getSeconds();
			selDate = new Date(yr,mo,day,h,m,s);
			isOpen = false;
			this.draw();
			textBox.value = this.dateString(selDate);
			ir.focus(textBox);
			if(callback!=null)
			{
				callback(myIdx);
			}
		};

		this.pickHours=function(hours)
		{
			ir.set(hourBoxId,hours);
			this.drawHandHour();
			this.showFace("minute");
		};
		this.pickMinutes=function(minutes)
		{
			ir.set(minuteBoxId,lpad(minutes,0,2));
			this.drawHandMinute();
			this.showFace("period");
		};
		this.pickPeriod=function(period)
		{
			ir.set(periodBoxId,period?"am":"pm");
			this.showFace("none");
			window.setTimeout(function(){irDatePicker.saveTime(myIdx)},500);
		};
		this.reDraw=function(){
			this.saveTime();
			this.openTime();
		};
		this.save=function()
		{
			isOpen = false;
			this.pickDate(selDate.getYear(),selDate.getMonth(),selDate.getDay());
			this.draw();
			if(callback!=null)
			{
				callback(myIdx);
			}
		};
		this.saveTime=function()
		{
			if(ir.exists(periodBoxId))
			{
				if(ir.v(periodBoxId).indexOf("am")>-1)
				{
					var hour = ir.vn(hourBoxId);
					selDate.setHours(hour==12?0:hour);
				}
				else
				{
					var hour = ir.vn(hourBoxId);
					selDate.setHours(hour!=12?hour+12:hour);
				}
			}
			else
			{
				selDate.setHours(ir.vn(hourBoxId));
			}			
			selDate.setMinutes(ir.vn(minuteBoxId));
			textBox.value = this.dateString(selDate);
			isOpen = false;
			this.draw();
			if(callback!=null)
			{
				callback(myIdx);
			}
		};
		this.showFace=function(name)
		{
			this.highlight(name);
			var clocks = ir.get("clockWrapper"+myIdx).getElementsByClassName("clock");
			for(var i =0;  i < clocks.length; i++)
			{
				var a = clocks[i];
				if(a.id.toLowerCase().indexOf(name)>-1)
				{
					a.className="clock visible";
				}
				else
				{
					a.className="clock hidden";
				}
			}
			if(name.indexOf("period")>-1 || name.indexOf("none")>-1)
			{
				ir.get("clockHandMin"+myIdx).className ="clockHand minute hidden";
				ir.get("clockHandHr"+myIdx).className ="clockHand hour hidden";
			}
			else
			{
				ir.get("clockHandMin"+myIdx).className ="clockHand minute visible";
				ir.get("clockHandHr"+myIdx).className ="clockHand hour visible";
			}
		};
		this.setPickCallback = function(callbackFunction)
		{
			callback = callbackFunction;
		};		
		this.setShowTime=function(trueFalse)
		{
			showTime = trueFalse;
			var selDate = this.parse();
			this.draw();
		};
		var selDate = this.parse();
		this.showDate = new Date(selDate.getUTCFullYear(),selDate.getMonth(),1);	
		//end irDatePickerInstance body
	}
}
//
var irDatePicker = new irDatePickerImpl();
//
irDatePicker.init = function()
{
	var dt = document.getElementsByClassName("jdatetime");
	for (var i=0,z=dt.length;i<z;i++)
	{
		try
		{
			irDatePicker.show(dt[i].id,true);
		}
		catch (nevermind)
		{			
		}
	}
	dt = document.getElementsByClassName("jdate");
	for (var i=0,z=dt.length;i<z;i++)
	{
		try
		{
			irDatePicker.show(dt[i].id,false);
		}
		catch (nevermind)
		{			
		}
	}
};