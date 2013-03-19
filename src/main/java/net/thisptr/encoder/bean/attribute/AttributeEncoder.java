package net.thisptr.encoder.bean.attribute;

public interface AttributeEncoder<T> {
	public interface Context {
		void setValue(final Object obj, final double value);
		double getValue(Object obj);
	}
	void encode(final Context context, final T record);
}