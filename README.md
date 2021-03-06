<pre>
<a href='www.srkit.ca'>SrKit Login</a> &emsp;&emsp; <a href='https://www.reddit.com/r/ShadowrunKit/'>SrKit Reddit</a> &emsp;&emsp; <a href='https://www.patreon.com/srkit'>SrKit Patreon</a>
</pre>
<h1 style='color:#EBB530'>SrKit</h1>
<img src='/images/banner2.png'>
Welcome Developers, 

SrKit is written in <a href='https://www.eclipse.org/downloads/packages/release/oxygen/3a'>Eclipse Oxygen</a> using <a href='https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html'>Java 1.8</a> and runs on a <a href='https://tomcat.apache.org/download-80.cgi'>Apache Tomcat 8.5</a> server with a <a href='https://dev.mysql.com/downloads/mysql/5.6.html'>MySql 5.6</a> database.

To get the project running there are a few steps you will need to take.

1. the <a href='/WEB-INF/web.xml'>web.xml</a> file ln:12-13 will need to be edited for you local machine. Replace "[[ your database username]]" with your local instance database username, and same with the password.

2. the SrKit Database/Schema will need be created and named "sr5". (You can name it whatever you like, but you'll have to change the code in multiple places, I do not recommend it)

3. once the Database is created you can use the <a href='/sr5_sql/sr5dump.sql'>sr5dump.sql</a> to import all the required tables and data for the project. Make sure you select the correct database/schema. <code>USE sr5</code>. You can either run the script from mysql workbench or from command line type: <code> mysql sr5 < [[path of sr5dump.sql file]]/sr5dump.sql</code>

4. You will need to create a user account before you can login to SrKit, before you can do this let me explain a bit about encryption. SrKit uses ecryption to store user passwords. We use mysql <code>AES_ENCRYPT('text','key')</code> to encrypt the tuser.pwd column. The 'key' is saved in the java class <a href='/WEB-INF/src/sr/data/LoginDb.java'>LoginDb.java</a> ln:19. So to insert a user record you will need to exucte the following mysql query: <code>INSERT INTO `sr5`.`tuser`
(`Login`,`Name`,`Email`,`CreatedAt`,`Pwd`,`Inactive`,`RowsPerPage`,`PageSettings`,`ShortName`,`SourceBooks`,`Role`,`PlayerCharacter`,`ThemeRow`) VALUES ('admin','admin','',now(),AES_ENCRYPT('admin','MageChummer'),0,10,'','','Core','SysAdmin',0,1);</code>. This should create a SysAdmin account with user name and password 'admin'.

5. SrKit also uses MySql Stored Procedures. There are 2 stored procedures you will need to run they are <a href='/sr5_sql/select_character.sql'>select_character.sql</a> and <a href='/sr5_sql/select_message_threads.sql'>select_message_threads.sql</a>. You can run them from mysql workbench or right from the command line: <code> mysql sr5 < [[path of select_character.sql file]]/select_character.sql</code>

6. Required Jar Files for Tomcat Lib. The project uses 3rd party *.jar files, place them in /[[your tomcat path]]/lib
 - <a href='https://jar-download.com/artifacts/org.json'>org.json.jar</a>
