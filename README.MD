# BinDbg

Providing dynamic analysis for applications running on a Java Virtual Machine.

![Sample Screenshot](https://i.imgur.com/BB6QPht.png)

BinDbg is a work in progress.
At the moment you can currently pause, resume, and terminate the JVM; and while the JVM is paused you can select a
 thread to debug and select a stackframe and view the local variables of that stackframe.

To do:
* Method Bytecode view
* Step over/out/back to user code
* VM introspection

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
