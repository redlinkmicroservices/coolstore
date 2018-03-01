
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

Q: How does the ui finds the api gateway?
