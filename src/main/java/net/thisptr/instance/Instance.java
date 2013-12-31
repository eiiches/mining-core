package net.thisptr.instance;

public class Instance<IdType, VectorType> {
	private final VectorType vector;
	private final IdType id;

	public Instance(final VectorType vector) {
		this(null, vector);
	}

	public Instance(final IdType id, final VectorType vector) {
		this.id = id;
		this.vector = vector;
	}

	public VectorType getVector() {
		return vector;
	}

	public IdType getId() {
		return id;
	}
}