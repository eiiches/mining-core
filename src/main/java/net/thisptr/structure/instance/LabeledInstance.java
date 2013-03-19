package net.thisptr.structure.instance;

public class LabeledInstance<VectorType, LabelType> extends Instance<VectorType> {
	private final LabelType label;
	public LabeledInstance(final VectorType vector, final LabelType label) {
		this(-1, vector, label);
	}
	public LabeledInstance(final long id, final VectorType vector, final LabelType label) {
		super(id, vector);
		this.label = label;
	}
	public LabelType getLabel() {
		return label;
	}
}