#!/bin/bash
mvn clean spring-boot:run -Dcatalog.service.url=localhost:8080 -Dserver.port=9090
