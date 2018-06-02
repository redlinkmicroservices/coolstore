#!/bin/bash
mvn clean wildfly-swarm:run -Dswarm.http.port=10080 -DskipTests -Dswarm.project.stage=local
