README
======

Prerequisites
-------------

- MongoBD 5.X
- Java
- Maven


How to build
------------

mvn clean package


How to run
----------

a) First run, which loads data in the DB, with:

java -jar OrderBench.jar -l


b) Subsequent runs with:

java -jar OrderBench.jar -h

c) Use a config file to specify your parameters, instead of the command line:

java -jar OrderBench.jar -l -c config_devel.json



