"use strict";
var irdate = {
		
	dd:["Sun","Mon","Tue","Wed","Thu","Fri","Sat"],
	format:"MMM d, yyyy",
	mm:['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'],
	dayCodes:function(){
		  return irdate.dd;
		},
	daysDiff:function(d1,d2)
	{
		var t1 = new Date(d1.getUTCFullYear(),d1.getUTCMonth(),d1.getUTCDate(),0,0,0);
		var t2 = new Date(d2.getUTCFullYear(),d2.getUTCMonth(),d2.getUTCDate(),0,0,0);
		var millisDiff = t1.getTime() - t2.getTime();
		var divisor =  1000 * 60 * 60 * 24;
		return Math.floor(millisDiff / divisor);
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
	  else if (dd > 0 && dd < 2)
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
	hm:function(d)
	{	
		 d = irdate.parse(d || new Date());
		if (d.getFullYear()<1970)
		{
		  return "";
		}
		 if (d.getHours()==0 && d.getMinutes()==0)
		 {
			 return "";
		 }
		 return (d.getHours() < 13 ? d.getHours() : d.getHours() - 12)
		 + ":" + ir.lpad(d.getMinutes(),'0',2)
		 + " " + (d.getHours() <= 11 ? "am" : "pm");
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