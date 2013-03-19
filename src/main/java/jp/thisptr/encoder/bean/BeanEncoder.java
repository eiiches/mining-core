package jp.thisptr.encoder.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.thisptr.encoder.Encoder;
import jp.thisptr.encoder.bean.annotation.EncodeWith;
import jp.thisptr.encoder.bean.annotation.Ignore;
import jp.thisptr.encoder.bean.attribute.AttributeEncoder;
import jp.thisptr.encoder.bean.attribute.NominalAttributeEncoder;
import jp.thisptr.encoder.bean.attribute.NumericalAttributeEncoder;
import jp.thisptr.encoder.bean.attribute.AttributeEncoder.Context;
import jp.thisptr.lang.tuple.Pair;
import jp.thisptr.math.vector.DenseArrayVector;
import jp.thisptr.math.vector.SparseMapVector;
import jp.thisptr.math.vector.Vector;
import jp.thisptr.util.SequencialIdMapper;

import org.apache.commons.lang3.StringUtils;

public class BeanEncoder<T> implements Encoder<T> {
	private static Map<Class<?>, Class<? extends AttributeEncoder<?>>> defaultAttributeEncoders = new HashMap<>();
	static {
		// default nominal
		defaultAttributeEncoders.put(String.class, NominalAttributeEncoder.class);
		
		// default numerical
		defaultAttributeEncoders.put(Byte.class, NumericalAttributeEncoder.class);
		defaultAttributeEncoders.put(Double.class, NumericalAttributeEncoder.class);
		defaultAttributeEncoders.put(Float.class, NumericalAttributeEncoder.class);
		defaultAttributeEncoders.put(Integer.class, NumericalAttributeEncoder.class);
		defaultAttributeEncoders.put(Long.class, NumericalAttributeEncoder.class);
		defaultAttributeEncoders.put(Short.class, NumericalAttributeEncoder.class);
		
		// default numerical
		defaultAttributeEncoders.put(byte.class, NumericalAttributeEncoder.class);
		defaultAttributeEncoders.put(double.class, NumericalAttributeEncoder.class);
		defaultAttributeEncoders.put(float.class, NumericalAttributeEncoder.class);
		defaultAttributeEncoders.put(int.class, NumericalAttributeEncoder.class);
		defaultAttributeEncoders.put(long.class, NumericalAttributeEncoder.class);
		defaultAttributeEncoders.put(short.class, NumericalAttributeEncoder.class);
	}
	
	private static class Accessor {
		private Field field;
		private Method getter;
		
		public Accessor(final Class<?> klass, final String fieldName) {
			try {
				this.field = klass.getField(fieldName);
				try {
					this.getter = klass.getMethod(fieldNameToGetterName(fieldName));
				} catch (NoSuchMethodException | SecurityException e1) { }
			} catch (NoSuchFieldException | SecurityException e) {
				throw new RuntimeException(e);
			}
		}
		
		private static String fieldNameToGetterName(final String fieldName) {
			return "get" + StringUtils.capitalize(fieldName);
		}
		
		public Object getValue(final Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			if (this.getter != null)
				return this.getter.invoke(obj);
			return this.field.get(obj);
		}
		
		@Override
		public String toString() {
			return getFieldName();
		}
		
		private static <T extends Annotation> T getAnnotation(final Field field, final Method getterMethod, final Class<T> annotationClass) {
			if (getterMethod != null) {
				final T annotation = getterMethod.getAnnotation(annotationClass);
				if (annotation != null)
					return annotation;
			}
			return field.getAnnotation(annotationClass);
		}

		public boolean isIgnored() {
			final Ignore ignoreAnnotation = getAnnotation(this.field, this.getter, Ignore.class);
			return ignoreAnnotation != null;
		}
		
		public Class<? extends AttributeEncoder<?>> getEncoderClass() {
			final EncodeWith encodeWithAnnotation = getAnnotation(this.field, this.getter, EncodeWith.class);
			if (encodeWithAnnotation != null)
				return encodeWithAnnotation.value();
			return defaultAttributeEncoders.get(this.field.getType());
		}

		public AttributeEncoder<?> newEncoder() {
			final Class<? extends AttributeEncoder<?>> encoderClass = getEncoderClass();
			if (encoderClass == null)
				return null;
			
			try {
				return encoderClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				return null;
			}
		}

		public String getFieldName() {
			return field.getName();
		}
	}
	
	private static class Attribute {
		private final Accessor accessor;
		private final AttributeEncoder<?> encoder;
		
		public Attribute(final Accessor accessor, final AttributeEncoder<?> encoder) {
			this.accessor = accessor;
			this.encoder = encoder;
		}

		public Accessor getAccessor() {
			return accessor;
		}

		public AttributeEncoder<?> getEncoder() {
			return encoder;
		}
		
		@Override
		public String toString() {
			return accessor.toString();
		}
	}

	private final ArrayList<Attribute> attributes;
	private final SequencialIdMapper<Pair<String, Object>> idMapper = new SequencialIdMapper<Pair<String, Object>>();
	private final boolean sparse;
	
	public BeanEncoder(final Class<T> klass, final boolean sparse) {
		this.sparse = sparse;
		this.attributes = new ArrayList<Attribute>();
		for (final Field field : klass.getFields()) {
			final Accessor accessor = new Accessor(klass, field.getName());
			if (accessor.isIgnored())
				continue;
			
			final AttributeEncoder<?> encoder = accessor.newEncoder();
			if (encoder == null)
				throw new RuntimeException(String.format("Unencodable attribute %s", accessor));
			
			this.attributes.add(new Attribute(accessor, encoder));
		}
	}
	
	private class ContextImpl implements Context {
		private final String fieldName;
		private Vector vector;
		
		public ContextImpl(final Accessor accessor, final Vector vector) {
			this.fieldName = accessor.getFieldName();
			this.vector = vector;
		}

		@Override
		public void setValue(final Object obj, final double value) {
			final int id = idMapper.map(Pair.make(fieldName, obj));
			vector.set(id, value);
		}
		
		@Override
		public double getValue(final Object obj) {
			final int id = idMapper.get(Pair.make(fieldName, obj));
			if (id < 0)
				return 0.0;
			return vector.get(id);
		}
	}

	@Override
	public Vector encode(final T record) {
		final Vector result = new SparseMapVector();
		for (final Attribute attribute : attributes) {
			try {
				final Object value = attribute.getAccessor().getValue(record);
				
				@SuppressWarnings("unchecked")
				final AttributeEncoder<Object> encoder = (AttributeEncoder<Object>) attribute.getEncoder();
				
				encoder.encode(new ContextImpl(attribute.getAccessor(), result), value);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
		if (sparse)
			return result;
		return new DenseArrayVector(result);
	}
	
	public Pair<String, Object> describeVectorIndex(final int id) {
		if (id < idMapper.size())
			return idMapper.reverse(id);
		return null;
	}

	public int currentVectorSize() {
		return idMapper.size();
	}
}