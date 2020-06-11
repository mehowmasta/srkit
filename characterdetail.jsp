<%@include  file="dochdr.jspf"%>
<script src="characterdetail.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<script>
model.characterSetting = <%= _bean.get("CharacterSetting") %>;
model.character = <%= _bean.get("Character") %>;
model.cyberwareGrade = <%= _bean.get("CyberwareGrade") %>;
model.groups = <%= _bean.get("Groups") %>;
</script>
<style>
	.characterHeader{
	    margin-bottom: -1rem;
    	margin-top: 1rem;
    	position:relative;  
	}
	.flexDetail{
		display:flex;
		flex-direction:column;
		color: white;
		font-size: 1.4rem;
		padding:0 1rem;
	}
	.flexStart{
		justify-content:flex-start;
	}
	.pageSubtitle{
		margin-top:2rem;
	}
	#personal{
		flex:4;
	}
	#personal2{
		flex:3;
	}
	.portrait{
		display:flex;
		justify-content:flex-end;
		position:relative;
		padding:1rem;
		flex:2;
		flex-wrap:wrap;
	}
	#portraitImg{
		height: 22rem;
		border: 0.2rem solid black;
	}	
	.section{
		max-width:100%;
	}
	.section > .pageSubtitle{
		margin-top:1rem;
	}
	.title{
		padding:1rem;
		width:100%;
	}
	.toggle > * {
		pointer-events:none;
	}
</style>
<body class='' onload='view.aaOnLoad()'>
<%=_bean.startForm() %>
<%=_bean.get("ctlRow") %>
<%=_bean.get("ctlPortrait") %>
<div id='characterTypeTag' class='characterHeader'>
	<div class='characterName' id='characterTypeTagName'></div>
	<div class='characterImg' id='characterTypeTagPortrait'></div>
</div>
<div id='characterTabs' class='flex' style='padding-top:1rem;justify-content:flex-end;overflow:hidden;align-items:flex-end;'>
	<div class='tabFillLeft'></div>
	<div id='tabPersonal' class='tab selected' onclick='view.pickTab("Personal");'>Personal</div>
	<div id='tabAttributes' class='tab' onclick='view.pickTab("Attributes");'>Attributes</div>
	<div id='tabAbilities' class='tab' onclick='view.pickTab("Abilities");'>Abilities</div>
	<div id='tabEquipment' class='tab' onclick='view.pickTab("Equipment");'>Equipment</div>
	<div id='tabStory' class='tab' onclick='view.pickTab("Story");'>Story</div>
	<div id='tabSettings' class='tab' onclick='view.pickTab("Settings");'>Settings</div>
</div>
<div id='divPersonal'>
	<div class='pageSubtitle'>Personal Data</div>
	<div class='container flex' style='flex-wrap: nowrap;align-items: center;margin: 0 auto;justify-content:flex-start;overflow-x: auto;'>
		<div id='portrait' class='portrait' style='display:none;'>
			<img id='portraitImg'>
			<div class='spacer'></div>
			<button class='mini' style='' type='button' onclick='view.changePortrait()'>Change</button>
			<button class='mini' style='' type='button' onclick='view.removePortrait()'>Remove</button>
		</div>
		<div id='personal' class='container flex' >
			<%=_bean.get("ctlName") %>
			<%=_bean.get("ctlMetatype") %>
			<%=_bean.get("ctlEthnicity") %>
			<div class='spacer'></div>
			<%=_bean.get("ctlSex") %>
			<%=_bean.get("ctlAge") %>
			<%=_bean.get("ctlHeight") %>
			<%=_bean.get("ctlWeight") %>
			<div class='spacer'></div>
			<%=_bean.get("ctlStreetCred") %>
			<%=_bean.get("ctlNotoriety") %>
			<%=_bean.get("ctlPublicAwareness") %>
			<div class='spacer'></div>
			<%=_bean.get("ctlKarma") %>
			<%=_bean.get("ctlTotalKarma") %>		
			<%=_bean.get("ctlNuyen") %>
			<div class='spacer'></div>	
			<button id='addPortraitBtn' class='mini' type='button' onclick='view.addPortrait()' style='display:none;' >Add Portrait</button>
		</div>
	</div>
