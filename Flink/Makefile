# app:
# 	$(MAKE) -C docker

include docker/Makefile

# SHELL := /bin/bash
# # .PHONY: up down print-var gen build clean run-local run-cluster run-detached cancel list help
# # .DEFAULT_GOAL:=help


# # Extracted properties from pom.xml
# PROJECT_NAME:= $(shell mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout | xargs echo -n)
# PROJECT_VERSION:= $(shell mvn help:evaluate -Dexpression=project.version -q -DforceStdout | xargs echo -n)

# MAIN_CLASS := $(shell mvn help:evaluate -Dexpression=main.class -q -DforceStdout | xargs echo -n)
# FLINK_VERSION:= $(shell mvn help:evaluate -Dexpression=flink.version -q -DforceStdout | xargs echo -n)
# SCALA_VERSION := $(call get_property,scala.binary.version)
# SCALA_VERSION:= $(shell mvn help:evaluate -Dexpression=scala.binary.version -q -DforceStdout | xargs echo -n)

# JAR_FILE := target/$(PROJECT_NAME)-$(PROJECT_VERSION)-jar-with-dependencies.jar
# #JAR_FILE := target/$(PROJECT_NAME)-$(PROJECT_VERSION).jar



# # Define paths and configurations
# FLINK_HOME ?= /opt/flink-1.18.1
# FLINK_CLUSTER ?= localhost:8081
# #INPUT_FILE ?= /path/to/input/file
# #OUTPUT_DIR ?= /path/to/output/dir
# ARGS ?= #--input $(INPUT_FILE) --output $(OUTPUT_DIR)

# COMPOSE_BASE := docker
# COMPOSE_ESK := $(COMPOSE_BASE)/elastic.yml
# COMPOSE_FNK := $(COMPOSE_BASE)/flink.yml
# COMPOSE_KFK := $(COMPOSE_BASE)/kafka.yml
# COMPOSE_DEB := $(COMPOSE_BASE)/debezium.yml
# COMPOSE_PG := $(COMPOSE_BASE)/postgres.yml
# COMPOSE_RED := $(COMPOSE_BASE)/redis.yml

# include $(COMPOSE_BASE)/.env
# export


# redis:
# 	@echo "Starting the Kafka services"
# 	docker-compose  -f $(COMPOSE_RED) up -d 

# kafka:
# 	@echo "Starting the Kafka services"
# 	docker-compose  -f $(COMPOSE_KFK) up -d 

# pg:
# 	@echo "Starting the Postgres services"
# 	docker-compose  -f $(COMPOSE_PG) up -d 

# flink:
# 	@echo "Starting the Flink services"
# 	docker-compose  -f $(COMPOSE_FNK) up -d 

# elastic:
# 	@echo "Starting the ElasticSearch services"
# 	docker-compose  -f $(COMPOSE_ESK) up -d 

# debezium:
# 	@echo "Starting the Debezium services"
# 	docker-compose  -f $(COMPOSE_DEB) up -d 



# up: kafka pg redis elastic flink debezium
# 	@echo "The all services are started"
	
# ps:
# 	@echo "List the services"
# 	docker-compose -f $(COMPOSE_KFK)  -f $(COMPOSE_PG) -f $(COMPOSE_RED) -f $(COMPOSE_ESK) -f $(COMPOSE_FNK) -f $(COMPOSE_DEB) ps -a 

# down:
# 	@echo "Stopping the services"
# 	docker-compose -f $(COMPOSE_KFK)  -f $(COMPOSE_PG) -f $(COMPOSE_RED) -f $(COMPOSE_ESK) -f $(COMPOSE_FNK) -f $(COMPOSE_DEB) down -v

# vars:
# 	@echo "Project Name	:$(PROJECT_NAME)"
# 	@echo "Project Version	:$(PROJECT_VERSION)"
# 	@echo "Main Class	:$(MAIN_CLASS)"
# 	@echo "Flink Version	:$(FLINK_VERSION)"
# 	@echo "Scala Version	:$(SCALA_VERSION)"
# 	@echo "JAR File	:$(JAR_FILE)"
# 	@echo "FLINK HOME	:$(FLINK_HOME)"
# 	@echo "FLINK CLUSTER	:$(FLINK_CLUSTER)"
# 	@echo "ARGS	:$(ARGS)"


# gen:
# 	@echo "Generating fake data..."
# 	@python3 ./Generator/main.py

# build:
# 	@echo "Building the Flink job..."
# 	mvn clean package

# clean:
# 	@echo "Cleaning the project..."
# 	mvn clean

# run-local:build
# 	@echo "Running the Flink job locally..."
# 	$(FLINK_HOME)/bin/flink run -c $(MAIN_CLASS) $(JAR_FILE) $(ARGS)


# # Run the Flink job on a remote cluster
# run-cluster: build
# 	@echo "Running the Flink job on a cluster..."
# 	$(FLINK_HOME)/bin/flink run --jobmanager $(FLINK_CLUSTER) -c $(MAIN_CLASS) $(JAR_FILE) $(ARGS)

# # Run the Flink job locally in detached mode
# run-detached: build
# 	@echo "Running the Flink job in detached mode..."
# 	$(FLINK_HOME)/bin/flink run -d -c $(MAIN_CLASS) $(JAR_FILE) $(ARGS)

# # Cancel the Flink job
# cancel:
# 	@echo "Canceling the Flink job..."
# 	$(FLINK_HOME)/bin/flink cancel $(JOB_ID)

# # Cancel the Flink job
# list:
# 	@echo "Listing the Flink running job..."
# 	$(FLINK_HOME)/bin/flink list

# # Print the help
# help:
# 	@echo "Usage:"
# 	@echo "  make build           - Build the project"
# 	@echo "  make clean           - Clean the project"
# 	@echo "  make run-local       - Run the job locally"
# 	@echo "  make run-cluster     - Run the job on a cluster"
# 	@echo "  make run-detached    - Run the job in detached mode"
# 	@echo "  make cancel          - Cancel the job on the cluster"
# 	@echo "  make help            - Print this help message"
# 	@echo "  make gen             - generate fake data"


#helm create flink-helm