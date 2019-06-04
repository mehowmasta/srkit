Delimiter $

DROP PROCEDURE IF EXISTS `select_message_threads` $
CREATE PROCEDURE `select_message_threads`(in in_user int)
BEGIN
#

drop table if exists tzThreads;
create temporary table tzThreads (
	Thread int, User int, LastRowSeen int, Role varchar(10), ShareRoll tinyint(1), Users varchar(2000), CreatedBy int,
	primary key(Thread)) engine = Memory;
#
insert into tzThreads(Thread,User,LastRowSeen,Role,ShareRoll,Users,CreatedBy)
SELECT tu.*, group_concat(distinct tu2.user) as Users, t.CreatedBy
FROM tmessagethreaduser tu 
inner join tmessagethreaduser tu2 on tu.thread = tu2.thread
inner join tmessagethread t on tu.thread = t.Row
where tu.user=in_user and t.deleted=0
group by tu.thread;
#
select * from tzThreads;
#
select a.* from tMessage a
inner join tzThreads t on a.thread = t.thread
where a.deleted=0;
#
select mtu2.thread as Thread,mtu2.shareroll as ShareRoll, u.*, ifnull(c.Portrait,0)as Portrait,
ifnull(i.Extension,"") as Extension, ifnull(i.User,0) as UserImage 
from tmessagethreaduser mtu
inner join tmessagethreaduser mtu2 on mtu.thread = mtu2.thread
inner join tuser u on mtu2.user=u.row
left join tcharacter c on u.playercharacter = c.row 
left join timage i on (c.user=i.user or i.user=0) and c.Portrait=i.row
where mtu.user=in_user;
END $

DELIMITER ;