# todo

* apply authorization for session editing only if the current task (search by taskid) has them for a sesson mentor or mentee!
* apply authorization to end points
* am I submitting the list of users in the session (not listed as a mentor or mentee, but in the multiselect in the form) to the endpoint?
* add a check to see if sessionCreator or members match the user of the token before adding a session
* start doing queries on the data for the visualization piece
* clean up the visualization piece (react native)


#longTermTodo

* make compatible with okta and google auth

* long term set up mysql server (Free) --h2 already made persistent

* figure out flywheel database rollback? or is it flyway script? does it even work with H2 -- yes it does. So Flyway is for database migrations but you can also use it to tear down databases for testing and rewrite values for regression tests and such in the databases. The thing is I do not need to generate existing data yet. So might not even be useful for my application



#knowledgeReadUp

* placeholder css
* css units for sizing
* graceful degredation (front end)?