#!/bin/bash
mvn clean package
java -jar target/osqa-1.3.jar