</div>
<div id='divAttributes' style='display:none;'>
	<div class='pageSubtitle'>Attributes</div>
	<div class='container flex'>	
		<div class='flex'>
			<div class='flex' style='align-items:center;'>
				<%=_bean.get("ctlBody") %>
				<%=_bean.get("ctlAgility") %>
				<%=_bean.get("ctlReaction") %>
				<%=_bean.get("ctlStrength") %>
			</div>
			<div class='flex' style='align-items:center;'>
				<%=_bean.get("ctlWillpower") %>
				<%=_bean.get("ctlLogic") %>
				<%=_bean.get("ctlIntuition") %>	
				<%=_bean.get("ctlCharisma") %>
			</div>
			<div class='flex' style='align-items:center;'>
				<%=_bean.get("ctlEdge") %>			
				<%=_bean.get("ctlEssence") %>
				<%=_bean.get("ctlMagic") %>
				<%=_bean.get("ctlResonance") %>
			</div>
		</div>
	</div>
	
	<div class='pageSubtitle'>Mechanics</div>
		<div class='container flex'>	
			<div class='flex' style='align-items:center;'>
				<%=_bean.get("ctlInitiative") %>
				<%=_bean.get("ctlMatrixInitiative") %>
				<%=_bean.get("ctlAstralInitiative") %>
			</div>
			<div class='flex' style='align-items:center;'>
				<%=_bean.get("ctlComposure") %>	
				<%=_bean.get("ctlJudgeIntentions") %>
				<%=_bean.get("ctlMemory") %>
			</div>	
			<div class='flex' style='align-items:center;'>
				<%=_bean.get("ctlLiftCarry") %>			
				<%=_bean.get("ctlMovement") %>
			</div>
			<div class='flex' style='align-items:center;'>
				<%=_bean.get("ctlPhysicalLimit") %>
				<%=_bean.get("ctlMentalLimit") %>
				<%=_bean.get("ctlSocialLimit") %>
			</div>
		</div>
	</div>
</div>

<div id='divAbilities' style='max-width:100%;display:none;'>
	<div class='pageSubtitle'>Abilities</div>
	<div style='padding-top:0.2rem;display:flex;flex-wrap:wrap;justify-content:center;position:sticky;top:3.3rem;z-index:50;max-width:100%;'>
		<button id='tbSkill' class='mini toggle on hover' data-hover='Toggle "Skills" section' type='button' onclick='return view.togglePanel(this)'><img class='smallIcon' src='<%= Images.get(Images.Skill) %>'></button>
		<button id='tbAdeptPower' class='mini toggle on hover' data-hover='Toggle "Adept Powers" section' type='button' onclick='return view.togglePanel(this)'><img class='smallIcon' src='<%= Images.get(Images.AdeptPower) %>'></button>
		<button id='tbSpell' class='mini toggle on hover' data-hover='Toggle "Spells" section' type='button' onclick='return view.togglePanel(this)'><img class='smallIcon' src='<%= Images.get(Images.Spell) %>'></button>
		<button id='tbQuality' class='mini toggle on hover' data-hover='Toggle "Qualities" section' type='button' onclick='return view.togglePanel(this)'><img class='smallIcon' src='<%= Images.get(Images.Quality) %>'></button>
		<button id='tbKnowledge' class='mini toggle on hover' data-hover='Toggle "Knowledge" section' type='button' onclick='return view.togglePanel(this)'><img class='smallIcon' src='<%= Images.get(Images.Book) %>'></button>
	</div>
	<div>	
		<div class='section panelWrap show' id='containerSkill'>
			<div class='pageSubtitle'><img style='margin:0.5rem;' class='smallIcon' src='<%= Images.get(Images.Skill) %>'><span>Skills</span></div>
			<div id='skillDetail' class='flexDetail'></div>
			<div class='flex' style='justify-content:flex-end;'><button type='button' class='hover' data-hover='Add/Remove "Skills" from runner' onclick='return view.pickSkills()'>Edit</button></div>
		</div>
		<div class='section panelWrap show' id='containerAdeptPower'>	
			<div class='pageSubtitle'><img style='margin:0.5rem;' class='smallIcon' src='<%= Images.get(Images.AdeptPower) %>'><span>Adept Powers</span></div>
			<div id='adeptPowerDetail' class='flexDetail'></div>
			<div class='flex' style='justify-content:flex-end;'><button type='button' class='hover' data-hover='Add/Remove "Adept Powers" from runner'  onclick='return view.pickAdeptPowers()'>Edit</button></div>
		</div>	
		<div class='section panelWrap show' id='containerSpell'>	
			<div class='pageSubtitle'><img style='margin:0.5rem;' class='smallIcon' src='<%= Images.get(Images.Spell) %>'><span>Spells</span></div>
			<div id='spellDetail' class='flexDetail'></div>
			<div class='flex' style='justify-content:flex-end;'><button type='button' class='hover' data-hover='Add/Remove "Spells" from runner'  onclick='return view.pickSpells()'>Edit</button></div>
		</div>		
		<div class='section panelWrap show' id='containerQuality'>	
			<div class='pageSubtitle'><img style='margin:0.5rem;' class='smallIcon' src='<%= Images.get(Images.Quality) %>'><span>Qualities</span></div>
			<div id='qualityDetail' class='flexDetail'></div>
			<div class='flex' style='justify-content:flex-end;'><button type='button' class='hover' data-hover='Add/Remove "Qualities" from runner'  onclick='return view.pickQualities()'>Edit</button></div>
		</div>		
		<div class='section panelWrap show' id='containerKnowledge'>	
			<div class='pageSubtitle'><img style='margin:0.5rem;' class='smallIcon' src='<%= Images.get(Images.Book) %>'><span>Knowledge</span></div>
			<div id='knowledgeDetail' class='flexDetail'></div>
			<div class='flex' style='justify-content:flex-end;'><button type='button' class='hover' data-hover='Add "Knowledge" to runner'  onclick='return view.addKnowledge()'>Add</button></div>
		</div>
	</div>
