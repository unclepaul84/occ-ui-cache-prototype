#!/bin/sh
$KAFKA/bin/kafka-topics.sh --list --bootstrap-server "localhost:9092"

$KAFKA/bin/kafka-topics.sh --create --bootstrap-server "localhost:9092" --topic current-collateral-views --config "cleanup.policy=compact" --config "delete.retention.ms=100"  --config "segment.ms=100" --config "min.cleanable.dirty.ratio=0.01"

$KAFKA/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic current-collateral-views --property  print.key=true --property key.separator=: --from-beginning

$KAFKA/bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic current-prices