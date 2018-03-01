---- End UCF ---

1. Create a project

oc new-project coolstore-ui

2. Deploy from sources

  oc new-app http://services.lab.example.com/coolstore --context-dir coolstore-ui \
  -e COOLSTORE_GW_ENDPOINT=http://coolstore-gateway-coolstore-gateway.apps.lab.example.com/api
 
3. Follow the build
   oc logs -f bc/coolstore
   
4. Expose the service:

   oc expose svc coolstore
   
5. Test with a web browser using the route URL

---- End UCF ---

Create a new openshift project and switch to that project.

1. `oc new-app https://USER:PW@github.com/RedHatTraining/coolstore.git --context-dir=coolstore-ui`
2. Run the following command, replacing the url with the gateway url: `oc create configmap gatewaycatalog --from-literal=CATALOG_SERVICE_URL=http://catalog-service-coolstore-catalog.192.168.99.100.nip.io:80`
3. `oc set env dc/coolstore-gateway --from=configmap/gatewaycatalog`

