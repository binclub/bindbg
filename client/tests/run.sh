#!/bin/bash

sudo /sbin/sysctl -w net.ipv4.ip_unprivileged_port_start=0
java -Xdebug -Xrunjdwp:transport=dt_socket,address=1000,server=y,suspend=n $1
