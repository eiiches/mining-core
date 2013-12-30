package net.thisptr.structure.instance;

public class LabeledInstance<IdType, VectorType, LabelType> extends Instance<IdType, VectorType> {
	private final LabelType label;

	public LabeledInstance(final VectorType vector, final LabelType label) {
		this(null, vector, label);
	}

	public LabeledInstance(final IdType id, final VectorType vector, final LabelType label) {
		super(id, vector);
		this.label = label;
	}

	public LabelType getLabel() {
		return label;
	}
}