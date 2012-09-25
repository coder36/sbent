cd ../../sbent/bin
perl sbent.pl http://localhost:8080/sbent-sample createQueueTestData errorCount=1,customerCount=2000,transactionCount=2,returnCount=10,populationSize=500
perl sbent.pl http://localhost:8080/sbent-sample loadFromQueue
perl sbent.pl http://localhost:8080/sbent-sample loadReturn
perl sbent.pl http://localhost:8080/sbent-sample processReturns
perl sbent.pl http://localhost:8080/sbent-sample report
