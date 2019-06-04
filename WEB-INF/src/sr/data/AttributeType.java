package sr.data;

import ir.util.StringKit;

public enum AttributeType {
	
	Body("BOD","Physical","Body measures your physical health and resiliency. It affects how much damage you can take and stay on your feet, how well you resist damage coming your way, your ability to recover from poisons and diseases, and things of that nature."),
	Agility("AGL","Physical","Agility measures things like hand-eye coordination, flexibility, nimbleness, and balance. Agility is the most important attribute when it comes to scoring hits during combat, as you need to be coordinated to land your blows, whether you’re swinging a sword or carefully aiming a rifle. It also is critical in non-combat situations, such as sneaking quietly past security guards or smoothly lifting a keycard from its secured position."),
	Reaction("REA","Physical","Reaction is about reflexes, awareness, and your character’s ability to respond to events happening around them. Reaction plays an important role in deciding how soon characters act in combat and how skilled they are in avoiding attacks from others. It also helps you make that quick turn down a narrow alley on your cycle to avoid the howling gangers on your tail."),
	Strength("STR","Physical","Strength is an indicator of, well, how strong your character is. The higher your strength, the more damage you’ll do when you’re raining blows down on an opponent, and the more you’ll be able to move or carry when there’s stuff that needs to be moved. Or carried. Strength is also important with athletic tasks such as climbing, running, and swimming."),
	Willpower("WIL","Mental","Willpower is your character’s desire to push through adversity, to resist the weariness of spellcasting, and to stay upright after being nailed in the head with a sap. Whether you’re testing yourself against a toxic wilderness or a pack of leather-clad orks with crowbars, Willpower will help you make it through."),
	Logic("LOG","Mental","The Logic attribute measures the cold, calculating power of your rational mind. Whether you are attempting to repair complicated machinery or patch up an injured teammate, Logic helps you get things right. Logic is also the attribute hermetic mages use to resist Drain from the spells they rain down on their hapless foes. Deckers also find Logic extremely useful, as it helps them develop the attacks and counterattacks that are part of their online battles."),
	Intuition("INT","Mental","Intuition is the voice of your gut, the instinct that tells you things before your logical brain can figure them out. Intuition helps you anticipate ambushes, notice that something is amiss or out of place, and stay on the trail of someone you’re pursuing."),
	Charisma("CHA","Mental","Charisma is your force of personality, the persuasiveness and charm you can call on to get people to do what you want without having to go to the trouble of pulling a gun on them. It’s not entirely about your appearance, but it’s also not entirely not about your appearance. What it’s mostly about is how you use what you have—your voice, your face, your words, and all the tools at your disposal—to charm and/or intimidate the people you encounter. Additionally, Charisma is an important attribute for shamanic mages, as it helps them resist the damaging Drain from spells they cast."),
	Essence("ESS","Special","Essence is your metahumanity encapsulated in a number. In Shadowrun, you have ample opportunities to alter your body or push it beyond its normal limits. Such actions often have a cost, and they can result in a loss of a portion of your metahumanity, which means a loss of Essence points. Each character starts with an Essence rating of 6, and it acts as a cap on the amount of alterations you can adopt. When it’s gone, it doesn’t come back. It also affects the Magic and Resonance attributes, as losses in Essence are reflected by losses in Magic and Resonance. While denizens of the Sixth World are accustomed to seeing a variety of augmentations and alterations to the metahuman form, the “uncanny valley” still exists. The uncanny valley is the disconcerting effect that happens when people see something that is almost, but not quite, metahuman. An animated cartoon with exaggerated features looks fine to metahuman eyes, but a computer program that closely, but not exactly, replicates human appearance is a troubling and unpleasant sight to most viewers. This is what happens when people see others with augmentations—on some level, people notice there is something less (or more) human about that, and they respond to it negatively. The change may not be exactly visible, but it is in some way noticeable—in one way or another, a person has become less human, and on some level other people notice this. This is why a character’s Essence is included in the calculation of their Social limit."),
	Edge("EDG","Special","Edge is the ultimate intangible, that certain something that provides a boost when you need it, that gets you out of a tough spot when the chips are down. It’s not used to calculate dice pools; instead, you spend a point of Edge to acquire a certain effect. Every character has at least one point of Edge, more if they want to take more frequent advantage of the boosts it offers. The possible effects of and more details about Edge are on p. 56."),
	Magic("MAG","Special","If you intend to cast spells or use magic in any way, your character needs to have the Magic attribute. Most individuals do not have this attribute, meaning their rating is zero. Mages, who cast spells, and adepts, who channel magic into enhanced physical and mental abilities, need this quality. Their Magic rating measures how capable they are in the arcane arts and how much power they can draw down to help them in their efforts."),
	Resonance("RES","Special","Similar to Magic for mages and adepts, Resonance is the special attribute for technomancers. Technomancers interface with the Matrix using the power of their mind, and Resonance measures the strength of their ability to interact with and shape that environment (see Technomancers, p. 249). Non-technomancers have a zero rating for Resonance.");
	
	private AttributeType(String abrv, String type, String desc)
	{
		this.Abbreviation = abrv;
		this.Type = type;
		this.Description = desc;
	}
	public String Abbreviation;
	public String Description;
	public String Type;

	public static String toJson()
	{
		StringBuilder b = new StringBuilder("[");
		String comma = "";
		for(AttributeType t : AttributeType.values())
		{
			b.append(comma)
			 .append("{Name:").append(StringKit.jsq(t.name()))
			 .append(",Abbreviation:").append(StringKit.jsq(t.Abbreviation))
			 .append(",Type:").append(StringKit.jsq(t.Type))
			 .append(",Description:").append(StringKit.jsq(t.Description))
			 .append("}");
			comma = ",";
		}
		return b.append("]").toString();
	}
}