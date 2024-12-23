# Makefile for Emergency Supply Network Project

# Compiler and Executable
JAVAC = javac
JAVA = java

# JSON Library
CLASSPATH = .:java-json.jar

# Source Files
SRC = EmergencySupplyNetwork.java ResourceRedistribution.java DynamicResourceSharing.java NetworkApp.java

# Targets
.PHONY: all clean run

# Default target
all: compile

# Compile all Java files
compile:
	$(JAVAC) -cp $(CLASSPATH) $(SRC)

# Run the program
run:
	$(JAVA) -cp $(CLASSPATH) NetworkApp

# Clean compiled files
clean:
	rm -f *.class

