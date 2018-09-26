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

## Arguments

Arguments are provided in a comma separated list after the javaagent argument:

```
java -javaagent:path/to/agent=ARGS
```

The following arguments are supported:

- `retransform-loaded`: retransforms all currently loaded classes after starting the agent. Without this option, only
classes loaded after the agent starts will be transformed. This option usually won't matter unless you load other
agents before this one, so the recommended is to leave it off for faster startup.
