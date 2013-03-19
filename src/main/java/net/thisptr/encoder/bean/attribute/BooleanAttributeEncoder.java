package net.thisptr.encoder.bean.attribute;


public class BooleanAttributeEncoder implements AttributeEncoder<Boolean> {
	@Override
	public void encode(final AttributeEncoder.Context context, final Boolean value) {
		context.setValue(null, value ? 1.0 : 0.0);
	}
}