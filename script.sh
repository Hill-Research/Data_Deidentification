#!/usr/bin/env bash

Parse_Args()
{
  while [[ $# -gt 0 ]]; do
    case $1 in
      -p|--package)
        PACKAGE="$2"
        shift
        shift
        ;;
      -c|--class)
        CLASS="$2"
        shift
        shift
        ;;
    esac
  done
}

Run()
{
  case "$GOAL" in
    reset)
      mvn clean #--log-file maven.log
      exit;;
    compile)
      mvn compile #--log-file maven.log
      exit;;
    run)
      mvn compile #--log-file maven.log
      mvn exec:java -Dexec.mainClass="com.dupreytherapeutics.${PACKAGE}.${CLASS}" #--log-file maven.log
      exit;;
    package)
      mvn package #--log-file maven.log
      exit;;
    docgen)
      mvn site #--log-file maven.log
      mvn javadoc:javadoc #--log-file maven.log
      exit;;
    reformat)
      mvn com.coveo:fmt-maven-plugin:format #--log-file maven.log
      exit;;
  esac
}
GOAL=$1
shift
PACKAGE="Demo"
CLASS="Main"
Parse_Args "$@"
Run "$GOAL"

