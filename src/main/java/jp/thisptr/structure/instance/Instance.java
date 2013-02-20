package jp.thisptr.structure.instance;

public class Instance<VectorType> {
	private final VectorType vector;
	private final long id;
	public Instance(final VectorType vector) {
		this(-1, vector);
	}
	public Instance(final long id, final VectorType vector) {
		this.id = id;
		this.vector = vector;
	}
	public VectorType getVector() {
		return vector;
	}
	public long getId() {
		return id;
	}
}