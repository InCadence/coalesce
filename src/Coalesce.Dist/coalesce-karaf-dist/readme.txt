Pull from local nexus server
docker run --name coalesce -p 127.0.0.1:8181:8181 -p 127.0.0.1:8101:8101 192.168.11.86:8082/coalesce

Run docker using local .m2/repository
docker run -v ~/.m2/repository:/opt/karaf/system -p 127.0.0.1:8181:8181 -p 127.0.0.1:8101:8101 192.168.11.86:8082/coalesce 
