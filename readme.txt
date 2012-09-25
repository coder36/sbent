Setup:


Install Maven
http://maven.apache.org/download.html

Install MySQL Server
http://dev.mysql.com/downloads/
Also install MySQL Workbench.  Use this to create a database called 'test'

Install RabbitMQ and the management plugin
http://www.rabbitmq.com
http://www.rabbitmq.com/management.html

Keep track of the rabbitMQ queues by going to http://localhost:55672/mgmt (guest/guest)

The sample batch application will create 2 queues:
returnQueue - publish XML returns onto this queue
reportQueue - customer reports will be published here (ie. <NINO>   : £<Balance>


Building the source:

cd sbent
mvn install

cd ..
cd sbent-sample
mvn jetty:run




