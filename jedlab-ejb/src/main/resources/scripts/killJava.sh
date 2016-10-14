#!/bin/bash
for i in $(ps -ef | grep -i "$1" | awk '{print $2}'); do kill -9 $i; done
