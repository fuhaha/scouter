// Generated by delombok at Sun Feb 26 12:31:38 KST 2017
package scouter.bytebuddy.implementation.attribute;

import scouter.bytebuddy.description.annotation.AnnotationDescription;
import scouter.bytebuddy.description.field.FieldDescription;
import scouter.bytebuddy.description.type.TypeDescription;
import scouter.bytebuddy.jar.asm.FieldVisitor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An appender that writes attributes or annotations to a given ASM {@link FieldVisitor}.
 */
public interface FieldAttributeAppender {
    /**
     * Applies this attribute appender to a given field visitor.
     *
     * @param fieldVisitor          The field visitor to which the attributes that are represented by this attribute appender are written to.
     * @param fieldDescription      The description of the field to which the field visitor belongs to.
     * @param annotationValueFilter The annotation value filter to apply when writing annotations.
     */
    void apply(FieldVisitor fieldVisitor, FieldDescription fieldDescription, AnnotationValueFilter annotationValueFilter);


    /**
     * A field attribute appender that does not append any attributes.
     */
    enum NoOp implements FieldAttributeAppender, Factory {
        /**
         * The singleton instance.
         */
        INSTANCE;

        @Override
        public FieldAttributeAppender make(TypeDescription typeDescription) {
            return this;
        }

        @Override
        public void apply(FieldVisitor fieldVisitor, FieldDescription fieldDescription, AnnotationValueFilter annotationValueFilter) {
            /* do nothing */
        }
    }


    /**
     * A factory that creates field attribute appenders for a given type.
     */
    interface Factory {
        /**
         * Returns a field attribute appender that is applicable for a given type description.
         *
         * @param typeDescription The type for which a field attribute appender is to be applied for.
         * @return The field attribute appender which should be applied for the given type.
         */
        FieldAttributeAppender make(TypeDescription typeDescription);


        /**
         * A field attribute appender factory that combines several field attribute appender factories to be
         * represented as a single factory.
         */
        class Compound implements Factory {
            /**
             * The factories that this compound factory represents in their application order.
             */
            private final List<Factory> factories;

            /**
             * Creates a new compound field attribute appender factory.
             *
             * @param factory The factories to represent in the order of their application.
             */
            public Compound(Factory... factory) {
                this(Arrays.asList(factory));
            }

            /**
             * Creates a new compound field attribute appender factory.
             *
             * @param factories The factories to represent in the order of their application.
             */
            public Compound(List<? extends Factory> factories) {
                this.factories = new ArrayList<Factory>();
                for (Factory factory : factories) {
                    if (factory instanceof Compound) {
                        this.factories.addAll(((Compound) factory).factories);
                    } else if (!(factory instanceof NoOp)) {
                        this.factories.add(factory);
                    }
                }
            }

            @Override
            public FieldAttributeAppender make(TypeDescription typeDescription) {
                List<FieldAttributeAppender> fieldAttributeAppenders = new ArrayList<FieldAttributeAppender>(factories.size());
                for (Factory factory : factories) {
                    fieldAttributeAppenders.add(factory.make(typeDescription));
                }
                return new FieldAttributeAppender.Compound(fieldAttributeAppenders);
            }

            @java.lang.Override
            @java.lang.SuppressWarnings("all")
            @javax.annotation.Generated("lombok")
            public boolean equals(final java.lang.Object o) {
                if (o == this) return true;
                if (!(o instanceof FieldAttributeAppender.Factory.Compound)) return false;
                final FieldAttributeAppender.Factory.Compound other = (FieldAttributeAppender.Factory.Compound) o;
                if (!other.canEqual((java.lang.Object) this)) return false;
                final java.lang.Object this$factories = this.factories;
                final java.lang.Object other$factories = other.factories;
                if (this$factories == null ? other$factories != null : !this$factories.equals(other$factories)) return false;
                return true;
            }

            @java.lang.SuppressWarnings("all")
            @javax.annotation.Generated("lombok")
            protected boolean canEqual(final java.lang.Object other) {
                return other instanceof FieldAttributeAppender.Factory.Compound;
            }

            @java.lang.Override
            @java.lang.SuppressWarnings("all")
            @javax.annotation.Generated("lombok")
            public int hashCode() {
                final int PRIME = 59;
                int result = 1;
                final java.lang.Object $factories = this.factories;
                result = result * PRIME + ($factories == null ? 43 : $factories.hashCode());
                return result;
            }
        }
    }


    /**
     * An attribute appender that writes all annotations that are declared on a field.
     */
    enum ForInstrumentedField implements FieldAttributeAppender, Factory {
        /**
         * The singleton instance.
         */
        INSTANCE;

        @Override
        public void apply(FieldVisitor fieldVisitor, FieldDescription fieldDescription, AnnotationValueFilter annotationValueFilter) {
            AnnotationAppender annotationAppender = new AnnotationAppender.Default(new AnnotationAppender.Target.OnField(fieldVisitor));
            annotationAppender = fieldDescription.getType().accept(AnnotationAppender.ForTypeAnnotations.ofFieldType(annotationAppender, annotationValueFilter));
            for (AnnotationDescription annotation : fieldDescription.getDeclaredAnnotations()) {
                annotationAppender = annotationAppender.append(annotation, annotationValueFilter);
            }
        }

