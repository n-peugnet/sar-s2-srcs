PROTOBUF_PATH ?= /usr/local/protobuf-master/src
GRPC_JAVA_PATH ?= /usr/local/grpc-java-master/compiler/build/exe/java_plugin/protoc-gen-grpc-java
JAVA_SRC_DIR := src
PROTO_SRC_DIR := proto
PROTO_JAVA_DIR := $(JAVA_SRC_DIR)/srcs/chat/proto
PROTO_SRC := $(wildcard proto/*.proto)
PROTO_JAVA := $(patsubst $(PROTO_SRC_DIR)/%.proto,$(PROTO_JAVA_DIR)/%.java,$(PROTO_SRC))

.PHONY: all
all: $(PROTO_JAVA)
	@echo $(PROTO_JAVA)

$(PROTO_JAVA_DIR)/%.java: $(PROTO_SRC_DIR)/%.proto
	protoc --proto_path=proto \
		--proto_path=$(PROTOBUF_PATH) \
		--plugin=$(GRPC_JAVA_PATH) \
		--grpc-java_out=$(JAVA_SRC_DIR) \
		--java_out=$(JAVA_SRC_DIR) \
		$<

