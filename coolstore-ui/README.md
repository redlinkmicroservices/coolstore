Create a new openshift project and switch to that project.

1. `oc new-app https://USER:PW@github.com/RedHatTraining/coolstore.git --context-dir=coolstore-ui`
2. Run the following command, replacing the url with the gateway url: `oc create configmap gatewaycatalog --from-literal=CATALOG_SERVICE_URL=http://catalog-service-coolstore-catalog.192.168.99.100.nip.io:80`
3. `oc set env dc/coolstore-gateway --from=configmap/gatewaycatalog`
