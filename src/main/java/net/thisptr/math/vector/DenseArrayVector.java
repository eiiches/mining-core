package net.thisptr.math.vector;

public class DenseArrayVector extends AbstractVector {
	private double[] buf;
	private VectorShape shape;
	private int size;

	public DenseArrayVector(final int size) {
		this(size, VectorShape.Column);
	}

	public DenseArrayVector(final int size, final VectorShape shape) {
		this.buf = new double[size];
		this.shape = shape;
		this.size = size;
	}

	public DenseArrayVector(final int size, final VectorShape shape, final double[] initializer) {
		this(size, shape);
		final int isize = Math.min(initializer.length, size);
		for (int i = 0; i < isize; ++i)
			this.buf[i] = initializer[i];
	}

	public DenseArrayVector(final Vector v) {
		this(v.size(), v.shape());
		v.walk(new VectorVisitor() {
			@Override
			public void visit(int index, double value) {
				buf[index] = value;
			}
		});
	}

	private DenseArrayVector(final int size, final VectorShape shape, final double[] buf, final boolean dummy) {
		this.buf = buf;
		this.shape = shape;
		this.size = size;
	}

	public double[] raw() {
		return buf;
	}

	public static DenseArrayVector wrap(final int size, final VectorShape shape, final double[] v) {
		return new DenseArrayVector(size, shape, v, false);
	}

	@Override
	public double get(final int index) {
		return buf[index];
	}

	@Override
	public void set(final int index, final double value) {
		buf[index] = value;
	}

	@Override
	public int size() {
		return buf.length;
	}

	@Override
	public void resize(final int size) {
		final double[] newarray = new double[size];
		System.arraycopy(this.buf, 0, newarray, 0, Math.min(this.buf.length, size));
		this.buf = newarray;
	}

	@Override
	public int capacity() {
		return buf.length;
	}

	@Override
	public double walk(final VectorVisitor visitor) {
		for (int i = 0; i < buf.length; ++i)
			if (buf[i] != 0.0)
				visitor.visit(i, buf[i]);
		return visitor.finish();
	}

	@Override
	public VectorShape shape() {
		return shape;
	}

	@Override
	public Vector transpose() {
		return wrap(size, shape.transpose(), buf);
	}

	@Override
	public Vector column(int column) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Vector row(int row) {
		throw new UnsupportedOperationException();
	}
}