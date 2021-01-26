#!/bin/sh
$KAFKA/bin/kafka-topics.sh --list --bootstrap-server "localhost:9092"

for value in current-stock-loans current-prices current-collateral-views
do

  echo deleting $value
  $KAFKA/bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic $value

done


for value in current-stock-loans current-prices current-collateral-views
do

  n=0
  until [ "$n" -ge 10 ]
  do
    echo creating topic $value
    $KAFKA/bin/kafka-topics.sh --create --bootstrap-server "localhost:9092" --topic $value --config "cleanup.policy=compact" --config "delete.retention.ms=100"  --config "segment.ms=100" --config "min.cleanable.dirty.ratio=0.01" && break  # substitute your command here
     n=$((n+1))
     sleep 30
     echo aborting
  done

done
