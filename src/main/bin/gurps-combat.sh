#!/bin/bash
base_dir=$(dirname "$0")
cd $base_dir
java -jar ../lib/${pom.artifactId}-${pom.version}.jar
exit 0
