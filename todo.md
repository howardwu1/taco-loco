# todo

* bug fix -- the time sent back to the app seems to be in UTC -- and I have a test that already adds flexiblity to accept UTC but maybe need to put a stoop on that!
* bugfix -- reactnative formating of mysessions gets narrow whenever you fill in both session mentor and mentee and can give ratings
* start doing queries on the data for the visualization piece
* clean up the visualization piece (react native)


# longTermTodo

* update out of moment (since it's deprecated) (react native)
* allow multiple people as mentee and mentors?
* make compatible with okta and google auth
* long term set up mysql server (Free) --h2 already made persistent
* figure out flyway database rollback? does it even work with H2 -- yes it does. So Flyway is for database migrations but you can also use it to tear down databases for testing and rewrite values for regression tests and such in the databases. The thing is I do not need to generate existing data yet. So might not even be useful for my application



# knowledgeReadUp

* css units for sizing
* graceful degredation (front end)?