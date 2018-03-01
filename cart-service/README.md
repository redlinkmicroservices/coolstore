Spring Boot Lab for GPTE Modern App Dev - Microservices development with RHOAR course - Completed Lab

1. Create a project for the application

[student@workstation cart-service]$ oc new-project cart-service

2. Add the view role to the default scc so the application can invoke the OpenShift master API

[student@workstation cart-service]$ oc policy add-role-to-user view  -z default

3. Get the hostname of the catalog service

4. Create the config map that points to the catalog service:

[student@workstation cart-service]$ CATALOG_HOST=$(oc get route -n catalog-service catalog-service -o jsonpath='{.spec.host}')
[student@workstation cart-service]$ oc create configmap cart-service --from-literal "catalog.service.url=http://${CATALOG_HOST}"

or

[student@workstation cart-service]$ oc create configmap cart-service --from-literal "catalog.service.url=http://catalog-service.coolstore-catalog.svc:8080"

5. Deploy the application using fabric8

[student@workstation cart-service]$ mvn clean fabric8:deploy -DskipTests -Popenshift