</div>
<div id='divEquipment' style='max-width:100%;display:none;'>
	<div class='pageSubtitle'>Equipment</div>
	<div style='padding-top:0.2rem;display:flex;flex-wrap:wrap;justify-content:center;position:sticky;top:3.3rem;z-index:50;max-width:100%;'>
		<button id='tbWeapon' class='mini toggle on hover' data-hover='Toggle "Weapons" section' type='button' onclick='return view.togglePanel(this)'><img class='smallIcon' src='<%= Images.get(Images.Weapon) %>'></button>
		<button id='tbArmor' class='mini toggle on hover' data-hover='Toggle "Armor" section' type='button' onclick='return view.togglePanel(this)'><img class='smallIcon' src='<%= Images.get(Images.Armor) %>'></button>
		<button id='tbCyberware' class='mini toggle on hover' data-hover='Toggle "Cyberware" section' type='button' onclick='return view.togglePanel(this)'><img class='smallIcon' src='<%= Images.get(Images.Cyberware) %>'></button>
		<button id='tbBioware' class='mini toggle on hover' data-hover='Toggle "Bioware" section' type='button' onclick='return view.togglePanel(this)'><img class='smallIcon' src='<%= Images.get(Images.Bioware) %>'></button>
		<button id='tbGear' class='mini toggle on hover' data-hover='Toggle "Gear" section' type='button' onclick='return view.togglePanel(this)'><img class='smallIcon' src='<%= Images.get(Images.Gear) %>'></button>
		<button id='tbCyberdeck' class='mini toggle on hover' data-hover='Toggle "Cyberdecks" section' type='button' onclick='return view.togglePanel(this)'><img class='smallIcon' src='<%= Images.get(Images.Cyberdeck) %>'></button>
		<button id='tbDrone' class='mini toggle on hover' data-hover='Toggle "Drones" section' type='button' onclick='return view.togglePanel(this)'><img class='smallIcon' src='<%= Images.get(Images.Rigging) %>'></button>
		<button id='tbVehicle' class='mini toggle on hover' data-hover='Toggle "Vehicles" section' type='button' onclick='return view.togglePanel(this)'><img class='smallIcon' src='<%= Images.get(Images.Vehicle) %>'></button>
	</div>
	<div>	
		<div class='section panelWrap show' id='containerWeapon'>
			<div class='pageSubtitle'><img style='margin:0.5rem;' class='smallIcon' src='<%= Images.get(Images.Weapon) %>'><span>Weapons</span></div>
			<div id='weaponDetail' class='flexDetail'></div>
			<div class='flex' style='justify-content:flex-end;'><button type='button' class='hover' data-hover='Add/Remove "Weapons" from runner'  onclick='return view.pickWeapons()'>Add</button></div>
		</div>	
		<div class='section panelWrap show' id='containerArmor'>
			<div class='pageSubtitle'><img style='margin:0.5rem;' class='smallIcon' src='<%= Images.get(Images.Armor) %>'><span>Armor</span></div>
			<div id='armorDetail' class='flexDetail'></div>
			<div class='flex' style='justify-content:flex-end;'><button type='button' class='hover' data-hover='Add/Remove "Armor" from runner'  onclick='return view.pickArmor()'>Add</button></div>
		</div>	
		<div class='section panelWrap show' id='containerCyberware'>	
			<div class='pageSubtitle'><img style='margin:0.5rem;' class='smallIcon' src='<%= Images.get(Images.Cyberware) %>'><span>Cyberware</span></div>
			<div id='cyberwareDetail' class='flexDetail'></div>
			<div class='flex' style='justify-content:flex-end;'><button type='button' class='hover' data-hover='Add/Remove "Cyberware" from runner'  onclick='return view.pickCyberware()'>Add</button></div>
		</div>	
		<div class='section panelWrap show' id='containerBioware'>	
			<div class='pageSubtitle'><img style='margin:0.5rem;' class='smallIcon' src='<%= Images.get(Images.Bioware) %>'><span>Bioware</span></div>
			<div id='biowareDetail' class='flexDetail'></div>
			<div class='flex' style='justify-content:flex-end;'><button type='button' class='hover' data-hover='Add "Bioware" to runner'  onclick='return view.pickBioware()'>Add</button></div>
		</div>	
		<div class='section panelWrap show' id='containerGear'>			
			<div class='pageSubtitle'><img style='margin:0.5rem;' class='smallIcon' src='<%= Images.get(Images.Gear) %>'><span>Gear</span></div>
			<div id='gearDetail' class='flexDetail'></div>
			<div class='flex' style='justify-content:flex-end;'><button type='button' class='hover' data-hover='Add "Gear" to runner'  onclick='return view.pickGear()'>Add</button></div>
		</div>	
		<div class='section panelWrap show' id='containerCyberdeck'>	
			<div class='pageSubtitle'><img style='margin:0.5rem;' class='smallIcon' src='<%= Images.get(Images.Cyberdeck) %>'><span>Cyberdecks</span></div>
			<div id='cyberdeckDetail' class='flexDetail'></div>
			<div class='flex' style='justify-content:flex-end;'><button type='button' class='hover' data-hover='Add "Cyberdecks" from runner'  onclick='return view.pickCyberdecks()'>Add</button></div>
		</div>	
		<div class='section panelWrap show' id='containerDrone'>	
			<div class='pageSubtitle'><img style='margin:0.5rem;' class='smallIcon' src='<%= Images.get(Images.Rigging) %>'><span>Drones</span></div>
			<div id='droneDetail' class='flexDetail'></div>
			<div class='flex' style='justify-content:flex-end;'><button  type='button' class='hover' data-hover='Add "Drones" to runner'  onclick='return view.pickDrones()'>Add</button></div>
		</div>	
		<div class='section panelWrap show' id='containerVehicle'>			
			<div class='pageSubtitle'><img style='margin:0.5rem;' class='smallIcon' src='<%= Images.get(Images.Vehicle) %>'><span>Vehicles</span></div>
			<div id='vehicleDetail' class='flexDetail'></div>
			<div class='flex' style='justify-content:flex-end;'><button type='button' class='hover' data-hover='Add "Vehicles" to runner'  onclick='return view.pickVehicles()'>Add</button></div>
		</div>	
	</div>
