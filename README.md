# reflection-warning-remover
gets rid of java 9+ warnings about illegal reflective accesses by rewriting all method calls that trigger them to an Unsafe based approach that won't trigger the warning

# Usage

* Compile

```
./gradlew shadowJar
```

* Grab the agent jar from build/libs/reflection-warning-remover-1.0-SNAPSHOT-all.jar

* Run your VM using the agent

```
java -javaagent:path/to/the/jar your-other-options-go-here
```
