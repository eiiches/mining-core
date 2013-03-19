package jp.thisptr.encoder.bean.attribute;


public class NumericalAttributeEncoder implements AttributeEncoder<Number> {
	@Override
	public void encode(final AttributeEncoder.Context context, final Number value) {
		context.setValue(null, value.doubleValue());
	}
}