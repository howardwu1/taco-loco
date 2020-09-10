# todo

* auth for adding new session/overwrite checking user from token == sessioncreator -- if the session doesn't exist (for overwrite -- session find by storyid?)
* change findallbySEssionCreatorSessionMentorSessionCreator to findallbyteammate
* add a check to see if sessionCreator or members match the user of the token before adding a session
* apply authorization for session editing only if the current task (search by taskid) has them for a sesson mentor or mentee!
* add a way to add new members to a current session -- maybe add them to mentor and mentee -- do a check and then modify the teammembers list
* start doing queries on the data for the visualization piece
* clean up the visualization piece (react native)


# longTermTodo

* allow multiple people as mentee and mentors?
* make compatible with okta and google auth

* long term set up mysql server (Free) --h2 already made persistent

* figure out flywheel database rollback? or is it flyway script? does it even work with H2 -- yes it does. So Flyway is for database migrations but you can also use it to tear down databases for testing and rewrite values for regression tests and such in the databases. The thing is I do not need to generate existing data yet. So might not even be useful for my application



# knowledgeReadUp

* css units for sizing
* graceful degredation (front end)?