        @Override
        public FieldAttributeAppender make(TypeDescription typeDescription) {
            return this;
        }
    }


    /**
     * Appends an annotation to a field. The visibility of the annotation is determined by the annotation type's
     * {@link java.lang.annotation.RetentionPolicy} annotation.
     */
    class Explicit implements FieldAttributeAppender, Factory {
        /**
         * The annotations that this appender appends.
         */
        private final List<? extends AnnotationDescription> annotations;

        /**
         * Creates a new annotation attribute appender for explicit annotation values. All values, including default values, are copied.
         *
         * @param annotations The annotations to be appended to the field.
         */
        public Explicit(List<? extends AnnotationDescription> annotations) {
            this.annotations = annotations;
        }

        @Override
        public void apply(FieldVisitor fieldVisitor, FieldDescription fieldDescription, AnnotationValueFilter annotationValueFilter) {
            AnnotationAppender appender = new AnnotationAppender.Default(new AnnotationAppender.Target.OnField(fieldVisitor));
            for (AnnotationDescription annotation : annotations) {
                appender = appender.append(annotation, annotationValueFilter);
            }
        }

        @Override
        public FieldAttributeAppender make(TypeDescription typeDescription) {
            return this;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @javax.annotation.Generated("lombok")
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof FieldAttributeAppender.Explicit)) return false;
            final FieldAttributeAppender.Explicit other = (FieldAttributeAppender.Explicit) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$annotations = this.annotations;
            final java.lang.Object other$annotations = other.annotations;
            if (this$annotations == null ? other$annotations != null : !this$annotations.equals(other$annotations)) return false;
            return true;
        }

        @java.lang.SuppressWarnings("all")
        @javax.annotation.Generated("lombok")
        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof FieldAttributeAppender.Explicit;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @javax.annotation.Generated("lombok")
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $annotations = this.annotations;
            result = result * PRIME + ($annotations == null ? 43 : $annotations.hashCode());
            return result;
        }
    }


    /**
     * A field attribute appender that combines several method attribute appenders to be represented as a single
     * field attribute appender.
     */
    class Compound implements FieldAttributeAppender {
        /**
         * The field attribute appenders this appender represents in their application order.
         */
        private final List<FieldAttributeAppender> fieldAttributeAppenders;

        /**
         * Creates a new compound field attribute appender.
         *
         * @param fieldAttributeAppender The field attribute appenders that are to be combined by this compound appender
         *                               in the order of their application.
         */
        public Compound(FieldAttributeAppender... fieldAttributeAppender) {
            this(Arrays.asList(fieldAttributeAppender));
        }

        /**
         * Creates a new compound field attribute appender.
         *
         * @param fieldAttributeAppenders The field attribute appenders that are to be combined by this compound appender
         *                                in the order of their application.
         */
        public Compound(List<? extends FieldAttributeAppender> fieldAttributeAppenders) {
            this.fieldAttributeAppenders = new ArrayList<FieldAttributeAppender>();
            for (FieldAttributeAppender fieldAttributeAppender : fieldAttributeAppenders) {
                if (fieldAttributeAppender instanceof Compound) {
                    this.fieldAttributeAppenders.addAll(((Compound) fieldAttributeAppender).fieldAttributeAppenders);
                } else if (!(fieldAttributeAppender instanceof NoOp)) {
                    this.fieldAttributeAppenders.add(fieldAttributeAppender);
                }
            }
        }

        @Override
        public void apply(FieldVisitor fieldVisitor, FieldDescription fieldDescription, AnnotationValueFilter annotationValueFilter) {
            for (FieldAttributeAppender fieldAttributeAppender : fieldAttributeAppenders) {
                fieldAttributeAppender.apply(fieldVisitor, fieldDescription, annotationValueFilter);
            }
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @javax.annotation.Generated("lombok")
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof FieldAttributeAppender.Compound)) return false;
            final FieldAttributeAppender.Compound other = (FieldAttributeAppender.Compound) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$fieldAttributeAppenders = this.fieldAttributeAppenders;
            final java.lang.Object other$fieldAttributeAppenders = other.fieldAttributeAppenders;
            if (this$fieldAttributeAppenders == null ? other$fieldAttributeAppenders != null : !this$fieldAttributeAppenders.equals(other$fieldAttributeAppenders)) return false;
            return true;
        }

        @java.lang.SuppressWarnings("all")
        @javax.annotation.Generated("lombok")
        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof FieldAttributeAppender.Compound;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @javax.annotation.Generated("lombok")
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $fieldAttributeAppenders = this.fieldAttributeAppenders;
            result = result * PRIME + ($fieldAttributeAppenders == null ? 43 : $fieldAttributeAppenders.hashCode());
            return result;
        }
    }
}