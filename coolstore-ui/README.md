This was modified to use npm to load bower dependencies. Ignore bower.json

Many front-end npm modules used by the app don't play well with offline builds using an npm mirror/proxy such as nexus. See:

- Why does Nexus download npm packages from sites other than the configured remote URL?
  https://support.sonatype.com/hc/en-us/articles/223134868-Why-does-Nexus-download-npm-packages-from-sites-other-than-the-configured-remote-URL-

- Dealing with problematic dependencies in a restricted network environment
  http://blog.npmjs.org/post/145724408060/dealing-with-problematic-dependencies-in-a

The workaround is to use npm online to download dependencies, and copy them to views/dist, them remove the dependencies from package.jon.

Both bower and npm are incredibly inefficient for containerized apps. The bower_components folder for this app was 42MiB and only 1.2 MiB was of files actually used. Using npm adds even more files to node_modues because nested dependencies.


---- Begin UCF ---

1. Create a project

oc new-project coolstore-ui

2. Deploy from sources

  - using template: (template includes resource limits, route and secret)
  
  oc new-app --name ui -f ocp/coolstore-ui-template.yaml
  
  - from oc new-app: (would need to define resource limits later)
  
  oc new-app http://services.lab.example.com/coolstore --context-dir coolstore-ui \
  -e COOLSTORE_GW_ENDPOINT=http://coolstore-gateway-coolstore-gateway.apps.lab.example.com/api
 
  oc expose svc coolstore-ui
  
3. Follow the build

   oc logs -f bc/coolstore-ui 
   
4. Test with a web browser using the route URL


ISSUE: bower won't work with nexus, so it fails in offline UCF

To pre-load bower dependencies:

$ npm install bower
$ ./node_modules/bower/bin/bower install
rm -rf node_modules/

Remove bower_components from .gitignore

Add the bower_components folder to git

Commit and push changes

---- End UCF ---

Create a new openshift project and switch to that project.

1. `oc new-app https://USER:PW@github.com/RedHatTraining/coolstore.git --context-dir=coolstore-ui`
2. Run the following command, replacing the url with the gateway url: `oc create configmap gatewaycatalog --from-literal=CATALOG_SERVICE_URL=http://catalog-service-coolstore-catalog.192.168.99.100.nip.io:80`
3. `oc set env dc/coolstore-gateway --from=configmap/gatewaycatalog`

