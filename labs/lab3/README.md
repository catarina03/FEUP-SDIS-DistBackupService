# HOW TO RUN

- Compile all .java files and put .class files in 'classDir' directory (run cmd in 'src'):

ubuntu:
```
javac -d classDir *.java
```


- Start the Java RMI registry (run cmd in 'classDir'):

ubuntu: 
```
rmiregistry &
```

windows: 
```
start rmiregistry [<2001>]
```


- Start the server (run cmd in 'src'):

ubuntu: 
```
java -classpath classDir -Djava.rmi.server.codebase=file:classDir/ Server <remote_object_name> &
```

windows: 
```
start java -classpath classDir -Djava.rmi.server.codebase=file:classDir/ Server <remote_object_name>
```


- Open another terminal -> Run the client (run cmd in 'src'):
```
java -classpath classDir Client <host> <remote_object_name> <operation> <operands>*
```