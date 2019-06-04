<%@include  file="dochdr.jspf"%>
<script src="skilllist.js?s=<%=stamp %>" TYPE="text/javascript"></script>

<body onload='view.aaOnLoad()'>
<%=_bean.startForm() %>
<div>
	<div id='stepPriority'>
		<div class='title'>MetaType</div>
		<div class='flex'>
			<button type='button' class='toggle' onclick='view.selectMetatypePriority("A")'>
				A 
				Human (9)
				Elf (8)
				Dwarf (7)
				Ork (7)
				Troll (5)
			</button>
		</div>
	</div>
	
</div>
<%=_bean.endForm() %>
<%@include  file="docftr.jspf"%>
