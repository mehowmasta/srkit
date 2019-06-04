"use strict";
var nav = {
		toggleMenu:function(){
			var navMenu = ir.get("navMenu");
			var navButton = ir.get("navButton");
			var background = ir.get("popupBackground");
			if(navMenu.classList.contains("show"))
			{				
				background.onclick=null;
			}
			else
			{
				background.onclick=nav.toggleMenu;
			}
			popup(navMenu);
		},
		zz_nav:0
};