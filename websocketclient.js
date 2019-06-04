"use strict";
var  WebSocketClient =function (protocol, hostname, port, endpoint) {        
        this.webSocket = null;        
        this.protocol = protocol;
        this.hostname = hostname;
        this.port     = port;
        this.endpoint = endpoint;
};  
WebSocketClient.prototype =
{
	getServerUrl:function () {
	    return this.protocol + "://" + this.hostname + ":" + this.port + this.endpoint;
	},
	connect:function() {
	    try {
	        this.webSocket = new WebSocket(this.getServerUrl());            
	        // 
	        // Implement WebSocket event handlers!
	        //
	        this.webSocket.onopen = function(event) {
	            //console.log('onopen::' + JSON.stringify(event, null, 4));
	        	var machineKey = sr5.user.Row + "."+ir.escapeHtml(navigator.userAgent);
	        	var remoteId = sr5.user.Row;
	        	this.send('{fn:"register",mk:\"'+machineKey+'\",id:\"'+remoteId+'\"}');
	        }            
	        this.webSocket.onmessage = function(event) {
	            var res = ir.evalObj(event.data);
	            if(res.ok)
	        	{
	            	if(res.message)
	            	{
	            		messengerPop.receiveMessage(res);
	            	}   
	            	else if(res.addUser)
            		{
	            		messengerPop.receiveAddUser(res);
            		}  
	            	else if(res.removeUser)
            		{
	            		messengerPop.receiveRemoveUser(res);
            		}
	            	else if (res.shareRoll)
            		{
	            		messengerPop.receiveShareRoll(res);
            		}
	        	}
	        }
	        this.webSocket.onclose = function(event) {
	            //console.log('onclose::' + JSON.stringify(event, null, 4));                
	        }
	        this.webSocket.onerror = function(event) {
	            //console.log('onerror::' + JSON.stringify(event, null, 4));
	        }            
	    } catch (exception) {
	        console.error(exception);
	    }
	},   
	getStatus:function() {
        return this.webSocket.readyState;
    },
    send:function(message) {
        
        if (this.webSocket.readyState == WebSocket.OPEN) {
            this.webSocket.send(message);
            
        } else {
            console.error('webSocket is not open. readyState=' + this.webSocket.readyState);
        }
    },
    disconnect:function() {
        if (this.webSocket.readyState == WebSocket.OPEN) {
            this.webSocket.close();
            
        } else {
            console.error('webSocket is not open. readyState=' + this.webSocket.readyState);
        }
    }
}; 
   