var view = {
		errorText:"",
		getClientHeight:function(){
			return document.documentElement.clientHeight;
		},
		getClientWidth:function(){
			return document.documentElement.clientWidth;
		},
		guest:function(){
			document.getElementById("_u").value = "guest";
			document.getElementById("_p").value = "guest";
			document.forms[0].submit();
		},
		onload:function(){
			document.addEventListener('mousemove',view.slide);
			if(view.errorText.length>0)
			{
				Status.error(view.errorText,7000);
			}
			hoverPop.init();
		},
		slide:function(e){
			var body = document.body;
			var container = document.getElementById("container");
			var social = document.getElementById("social");
			var cWidth = view.getClientWidth();
			var cHeight = view.getClientHeight();	
			var offsetY = cHeight/2;
			var offsetX = cWidth/2;
			var x = e.pageX-offsetX || 0;
			var y = e.pageY-offsetY || 0;
			var clientMagnitude = Math.sqrt(cWidth*cWidth + cHeight*cHeight);
			var divMagnitude = 60;
			var ratio = divMagnitude/clientMagnitude;
			body.style.backgroundPosition = (50 + (x*ratio)) + "% " + (40 + (y*ratio)) +"%";
			//container.style.transform = "translate(" + ((x*ratio/20)) + "%, " + ((y*ratio/20)) + "%)";
			//social.style.transform = "scale("+Math.max(1,(Math.sqrt(x*x + y*y)+60)/Math.sqrt(offsetX*offsetX + offsetY*offsetY)*ratio*6+1)+")";
			
		},
		submit : function()
		{
			return false;
		},
		zz_view:0
};