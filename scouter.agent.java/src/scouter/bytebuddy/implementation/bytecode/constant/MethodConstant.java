// Generated by delombok at Sun Feb 26 12:31:38 KST 2017
package scouter.bytebuddy.implementation.bytecode.constant;

import scouter.bytebuddy.description.method.MethodDescription;
import scouter.bytebuddy.description.type.TypeDescription;
import scouter.bytebuddy.implementation.Implementation;
import scouter.bytebuddy.implementation.bytecode.StackManipulation;
import scouter.bytebuddy.implementation.bytecode.collection.ArrayFactory;
import scouter.bytebuddy.implementation.bytecode.member.FieldAccess;
import scouter.bytebuddy.implementation.bytecode.member.MethodInvocation;
import scouter.bytebuddy.jar.asm.MethodVisitor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the creation of a {@link java.lang.reflect.Method} value which can be created from a given
 * set of constant pool values and can therefore be considered a constant in the broader meaning.
 */
public abstract class MethodConstant implements StackManipulation {
    /**
     * The internal name of the {@link Class} type.
     */
    private static final String CLASS_TYPE_INTERNAL_NAME = "java/lang/Class";
    /**
     * A description of the method to be loaded onto the stack.
     */
    protected final MethodDescription.InDefinedShape methodDescription;

    /**
     * Creates a new method constant.
     *
     * @param methodDescription The method description for which the {@link java.lang.reflect.Method} representation
     * should be created.
     */
    protected MethodConstant(MethodDescription.InDefinedShape methodDescription) {
        this.methodDescription = methodDescription;
    }

    /**
     * Creates a stack manipulation that loads a method constant onto the operand stack.
     *
     * @param methodDescription The method to be loaded onto the stack.
     * @return A stack manipulation that assigns a method constant for the given method description.
     */
    public static CanCache forMethod(MethodDescription.InDefinedShape methodDescription) {
        if (methodDescription.isTypeInitializer()) {
            return CanCacheIllegal.INSTANCE;
        } else if (methodDescription.isConstructor()) {
            return new ForConstructor(methodDescription);
        } else {
            return new ForMethod(methodDescription);
        }
    }

    /**
     * Returns a list of type constant load operations for the given list of parameters.
     *
     * @param parameterTypes A list of all type descriptions that should be represented as type constant
     * load operations.
     * @return A corresponding list of type constant load operations.
     */
    private static List<StackManipulation> typeConstantsFor(List<TypeDescription> parameterTypes) {
        List<StackManipulation> typeConstants = new ArrayList<StackManipulation>(parameterTypes.size());
        for (TypeDescription parameterType : parameterTypes) {
            typeConstants.add(ClassConstant.of(parameterType));
        }
        return typeConstants;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
        return new Compound(preparation(), ArrayFactory.forType(new TypeDescription.Generic.OfNonGenericType.ForLoadedType(Class.class)).withValues(typeConstantsFor(methodDescription.getParameters().asTypeList().asErasures())), MethodInvocation.invoke(accessorMethod())).apply(methodVisitor, implementationContext);
    }

    /**
     * Returns a stack manipulation that loads the values that are required for loading a method constant onto the operand stack.
     *
     * @return A stack manipulation for loading a method or constructor onto the operand stack.
     */
    protected abstract StackManipulation preparation();

    /**
     * Returns the method for loading a declared method or constructor onto the operand stack.
     *
     * @return The method for loading a declared method or constructor onto the operand stack.
     */
    protected abstract MethodDescription accessorMethod();

    /**
     * Returns a cached version of this method constant as specified by {@link CachedMethod} and {@link CachedConstructor}.
     *
     * @return A cached version of this method constant.
     */
    public StackManipulation cached() {
        return methodDescription.isConstructor() ? new CachedConstructor(this) : new CachedMethod(this);
    }


    /**
     * Represents a method constant that cannot be represented by Java's reflection API.
     */
    protected enum CanCacheIllegal implements CanCache {
        /**
         * The singleton instance.
         */
        INSTANCE;

        @Override
        public StackManipulation cached() {
            return Illegal.INSTANCE;
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            return Illegal.INSTANCE.apply(methodVisitor, implementationContext);
        }
    }


    /**
     * Represents a {@link MethodConstant} that is
     * directly loaded onto the operand stack without caching the value. Since the look-up of a Java method bares
     * some costs that sometimes need to be avoided, such a stack manipulation offers a convenience method for
     * defining this loading instruction as the retrieval of a field value that is initialized in the instrumented
     * type's type initializer.
     */
    public interface CanCache extends StackManipulation {
        /**
         * Returns this method constant as a cached version.
         *
         * @return A cached version of the method constant that is represented by this instance.
         */
        StackManipulation cached();
    }


    /**
     * Creates a {@link MethodConstant} for loading
     * a {@link java.lang.reflect.Method} instance onto the operand stack.
     */
    protected static class ForMethod extends MethodConstant implements CanCache {
        /**
         * Creates a new {@link MethodConstant} for
         * creating a {@link java.lang.reflect.Method} instance.
         *
         * @param methodDescription The method to be loaded onto the stack.
         */
        protected ForMethod(MethodDescription.InDefinedShape methodDescription) {
            super(methodDescription);
        }

        @Override
        protected StackManipulation preparation() {
            return new Compound(ClassConstant.of(methodDescription.getDeclaringType()), new TextConstant(methodDescription.getInternalName()));
        }

