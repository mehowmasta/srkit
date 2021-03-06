"use strict";
//
var irPop = { 
  backgroundId : "popupBackground", 
  left : 0, 
  top : 0, 
  visibleIds : [],
  active:function(){
	  return irPop.visibleIds.length > 0;
  },
  closeLast:function(){
	if(irPop.visibleIds.length>0)
	{
		irPop.showHide(irPop.visibleIds[irPop.visibleIds.length - 1],false);
	}
	else
	{
	    var background = ir.get(irPop.backgroundId);
		background.classList.remove("show");
    	background.style.zIndex = -20;
    	background.onclick = null;
	}
  },
  showHide : function(idOrObj,supressBackground) {
	var visIds = irPop.visibleIds;
    var pop = idOrObj;
    if (typeof (idOrObj) == "string") {
        pop = ir.get(idOrObj);
    }
    var background = ir.get(irPop.backgroundId);
    if (pop.classList.contains("show")) {// hiding
    	pop.classList.remove("show");
        for (var i = 0; i < visIds.length; i++) {
            if (visIds[i] == pop.id) {
            	visIds.splice(i, 1);
                break;
            }
        }
        if(visIds.length>0)
    	{
        	var lastPop = ir.get(visIds[visIds.length-1]);
        	background.style.zIndex = lastPop.style.zIndex - 1;
        	background.onclick = irPop.closeLast;
        	if(lastPop.classList.contains("noback"))
    		{
                background.classList.remove("show");
    		}
    	}
        else
    	{
            background.classList.remove("show");
        	background.style.zIndex = -20;
        	background.onclick = null;
    	}
    } else {// showing
        var zIndex = 200;
        if (irPop.visibleIds.length > 0) {
            var lastPop = document.getElementById(irPop.visibleIds[irPop.visibleIds.length - 1]);
            if (lastPop != null) {
                zIndex = Number(lastPop.style.zIndex) + 2;
            }
        }
        pop.style.zIndex = zIndex;
        irPop.visibleIds.push(pop.id);
        if(supressBackground || pop.classList.contains("noback"))
    	{
        	background.classList.remove("show");
        	background.onclick = null;
    	}
        else if (!background.classList.contains("show")) {
            background.classList.add("show");
        	background.onclick = irPop.closeLast;
            background.style.zIndex = zIndex - 1;
        }
        else
    	{
            background.style.zIndex = zIndex - 1;
    	}
        pop.classList.add("show");
        pop.focus();
        try{pop.scrollTop=0;}catch(awNevermind){}
    }
}, zz_irPop : 0 };
function popup(id,suppressBackground) {
    irPop.showHide(id,suppressBackground);
}