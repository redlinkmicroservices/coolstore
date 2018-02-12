Inventory Service

1. Create a new project in OCP

2. In that project, create an instance of PostgreSQL using the inventory-service-postresql-persistent.yaml file in the ocp/inventory-service directory.

`$ export INVENTORY_PRJ=<name of the OpenShift coolstore inventory project>`

 `$ oc process -f ocp/inventory-service/inventory-service-postgresql-persistent.yaml -p INVENTORY_DB_USERNAME=jboss -p INVENTORY_DB_PASSWORD=jboss -p INVENTORY_DB_NAME=inventorydb | oc create -f - -n $INVENTORY_PRJ`

3. Create a config map called app-config using the etc/project-defaults.yml for the database connection


`$ oc create configmap app-config --from-file=project-defaults.yml -n $INVENTORY_PRJ`

4. Run/deploy the service with 

`mvn clean fabric8:deploy -Popenshift -DskipTests=true -Dfabric8.namespace=$INVENTORY_PRJ`
