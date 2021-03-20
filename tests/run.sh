#!/bin/bash

# TODO: don't assume user is using sudo?
# Maybe it is also better to run the application as super user, that way we
# don't disable the port protection for every application, however that also
# means the user has to trust the application
sudo /sbin/sysctl -w net.ipv4.ip_unprivileged_port_start=0
java -Xdebug -Xrunjdwp:transport=dt_socket,address=1000,server=y,suspend=n $1
