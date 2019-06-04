<%@include  file="dochdr.jspf"%>
<script src="skills.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm() %>
<div class='container flex'>
	<button type='button' onclick='sr5.go("skilllist.jsp")'>Skill List</button>
</div>
<% /* commented out due to copyrights...
<div class='container flex'>
<div class='pWrap'>
		<div class='pTitle'>Combat Active Skills</div>
		<p id='combatActiveSkillsP'>&emsp;When the punching and the kicking and the shooting
		starts, these are the skills you use. All Combat skills
		are linked to Agility unless otherwise noted. For more
		on how skills are used in combat, refer to the Combat
		chapter, p. 158.</p>
	</div>
	
	<div class='pWrap'>
		<div class='pTitle'>Knowledge Skills</div>
		<p id='knowledgeSkillsP'>&emsp;Knowledge skills fall into four categories: Street, Academic,
		Professional, and Interests. Each category presents
		an opportunity to shape the experiences of a character
		far beyond what happens on a run. Knowledge
		skills do not affect tests the way Active skills do. In
		certain cases Knowledge skills may provide the background
		needed to complete an action, but they typically
		do not provide dice for Active skill tests.
		You get a number of free Knowledge skill points at
		character creation. Skill advancement and additional
		Knowledge skills follow the skill advancement rules
		(Character Advancement, p. 103).</p>
	</div>
	
	<div class='pWrap'>
		<div class='pTitle'>Language Skills</div>
		<p id='languageSkillsP'>&emsp;There are few situations where language skills should
		require a dice roll. Characters with a language skill don`t
		need to make tests to understand one another in every
		day situations. The character`s skill level serves as a
		benchmark for how well they can communicate in a foreign
		language over time. However, in critical situations
		where precise translation is important, a gamemaster
		may elect to require a Language skill test. For more information,
		see Using Language Skills, at right.</p>
	</div>
	
	<div class='pWrap'>
		<div class='pTitle'>PHYSICAL ACTIVE SKILLS</div>
		<p id='physicalActiveSkillsP'>&emsp;These skills are all about actions you take with your
		body (besides things covered in Combat skills). You`ll
		find the specific rules for using each skill (or a reference
		for where you can find the rules) in the skill description.</p>
	</div>
	<div class='pWrap'>
		<div class='pTitle'>SOCIAL SKILLS</div>
		<p id='socialSkillsP'>&emsp;Dice rarely need to get involved when characters need
		to solve problems between one another. Dealing with
		NPCs isn`t always so easy. Social skills give characters
		the ability to problem-solve without expending bullets
		or mana. These skills tend to be linked to the Charisma
		attribute.<br>&emsp;Social skills are intended to be used to complement
		good role-playing, not replace it. The Social Test should
		come either at the end of a well-role-played scene to
		wrap it up, or in place of a social situation that would be
		less interesting to actually play through to get through it
		quickly. The gamemaster can provide modifiers on this
		test based on how well you make your point, or how
		much bulldrek you`re able to pile up without flinching.</p>
	</div>
	<div class='pWrap'>
		<div class='pTitle'>MAGICAL SKILLS</div>
		<p id='magicSkillsP'>&emsp;Magic skills are reserved for those who practice magic.
		In order to acquire magic-specific skills, characters
		must be an Aspected Magician, Magician, or Mystic
		Adept. In order to use these skills, their Magic rating
		must be 1 or higher. Please visit Magic, p. 276, for all
		your Magical skill-using needs.</p>
	</div>
	
	<div class='pWrap'>
		<div class='pTitle'>RESONANCE SKILLS</div>
		<p id='resonanceSkillsP'>&emsp;Resonance skills are a unique subset of Matrix skills
		that can only be used by technomancers. Resonance
		skills, like magic skills, require the character to have a
		special attribute. The Resonance attribute also serves
		as the linked attribute for all of the skills.</p>
	</div>
	
	<div class='pWrap'>
		<div class='pTitle'>TECHNICAL SKILLS</div>
		<p id='technicalSkillsP'>&emsp;Technical skills are called upon when you operate or fix
		something. Technical skills link to a variety of attributes,
		listed with the skill.</p>
	</div>
	<div class='pWrap'>
		<div class='pTitle'>VEHICLE SKILLS</div>
		<p id='vehicleSkillsP'>&emsp;
		Vehicle skills are used for driving and performing combat
		maneuvers with vehicles (see Vehicles, p. 198). Unless
		otherwise noted, Vehicle skills are linked to Reaction.</p>
	</div>
</div>
*/ %>
<%=_bean.endForm() %>
<%@include  file="docftr.jspf"%>
