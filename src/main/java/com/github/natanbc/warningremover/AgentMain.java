package com.github.natanbc.warningremover;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class AgentMain {
    public static void premain(String args, Instrumentation instrumentation) {
        setup(args, instrumentation);
    }

    public static void agentmain(String args, Instrumentation instrumentation) {
        setup(args, instrumentation);
    }

    private static void setup(String args, Instrumentation instrumentation) {
        boolean retransformLoaded = false;
        if(args != null) {
            String[] splitArgs = args.split(",");
            for(String s : splitArgs) {
                if(s.equalsIgnoreCase("retransform-loaded")) {
                    retransformLoaded = true;
                }
            }
        }

        AccessibleSetter.init();
        instrumentation.addTransformer(new Transformer(), true);
        if(retransformLoaded) {
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
}
