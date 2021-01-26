#!/bin/sh

$KAFKA/bin/kafka-server-stop.sh
sleep 10
$KAFKA/bin/zookeeper-server-stop.sh
sleep 20
$KAFKA/bin/zookeeper-server-start.sh $KAFKA/config/zookeeper.properties &
sleep 10
$KAFKA/bin/kafka-server-start.sh $KAFKA/config/server.properties &
