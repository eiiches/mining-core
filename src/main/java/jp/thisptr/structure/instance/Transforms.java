package jp.thisptr.structure.instance;


public final class Transforms {
	private Transforms() { }
	
//	public static <InstanceType extends Instance<SparseMapVector>> void idf(final List<InstanceType> instances) {
//		final int n = instances.size();
//		final int dim = Instances.getDimension(instances);
//
//		// count df(q) first
//		final double[] df = new double[dim];
//		for (final InstanceType instance : instances)
//			instance.getVector().accept(new Vector.Visitor() {
//				public void visit(final int index, final double value) {
//					df[index] += value;
//				}
//			});
//
//		// update values
//		for (int i = 0; i < instances.size(); ++i) {
//			final InstanceType instance = instances.get(i);
//			final SparseMapVector transformed = new SparseMapVector();
//			instance.getVector().accept(new Vector.Visitor() {
//				public void visit(int index, double value) {
//					final double idf = Math.log(n / df[index]);
//					transformed.set(index, idf * value);
//				}
//			});
//			instances.set(i, transformed);
//		}
//	}
}