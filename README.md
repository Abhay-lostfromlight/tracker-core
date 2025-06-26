# Tracker Core

A simple Java application to probe and aggregate active window usage on Windows (and potentially other platforms). Uses JNA and OSHI to detect the currently active window and its process name.

## Features
- Detects the active window on Windows
- Retrieves process name using native APIs

## Requirements
- Java 8 or higher
- Maven

## Usage
Build and run using Maven:
```sh
mvn package
java -cp target/tracker-core-1.0-SNAPSHOT.jar dev.tracker.Main
```

Didn't finish this because there already exist many projects like this. 