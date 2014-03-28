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

Student can only sign in from 15 minutes before the exam.
So you need to change exam time directly in database to test this functionality.
After student sign in, camera start to grab photos. This will take a few seconds...
Camera can only be started in client, namely, you should run the CodeMonkey.java in client folder.
After testing, you can find the grabbed photos in public/videos.
Communication with invigilator has not been done yet...
