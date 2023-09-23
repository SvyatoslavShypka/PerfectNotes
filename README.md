This service for storing text notes with the ability to send these notes to other users. Each note is bound to a single user. 
One user can have many notes (0 or more).
By default, it works with the H2 database, which is created automatically on the Tomcat server with port 9999.
To start this application on own Postgres database you should change properties in application-prod.properties.
This is example of properties:
DB_URL = localhost:5432/finaldb
DB_USERNAME = postgres
DB_PASSWORD = 1111

To run this application you should run FinalProjectApplication.java. This app will then be available at localhost:9999.

Deployed app:
https://noteproject-n7yejbmziq-lm.a.run.app/
