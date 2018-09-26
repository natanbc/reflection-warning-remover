package com.github.natanbc.warningremover;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class AccessibleSetter {
    private static final long OFFSET;
    private static final MethodHandle SET_VALUE;

    static {
        long offset;
        MethodHandle setValue;
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Constructor<?> constructor = unsafeClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object unsafe = constructor.newInstance();
            Field overrideField = AccessibleObject.class.getDeclaredField("override");
            offset = (Long)unsafeClass.getMethod("objectFieldOffset", Field.class).invoke(unsafe, overrideField);
            setValue = MethodHandles.lookup().unreflect(unsafeClass.getDeclaredMethod("putBoolean", Object.class, long.class, boolean.class)).bindTo(unsafe);
        } catch(Exception ignored) {
            offset = -1;
            setValue = null;
        }
        OFFSET = offset;
        SET_VALUE = setValue;
    }

    public static void setAccessible(AccessibleObject object, boolean flag) {
        //this means we cannot use Unsafe
        if(OFFSET == -1) {
            object.setAccessible(flag);
            return;
        }
        try {
            SET_VALUE.invokeExact((Object)object, OFFSET, flag);
        } catch(Throwable throwable) {
            throw new AssertionError(throwable);
        }
    }

    static void init() {}
}
