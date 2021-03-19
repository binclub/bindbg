# Tests

This directory contains some simple test applications for BinDbg.

These are just java applications that can be ran using `run.sh` and will provide a jdwp interface for BinDbg to connect to.

You can run them like so:
```bash
./run.sh LoopTest.java
```

It will require superuser permissions in order to allow it to start a jdwp socket (on some linux distributions you cannot listen to a port without permissions).

