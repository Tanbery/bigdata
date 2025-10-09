COMPOSE_BASE := .
COMPOSE_ESK := $(COMPOSE_BASE)/elastic.yml
COMPOSE_FNK := $(COMPOSE_BASE)/flink.yml
COMPOSE_KFK := $(COMPOSE_BASE)/kafka.yml
COMPOSE_DEB := $(COMPOSE_BASE)/debezium.yml
COMPOSE_PG := $(COMPOSE_BASE)/postgres.yml
COMPOSE_RED := $(COMPOSE_BASE)/redis.yml

include $(COMPOSE_BASE)/.env
export


redis:
	@echo "Starting the Redis services"
	docker-compose  -f $(COMPOSE_RED) up -d 

kafka:
	@echo "Starting the Kafka services"
	docker-compose  -f $(COMPOSE_KFK) up -d 

pg:
	@echo "Starting the Postgres services"
	docker-compose  -f $(COMPOSE_PG) up -d 

flink:
	@echo "Starting the Flink services"
	docker-compose  -f $(COMPOSE_FNK) up -d 

elastic:
	@echo "Starting the ElasticSearch services"
	docker-compose  -f $(COMPOSE_ESK) up -d 

debezium:
	@echo "Starting the Debezium services"
	docker-compose  -f $(COMPOSE_DEB) up -d 



up: kafka pg redis elastic flink debezium
	@echo "The all services are started"
	
ps:
	@echo "List the services"
	docker-compose -f $(COMPOSE_KFK)  -f $(COMPOSE_PG) -f $(COMPOSE_RED) -f $(COMPOSE_ESK) -f $(COMPOSE_FNK) -f $(COMPOSE_DEB) ps -a 

down:
	@echo "Stopping the services"
	docker-compose -f $(COMPOSE_KFK)  -f $(COMPOSE_PG) -f $(COMPOSE_RED) -f $(COMPOSE_ESK) -f $(COMPOSE_FNK) -f $(COMPOSE_DEB) down 

#helm create flink-helm