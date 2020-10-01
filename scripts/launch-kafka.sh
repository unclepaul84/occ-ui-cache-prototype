#!/bin/sh

$KAFKA/bin/kafka-server-stop.sh
sleep 5
$KAFKA/bin/zookeeper-server-stop.sh
sleep 5
$KAFKA/bin/zookeeper-server-start.sh $KAFKA/config/zookeeper.properties &
sleep 5
$KAFKA/bin/kafka-server-start.sh $KAFKA/config/server.properties &
