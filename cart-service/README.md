Spring Boot Lab for GPTE Modern App Dev - Microservices development with RHOAR course - Completed Lab

1.  `export CART_PRJ=<name of the OpenShift coolstore cart project>`
2. `$ oc policy add-role-to-user view -n $CART_PRJ -z default`
3. `$ oc create configmap cart-service --from-literal=catalog.service.url=<catalog service url>`
4. `mvn clean fabric8:deploy -Popenshift -Dfabric8.namespace=$CART_PRJ`