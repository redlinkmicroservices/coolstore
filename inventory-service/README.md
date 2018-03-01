Inventory Service

--- Begin UCF ---

1. See instructions and sample files on catalog-service to create NFS shares in the services VM and PV in the master VM

2. After you deploy PostgreSQL, verify that the PVC is bound:

[student@workstation inventory-service]$ oc new-app -f ocp/inventory-service-postgresql-persistent.yaml -p INVENTORY_DB_USERNAME=jboss -p INVENTORY_DB_PASSWORD=jboss -p INVENTORY_DB_NAME=inventorydb

[student@workstation inventory-service]$ oc get pvc
NAME                      STATUS    VOLUME    CAPACITY   ACCESSMODES   STORAGECLASS   AGE
inventory-postgresql-pv   Bound     vol02     1Gi        RWO                          34s
mongodb-data-pv           Bound     vol01     1Gi        RWO                          13m

3. If you need to retry, you need to manually delete the PV and clean the NFS share. It looks like recycling is not working

4. It looks like test data loading always fail the first time:

[student@workstation inventory-service]$ oc get pod
NAME                               READY     STATUS    RESTARTS   AGE
inventory-postgresql-1-deploy      0/1       Error     0          6m
inventory-postgresql-1-hook-post   0/1       Error     0          3m

If this happen, just try again:

[student@workstation inventory-service]$ oc rollout latest dc/inventory-postgresql

After a while:

[student@workstation inventory-service]$ oc get pod
NAME                               READY     STATUS    RESTARTS   AGE
inventory-postgresql-1-deploy      0/1       Error     0          8m
inventory-postgresql-1-hook-post   0/1       Error     0          5m
inventory-postgresql-2-vkgfm       1/1       Running   0          1m

The database has product data, so I assume the hook worked the second time.

[student@workstation inventory-service]$ oc rsh -t inventory-postgresql-2-vkgfm
sh-4.2$ psql inventorydb jboss
psql (9.5.9)
Type "help" for help.

inventorydb=> select count(*) from product_inventory ;
 count 
-------
     8
(1 row)

inventorydb=> \q
sh-4.2$ exit

--- End UCF ---

1. Create a new project in OCP

2. In that project, create an instance of PostgreSQL using the inventory-service-postresql-persistent.yaml file in the ocp/inventory-service directory.

`$ export INVENTORY_PRJ=<name of the OpenShift coolstore inventory project>`

 `$ oc process -f ocp/inventory-service/inventory-service-postgresql-persistent.yaml -p INVENTORY_DB_USERNAME=jboss -p INVENTORY_DB_PASSWORD=jboss -p INVENTORY_DB_NAME=inventorydb | oc create -f - -n $INVENTORY_PRJ`

3. Create a config map called app-config using the etc/project-defaults.yml for the database connection


`$ oc create configmap app-config --from-file=project-defaults.yml -n $INVENTORY_PRJ`

4. Run/deploy the service with 

`mvn clean fabric8:deploy -Popenshift -DskipTests=true -Dfabric8.namespace=$INVENTORY_PRJ`
