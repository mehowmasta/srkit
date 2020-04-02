package sr.web.page;

import ir.web.JControl;
import sr.data.CharacterRec;
import sr.data.CharacterRow;
import sr.data.CharacterRec.CharacterType;
import sr.data.CharacterSettingRec;
import sr.data.CyberwareGrade;
import sr.data.GroupRec;
import sr.data.ProfessionalRating;
import sr.data.RaceRec;

/**
 * Controls similarly named jsp file
 */
public class CharacterDetailPage extends AppBasePage {

	public JControl ctlAge;//
	public JControl ctlHeight;//
	public JControl ctlKarma;//
	public JControl ctlMetatype;//
	public JControl ctlMisc;//
	public JControl ctlNote;//
	public JControl ctlNotoriety;//
	public JControl ctlPublicAwareness;//
	public JControl ctlSex;//
	public JControl ctlStreetCred;//
	public JControl ctlTotalKarma;//
	public JControl ctlWeight;//
	public JControl ctlPortrait;
	//
	/** Attributes */
	public JControl ctlAgility;//
	public JControl ctlAstralInitiative;//
	public JControl ctlBody;//
	public JControl ctlCharisma;//
	public JControl ctlComposure;//
	public JControl ctlEdge;//
	public JControl ctlEdgePoints;//
	public JControl ctlEssence;//
	public JControl ctlInitiative;//
	public JControl ctlIntuition;//
	public JControl ctlJudgeIntentions;//
	public JControl ctlLiftCarry;//
	public JControl ctlLogic;//
	public JControl ctlMagic;//
	public JControl ctlMatrixInitiative;//
	public JControl ctlMemory;//
	public JControl ctlMentalLimit;//
	public JControl ctlMovement;
	public JControl ctlPhysicalLimit;//
	public JControl ctlReaction;//
	public JControl ctlResonance;//
	public JControl ctlSocialLimit;//
	public JControl ctlStrength;//
	public JControl ctlWillpower;//
	//
	/** ConditionMonitor */
	public JControl ctlPhysicalMax;//
	public JControl ctlPhysicalCurrent;//
	public JControl ctlStunMax;//
	public JControl ctlStunCurrent;//
	//
	/** Id's, Lifestyles, Currency */
	public JControl ctlLifestyle;//
	public JControl ctlNuyen;//
	//
	/**Record Data*/
	public JControl ctlName;
	public JControl ctlRow;
	public JControl ctlType;//
	public JControl ctlProfessionalRating;
	public JControl ctlInactive;
	public JControl ctlImportNotes;
	public JControl ctlRegister;

