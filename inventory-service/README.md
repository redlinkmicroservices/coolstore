# Coolstore Inventory service [branch=do292-inventory-lab-solution]

This repo contains the source code, tests and OpenShift resources for the Inventory service.

It looks like Minishift auto provision the volumes. For UCF we need to create them

1. ssh root@services to create the NFS shares

# mkdir -p /var/exports/vol01
# mkdir -p /var/exports/vol02
# chown nfsnobody:nfsnobody /var/exports/vol*
# chmod a+rwx /var/exports/vol*

2. Export the NFS shares

[root@services ~]# cat /etc/exports.d/do292-volumes.exports 
/var/exports/vol01 *(rw,root_squash)
/var/exports/vol02 *(rw,root_squash)

# exportfs -a

3. ssh root@master to create the PVs

# oc create -f vol01-pv.yaml
# oc create -f vol02-pv.yaml

[root@master ~]# oc get pv
NAME              CAPACITY   ACCESSMODES   RECLAIMPOLICY   STATUS      CLAIM                    STORAGECLASS   REASON    AGE
registry-volume   10Gi       RWX           Retain          Bound       default/registry-claim                            2d
vol01             1Gi        RWO           Recycle         Available                                                     1s
vol02             1Gi        RWO           Recycle         Available 

4. After you deploy postgresql, verify the PVC is bound:

[student@workstation inventory-service]$ oc new-app -f ocp/inventory-service-postgresql-persistent.yaml -p INVENTORY_DB_USERNAME=jboss -p INVENTORY_DB_PASSWORD=jboss
[student@workstation inventory-service]$ oc get pvc
NAME              STATUS    VOLUME    CAPACITY   ACCESSMODES   STORAGECLASS   AGE
postgresql-data-pv   Bound     vol01     1Gi        RWO                          5m


5. oc policy add-role-to-user view -z default

6. mvn clean fabric8:deploy -DskipTests


