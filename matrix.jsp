<%@include file="dochdr.jspf"%>
<script src="matrix.js?s=<%=stamp%>" TYPE="text/javascript"></script>
<body onload='view.aaOnLoad()'>
	<%=_bean.startForm()%>
	<div class='container flex'>
		<button type='button' onclick='sr5.go("matrixactions.jsp")'>Matrix Actions</button>
		<button type='button' onclick='sr5.go("programs.jsp")'>Programs</button>
	</div>
<% /* commented out due to copyrights...
	<div class='pageSubtitle'>Matrix Attributes (ASDF)</div>
	<div class='container flex'>
		<div class='pWrap'>
			<div class='pTitle'>Attack</div>
			<p id='attackP'>&emsp;Your Attack rating reflects the programs
				and utilities you have running on your deck that inject harmful code
				into other operating systems, or use brute-force algorithms to break
				encryptions and protections to lay the virtual smackdown. Attack
				software is high-risk, high-reward, because firewall protocols tend
				to treat it harshly, doing damage that could hurt your persona if
				you blow it. Attack actions are good for making quick break-ins,
				damaging devices, and dealing with Matrix threats in a very fast but
				loud way.</p>
		</div>
		<div class='pWrap'>
			<div class='pTitle'>Sleaze</div>
			<p id='sleazeP'>&emsp;The applications making up your Sleaze
				attribute mask your Matrix presence, probe the defenses of targets,
				and subtly alter a target system`s code. Sleaze software is
				delicate, and one mistake will spill the soybeans on you to your
				target. Sleaze actions are good for intrusions in which you have
				plenty of time and in dealing with Matrix problems in a slow but
				quiet way.</p>
		</div>
		<div class='pWrap'>
			<div class='pTitle'>Data Processing</div>
			<p id='dataProcessingP'>&emsp;Your Data Processing attribute
				measures your device`s ability to handle information, datastreams,
				and files. It is used for Matrix actions that aren`t, as a general
				rule, illegal.</p>
		</div>

		<div class='pWrap'>
			<div class='pTitle'>Firewall</div>
			<p id='firewallP'>&emsp;Your Firewall attribute is your
				protection against outside attacks. It contains code filters, file
				checkers, virus detection and eradication software, and other
				defensive programming. Firewall actions are defensive in nature. The
				most important role of the Firewall is as virtual armor against
				Matrix damage.</p>
		</div>

	</div>
*/ %>
	<%=_bean.endForm() %>
	<%@include file="docftr.jspf"%>