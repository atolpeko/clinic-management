#!/bin/bash
# Author : Alexander Tolpeko

mkdir logs

echo "Starting config server"
java -jar config-server/target/config-server-1.0.jar > logs/config-server.log &
sleep 20
echo "Config server started"
echo

echo "Starting discovery server"
java -jar discovery-server/target/discovery-server-1.0.jar > logs/discovery-server.log &
sleep 20
echo "Discovery server started"
echo

echo "Starting microservices"
java -jar api-gateway/target/api-gateway-1.0.jar > logs/api-gateway.log &
java -jar auth-server/target/auth-server-1.0-SNAPSHOT.jar > logs/auth-server.log &
java -jar client-service/target/client-service-1.0.jar > logs/client-service.log &
java -jar clinic-service/target/clinic-service-1.0-SNAPSHOT.jar > logs/clinic-service.log &
java -jar registration-service/target/registration-service-1.0-SNAPSHOT.jar > logs/registration-service.log &
java -jar results-service/target/results-service-1.0-SNAPSHOT.jar > logs/results-service.log &
sleep 70
echo "System started"

# wait for Ctrl-C
( trap exit SIGINT ; read -r -d '' _ </dev/tty ) 
