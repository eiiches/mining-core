package net.thisptr.encoder.bean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.thisptr.encoder.bean.attribute.AttributeEncoder;

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EncodeWith {
	Class<? extends AttributeEncoder<?>> value();
}