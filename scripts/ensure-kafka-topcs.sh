#!/bin/sh
$KAFKA/bin/kafka-topics.sh --list --bootstrap-server "localhost:9092"

$KAFKA/bin/kafka-topics.sh --create --bootstrap-server "localhost:9092" --topic current-stock-loans --config "cleanup.policy=compact" --config "delete.retention.ms=100"  --config "segment.ms=100" --config "min.cleanable.dirty.ratio=0.01"

$KAFKA/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic current-stock-loans --property  print.key=true --property key.separator=: --from-beginning

$KAFKA/bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic current-stock-loans

http://localhost:8080/?/download?query=SELECT%20*%20FROM%20t%20ORDER%20BY%20LoanQty%20DESC
curl --header "Content-Type: application/json" --request POST --data '{"queryText":"SELECT * FROM t WHERE LoanQty > 10000 ORDER BY LoanQty DESC","limit":5}' http://localhost:8080/customQuery