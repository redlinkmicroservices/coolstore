1. Create new OCP project -- coolstore-gateway

2. Create a configmap for both the catalog service and the cart service:

CATALOG_HOST=$(oc get route -n catalog-service catalog-service -o jsonpath='{.spec.host}')
CART_HOST=$(oc get route -n cart-service cart-service -o jsonpath='{.spec.host}')
oc create configmap gatewaycatalog --from-literal "CATALOG_SERVICE_URL=http://${CATALOG_HOST}"
oc create configmap gatewaycart --from-literal "CART_SERVICE_URL=http://${CART_HOST}"

ex. `oc create configmap gatewaycatalog --from-literal=CATALOG_SERVICE_URL=http://catalog-service-coolstore-catalog.192.168.99.100.nip.io:80`

and

ex. `oc create configmap gatewaycart --from-literal=CART_SERVICE_URL=http://cart-service-coolstore-cart.192.168.99.100.nip.io:80`


3. Run fabric8 to deploy:

`mvn clean fabric8:deploy -Popenshift -DskipTests`

4. Set the environment variables: [SKIP: no need to do anymore]

`oc set env dc/coolstore-gateway --from=configmap/gatewaycatalog`

`oc set env dc/coolstore-gateway --from=configmap/gatewaycart`

5. To test the API: ( | python -m json.tool for pretty-print or install jq) 

  curl http://coolstore-gateway-coolstore-gateway.apps.lab.example.com/api/product/165614
  curl http://coolstore-gateway-coolstore-gateway.apps.lab.example.com/api/cart/mycart
  curl -X POST http://coolstore-gateway-coolstore-gateway.apps.lab.example.com/api/cart/mycart/165614/2