	private CharacterRow currentCharacter;
	private String setDefaultButton = "setDefBtn";
	@Override
	protected void init() throws Exception {
		if(hasParm("ctlRow"))
		{
			int row = readInt("ctlRow");
			if(row>0)
			{
				currentCharacter = CharacterRow.selectCharacter(db, row);
				if(currentCharacter==null || currentCharacter.Character==null || currentCharacter.Character.Row==0)
				{
					setError("Failed to retrieve character.");
				  	setRedirectBack();
				  	return;
				}
				if(currentCharacter.Character.User != currentUser.Row)
				{
					setError("Sneaky sneaky. This is not your character.");
				  	setRedirectBack();
				  	return;
				}
			}
			else
			{
				currentCharacter = new CharacterRow();
				currentCharacter.Character = new CharacterRec();
				currentCharacter.Character.Type = CharacterType.lookup(readString("ctlType",20));
			}
		}
		else
		{
			setError("Missing character parameters.");
	  	  	setRedirectBack();
	  	  	return;
		}
		mapControl(ctlRow,currentCharacter.Character,CharacterRec.ROW);
		mapControl(ctlRegister,currentCharacter.Character,CharacterRec.REGISTER);
		mapControl(ctlInactive,currentCharacter.Character,CharacterRec.INACTIVE);
		mapControl(ctlType,currentCharacter.Character,CharacterRec.TYPE);
		mapControl(ctlProfessionalRating,currentCharacter.Character,CharacterRec.PROFESSIONALRATING);
		mapControl(ctlAge, currentCharacter.Character, CharacterRec.AGE);
		mapControl(ctlHeight, currentCharacter.Character, CharacterRec.HEIGHT);
		mapControl(ctlKarma, currentCharacter.Character, CharacterRec.KARMA);
		mapControl(ctlLifestyle, currentCharacter.Character, CharacterRec.LIFESTYLE);
		mapControl(ctlMetatype, currentCharacter.Character, CharacterRec.METATYPE);
		mapControl(ctlMisc, currentCharacter.Character, CharacterRec.MISC);
		mapControl(ctlNotoriety, currentCharacter.Character, CharacterRec.NOTORIETY);
		mapControl(ctlNote, currentCharacter.Character, CharacterRec.NOTE);
		mapControl(ctlNuyen, currentCharacter.Character, CharacterRec.NUYEN);
		mapControl(ctlPublicAwareness, currentCharacter.Character, CharacterRec.PUBLICAWARENESS);
		mapControl(ctlSex, currentCharacter.Character, CharacterRec.SEX);
		mapControl(ctlStreetCred, currentCharacter.Character, CharacterRec.STREETCRED);
		mapControl(ctlTotalKarma, currentCharacter.Character, CharacterRec.TOTALKARMA);
		mapControl(ctlWeight, currentCharacter.Character, CharacterRec.WEIGHT);
		//
		mapControl(ctlAgility, currentCharacter.Character, CharacterRec.AGILITY);
		mapControl(ctlAstralInitiative, currentCharacter.Character, CharacterRec.ASTRALINITIATIVE);
		mapControl(ctlBody, currentCharacter.Character, CharacterRec.BODY);
		mapControl(ctlCharisma, currentCharacter.Character, CharacterRec.CHARISMA);
		mapControl(ctlComposure, currentCharacter.Character, CharacterRec.COMPOSURE);
		mapControl(ctlEdge, currentCharacter.Character, CharacterRec.EDGE);
		mapControl(ctlEdgePoints, currentCharacter.Character, CharacterRec.EDGEPOINTS);
		mapControl(ctlEssence, currentCharacter.Character, CharacterRec.ESSENCE);
		mapControl(ctlInitiative, currentCharacter.Character, CharacterRec.INITIATIVE);
		mapControl(ctlIntuition, currentCharacter.Character, CharacterRec.INTUITION);
		mapControl(ctlJudgeIntentions, currentCharacter.Character, CharacterRec.JUDGEINTENTIONS);
		mapControl(ctlLiftCarry, currentCharacter.Character, CharacterRec.LIFTCARRY);
		mapControl(ctlLogic, currentCharacter.Character, CharacterRec.LOGIC);
		mapControl(ctlMagic, currentCharacter.Character, CharacterRec.MAGIC);
		mapControl(ctlMatrixInitiative, currentCharacter.Character, CharacterRec.MATRIXINITIATIVE);
		mapControl(ctlMemory, currentCharacter.Character, CharacterRec.MEMORY);
		mapControl(ctlMentalLimit, currentCharacter.Character, CharacterRec.MENTALLIMIT);
		mapControl(ctlMovement, currentCharacter.Character, CharacterRec.MOVEMENT);
		mapControl(ctlPortrait, currentCharacter.Character, CharacterRec.PORTRAIT);
		mapControl(ctlPhysicalLimit, currentCharacter.Character, CharacterRec.PHYSICALLIMIT);
		mapControl(ctlReaction, currentCharacter.Character, CharacterRec.REACTION);
		mapControl(ctlResonance, currentCharacter.Character, CharacterRec.RESONANCE);
		mapControl(ctlSocialLimit, currentCharacter.Character, CharacterRec.SOCIALLIMIT);
		mapControl(ctlStrength, currentCharacter.Character, CharacterRec.STRENGTH);
		mapControl(ctlWillpower, currentCharacter.Character, CharacterRec.WILLPOWER);
		//
		mapControl(ctlName, currentCharacter.Character, CharacterRec.NAME);
		mapControl(ctlPhysicalMax,currentCharacter.Character,CharacterRec.PHYSICALMAX);
		mapControl(ctlStunMax,currentCharacter.Character,CharacterRec.STUNMAX);
		mapControl(ctlImportNotes,currentCharacter.Character,CharacterRec.IMPORTNOTES);
		initSizing();
		
		JControl.setHidden(ctlRow,ctlPortrait);
		initHovering();
		ctlName.setEvents("onkeyup='view.nameKeyup(event)'");
		ctlType.setEvents("onchange='view.changeType()'");
		ctlAgility.setEvents("onchange='view.changeAgility()' data-hover='Agility'");
		ctlBody.setEvents("onchange='view.changeBody()' data-hover='Body'");
		ctlCharisma.setEvents("onchange='view.changeCharisma()' data-hover='Charisma'");
		ctlEssence.setEvents("onchange='view.changeEssence()' data-hover='Essence'");
		ctlEssence.setDecimals(2);
		ctlIntuition.setEvents("onchange='view.changeIntuition()' data-hover='Inuition'");
		ctlLogic.setEvents("onchange='view.changeLogic()' data-hover='Logic'");
		ctlMagic.setEvents("onchange='view.changeMagic()' data-hover='Magic'");
		ctlReaction.setEvents("onchange='view.changeReaction()' data-hover='Reaction'");
		ctlResonance.setEvents("onchange='view.changeResonance()' data-hover='Resonance'");
		ctlStrength.setEvents("onchange='view.changeStrength()' data-hover='Strength'");
		ctlWillpower.setEvents("onchange='view.changeWillpower()' data-hover='Willpower'");
		initCharacterType();
		ctlMisc.setTitle("Short Description");
	}
	private void initCharacterType() throws Exception{
		if(currentCharacter.Character.Type == CharacterType.NPC){
			JControl.setHidden(ctlComposure,ctlJudgeIntentions,ctlMemory,ctlKarma,ctlTotalKarma,ctlPublicAwareness,ctlNotoriety,ctlStreetCred,ctlEdgePoints,ctlLiftCarry,ctlMovement);
		}
	}
	private void initHovering() throws Exception {
		ctlAstralInitiative.setEvents("data-hover='Astral Initiative = Intuition x 2'");
		ctlComposure.setEvents("data-hover='Composure = Charisma + Willpower'");
		ctlInitiative.setEvents("data-hover='Initiative = Intuition + Reaction'");
		ctlJudgeIntentions.setEvents("data-hover='Judge Intentions = Charisma + Intuition'");
		ctlLiftCarry.setEvents("data-hover='Lift = Strength x 15<br>Carry = Strength x 10'");
		ctlMatrixInitiative.setEvents("data-hover='Matrix Initiative = Intuition + Reaction'");
		ctlMemory.setEvents("data-hover='Memory = Logic + Willpower'");
		ctlMentalLimit.setEvents("data-hover='Mental Limit = ( (Logic x 2) + Intuition + Willpower ) ÷ 3'");
		ctlMovement.setEvents("data-hover='Walk = Agility x 2<br>Run = Agility x 4'");
		ctlPhysicalLimit.setEvents("data-hover='Physical Limit = ( (Strength x 2) + Body + Reaction ) ÷ 3'");
		ctlSocialLimit.setEvents("data-hover='Social Limit = ( (Charisma x 2) + Willpower + Essence ) ÷ 3'");
		
	}
	private void initSizing() throws Exception {
		JControl.setSize(6,ctlAge,ctlHeight,ctlWeight,ctlWillpower,ctlEssence,ctlMagic,ctlAgility,ctlBody,ctlCharisma,ctlEdge,ctlIntuition,ctlLogic,ctlMagic,ctlReaction,ctlStrength,ctlResonance);
		JControl.setSize(8, ctlPublicAwareness,ctlNotoriety,ctlStreetCred,
				ctlKarma,ctlTotalKarma,ctlInitiative,ctlMatrixInitiative,
				ctlAstralInitiative,ctlComposure,ctlJudgeIntentions,ctlMemory,
				ctlPhysicalLimit,ctlSocialLimit,ctlMentalLimit);
		JControl.setSize(10,ctlLiftCarry,ctlMovement,ctlNuyen);
		ctlNote.setSuppressTextarea(false);
		ctlImportNotes.setSuppressTextarea(false);
		ctlNote.setTextArea(8, 0);
		ctlImportNotes.setTextArea(10, 0);
	}
	@Override
	protected void read() throws Exception {
		if(currentUser.isGuest())
		{			
		  	return;
		}
	}
	@Override
	protected void update() throws Exception {
		if(currentUser.isGuest())
		{
			setError("Buzz!");
			return;
		}
		if(hasParm(DELETE_BUTTON))
		{
			if(currentUser.PlayerCharacter == currentCharacter.Character.Row)
			{
				currentUser.PlayerCharacter = 0;
				db.update(currentUser);
			}
			db.delete(currentCharacter.Character);
			setStatus("{0} has been deleted.",currentCharacter.Character.Name);
		  	setRedirectBack();
		    return;
		}
		if(hasParm(setDefaultButton) && currentCharacter.Character.Row>0)
		{
			currentUser.PlayerCharacter = currentCharacter.Character.Row;
			db.update(currentUser);
			setStatus("Default character changed to {0}.",currentCharacter.Character.Name);
		  	setRedirectBack();
		    return;
		}
		if(currentCharacter.Character.Row==0)
		{
			currentCharacter.Character.User = currentUser.Row;
			if(currentCharacter.Character.Name.isEmpty())
			{
				setError("The least you could do is give it a Name.");
				return;
			}
			db.insert(currentCharacter.Character);
			if(currentUser.PlayerCharacter==0)
			{
				currentUser.PlayerCharacter=currentCharacter.Character.Row;
			}
			setStatus("Created runner {0}. You can now add equipment and abilities.",currentCharacter.Character.Name);
		  	//setRedirectBack();
			setRedirect(Page.CharacterDetail, "ctlRow",currentCharacter.Character.Row);
		    return;
		}
		else
		{
			if (db.update(currentCharacter.Character))
		    {
		      setStatus("{0} has been updated.",currentCharacter.Character.Name);
			  setRedirect(Page.CharacterDetail, "ctlRow",currentCharacter.Character.Row);
		      return;
		    }
		}
	}
	@Override
	protected void write() throws Exception {
		ctlSex.addValue(CharacterRec.CharacterSex.M,"Male");
		ctlSex.addValue(CharacterRec.CharacterSex.F,"Female");
		ctlSex.addValue(CharacterRec.CharacterSex.T,"Trans");
		ctlMetatype.addValues(RaceRec.selectAll(db));
		ctlType.addValue(CharacterType.PC.name(),CharacterType.PC.name());
		ctlType.addValue(CharacterType.NPC.name(),CharacterType.NPC.name());
		if(!currentCharacter.Character.IsImport || currentCharacter.Character.ImportNotes.length()==0)
		{
			ctlImportNotes.setHidden();
		}
		set("CyberwareGrade",CyberwareGrade.toJson());
		set("Character",currentCharacter.toJson());
		set("CharacterSetting",CharacterSettingRec.selectForCharacter(db,currentCharacter.Character.Row).toString());
		set("Groups",GroupRec.selectForCharacter(db,currentCharacter.Character.Row,true).toString());
		writeProfessionalRating();
		writeButtons();
		
	}	
	private void writeButtons() {
		StringBuilder btns = new StringBuilder("");
		StringBuilder defaultBtn = new StringBuilder("");
		if(currentCharacter.Character.Row>0)
		{
			if(currentCharacter.Character.Row != currentUser.PlayerCharacter)
			{
				defaultBtn.append(submit(setDefaultButton,"Set as Primary Character",currentUser.isGuest()?" disabled class='hover' data-hover='Disabled for guest accounts' " : "class='hover' data-hover='Set as default runner for character sheet'"));
			}
			btns.append(eventButton(DELETE_BUTTON, "Delete",  "view.deleteCharacter()",currentUser.isGuest()?" disabled class='hover' data-hover='Disabled for guest accounts'" : "class='hover' data-hover='Delete this runner'"));
			btns.append(eventButton("Preview","Preview","view.showSheet()","class='hover' data-hover='Preview this Runners Sheet'"));
			btns.append(eventButton(SUBMIT_BUTTON,"Update","view.submit()",currentUser.isGuest()?" disabled class='hover' data-hover='Disabled for guest accounts'" : "class='hover' data-hover='Save all changes made'"));
			
		}
		else
		{
			btns.append(eventButton(SUBMIT_BUTTON,"Create","view.submit()",currentUser.isGuest()?" disabled class='hover' data-hover='Disabled for guest accounts'" : "class='hover' data-hover='Save all changes made'"));
		}
		set("Buttons", btns.toString());
		set("DefaultBtn", defaultBtn.toString());
	}
	private void writeProfessionalRating() {
		ctlProfessionalRating.addValues(ProfessionalRating.toNameRows());
	}
}
