# srkit
<pre>
<a href='www.srkit.ca'>SrKit Login</a> &emsp;&emsp;
</pre>
<img src='/images/banner2.png'>
Welcome Developers, 

SrKit is written in <a href='https://www.eclipse.org/downloads/packages/release/oxygen/3a'>Eclipse Oxygen</a> using <a href='https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html'>Java 1.8</a> and runs on a <a href='https://tomcat.apache.org/download-80.cgi'>Apache Tomcat 8.5</a> server with a <a href='https://dev.mysql.com/downloads/mysql/5.6.html'>MySql 5.6</a> database.

To get the project running there are a few steps you will need to take.

1. the <a href='/WEB-INF/web.xml'>web.xml</a> <line>ln:12-13</line> file will need to be edited for you local machine. Replace "[[ your database username]]" with your local instance database username, and same with the password.

2. the SrKit Database/Schema will need be created and named "sr5". (You can name it whatever you like, but you'll have to change the code in multiple places, I do not recommend it)

3. once the Database is created you can use the <a href='/sr5_sql/sr5dump.sql'>sr5dump.sql</a> to import all the required tables for the project. Make sure you select the correct database/schema. <code>USE sr5</code>

you can either run the script from mysql workbench or 

from command line <code> mysql sr5 < [[path]]/sr5dump.sql</code>

4. You will need to create a user account before you can login to SrKit, before you can do this let me explain a bit about encryption. SrKit uses ecryption to store user passwords. We use mysql <code>AES_ENCRYPT('text','key')</code> to encryt the tuser.pwd column. The 'key' is saved in the java class <a href='/WEB-INF/src/sr/data/LoginDb.java'>LoginDb.java</a> ln:19. So to insert a user record you will need to exucte the following mysql query:

<code>INSERT INTO `sr5`.`tuser`
(`Login`,`Name`,`Email`,`CreatedAt`,`Pwd`,`Inactive`,`RowsPerPage`,`PageSettings`,`ShortName`,`SourceBooks`,`Role`,`PlayerCharacter`,`ThemeRow`)
VALUES
('admin','admin','',now(),AES_ENCRYPT('admin','MageChummer'),0,10,'','','Core','SysAdmin',0,1);</code>

This should create a SysAdmin account with user name and password 'admin'


<i>Note: the project may use 3rd party *.jar files. If some classes are missing they are probably 3rd party. You can download them, or ask me for them and place them in /[[your tomcat path]]/lib</i>
