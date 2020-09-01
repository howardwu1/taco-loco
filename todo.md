# todo

* work on modifying the sessions by session creator to combining sessions from session Mentor or session Mentee or session creator
   * note will need to make it so it's only unique sessions from this list (i.e. overlap from session creator and being session mentee for example)

* allow people to modify existing records via mysesssions
* start doing queries on the data for the visualization piece
* clean up the visualization piece (react native)


#longTermTodo

* long term set up mysql server (Free) --h2 already made persistent

* figure out flywheel database rollback? or is it flyway script? does it even work with H2 -- yes it does. So Flyway is for database migrations but you can also use it to tear down databases for testing and rewrite values for regression tests and such in the databases. The thing is I do not need to generate existing data yet. So might not even be useful for my application

