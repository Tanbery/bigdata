#Bigdata

```shell
sudo docker ps --format "table {{.Names}}"
docker ps -a --format "table {{.Names}}\t {{.Status}}"

watch -n 1 docker ps -a

```