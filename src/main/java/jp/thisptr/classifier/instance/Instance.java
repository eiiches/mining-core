package jp.thisptr.classifier.instance;

public class Instance<VectorType, CategoryType> {
	private final VectorType vector;
	private final CategoryType label;
	
	public Instance(final VectorType x, final CategoryType label) {
		this.vector = x;
		this.label = label;
	}
	
	public VectorType getVector() {
		return vector;
	}
	
	public CategoryType getLabel() {
		return label;
	}
}