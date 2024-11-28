#Bigdata

```shell
sudo docker ps --format "table {{.Names}}"
docker ps -a --format "table {{.Names}}\t {{.Status}}"

watch -n 1 docker ps -a

```

# Run all docker-compose files
```bash 
# Variables
COMPOSE_FILES=docker-compose-1.yml docker-compose-2.yml

.PHONY: help up down logs ps clean

# Default help target
help:
    @echo "Usage:"
    @echo "  make up               Start all services in the background"
    @echo "  make down             Stop all services"
    @echo "  make logs             View logs for all services"
    @echo "  make ps               List all running services"
    @echo "  make clean            Stop and remove all services"

# Start all services
up:
    @for compose_file in $(COMPOSE_FILES); do \
        docker-compose -f $$compose_file up -d; \
    done
    @echo "All services are up and running in the background."

# Stop all services
down:
    @for compose_file in $(COMPOSE_FILES); do \
        docker-compose -f $$compose_file down; \
    done
    @echo "All services have been stopped."

# View logs for all services
logs:
    @for compose_file in $(COMPOSE_FILES); do \
        docker-compose -f $$compose_file logs -f & \
    done

# List all running services
ps:
    @for compose_file in $(COMPOSE_FILES); do \
        docker-compose -f $$compose_file ps; \
    done

# Stop and remove all services
clean: down
    @echo "Cleaned up all services."

```

```shell
# Variables
COMPOSE_FILES=$(shell find . -type f -name "docker-compose.yml")

.PHONY: help up down logs ps clean

# Default help target
help:
    @echo "Usage:"
    @echo "  make up               Start all services in subdirectories"
    @echo "  make down             Stop all services in subdirectories"
    @echo "  make logs             View logs for all services"
    @echo "  make ps               List all running services"
    @echo "  make clean            Stop and remove all services in subdirectories"

# Start all services in subdirectories
up:
    @for compose_file in $(COMPOSE_FILES); do \
        echo "Starting services in $$(dirname $$compose_file)"; \
        docker-compose -f $$compose_file up -d; \
    done
    @echo "All services are up."

# Stop all services in subdirectories
down:
    @for compose_file in $(COMPOSE_FILES); do \
        echo "Stopping services in $$(dirname $$compose_file)"; \
        docker-compose -f $$compose_file down; \
    done
    @echo "All services are stopped."

# View logs for all services
logs:
    @for compose_file in $(COMPOSE_FILES); do \
        echo "Showing logs for $$(dirname $$compose_file)"; \
        docker-compose -f $$compose_file logs -f; \
    done

# List all running services
ps:
    @for compose_file in $(COMPOSE_FILES); do \
        echo "Listing services in $$(dirname $$compose_file)"; \
        docker-compose -f $$compose_file ps; \
    done

# Stop and remove all services
clean: down
    @echo "Cleaned up all services."

```
