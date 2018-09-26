package com.github.natanbc.warningremover;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class AgentMain {
    public static void premain(String s, Instrumentation instrumentation) {
        setup(instrumentation);
    }

    public static void agentmain(String s, Instrumentation instrumentation) {
        setup(instrumentation);
    }

    private static void setup(Instrumentation instrumentation) {
        AccessibleSetter.init();
        instrumentation.addTransformer(new Transformer(), true);
        Class<?>[] holder = new Class[1];
        for(Class<?> c : instrumentation.getAllLoadedClasses()) {
            if(instrumentation.isModifiableClass(c)) {
                holder[0] = c;
                try {
                    instrumentation.retransformClasses(holder);
                } catch(UnmodifiableClassException ignore) {}
            }
        }
    }
}
