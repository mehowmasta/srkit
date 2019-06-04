DELIMITER $$

DROP PROCEDURE IF EXISTS `select_character` $$
CREATE PROCEDURE `select_character`(in in_characterRow int)
BEGIN
#
#select Character
select * 
	from tcharacter
	where Row=in_characterRow;
#
# select AdeptPower
select s.*, cs.Level, cs.Row as ItemRow, cs.CharacterRow
	from tcharacteradeptpower cs
	inner join tadeptpower s on s.Row = cs.AdeptPowerRow
	where cs.characterrow=in_characterRow
	order by s.Name;
#
# select Armor
select s.*, cs.Quantity, cs.Equipped, cs.Row as ItemRow, cs.CharacterRow
	from tcharacterarmor cs
	inner join tarmor s on s.Row = cs.ArmorRow 
	where cs.characterrow=in_characterRow
	order by s.Name;
#
# select Bioware
select s.*, cs.Rating, cs.Grade, cs.Row as ItemRow, cs.CharacterRow, cs.Rating
	from tcharacterbioware cs 
	inner join tcyberware s on s.Row = cs.BiowareRow 
	where cs.characterrow=in_characterRow
	order by s.Name;
#
# select Contacts
select cs.*
	from tcharactercontact cs 
	where cs.characterrow=in_characterRow
	order by cs.Archetype;
#
# select Cyberdeck
select s.*, cs.Quantity, cs.Equipped, cs.CurrentAmount, cs.Row as ItemRow, cs.CharacterRow
	from tcharactercyberdeck cs 
	inner join tcyberdeck s on s.Row = cs.CyberdeckRow 
	where cs.characterrow=in_characterRow
	order by s.Name;
#
# select Cyberware
select s.*, cs.Rating, cs.Grade, cs.Parent, cs.Row as ItemRow, cs.CharacterRow, cs.Rating
	from tcharactercyberware cs 
	inner join tcyberware s on s.Row = cs.CyberwareRow 
	where cs.characterrow=in_characterRow
	order by s.Name;
#
# select CyberwareAttachments
select s.*,  cs.Row as ItemRow, cs.CharacterRow, cs.CyberwareRow, cs.ParentRow, cs.Grade, cs.Rating
	from tcharactercyberwareattachment cs 
	inner join tcyberware s on s.Row = cs.CyberwareRow 
	where cs.characterrow=in_characterRow
	order by s.Name;
#
# select Drone
select s.*, cs.Quantity, cs.Equipped, cs.CurrentAmount, cs.Row as ItemRow, cs.CharacterRow
	from tcharacterdrone cs
	inner join tdrone s on s.Row = cs.DroneRow 
	where cs.characterrow=in_characterRow
	order by s.Name;
#
# select Gear
select s.*, cs.Quantity, cs.Rating, cs.Row as ItemRow, cs.CharacterRow
	from tcharactergear cs
	inner join tgear s on s.Row = cs.GearRow 
	where cs.characterrow=in_characterRow
	order by s.Name;
#
# select Knowledge
select s.Description, s.Row, s.Source, s.Attribute,cs.CharacterRow, cs.Rating,cs.Native, cs.Name as Name,cs.Type as Type, cs.Row as ItemRow
	from tcharacterknowledge cs
	inner join tSkill s on s.Name = cs.Type
	where cs.characterrow=in_characterRow
	order by cs.Type;
#
# select Metatype
select r.* 
	from trace r
	inner join tcharacter c on c.metatype=r.row
	where c.Row=in_characterRow;
#
#select Portrait
select i.*
	from tcharacter c
	inner join timage i on c.Portrait=i.Row
	where c.Row=in_characterRow;
#
# select Program
select s.*,  cs.Row as ItemRow, cs.CharacterRow, cs.ProgramRow, cs.ParentRow
	from tcharactercyberdeckprogram cs 
	inner join tprogram s on s.Row = cs.ProgramRow 
	where cs.characterrow=in_characterRow
	order by s.Name;
#
# select Quality
select q.*, cq.Rating , cq.Row as ItemRow, cq.CharacterRow
	from tcharacterquality cq 
	inner join tquality q on q.Row = cq.QualityRow 
	where cq.characterrow=in_characterRow
	order by q.Type desc,q.Name;
#
# select Skills
select s.*, ifnull(cs.Rating,0) as Rating , ifnull(cs.Row,0) as ItemRow, ifnull(cs.CharacterRow,in_characterRow) as CharacterRow, ifnull(cs.Special,'') as Special
	from tskill s
	left join tcharacterskill cs on s.Row=cs.SkillRow and cs.characterrow=in_characterRow
	order by s.Name;
#
# select Spells
select s.*, cs.Row as ItemRow, cs.CharacterRow
	from tcharacterspell cs
	inner join tspell s on s.Row = cs.SpellRow 
	where cs.characterrow=in_characterRow
	order by s.Name;
#
# select Vehicles
select s.*, cs.Quantity, cs.Equipped, cs.CurrentAmount, cs.Row as ItemRow, cs.CharacterRow
	from tcharactervehicle cs
	inner join tvehicle s on s.Row = cs.VehicleRow 
	where cs.characterrow=in_characterRow
	order by s.craft, s.Name;
#
# select Weapons
select s.*, cs.Quantity, cs.Equipped , cs.CurrentAmount, cs.Row as ItemRow, cs.CharacterRow
	from tcharacterweapon cs
	inner join tweapon s on s.Row = cs.WeaponRow 
	where cs.characterrow=in_characterRow
	order by s.Name;	
# 
END $$

DELIMITER ;