</div>
<div id='divStory' style='display:none;'>
	<div class='pageSubtitle'>Story</div>
		<div id='' class='container flex' style='justify-content: flex-start;'>
			<%=_bean.get("ctlMisc") %>
			<%=_bean.get("ctlLifestyle") %>	
			<div class='spacer'></div>
			<%=_bean.get("ctlNote") %>	
		</div>
	<div class='pageSubtitle'>Contacts</div>
	<div class='container flex' style='justify-content: flex-start;'>	
		<div id='contactDetail' class='flexDetail'></div>
		<div class='flex' style='justify-content:flex-end;width:100%;'>
			<button type='button' class='hover' data-hover='Add "Contacts" for runner'  onclick='return view.addContact()'>Add</button>
		</div>
	</div>
	<div class='pageSubtitle'>Teams</div>
	<div class='container flex' style='justify-content: flex-start;'>	
		<div id='groupsDiv' class='flexDetail' style='width:100%;'></div>
		<div class='flex' style='justify-content:flex-end;width:100%;'>
			<button id='btnJoinTeam' type='button' style='display:none;' class='parentBtn hover' data-hover='Join this runner to a to an existing team using the Team Key.' onclick='view.toggleJoinTeam()' >Join Team</button>
			<div class='childBtnContainer flex' id='btnJoinTeamSelector' style='align-items:center;'>
				<div class='x' style='top:-2rem;' onclick='view.toggleJoinTeam()'>X</div>
				<div class='inputWrap childBtn'>
					<input type='text' id='groupKey' size='6' maxlength='6' class='hover' data-hover='Join team that matches input Team Key' onkeydown='view.joinTeamKeydown(event);' style='text-transform:uppercase;'>
					<label class='inputLabel'>Team Key</label>
				</div>
				<button type='button' class='childBtn' style='max-height:3.75rem' onclick='view.joinTeam()'>Join</button>
			</div>
		</div>
	</div>
