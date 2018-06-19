1. Create new OCP project -- api-gateway-service

2. Run fabric8 to deploy:

`cd coolstore-gateway-jaxrs; mvn clean fabric8:deploy -DskipTests`

4. To test the API: ( | python -m json.tool for pretty-print or install jq) 

  curl http://coolstore-gateway-coolstore-gateway.apps.lab.example.com/api/product/165614
  curl http://coolstore-gateway-coolstore-gateway.apps.lab.example.com/api/cart/mycart
  curl -X POST http://coolstore-gateway-coolstore-gateway.apps.lab.example.com/api/cart/mycart/165614/2

