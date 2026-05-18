#!/bin/bash
mvn clean verify package
java -jar target/osqa-1.3.jar
