<%@include file="dochdr.jspf"%>
<script src="matrixactions.js?s=<%=stamp%>" TYPE="text/javascript"></script>
<script>
model.actions = <%=_bean.get("Actions") %>;
model.sortType = <%=_bean.get("SortType") %>;
</script>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm()%>
<div id='infoPopup' class='popup info'>	
		<div class='tableWrap'>
			<div class='tableTitle'>User Modes Table</div>
			<table class='table' id='userModesTable'>
				<thead>
					<tr>
						<td class='tdl'>User Mode</td><td class='tdc'>Initiative</td><td class='tdc'>Initiative dice</td><td class='tdc'>Notes</td>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td class='tdl'>Augmented Reality (AR)</td>
						<td class='tdc'>Physical Initiative</td>
						<td class='tdc'>Physical Initiative Dice</td>
						<td class='tdc'>can be distracting</td>
					</tr>
					<tr>
						<td class='tdl'>Cold-Sim (VR)</td>
						<td class='tdc'>Data Processing + Intuition</td>
						<td class='tdc'>3D6</td>
						<td class='tdc'></td>
					</tr>
					<tr>
						<td class='tdl'>Hot-Sim (VR)</td>
						<td class='tdc'>Data Processing + Intuition</td>
						<td class='tdc'>4D6</td>
						<td class='tdc'>+2 dice pool bonus to Matrix actions</td>
					</tr>
				</tbody>
			</table>
		</div>	
		
		
		<div class='pWrap'>
			<div class='pTitle'>USES OF SNOOP</div>
			<p id='snoopP'>&emsp;Snooping is often used for more than just eavesdropping.
				If your target makes a commcall, you can note the person they
				called and try to find them online with a Matrix Perception
				action; if they`re within 100 meters, you spot them right away
				(if they`re not running silent; if they are, then it`s back to the
				Matrix Perception Test).
			</p>
		</div>
		
		<div class='pWrap'>
			<div class='pTitle'>Matrix Perception</div>
			<p id='matrixPerceptionP'>&emsp;When you take a Matrix Perception action, each hit can reveal one piece of information you ask of your gamemaster. Here`s a list of some of the things Matrix Perception can tell you. It`s not an exhaustive list, but it should give you a pretty good idea about how to use Matrix Perception:
				<br>&emsp;&bull;Spot a target icon you`re looking for.
				<br>&emsp;&bull;The most recent edit date of a file.
				<br>&emsp;&bull;The number of boxes of Matrix damage on the target`s Condition Monitor.
				<br>&emsp;&bull;The presence of a data bomb on a file.
				<br>&emsp;&bull;The programs being run by a persona.
				<br>&emsp;&bull;The target`s device rating.
				<br>&emsp;&bull;The target`s commode.
				<br>&emsp;&bull;The rating of one of the target`s Matrix attributes.
				<br>&emsp;&bull;The type of icon (host, persona, device, file), if it is using a non-standard (or even illegal) look.
				<br>&emsp;&bull;Whether a file is protected, and at what rating.
				<br>&emsp;&bull;The grid a persona, device, or host is using.
				<br>&emsp;&bull;If you`re out on the grid, whether there is an icon running silent within 100 meters.
				<br>&emsp;&bull;If you`re in a host, whether there is an icon running silent in the host.
				<br>&emsp;&bull;If you know at least one feature of an icon running silent, you can spot the icon (Running Silent, below).
				<br>&emsp;&bull;The last Matrix action an icon performed, and when.
				<br>&emsp;&bull;The marks on an icon, but not their owners.
			</p>
		</div>
		<div class='tableWrap'>
			<div class='tableTitle'>Matrix Search Table</div>
			<table class='table' id='matrixSearchTable'>
				<thead>
					<tr>
						<td class='tdl'>Information is...</td><td class='tdc'>Threshold</td><td class='tdc'>Time</td>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td class='tdl'>General Knowledge or Public</td>
						<td class='tdc'>1</td>
						<td class='tdc'>1 minute</td>
					</tr>
					<tr>
						<td class='tdl'>Limited Interest or Not Publicized</td>
						<td class='tdc'>3</td>
						<td class='tdc'>30 minutes</td>
					</tr>
					<tr>
						<td class='tdl'>Hidden or Actively Hunted and Erased</td>
						<td class='tdc'>6</td>
						<td class='tdc'>12 hours</td>
					</tr>
					<tr>
						<td class='tdl'>Protected or Secret</td>
						<td class='tdc'>N/A</td>
						<td class='tdc'>N/A</td>
					</tr>
				</tbody>
			</table>
			<table class='table'>
				<thead>
					<tr>
						<td class='tdl'>Information is...</td><td class='tdc'>Dice pool Modifier</td>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td class='tdl'>Intricate or Specialized</td>
						<td class='tdc'>-1</td>
					</tr>
					<tr>
						<td class='tdl'>Obscure</td>
						<td class='tdc'>-2</td>
					</tr>
					<tr>
						<td class='tdl'>On another grid</td>
						<td class='tdc'>-2</td>
					</tr>
				</tbody>
			</table>
		</div>		
		<div class='tableWrap'>
			<div class='tableTitle'>Matrix Spotting Table</div>
			<table class='table' id='matrixSpottingTable'>
				<thead>
					<tr>
						<td class='tdl'>Target is...</td><td class='tdc'>NOT RUNNING SILENT</td><td class='tdc'>RUNNING SILENT</td>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td class='tdl'>Within 100 meters</td>
						<td class='tdc'>Automatic</td>
						<td class='tdc' rowspan='3'>Opposed Computer + Intuition [Data Processing] v. Logic + Sleaze Test</td>
					</tr>
					<tr>
						<td class='tdl'>Outside 100 meters</td>
						<td class='tdc'>Simple Computer + Intuition [Data Processing]</td>
					</tr>
					<tr>
						<td class='tdl'>A Host</td>
						<td class='tdc'>Automatic</td>
					</tr>
				</tbody>
			</table>
		</div>
</div>

<div class='container' id='actionsDiv'>	
	<div id='actionList'></div>
</div>
	<%=_bean.endForm() %>
	<%@include file="docftr.jspf"%>