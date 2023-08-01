#!/usr/bin/env bash

#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 3
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor,
# Boston, MA  02110-1301, USA.
#

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

