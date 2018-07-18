To pull image from local Nexus server
docker pull nexus_ip:docker_port/coalesce

To run image:
docker run --name coalesce -p 8181:8181 -p 8101:8101 nexus_ip:docker_port/coalesce

If you want to include your .m2 folder in the container when running

Linux/Unix (should be the same in terms of running this command)
the 'name' flag is optional, and simply names the container once it is running (by default the name is random)

docker run -v ~/.m2/repository:/opt/karaf/system --name coalesce -p 8181:8181 -p 8101:8101 nexus_ip:docker_port/coalesce

Windows:
For Windows both directories must be an absolute path, not relative
This command must also be run in cmd, due to the path being converted to a Unix path format,
  which Docker doesn't like
docker run -v ~/.m2/repository:/opt/karaf/system --name coalesce -p 8181:8181 -p 8101:8101 nexus_ip:docker_port/coalesce
