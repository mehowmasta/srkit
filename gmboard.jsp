<%@include  file="dochdr.jspf"%>
<script src="gmboard.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<script>
model.blankCharacter = <%= new CharacterRec()%>;
model.groups = new KeyedArray("Row",<%= _bean.get("Groups")%>);
</script>
<style>
.initBlock .thumb{
	margin:0.2rem 0 0.2rem 0.2rem;
}
.title{
	width:100%;padding-top: 0.5rem;
}
</style>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm() %>
<div class='pageSubtitle' style='display:block;'>Initiative<span style='float:right;padding-right:1rem;border:0 none;' class='subtitle' id='initStatus'></span></div>
<div class='initiativeContainer' id='initiativeContainer'></div>
<div class='container flex'>
	<div class='flex' style='padding:1rem 0;text-align: center;'>
		<button class='parentBtn' id='addInitBtn' type='button' onclick='view.addInit()'>Add</button>
		<div class='childBtnContainer' id='addInitBtnSelector'>
			<div class='x' style='top:-2rem;' onclick='view.toggleAddButtons()'>X</div>
			<button type='button' class='childBtn hover' data-hover='Choose a blank PC template' onclick='view.pickDefault("pc")'>Add PC</button>
			<button type='button' class='childBtn hover' data-hover='Choose a blank NPC template' onclick='view.pickDefault("npc")'>Add NPC</button>		
			<button type='button' class='childBtn hover' data-hover='Choose a blank Critter template' onclick='view.pickDefault("critter")'>Add Critter</button>
			<button type='button' class='childBtn hover' data-hover='Choose a runner from your private collection' onclick='view.pickCharacter()'>Runners</button>
			<% if(!_bean.isGuest()) {%>
			<button type='button' class='childBtn hover' data-hover='Choose a NPC from the Global SIN Registry' onclick='view.pickNPC()'>Registry</button>
			<% } %>
			<br>
		</div>
		<button type='button' id='clearInitBtn' style='display:none;' onclick='view.clearInitiative()'>Clear</button>
		<button type='button' id='startCombatBtn' tabindex='1' style='display:none;' onclick='view.startCombat()'>Start Combat</button>
		<button type='button' id='endCombatBtn' tabindex='1' style='display:none;' onclick='view.endCombat()'>End Combat</button>
	</div>
</div>
<div class='pageSubtitle'>Score Board</div>
<div class='boardContainer' id='boardContainer'></div>
<div class='container flex' style='padding:1rem 0;'>
		<button class='parentBtn' id='addGridBtn' type='button' onclick='view.addGrid()'>Add Track</button>
		<div class='childBtnContainer' id='addGridBtnSelector'></div>	
</div>
<div class='pageSubtitle'>Award</div>
<div class='awardContainer' id='awardContainer'></div>
<div class='container flex' style='padding-bottom:1rem;'>
	<div class='flex' style='padding:1rem 0;text-align: center;justify-content:space-between;align-items:flex-end;'>
		<div id='awardCharacterList' class='flex' style='flex:1;justify-content:flex-start;'>
		</div>
	</div>
	<div class='spacer'></div>
	<div id='awardNuyenWrap' class='flex' style='align-items:center;margin:0 1rem;'>
		<button type='button' class='mini' onclick='view.minusAward("Nuyen")'  onmousedown='view.minusHoldAward("Nuyen")' onmouseup='view.minusReleaseAward("Nuyen")'>-</button>
		<div class='inputWrap'>
			<input type='number' id='awardNuyen' name='awardNuyen' tabindex='1' size='2' style='width:10rem;text-align: right;' value='0' min='0' step='500' onfocus='this.select();'>
			<label class='inputLabel'>nuyen</label>
		</div>
		<button type='button' class='mini' onclick='view.plusAward("Nuyen")'  onmousedown='view.plusHoldAward("Nuyen")'  onmouseup='view.plusReleaseAward("Nuyen")'>+</button>
	</div>
	<div id='awardKarmaWrap' class='flex' style='align-items:center;margin:0 1rem;'>
		<button type='button' class='mini' onclick='view.minusAward("Karma")' onmousedown='view.minusHoldAward("Karma")'  onmouseup='view.minusReleaseAward("Karma")'>-</button>
		<div class='inputWrap'>
			<input type='number' id='awardKarma' name='awardKarma' tabindex='1' size='2' style='width:6rem;text-align: right;' value='0' min='0' step='1' onfocus='this.select();'>
			<label class='inputLabel'>karma</label>
		</div>
		<button type='button' class='mini' onclick='view.plusAward("Karma")'   onmousedown='view.plusHoldAward("Karma")'  onmouseup='view.plusReleaseAward("Karma")'>+</button>
	</div>
	<div class='spacer'></div>
	<div  style='text-align: center;'>
		<button type='button'  onclick='view.addAwardCharacter()'>Add Runners</button>
		<button type='button' style='display:none;'  onclick='view.addAwardItem()'>Add Item</button>
		<button type='button' id='awardSendBtn' disabled tabindex='1' onclick='view.sendAward()'>Send</button>
	</div>
</div>
<div class='pageSubtitle'>Map</div>
<div class='container flex' style='padding:1rem 0;align-items:center;'>
	<%= _bean.get("ctlMaps") %>	
	<div class='inputWrap' id='ctlZoomWrap' style='display:none;'>
		<input id='ctlZoom' name='ctlZoom' type='number' min=0.2 max=3 step=0.1 oninput='view.updateZoom()' onfocus='this.select()' style='width:5rem;text-align:right;' value='<%=_bean.get("ZoomValue")%>'>
		<label class='inputLabel'>Zoom</label>
	</div>
	<div class='inputWrap' id='ctlHideGrid' style='display:none;'>
		<input id='hideGridChk' type='checkbox' onclick='view.toggleGrid()'><label for='hideGridChk'>Hide Grid</label>
	</div>
	
	<button id='saveMapBtn' type='button' onclick='view.saveMapData()' <%= _bean.userIsGuest()?"disabled":"" %> style='max-height: 3.7rem;display: none;'>Save</button>
</div>
<div class='container flex mapColorPalette' id='colorDiv' style='display:none;'></div>
<div class='container flex mapContainer' id='mapDiv'></div>

<%=_bean.endForm() %>

<%@include  file="pickarmorpop.jspf" %>
<%@include  file="pickcharacterpop.jspf" %>
<%@include  file="pickcyberdeckpop.jspf"%>
<%@include  file="pickcyberwarepop.jspf"%>
<%@include  file="pickdronepop.jspf"%>
<%@include  file="pickgearpop.jspf"%>
<%@include  file="pickmapobjectpop.jspf"%>
<%@include  file="picknpcpop.jspf"%>
<%@include  file="pickvehiclepop.jspf"%>
<%@include  file="pickweaponpop.jspf"%>
<%@include  file="detailpop.jspf"%>
<%@include  file="detailmodpop.jspf"%>
<%@include  file="docftr.jspf"%>