        @Override
        protected MethodDescription accessorMethod() {
            try {
                return new MethodDescription.ForLoadedMethod(Class.class.getMethod("getDeclaredMethod", String.class, Class[].class));
            } catch (NoSuchMethodException exception) {
                throw new IllegalStateException("Cannot locate Class::getDeclaredMethod", exception);
            }
        }
    }


    /**
     * Creates a {@link MethodConstant} for loading
     * a {@link java.lang.reflect.Constructor} instance onto the operand stack.
     */
    protected static class ForConstructor extends MethodConstant implements CanCache {
        /**
         * Creates a new {@link MethodConstant} for
         * creating a {@link java.lang.reflect.Constructor} instance.
         *
         * @param methodDescription The constructor to be loaded onto the stack.
         */
        protected ForConstructor(MethodDescription.InDefinedShape methodDescription) {
            super(methodDescription);
        }

        @Override
        protected StackManipulation preparation() {
            return ClassConstant.of(methodDescription.getDeclaringType());
        }

        @Override
        protected MethodDescription accessorMethod() {
            try {
                return new MethodDescription.ForLoadedMethod(Class.class.getMethod("getDeclaredConstructor", Class[].class));
            } catch (NoSuchMethodException exception) {
                throw new IllegalStateException("Cannot locate Class::getDeclaredConstructor", exception);
            }
        }
    }


    /**
     * Represents a cached method for a {@link MethodConstant}.
     */
    protected static class CachedMethod implements StackManipulation {
        /**
         * A description of the {@link java.lang.reflect.Method} type.
         */
        private static final TypeDescription METHOD_TYPE = new TypeDescription.ForLoadedType(Method.class);
        /**
         * The stack manipulation that is represented by this caching wrapper.
         */
        private final StackManipulation methodConstant;

        /**
         * Creates a new cached {@link MethodConstant}.
         *
         * @param methodConstant The method constant to store in the field cache.
         */
        protected CachedMethod(StackManipulation methodConstant) {
            this.methodConstant = methodConstant;
        }

        @Override
        public boolean isValid() {
            return methodConstant.isValid();
        }

        @Override
        public Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            return FieldAccess.forField(implementationContext.cache(methodConstant, METHOD_TYPE)).read().apply(methodVisitor, implementationContext);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @javax.annotation.Generated("lombok")
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof MethodConstant.CachedMethod)) return false;
            final MethodConstant.CachedMethod other = (MethodConstant.CachedMethod) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$methodConstant = this.methodConstant;
            final java.lang.Object other$methodConstant = other.methodConstant;
            if (this$methodConstant == null ? other$methodConstant != null : !this$methodConstant.equals(other$methodConstant)) return false;
            return true;
        }

        @java.lang.SuppressWarnings("all")
        @javax.annotation.Generated("lombok")
        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof MethodConstant.CachedMethod;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @javax.annotation.Generated("lombok")
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $methodConstant = this.methodConstant;
            result = result * PRIME + ($methodConstant == null ? 43 : $methodConstant.hashCode());
            return result;
        }
    }


    /**
     * Represents a cached constructor for a {@link MethodConstant}.
     */
    protected static class CachedConstructor implements StackManipulation {
        /**
         * A description of the {@link java.lang.reflect.Constructor} type.
         */
        private static final TypeDescription CONSTRUCTOR_TYPE = new TypeDescription.ForLoadedType(Constructor.class);
        /**
         * The stack manipulation that is represented by this caching wrapper.
         */
        private final StackManipulation constructorConstant;

        /**
         * Creates a new cached {@link MethodConstant}.
         *
         * @param constructorConstant The method constant to store in the field cache.
         */
        protected CachedConstructor(StackManipulation constructorConstant) {
            this.constructorConstant = constructorConstant;
        }

        @Override
        public boolean isValid() {
            return constructorConstant.isValid();
        }

        @Override
        public Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            return FieldAccess.forField(implementationContext.cache(constructorConstant, CONSTRUCTOR_TYPE)).read().apply(methodVisitor, implementationContext);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @javax.annotation.Generated("lombok")
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof MethodConstant.CachedConstructor)) return false;
            final MethodConstant.CachedConstructor other = (MethodConstant.CachedConstructor) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$constructorConstant = this.constructorConstant;
            final java.lang.Object other$constructorConstant = other.constructorConstant;
            if (this$constructorConstant == null ? other$constructorConstant != null : !this$constructorConstant.equals(other$constructorConstant)) return false;
            return true;
        }

        @java.lang.SuppressWarnings("all")
        @javax.annotation.Generated("lombok")
        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof MethodConstant.CachedConstructor;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @javax.annotation.Generated("lombok")
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $constructorConstant = this.constructorConstant;
            result = result * PRIME + ($constructorConstant == null ? 43 : $constructorConstant.hashCode());
            return result;
        }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    @javax.annotation.Generated("lombok")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof MethodConstant)) return false;
        final MethodConstant other = (MethodConstant) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$methodDescription = this.methodDescription;
        final java.lang.Object other$methodDescription = other.methodDescription;
        if (this$methodDescription == null ? other$methodDescription != null : !this$methodDescription.equals(other$methodDescription)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    @javax.annotation.Generated("lombok")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof MethodConstant;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    @javax.annotation.Generated("lombok")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $methodDescription = this.methodDescription;
        result = result * PRIME + ($methodDescription == null ? 43 : $methodDescription.hashCode());
        return result;
    }
}
