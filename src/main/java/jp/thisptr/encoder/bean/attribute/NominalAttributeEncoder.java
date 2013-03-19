package jp.thisptr.encoder.bean.attribute;


public class NominalAttributeEncoder implements AttributeEncoder<Object> {
	@Override
	public void encode(final Context context, final Object value) {
		context.setValue(value, 1.0);
	}
}