</div>
<div id='divSettings' style='display:none;'>

	<div class='pageSubtitle'>Settings</div>
	<div class='flex' style='align-items:center;'>
		<%=_bean.get("ctlType") %>
		<%=_bean.get("ctlProfessionalRating") %>
		<%=_bean.get("ctlRegister") %>
		<div class='spacer'></div>
		<%=_bean.get("ctlInactive") %>
		<div class='spacer'></div>
		<%=_bean.get("ctlImportNotes") %>
		<div class='spacer'></div>
		<button type='button' onclick='view.calculateEssence()'>Re-calc Essence</button>
		<%=_bean.get("DefaultBtn") %><br>
		<div class='spacer'></div>
		<div id='transferBtnWrap'>
			<button id='btnTransfer' type='button' style='' class='parentBtn hover' data-hover='Send this character to a friend.' onclick='view.toggleTransfer()' >Transfer Character</button>
			<div class='childBtnContainer flex' id='btnTransferSelector' style='align-items:center;'>
				<div class='x' style='top:-2rem;' onclick='view.toggleTransfer()'>X</div>
				<div class='selectWrap childBtn'>
					<select id='transferSelect' class='hover' data-hover='Transfer character to...'>
					</select>
					<label class='inputLabel'>Transfer to...</label>
				</div>
				<button type='button' class='childBtn' style='max-height:3.75rem' onclick='view.transfer()'>Transfer</button>
			</div>
		</div>
		<div id='transferRequestWrap' style='width:100%;'>
			<div class="pageSubtitle">Transfer</div>
			<div class='section message flex' style='justify-content: flex-end;flex:1;'>
				<h3 id='transferTo' style='margin:0.5rem;width:100%;'></h3><div class='spacer'></div><button class='mini' type='button' onclick='view.cancelTransfer()'>Cancel Transfer</button>
			</div>
		</div>
	</div>
</div>
<div class='container flex footer' style='align-items:center;'>		
	<%=_bean.get("Buttons") %>
</div>
<%=_bean.endForm() %>
<%@include  file="pickadeptpowerpop.jspf"%>
<%@include  file="pickarmorpop.jspf"%>
<%@include  file="pickbiowarepop.jspf"%>
<%@include  file="pickcyberdeckpop.jspf"%>
<%@include  file="pickcyberwarepop.jspf"%>
<%@include  file="pickcyberwareattachmentpop.jspf"%>
<%@include  file="pickdronepop.jspf"%>
<%@include  file="pickgearpop.jspf"%>
<%@include  file="pickprogrampop.jspf"%>
<%@include  file="pickqualitypop.jspf"%>
<%@include  file="pickskillpop.jspf"%>
<%@include  file="pickspellpop.jspf"%>
<%@include  file="pickvehiclepop.jspf"%>
<%@include  file="pickweaponmodifierpop.jspf"%>
<%@include  file="pickweaponpop.jspf"%>
<%@include  file="contactpop.jspf"%>
<%@include  file="detailpop.jspf"%>
<%@include  file="detailmodpop.jspf"%>
<%@include  file="docftr.jspf"%>
<script>
 detailPop.grade = model.cyberwareGrade;
</script>
