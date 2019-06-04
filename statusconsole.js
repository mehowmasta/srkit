"use strict";
var Status = {
    Error: "statusError",
    IdSuffix:"statusElement",
    Info: "statusInfo",
    LONG: 5000,
    Success: "statusSuccess",
    SessionKey:"status$@@$",
    SessionTimeout:null,
    SHORT: 3000,
    Warn: "statusWarn",
    XLONG: 7000,    
    /** Status.add displays a message at bottom right with various options */
    add: function(message, errorType, timeout,noise, fromLoad){
    	timeout = timeout || Status.LONG;    	
    	var sessionStr = "";
    	if(typeof sesGet !== "undefined")
		{
    		sessionStr = sesGet(Status.SessionKey,"[]")
		}
    	var sessionArray = [];
    	try{
    		sessionArray = JSON.parse(sessionStr);
    	}catch(e){}
    	
    	var matchingstatus = [];
    	// if(!fromLoad){
	    	if(sessionArray.constructor && sessionArray.constructor === Array){
		    	matchingstatus = sessionArray.filter(function(item){
		    		return item.message == message;
		    	})
	    	}
	    	if(matchingstatus.length>0){
	    		return;
	    	}    	
    	// }
    	sessionArray.push({message:message,errorType:errorType,timeout:timeout,noise:noise});
    	if(typeof sesSet !== "undefined")
		{
        	sesSet(Status.SessionKey,JSON.stringify(sessionArray));
		}
    	sessionArray.pop();
    	clearTimeout(Status.SessionTimeout);
    	Status.SessionTimeout = window.setTimeout(function(){
    		// sessionArray.pop();
        	if(typeof sesSet !== "undefined")
    		{
        		sesSet(Status.SessionKey,sessionArray);
    		}
    		},1);
        var statusBox = Status.getStatusBox();
        var id = Status.IdSuffix + "_" + errorType + "_" + message.substring(0,40);
        if(Status.exists(id))
    	{
        	return;
    	}
        var alert = document.createElement("div");
        alert.id = id;
        alert.setAttribute("class", "statusBase " +  (errorType || Status.Success));        
        alert.style.opacity = 0;
        alert.style.transition = "all 0.4s ease-in";
        var btn = document.createElement("div");
        btn.setAttribute("class", "statusBtn");
        btn.addEventListener("click", Status.deleteSelf, false);
        btn.innerHTML = "X";
        alert.appendChild(btn);
        var msgDiv = document.createElement("DIV");
        msgDiv.innerHTML = message;
        alert.appendChild(msgDiv);
        statusBox.appendChild(alert);
        if (noise) {
        	if (errorType==Status.Error) {
        		ir.playSound("error.wav");
        	}
        }
        setTimeout(function(){
            alert.style.opacity = 0.98;
        }, 50);
        if(timeout != null){
        	setTimeout(function(){
        		Status.deleteSelf({target: btn});
        	}, timeout);
        }        
    },
    deleteSelf: function(evt){
        var self = evt.target.parentElement;
        self.style.height = "0";
        self.style.maxHeight = "0";
        self.style.minHeight = "0";
        self.style.paddingTop = "0";
        self.style.paddingBottom = "0";
        self.style.marginTop = "0";
        self.style.marginBottom = "0";
        // self.style.boxShadow = "0 0";
        // self.style.border = "0 none";
        window.setTimeout(function(){  
        	if(Status.exists(self.id)) {
                self.parentElement.removeChild(self);
        	}                     
        }, 400);
    },
    exists : function(id) {
        var ele = document.getElementById(id);
        if (ele == null) {
            var nl = document.getElementsByName(id);
            if (nl && nl.length > 0) {
                ele = nl[0];
            }
        }
    },
    getStatusBox: function(){
        if(document.getElementById("StatusBox") != null){
            return document.getElementById("StatusBox");
        }else{
            var statusDiv = document.createElement("div");
            statusDiv.setAttribute("id", "StatusBox");
            document.body.appendChild(statusDiv);
            return statusDiv;
        }
    },
    error: function(message, timeout,noise){    	
        Status.add(message, Status.Error, timeout,noise);
    },
    info: function(message, timeout){
        Status.add(message, Status.Info, timeout);
    },
    restoreSession: function(){
    	var statusStr = sesGet(Status.SessionKey,"[]")
    	var statusArray = [];
    	try{
    		statusArray = JSON.parse(statusStr);
    	}catch(e){}
    	if(typeof sesSet !== "undefined")
		{
        	sesSet(Status.SessionKey, "[]");
		}
    	for(var i=0;i<statusArray.length; i++){
    		var m = statusArray[i];
    		Status.add(m.message,m.errorType,m.timeout,m.noise);
    	}
    },
    success: function(message, timeout){
        Status.add(message, Status.Success, timeout);
    },
    warn: function(message, timeout){
        Status.add(message, Status.Warn, timeout);
    },
    zz_status:null
};