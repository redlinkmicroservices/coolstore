1. Create new OCP project -- coolstore-gateway

2. Create a configmap for both the catalog service and the cart service:

ex. `oc create configmap gatewaycatalog --from-literal=CATALOG_SERVICE_URL=http://catalog-service-coolstore-catalog.192.168.99.100.nip.io:80`

and

ex. `oc create configmap gatewaycart --from-literal=CART_SERVICE_URL=http://cart-service-coolstore-cart.192.168.99.100.nip.io:80`


3. Run fabric8 to deploy:

`mvn clean fabric8:deploy -Popenshift`

4. Set the environment variables:

`oc set env dc/coolstore-gateway --from=configmap/gatewaycatalog`

`oc set env dc/coolstore-gateway --from=configmap/gatewaycart`

5. To test the API:

  curl http://coolstore-gateway-coolstore-gateway.apps.lab.example.com/api/product/165614
  curl http://coolstore-gateway-coolstore-gateway.apps.lab.example.com/api/cart/mycart
  curl -X POST http://coolstore-gateway-coolstore-gateway.apps.lab.example.com/api/cart/mycart/165614/2

