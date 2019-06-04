<%@include  file="dochdr.jspf"%>
<script src="magic.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm() %>
<div class='container flex'>
	<button type='button' onclick='sr5.go("spelllist.jsp")'>Spells</button>
	<button type='button' onclick='sr5.go("adeptpowerlist.jsp")'>Adept Powers</button>
	<button type='button' onclick='sr5.go("mentorspiritlist.jsp")'>Mentor Spirits</button>
	<button type='button' onclick='sr5.go("critterpowerlist.jsp")'>Critter Powers</button>
</div>
<% /* commented out due to copyrights...
<div class='container flex'>
<div class='pWrap'>
		<div class='pTitle'>Combat Spells</div>
		<p id='combatSpellsP'>&emsp;These spells are quick, dirty, and violent.
		The energy of the spell is used to harm, maim, or
		otherwise frag the target. Spell energy may be
		channeled directly into the target, damaging it from
		within, or it may generate external energy to damage
		the target from the outside.</p>
	</div>
	
	<div class='pWrap'>
		<div class='pTitle'>Detection Spells</div>
		<p id='detectionSpellsP'>&emsp;These spells enhance the senses. They
		allow the subject of the spell to see or hear over great
		distances, or grant new sensory abilities. There are
		also Detection spells to sense the presence of other
		beings, magic, life, and/or enemies.</p>
	</div>
	
	<div class='pWrap'>
		<div class='pTitle'>Health Spells</div>
		<p id='healthSpellsP'>&emsp;These spells affect the condition and
		performance of a living body. They can be used to treat
		or heal injuries, purge poisons or toxins, and increase
		or decrease Attributes.</p>
	</div>
	
	<div class='pWrap'>
		<div class='pTitle'>Illusion Spells</div>
		<p id='illusionSpellsP'>&emsp;These spells can mess with a target`s
		perception. They can be used to deceive, make things
		invisible, confuse the senses, or provide simple or
		complex entertainments.</p>
	</div>
	<div class='pWrap'>
		<div class='pTitle'>Manipulation Spells</div>
		<p id='manipulationSpellsP'>&emsp;These spells are used by magicians
		to alter and shape their environment in a variety of
		ways. Manipulation spells can control the emotions
		or actions of a person, move objects, shape, create,
		or channel energy, or change a target`s form or
		appearance by altering its structure.</p>
	</div>
</div>
 */ %>
<%=_bean.endForm() %>
<%@include  file="docftr.jspf"%>
