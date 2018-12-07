# AptApp 

## run on local

### assumptions

* running postgres on ```localhost:5432```
* created database ```aptapp```
* created user ```aptapp``` with password ```aptapp```

this can be achieved by running docker-compose

### db migration

Db migration is running within build process of application 

```./gradlew clean bootRun```

### tests

before you cen start tests on your local machine you has to have imported data in your database

* you must start [db migration](#db-migration)
* you must run [import script](/src/test/resources/import.sql)

After all you can simply call

```./gradlew check``` or ```./gradle test```