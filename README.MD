# BinDbg

Providing dynamic analysis for applications running on a Java Virtual Machine.

# Building

```
mvn package
java -jar target/bindbg.jar
```

You can run a test application:
```
cd tests
./run.sh LoopTest.java
```

You can then connect BinDbg to the test application using the ip `localhost` and port `1000`.
