#/bin/bash
export CART_SERVICE_URL=localhost:7070
export CATALOG_SERVICE_URL=localhost:9090
mvn clean wildfly-swarm:run -DskipTests
