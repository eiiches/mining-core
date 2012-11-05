package jp.thisptr._broken;

public class Instance<WordType, CategoryType> {
	private final SparseVector<WordType> document;
	private final CategoryType category;
	public Instance(final SparseVector<WordType> document, final CategoryType category) {
		this.document = document;
		this.category = category;
	}
	public SparseVector<WordType> getDocument() {
		return document;
	}
	public CategoryType getCategory() {
		return category;
	}
}