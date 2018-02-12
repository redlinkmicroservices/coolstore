Vert.x Lab for GPTE Modern App Dev - Microservices development with RHOAR course - Completed Lab

1. export CATALOG_PRJ=coolstore-catalog
2. oc process -f ocp/coolstore-catalog-mongodb-persistent.yaml -p CATALOG_DB_USERNAME=mongo -p CATALOG_DB_PASSWORD=mongo -n $CATALOG_PRJ | oc create -f - -n $CATALOG_PRJ
3. oc policy add-role-to-user view -z default -n $CATALOG_PRJ
4. oc create configmap app-config --from-file=etc/app-config.yaml -n $CATALOG_PRJ
5. mvn clean fabric8:deploy -Popenshift -Dfabric8.namespace=$CATALOG_PRJ