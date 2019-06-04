var hoverPop = {
		id:'hoverPop',
		init:function(){
			if(sr5.isMobile)
			{
				return;
			}			
			var hoverElements = document.getElementsByClassName("hover");
			for(var i = 0, z= hoverElements.length;i<z;i++)
			{
				ele = hoverElements[i];
				if(!ele.dataset.hover)
				{
					continue;
				}
				ele.onmouseover = hoverPop.mouseOver;
				ele.onmouseout = hoverPop.mouseOut;
			}
		},
		mouseMove:function(event){
			var self = hoverPop;
			var pop = document.getElementById(self.id);
			var x = event.clientX;
			var y = event.clientY+20;
			var xDiff = x - (document.documentElement.clientWidth - pop.clientWidth);
			var yDiff = y - (document.documentElement.clientHeight - (pop.clientHeight+20));
			if(xDiff > 0)
			{
				x-=xDiff;
			}
			if(yDiff > 0)
			{
				y-=(pop.clientHeight+20);
			}
			x += document.documentElement.scrollLeft;
			y += document.documentElement.scrollTop;
			pop.style.transform = "translate("+x+"px,"+y+"px)";
		},
		mouseOver:function(event){
			var self = hoverPop;
			var target = event.target;
			if(target.dataset.hover)
			{
				var pop = document.getElementById(self.id);
				document.getElementById(self.id +"Content").innerHTML = target.dataset.hover;
				target.onmousemove = hoverPop.mouseMove;
				pop.classList.add("show");
			}
			
		},
		mouseOut:function(event){
			var self = hoverPop;
			var pop = document.getElementById(self.id);
			pop.onmousemove = null;
			pop.classList.remove("show");
		},
		zz_hoverPop:0
}