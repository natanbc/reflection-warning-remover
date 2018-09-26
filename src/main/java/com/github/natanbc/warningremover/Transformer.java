package com.github.natanbc.warningremover;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class Transformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if(protectionDomain.getClassLoader() == null) return null;
        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        reader.accept(new Visitor(ASM6, writer), 0);
        return writer.toByteArray();
    }

    private static class Visitor extends ClassVisitor {
        Visitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            return new ReplacerVisitor(api, super.visitMethod(access, name, descriptor, signature, exceptions));
        }
    }

    private static class ReplacerVisitor extends MethodVisitor {
        private static final List<String> TARGET_OWNERS = Arrays.asList(
                Type.getInternalName(AccessibleObject.class),
                Type.getInternalName(Method.class),
                Type.getInternalName(Constructor.class),
                Type.getInternalName(Field.class)
        );
        private static final String TARGET_NAME = "setAccessible";
        private static final String TARGET_DESCRIPTOR = Type.getMethodDescriptor(Type.VOID_TYPE, Type.BOOLEAN_TYPE);
        private static final String REPLACEMENT_OWNER = Type.getInternalName(AccessibleSetter.class);
        private static final String REPLACEMENT_NAME = "setAccessible";
        private static final String REPLACEMENT_DESCRIPTOR =
                Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(AccessibleObject.class), Type.BOOLEAN_TYPE);

        ReplacerVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if(TARGET_OWNERS.contains(owner) && name.equals(TARGET_NAME) && descriptor.equals(TARGET_DESCRIPTOR)) {
                super.visitMethodInsn(INVOKESTATIC, REPLACEMENT_OWNER, REPLACEMENT_NAME, REPLACEMENT_DESCRIPTOR, false);
                return;
            }
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }
}
