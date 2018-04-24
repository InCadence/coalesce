docker run --name rbcf-elasticsearch -p 9200:9200 -p 9300:9300 -d elasticsearch:5.4.0 -E "http.host=0.0.0.0" -E "transport.host=0.0.0.0"
docker run --name rbcf-kibana --link rbcf-elasticsearch:elasticsearch -p 5601:5601 -d kibana:5.4.0
