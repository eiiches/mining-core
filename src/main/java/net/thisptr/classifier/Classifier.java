package net.thisptr.classifier;

public interface Classifier<VectorType, ClassType> {
	ClassType classify(final VectorType x);
}