JAVAC = javac
JAVA = java
SRC = src/com/rin/Rin.java
BUILD_DIR = build
BIN_DIR = bin
DIST_DIR = dist
JAR = $(BIN_DIR)/rin.jar
WRAPPER = $(BIN_DIR)/rin
VERSION = 1.0.1

.PHONY: all clean dist install

all: $(JAR) $(WRAPPER)

$(JAR): $(SRC)
	mkdir -p $(BUILD_DIR)
	mkdir -p $(BIN_DIR)
	$(JAVAC) --release 11 -d $(BUILD_DIR) $(SRC)
	jar cfe $(JAR) com.rin.Rin -C $(BUILD_DIR) .

$(WRAPPER):
	@echo '#!/bin/bash' > $(WRAPPER)
	@echo 'SCRIPT_DIR="$$(cd "$$(dirname "$${BASH_SOURCE[0]}")" && pwd)"' >> $(WRAPPER)
	@echo 'if ! command -v java &> /dev/null; then' >> $(WRAPPER)
	@echo '    echo "Error: Java is not installed. Please install JRE (Java Runtime Environment)."' >> $(WRAPPER)
	@echo '    exit 1' >> $(WRAPPER)
	@echo 'fi' >> $(WRAPPER)
	@echo 'java -jar "$$SCRIPT_DIR/rin.jar" "$$@"' >> $(WRAPPER)
	chmod +x $(WRAPPER)

dist: all
	mkdir -p $(DIST_DIR)
	tar -cvzf $(DIST_DIR)/rin-$(VERSION).tar.gz -C $(BIN_DIR) rin rin.jar
	@echo "Distribution package created at $(DIST_DIR)/rin-$(VERSION).tar.gz"

clean:
	rm -rf $(BUILD_DIR) $(BIN_DIR) $(DIST_DIR)

install: all
	@echo "Add $(shell pwd)/bin to your PATH to use 'rin' from anywhere."
