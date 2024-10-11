# Function to extract property values using Maven Help Plugin
define get_property
	$(shell mvn help:evaluate -Dexpression=$(1) -q -DforceStdout  | xargs echo -n)
endef

# Extracted properties from pom.xml
# PROJECT_NAME := $(call get_property,project.artifactId )
# PROJECT_VERSION := $(call get_property,project.version ) 
PROJECT_NAME:= $(shell mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout | xargs echo -n)
PROJECT_VERSION:= $(shell mvn help:evaluate -Dexpression=project.version -q -DforceStdout | xargs echo -n)


MAIN_CLASS := $(call get_property,main.class)
FLINK_VERSION := $(call get_property,flink.version)
SCALA_VERSION := $(call get_property,scala.binary.version)
JAR_FILE := target/$(PROJECT_NAME)-$(PROJECT_VERSION).jar


# Define paths and configurations
FLINK_HOME ?= /opt/flink-1.18.1
FLINK_CLUSTER ?= localhost:8081
#INPUT_FILE ?= /path/to/input/file
#OUTPUT_DIR ?= /path/to/output/dir
ARGS ?= #--input $(INPUT_FILE) --output $(OUTPUT_DIR)

# Maven goals
MVN := mvn
PYTHON := python3

print-var:
	@echo "Project Name: 	$(PROJECT_NAME)"
	@echo "Project Version:  $(PROJECT_VERSION)"
	@echo "Main Class: 		  $(MAIN_CLASS)"
	@echo "Flink Version: 	  $(FLINK_VERSION)"
	@echo "Scala Version:    $(SCALA_VERSION)"
	@echo "JAR File:            $(JAR_FILE)"
	@echo "FLINK HOME:     $(FLINK_HOME)"
	@echo "FLINK CLUSTER: $(FLINK_CLUSTER)"
	@echo "ARGS:               $(ARGS)"

gen:
	@echo "Generating fake data..."
	@$(PYTHON) ./Generator/main.py

all: build

build:
	@echo "Building the Flink job..."
	$(MVN) clean package

clean:
	@echo "Cleaning the project..."
	$(MVN) clean

run-local:build
	@echo "Running the Flink job locally..."
	$(FLINK_HOME)/bin/flink run -c $(MAIN_CLASS) $(JAR_FILE) $(ARGS)


# Run the Flink job on a remote cluster
run-cluster: build
	@echo "Running the Flink job on a cluster..."
	$(FLINK_HOME)/bin/flink run --jobmanager $(FLINK_CLUSTER) -c $(MAIN_CLASS) $(JAR_FILE) $(ARGS)

# Run the Flink job locally in detached mode
run-detached: build
	@echo "Running the Flink job in detached mode..."
	$(FLINK_HOME)/bin/flink run -d -c $(MAIN_CLASS) $(JAR_FILE) $(ARGS)

# Cancel the Flink job
cancel:
	@echo "Canceling the Flink job..."
	$(FLINK_HOME)/bin/flink cancel $(JOB_ID)

# Cancel the Flink job
list:
	@echo "Listing the Flink running job..."
	$(FLINK_HOME)/bin/flink list)

# Print the help
help:
	@echo "Usage:"
	@echo "  make build           - Build the project"
	@echo "  make clean           - Clean the project"
	@echo "  make run-local       - Run the job locally"
	@echo "  make run-cluster     - Run the job on a cluster"
	@echo "  make run-detached    - Run the job in detached mode"
	@echo "  make cancel          - Cancel the job on the cluster"
	@echo "  make help            - Print this help message"
	@echo "  make gen             - generate fake data"


# .PHONY: all build clean run run-cluster run-detached cancel help
.PHONY: help
.DEFAULT_GOAL:=help