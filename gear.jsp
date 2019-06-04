<%@include file="dochdr.jspf"%>
<script src="gear.js?s=<%=stamp%>" TYPE="text/javascript"></script>
<body onload='view.aaOnLoad()'>
	<%=_bean.startForm()%>
	<div id='infoPopup' class='popup info'>
		<div class='container flex'>
			<div class='tableWrap'>
				<div class='tableTitle'>CONCEALABILITY MODIFIERS</div>
				<table class='table' id='concealTable'>
					<thead>
						<tr>
							<td class='tdc'>Modifier*</td>
							<td class='tdl'>Example Items</td>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td class='tdc'>-6</td>
							<td class='tdl'>RFID tag, bug slap patch, microdrone,
								contact lenses</td>
						</tr>
						<tr>
							<td class='tdc'>-4</td>
							<td class='tdl'>Hold-out pistol, monowhip, ammo clip,
								credstick, chips/softs, sequencer/passkey, autopicker, lockpick
								set, commlink, glasses</td>
						</tr>
						<tr>
							<td class='tdc'>-2</td>
							<td class='tdl'>Light pistol, knife, sap, minidrone,
								microgrenade, flash-pak, jammer, cyberdeck, rigger command
								console</td>
						</tr>
						<tr>
							<td class='tdc'>-0</td>
							<td class='tdl'>Heavy pistol, machine pistol with folding
								stock collapsed, grenade, goggles, ammo belt/drum, club,
								extendable baton (collapsed)</td>
						</tr>
						<tr>
							<td class='tdc'>+2</td>
							<td class='tdl'>SMG, machine pistol with folding stock
								extended, medkit, small drone, extendable baton (extended), stun
								baton</td>
						</tr>
						<tr>
							<td class='tdc'>+4</td>
							<td class='tdl'>Sword, sawed-off shotgun, bullpup assault
								rifle</td>
						</tr>
						<tr>
							<td class='tdc'>+6</td>
							<td class='tdl'>Katana, monosword, shotgun, assault rifle,
								sport rifle, crossbow</td>
						</tr>
						<tr>
							<td class='tdc'>+8</td>
							<td class='tdl'>Sniper rifle, bow, grenade launcher, medium
								drone</td>
						</tr>
						<tr>
							<td class='tdc'>+10/Forget about it</td>
							<td class='tdl'>Machine gun, rocket launcher, missile
								launcher, staff, claymore, metahuman body</td>
						</tr>
					</tbody>
				</table>
				<div class='table' style='text-align: right;color:white;'>
					<i  style='font-size:1rem;'>*Applies to observer</i>
				</div>
			</div>
		</div>
	</div>
	<div class='container flex' style='padding-top:1rem;'>	
		<%=_bean.get("Buttons") %>
	</div>
	<% /* commented out due to copyrights...
	<div class='pageSubtitle'>GEAR GLOSSARY</div>
	<div class='container flex'>
		<div class='pWrap'>
			<div class='pTitle'>Accuracy</div>
			<p id='accuracyP'>&emsp;Refers to a weapon`s overall accuracy. A
				firearm`s Accuracy acts as the limit for tests involving that
				weapon, capping the number of hits that can be achieved on an attack
				roll using it.</p>
		</div>
		<div class='pWrap'>
			<div class='pTitle'>Ammo</div>
			<p id='ammoP'>&emsp;Refers to the amount of ammunition a
				ranged weapon can hold, followed by the method of reloading in
				parentheses: (b) means break action, (c) means detachable external
				box magazine, or "clip" in modern street parlance, (d) means drum,
				(ml) means muzzle-loader, (m) means internal magazine, (cy) means
				cylinder, and (belt) means belt-fed.</p>
		</div>
		<div class='pWrap'>
			<div class='pTitle'>Armor</div>
			<p id='armorP'>&emsp;Each piece of armor has an Armor value
				that adds to the wearer`s Damage Resistance dice pool (see Armor, p.
				168). Armor Penetration: All weapons have an Armor Penetration
				value, although in some cases, this value is listed with no value,
				meaning no AP. The AP value indicates how a weapon interacts with
				armor (see Armor Penetration, p. 169). A positive value adds to the
				target`s Armor value, while a negative value reduces the target`s
				Armor value.</p>
		</div>
		<div class='pWrap'>
			<div class='pTitle'>Availability</div>
			<p id='availP'>&emsp;The higher the Availability of an item,
				the more difficult and costly it is to get it (see Buying Gear, p.
				416). Gear without an Availability rating can be bought at an
				appropriate local store or ordered online without any trouble. The
				letter that follows an item`s numerical Availability rating shows
				whether the item is Restricted (R) or Forbidden (F). Items without a
				letter in parentheses are considered legal-they aren`t necessarily
				easy to find, but you won`t ever get arrested for seeking them (see
				(Il)legality, p. 419.</p>
		</div>
		<div class='pWrap'>
			<div class='pTitle'>Blast</div>
			<p id='blastP'>&emsp;This rating is possessed by grenades,
				missiles, rockets, and other area-of-effect weapons. Blast is the
				amount the blast weapon`s damage value is reduced per meter of
				distance from the explosion`s point of origin (see Blast Effects, p.
				436).</p>
		</div>
		<div class='pWrap'>
			<div class='pTitle'>Capacity</div>
			<p id='capacityP'>&emsp;Some sensor packages and cyberware can be
				equipped with a range of subsystems. A Capacity value is listed for
				these, indicating the maximum amount of "slots" worth of accessories
				the item can hold. If the Capacity is listed in brackets, it`s the
				cost of that subsystem or accessory, or the number of slots that
				item takes up. Some cyberware items with a Capacity cost can also be
				installed as standalone items (taking up Essence) rather than
				subsystems (taking up Capacity); if both costs are listed, only one
				applies, depending on whether you installed it in another item or in
				yourself.</p>
		</div>
		<div class='pWrap'>
			<div class='pTitle'>Concealability Modifier</div>
			<p id='concealP'>&emsp;This indicates how easy it is to hide a
				given item, and is applied as a dice pool modifier to Perception +
				Intuition Tests to spot the item (see Concealing Gear, p. 419).</p>
		</div>
		<div class='pWrap'>
			<div class='pTitle'>Cost</div>
			<p id='costP'>&emsp;This is the base price a character must
				pay to buy the item. If the item is legal, this is the standard
				price found at stores or online. Note that rare and/or illegal items
				may cost less or more depending on certain black market variations
				(see (Il)legality, p. 419). Cost is also subject to local supply and
				demand, so the gamemaster should feel free to adjust it accordingly
				for certain settings.</p>
		</div>
		<div class='pWrap'>
			<div class='pTitle'>Damage Value (DV)</div>
			<p id='dvP'>&emsp;A weapon`s Damage Value represents the
				base amount of harm, in points of damage, it causes when it hits a
				target. Damage Values consist of a number (the boxes of damage
				inflicted) and a letter indicating the type of damage caused: P for
				Physical, S for Stun. A parenthetical annotation following the
				damage type, such as (f) or (e), indicates that the damage is
				flechette or electrical (see Damage, p. 169).</p>
		</div>
		<div class='pWrap'>
			<div class='pTitle'>Device Rating</div>
			<p id='deviceRatingP'>&emsp;The Device Rating determines the overall
				quality and effectiveness of a device, from a stimulant patch to a
				commlink. Device ratings are described in detail on p. 234.</p>
		</div>
		<div class='pWrap'>
			<div class='pTitle'>Essence Cost</div>
			<p id='essCostP'>&emsp;All cyberware and bioware implants have
				an Essence Cost, representing the reduction of the character`s
				Essence rating that occurs when the augmentation is implanted.</p>
		</div>
		<div class='pWrap'>
			<div class='pTitle'>Mode</div>
			<p id='modeP'>&emsp;A firearm`s firing mode indicates the
				rate of fire it is capable of. Some weapons have more than one mode
				available, so characters may switch between them (see Firearms, p.
				178). The firing modes are: SS (single-shot), SA (semi-automatic),
				BF (burst fire), and FA (full auto).</p>
		</div>
		<div class='pWrap'>
			<div class='pTitle'>Mounts</div>
			<p id='mountsP'>&emsp;There are several places where a weapon
				accessory can be attached to a firearm: underbarrel, barrel, or
				top-mount. Only one accessory can be attached to a particular mount.
				Integral accessories (those that come with the weapon) don`t take up
				mount locations. Hold-outs don`t have mounts. Pistols, machine
				pistols, and SMGs do not have an underbarrel mount, just top and
				barrel mounts. All rifles and heavy weapons have all three types of
				mounts. Projectile weapons can only take accessories designed for
				them specifically.</p>
		</div>
		<div class='pWrap'>
			<div class='pTitle'>Reach</div>
			<p id='reachP'>&emsp;Melee weapons may have a Reach rating, an
				abstract value that rates the length and size of the weapon. A
				weapon with longer Reach gives its wielder an advantage over enemies
				with a lower Reach (see Reach, p. 184).</p>
		</div>
		<div class='pWrap'>
			<div class='pTitle'>Recoil Compensation (RC)</div>
			<p id='rcP'>&emsp;This lists the amount of recoil
				compensation a firearm has to offer, reducing the modifiers from a
				weapon`s recoil (see Recoil, p. 175). Numbers in parentheses refer
				to full recoil compensation that applies only when all integral
				accessories are deployed (folding or detachable stocks and so
				forth).</p>
		</div>

	</div>
	*/ %>

	<%=_bean.endForm() %>
	<%@include file="docftr.jspf"%>