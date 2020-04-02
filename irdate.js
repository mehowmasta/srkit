"use strict";
var irdate = {
		
	dd:["Sun","Mon","Tue","Wed","Thu","Fri","Sat"],
	format:"MMM d, yyyy",
	formatTime:"MMM d, yyyy HH:mm",
	mm:['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'],
	addDays:function(d,toAdd) {		
	    return new Date(d.getUTCFullYear(), d.getMonth(), d.getDate() + toAdd);
	},
	dayCodes:function(){
		  return irdate.dd;
		},
	daysDiff:function(d1,d2)
	{
		var t1 = new Date(d1.getFullYear(),d1.getMonth(),d1.getDate(),0,0,0);
		var t2 = new Date(d2.getFullYear(),d2.getMonth(),d2.getDate(),0,0,0);
		var millisDiff = t1.getTime() - t2.getTime();
		var divisor =  1000 * 60 * 60 * 24;
		return Math.ceil(millisDiff / divisor);
	},
	friendly:function(d,showTime)
	{
      if (d == null || d=="")
	  {
    	  return "";
	  }
	  if (typeof(d)=="string")
      {
		  d = irdate.parse(d);
      }
	  if (d.getFullYear()<1970)
	  {
		  return "";
	  } 
	  var now = new Date();
	  var dd = irdate.daysDiff(now,d);
	  if (dd >= 0 && dd < 1)
	  {
		return (showTime?irdate.hm(d):"");
	  }
	  else if (dd >= 1 && dd <2)
	  { 
		  return "Yesterday " + (showTime?irdate.hm(d):"");
	  }
	  else if (dd > 0 && dd < 7)
	  {
		  return irdate.w(d) + " " + (showTime?irdate.hm(d):"");
	  }
	  return irdate.w(d) + " " 
	  	+ (d.getYear() == now.getYear() ? irdate.md(d) : irdate.mdy(d))
	  	+ " " + (showTime?irdate.hm(d):"");
	},
	/**@return the date of the passed date's sunday */
	getSunday:function(date) {
	    return irdate.addDays(date, 0 - date.getDay());
	},	
	hm:function(d)
	{	
		 d = irdate.parse(d || new Date());
		if (d.getFullYear()<1970)
		{
		  return "";
		}
		var hr = d.getHours();
		 if (hr==0 && d.getMinutes()==0)
		 {
			 return "";
		 }
		 return (hr==0? "12" : hr < 13 ? hr : hr - 12)
		 + ":" + ir.lpad(d.getMinutes(),'0',2)
		 + " " + (hr <= 11 ? "am" : "pm");
	},
	hmn:function(d)
	{	
		 d = irdate.parse(d || new Date());
		if (d.getFullYear()<1970)
		{
		  return "";
		}
	    if (d.getHours()==0 && d.getMinutes()==0 && d.getSeconds()==0)
		{
		  return "";
		}
		 return ir.lpad(d.getHours(),'0',2)
		 + ":" + ir.lpad(d.getMinutes(),'0',2);
	},
	hms:function(d)
	{
		try
		{	
			d = irdate.parse(d || new Date());
			if (d.getFullYear()<1970)
			{
			  return "";
			}
		    if (d.getHours()==0 && d.getMinutes()==0 && d.getSeconds()==0)
			{
			  return "";
			}
			return ir.lpad(d.getHours(),'0',2)
				+ ":" + ir.lpad(d.getMinutes(),'0',2)
				+ ":" + ir.lpad(d.getSeconds(),'0',2);
		}
		catch (e)
		{
			return "hms(" + d + ");";
		}
	},
	m:function(d){		
		  d = irdate.parse(d || new Date());
		  if (irdate.isZero(d)) {
			  return "";
		  }
		  return irdate.mm[d.getMonth()];
		},
		/** returns whether arg is zero date or string that parses to zero date */
		isZero:function(d) {
			return d==null || d=="" || (d.getTime && d.getUTCFullYear()<=1970) || irdate.parse(d).getUTCFullYear()<=1970;
		},
	md:function(d)
	{		
	  d = irdate.parse(d || new Date());
	  if (d.getFullYear()<1970)
	  {
		  return "";
	  }
	  return irdate.mm[d.getMonth()] + " " + d.getDate();
	},
	mdy:function(d)
	{	
	  d = irdate.parse(d || new Date());
	  if (d.getFullYear()<1970)
	  {
		  return "";
	  }
	  var w_m_d_y = d.toDateString().split(" ");
	  return w_m_d_y[1] + " " + Number(w_m_d_y[2]) + ", " + " " + w_m_d_y[3];
	},
	mdyhm:function(d)
	{	
	  d = irdate.parse(d || new Date());
	  if (d.getFullYear()<1970)
	  {
		  return "";
	  }
	  return irdate.mdy(d) + " " + irdate.hm(d);
	},
	minsDiff:function(d1,d2){
		var millisDiff = d1.getTime() - d2.getTime();
		var divisor =  1000 * 60;
		return Math.floor(millisDiff / divisor);
	},
	monthCodes:function(){
		  return irdate.mm;	
	},
	offsetTimeZone:function(date){
		try{
			var n = date.getTimezoneOffset();
			date.setTime(date.getTime() - n *60000);
			return date;
		}
		catch(e)
		{
			return date;
		}
	},
	/**
	 * Always returns a date; Date(0) if input is not a valid 
	 * date string in yyyy-mm-dd format.  Will set hours, minutes
	 * seconds if included.
	 */
	parse:function(str)
	{
		if (str == null || str =="" || str=="0")
		{
			return new Date(0);
		}
	   if (str.getTime)
	   {
	      return str;
	   }
		var dateTime = str.split(" ");
		var ymd = dateTime[0].split("-");
		if (ymd.length < 2)
		{
			return str;
		}
		if (ymd.length == 2)
		{
			ymd.splice(0,0,new Date().getFullYear());
		}
		var h=0,m=0,s=0;
		if (dateTime.length>1)
		{
			var time = dateTime[1].split(":");
			h = Number(time[0]);
			if (time.length>1)
			{
				m = Number(time[1]);
			}
			if (time.length>2)
			{
				s = Number(time[2]);
			}
		}
		try
		{			
			var result = new Date(Number(ymd[0]),Number(ymd[1]) - 1,Number(ymd[2]),h,m,s);
			//ir.log("irdate.parse('" + str + "')  = " + result);
			return result;
		}
		catch (exc)
		{
			try
			{				
				return Date.parse(str);
			}
			catch (e2)
			{
				ir.log("ir.parse('" + str + "'): " + exc
						+ "\n::" + e2);
			}
			return new Date(0);
		}
	},
	q:function(d){
		d = irdate.parse(d || new Date());
		var m = d.getMonth() + 1;
	    return Math.floor(m / 3 + (m % 3 == 0 ? 0 : 1));
	},
	/** irdate.rangeMonth yields {from:?,to:?} where from is the
	 * first day of the month of the  date passed in and to is the 
	 * last day of that month */
	rangeMonth:function(d) {
		var from = d == null ? new Date() : irdate.parse(d);
		var to = new Date(from.getTime());
		from.setDate(1);
		to.setMonth(from.getMonth() + 1);
		to.setDate(0);
		return {from:from,to:to};
	},	
	/** irdate.rangeOffset takes in a date range like {from:?,to:?},
	 * counts the days between from and to, and returns an offset
	 * range  */
	rangeOffset:function(rangeIn,offset) {
		var days = irdate.daysDiff(rangeIn.to,rangeIn.from) + 1;
		var from = addDays(rangeIn.from,days * offset);
		var to = addDays(rangeIn.to,days * offset);
		//ir.log(irdate.md(rangeIn.from) + "-" + irdate.md(rangeIn.to) + (offset>0 ? " + " : " ") + offset + " = " + irdate.md(from) + "-" + irdate.md(to));
		return {from:from,to:to};
	},
	/** irdate.rangeQuarter yields {from:?,to:?} where from is the
	 * first day of the quarter of the  date passed in and to is the 
	 * last day of that quarter - using March,June,September,December */
	rangeQuarter:function(d) {
		var from = d == null ? new Date() : irdate.parse(d);
		var to = new Date(from.getTime());
		from.setDate(1);
		from.setMonth(Math.floor(from.getMonth() / 3) * 3);
		to.setMonth(from.getMonth() + 3);
		to.setDate(0);
		return {from:from,to:to};
	},
	/** irdate.rangeWeek yields {from:?,to:?} where from is the
	 * nearest Sunday before date passed in and to is the 
	 * first Saturday after the Sunday */
	rangeWeek:function(d) {
		var from = irdate.getSunday(d);
		var to = new Date(from.getTime());
		to.setDate(from.getDate() + 6);
		return {from:from,to:to};
	},
	/** rangeYear yields {from:?,to:?} where from is the
	 * first day of the year of the date passed in and to is the 
	 * last day of that year */
	rangeYear:function(d) {
		var from = d == null ? new Date() : irdate.parse(d);
		var to = new Date(from.getTime());
		from.setDate(1);
		from.setMonth(0);
		to.setMonth(12);
		to.setDate(0);
		return {from:from,to:to};
	},
	ymd:function(d)
	 {
		 d = irdate.parse(d || new Date());
		 try
		 {
			 return d.getFullYear() 
			 + "-" + ir.lpad((d.getMonth()+1),'0',2)
			 + "-" + ir.lpad(d.getDate(),'0',2);
		 }
		 catch (e)
		 {
			 return "ymd(" + d + ")";
		 }
	 },
	ymdhm:function(d)
	 {
		 d = irdate.parse(d || new Date());
		 return irdate.ymd(d) + " " + irdate.hm(d);
 	 },
 	ymdhmn:function(d)
	 {
		 d = irdate.parse(d || new Date());
		 return irdate.ymd(d) + " " + irdate.hmn(d);
	 },
	 	ymdhms:function(d)
		 {
			 d = irdate.parse(d || new Date());
			 return irdate.ymd(d) + " " + irdate.hms(d);
		 },
	short:function(d)
	{
		var now = new Date();
		  var dd = irdate.daysDiff(now,d);
		  if (dd >= 0 && dd < 1)
		  {
			return irdate.hm(d);
		  }
		  else
		  {
			  return irdate.md(d);
		  }
		  
	},
 	stamp:function(d)
	 {
		 d = irdate.parse(d || new Date());
		 return irdate.ymd(d) + " " + irdate.hms(d);
	 },
	w:function(d)
	{	
	  d = irdate.parse(d || new Date());
	  return irdate.dd[d.getDay()];
	},
	 zz_irdate:0
};