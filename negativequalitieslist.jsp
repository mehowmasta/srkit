<%@include  file="dochdr.jspf"%>
<script src="negativequalitieslist.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<script>
model.qualities = <%=_bean.get("Qualities") %>;
</script>
<style>
</style>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm() %>
<div class='popup info' id='infoPopup'>	
	<div class='popupContent'>
	<div class='tableWrap'>
		<div class='tableTitle'>ALLERGY TABLE</div>
		<table class='table' id='allergyTableTable'>
			<thead>
				<tr>
					<td class='tdl'>Condition</td><td class='tdc'>Karma</td><td class='tdl'>Description</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class='tdl'>Uncommon</td>
					<td class='tdc'>2</td>
					<td class='tdl'>The substance or condition is rare for the local environment. Examples: silver, gold,
					antibiotics, grass.</td>
				</tr>
				<tr>
					<td class='tdl'>Common</td>
					<td class='tdc'>7</td>
					<td class='tdl'>+The substance or condition is prevalent in the local environment. Examples: sunlight,
					seafood, bees, pollen, pollutants, Wi-Fi sensitivity, soy, wheat.</td>
				</tr>
			</tbody>
		</table>
		<table class='table' style='width:100%;'>
			<thead>
				<tr>
					<td class='tdl'>Severity</td><td class='tdc'>KARMA</td><td class='tdl'>Effects</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class='tdl'>Mild</td>
					<td class='tdc'>3</td>
					<td class='tdl'>Symptoms are discomfiting and distracting. Apply a -2 dice pool modifier to the character`s
					Physical Tests while under the effects of the Allergy.</td>
				</tr>
				<tr>
					<td class='tdl'>Moderate</td>
					<td class='tdc'>8</td>
					<td class='tdl'>Contact with the allergen produces intense pain. Apply a -4 dice pool modifier to all Physical
					Tests made while a character experiences the symptoms.</td>
				</tr>
				<tr>
					<td class='tdl'>Severe</td>
					<td class='tdc'>13</td>
					<td class='tdl'>Contact with the allergen results in extreme pain and actual physical damage. Apply a -4
					dice pool modifier to all tests made while a character experiences symptoms. The character
					also suffers 1 box of Physical Damage (unresisted) for every 1 minute they are exposed to
					the allergen.</td>
				</tr>
				<tr>
					<td class='tdl'>Extreme</td>
					<td class='tdc'>18</td>
					<td class='tdl'>A character at this level, when exposed to the allergen, goes into full anaphylactic shock. The
					character receives a -6 dice pool modifier for anything they do. The character is considered
					to be in excruciating agony. The character suffers 1 box of Physical Damage (unresisted) for
					every 30 seconds they are exposed to the allergen. First Aid, Medicine, or magical means can
					stop the damage taken from the anaphylactic shock.</td>
				</tr>
			</tbody>
		</table>
	</div>
	<div class='tableWrap'>
		<div class='tableTitle'>Asthma Table</div>
		<table class='table' id='asthmaTable'>
			<thead>
				<tr>
					<td class='tdl'>Damage</td>
					<td class='tdl'>All Effect Are Cumulative</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class='tdl'>1 box</td>
					<td class='tdl'>Wheezing; -1 dice pool modifier to all
					Physical Actions; Social Limit decreased by 1</td>
				</tr>
				<tr>
					<td class='tdl'>2 boxes</td>
					<td class='tdl'>Shortness of breath; -1 dice pool modifier to all
					Actions; Social Limit decreased by 1 additional point</td>
				</tr>
				<tr>
					<td class='tdl'>4 boxes</td>
					<td class='tdl'>Chest tightness; further Fatigue damage resisted
					with only Willpower</td>
				</tr>
				<tr>
					<td class='tdl'>8 boxes</td>
					<td class='tdl'>Wracking cough; -1 dice pool modifier to all
					Actions; Social Limit decreased by 1 additional point</td>
				</tr>				
			</tbody>
		</table>
	</div>
	
	
	<div class='tableWrap'>
		<div class='tableTitle'>Day Job Table</div>
		<table class='table' id='dayJobTable' style='width:100%;'>
			<thead>
				<tr>
					<td class='tdc'>KARMA VALUES</td><td class='tdc'>Salary/Month</td><td class='tdc'>Hours/Week</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class='tdc'>5</td>
					<td class='tdc'>1,000¥</td>
					<td class='tdc'>10</td>
				</tr>
				<tr>
					<td class='tdc'>10</td>
					<td class='tdc'>2,500¥</td>
					<td class='tdc'>20</td>
				</tr>
				<tr>
					<td class='tdc'>15</td>
					<td class='tdc'>5,000¥</td>
					<td class='tdc'>40</td>
				</tr>
			</tbody>
		</table>		
	</div>
	<div class='tableWrap'>
		<div class='tableTitle'>Phobia TABLE</div>
		<table class='table' id='phobiaTable' style='width:100%;'>
			<thead>
				<tr>
					<td class='tdl'>Condition</td><td class='tdc'>KARMA</td><td class='tdl'>Description</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class='tdl'>Uncommon</td>
					<td class='tdc'>2</td>
					<td class='tdl'>The triggering condition is
					relatively rare, such as the
					smell of roses or specific
					insects.</td>
				</tr>
				<tr>
					<td class='tdl'>Common</td>
					<td class='tdc'>5</td>
					<td class='tdl'>The triggering condition is
					commonly encountered, such
					as sunlight, trolls, insects in
					general, magic, the outdoors,
					or crowds.</td>
				</tr>
			</tbody>
		</table>
		<table class='table' style='width:100%;'>
			<thead>
				<tr>
					<td class='tdl'>Severity</td><td class='tdc'>KARMA</td><td class='tdl'>Effects</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class='tdl'>Mild</td>
					<td class='tdc'>2</td>
					<td class='tdl'>-1 dice pool modifier to all
					actions</td>
				</tr>
				<tr>
					<td class='tdl'>Moderate</td>
					<td class='tdc'>5</td>
					<td class='tdl'>-3 dice pool modifier to all
					actions; Composure (2) Test or
					must get away from the source</td>
				</tr>
				<tr>
					<td class='tdl'>Severe</td>
					<td class='tdc'>10</td>
					<td class='tdl'>-6 dice pool modifier;
					Composure (5) Test or must flee
					from the source for (5 - hits)
					Combat Turns</td>
				</tr>				
			</tbody>
		</table>
	</div>
	<div class='tableWrap'>
		<div class='tableTitle'>Prejudiced TABLE</div>
		<table class='table' id='prejudicedTable' style='width:100%;'>
			<thead>
				<tr>
					<td class='tdl'>PREVALENCE OF TARGET GROUP</td><td class='tdc'>KARMA VALUE</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class='tdl'>Common target group (e.g, humans,metahumans)</td>
					<td class='tdc'>5 Karma</td>
				</tr>
				<tr>
					<td class='tdl'>Specific target group (e.g., the Awakened,technomancers, shapeshifters, aspectedmagicians)</td>
					<td class='tdc'>3 Karma</td>
				</tr>
			</tbody>
		</table>
		<table class='table' style='width:100%;'>
			<thead>
				<tr>
					<td class='tdl'>Degree</td><td class='tdc'>KARMA VALUE</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class='tdl'>Biased (e.g., closet meta-hater)</td>
					<td class='tdc'>0 Karma</td>
				</tr>
				<tr>
					<td class='tdl'>Outspoken (e.g., typical memberof Humanis)</td>
					<td class='tdc'>2 Karma</td>
				</tr>
				<tr>
					<td class='tdl'>Radical (e.g., racial supremacist)</td>
					<td class='tdc'>5 Karma</td>
				</tr>				
			</tbody>
		</table>
	</div>
	<div class='tableWrap'>
		<div class='tableTitle'>Scorched Table</div>
		<table class='table' id='scorchedTable'>
			<thead>
				<tr>
					<td class='tdl'>Effect</td><td class='tdc'>Game Rules</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class='tdl'>Memory Loss (short term)</td>
					<td class='tdl'>The character does not remember slotting
					a BTL chip. The character makes another
					Withdrawal Test immediately. A failed test
					means the craving comes back immediately,
					as do the symptoms of withdrawal. Character
					must slot another chip. For encountering IC, a
					character must make a Memory Test with a
					threshold increased by +1. A failed Memory
					Test results in gaps in memory and possible
					disorientation while in host.</td>
				</tr>
				<tr>
					<td class='tdl'>Memory Loss (long term)</td>
					<td class='tdl'>The same effects of Memory Loss (short
					term) apply. In addition, for the duration of
					the effect, the character loses access to one
					active skill. He simply does not remember
					how to use it (for example, the Pistols skill).
					Treat as unaware in that skill until symptoms
					abate.</td>
				</tr>
				<tr>
					<td class='tdl'>Blackout</td>
					<td class='tdl'>For the duration of the effect, the character
					retains no memories of events during that
					time frame. Memory cannot be restored by
					technological or magical means.</td>
				</tr>
				<tr>
					<td class='tdl'>Migraines</td>
					<td class='tdl'>The character receives -2 to all Physical and
					Mental tests, sensitivity to light, and nausea
					(p. 409).</td>
				</tr>
				<tr>
					<td class='tdl'>Paranoia/Anxiety</td>
					<td class='tdl'>Character must make Social Tests for even
					basic interactions. These are Success
					Tests with a threshold of 5. If no apparent
					skill applies, the character must default to
					Charisma -1. Failure means the character
					reacts with paranoia or anxiety in that
					situation for the duration of the effect.</td>
				</tr>
			</tbody>
		</table>
	</div>
	</div>
</div>
<div class='container' id='qualitiesDiv'>	
	<div class='divider'>Negative Qualities</div>
	<div class='flex' style='flex-wrap:wrap;' id='qualityList'></div>
</div>
<%=_bean.endForm() %>
<%@include  file="docftr.jspf"%>
