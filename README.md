CZ2006 Code_Monkey
==================
Please create two databases: courseware, codemonkey
And then restart the play server

After starting the server:
* type localhost:9000/adddata1 to add test data: student, course
* type localhost:9000/adddata2 to add test data: registration, question, timeslot
* type localhost:9000 to enter the application

When a student is added, by now the system just set the password to be the matricNo but UPPERCASE.
Admin account and password are "admin".

For testing purpose:
Lockdown window of client is disabled.
Student can sign in at any time.

After student sign in, camera start to grab photos. This will take a few seconds...
Camera can only be started in client, namely, you should run the CodeMonkey.java in client folder.
After testing, you can find the grabbed photos in public/videos.

By now, invigilator can invigilate and communicate with students.
However, exam is not auto started or finished yet.
