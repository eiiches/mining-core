package jp.thisptr.math.vector;

import java.util.Arrays;
import java.util.Iterator;

public class DenseArrayVector extends DenseVector {
	private final double[] array;
	
	public DenseArrayVector(final int dimension) {
		array = new double[dimension];
	}
	
	public DenseArrayVector(final double... values) {
		array = Arrays.copyOf(values, values.length);
	}
	
	private DenseArrayVector(final double[] v, final boolean dummy) {
		array = v;
	}
	
	public static DenseArrayVector wrap(final double[] v) {
		return new DenseArrayVector(v, true);
	}
	
	@Override
	public double get(final int index) {
		return array[index];
	}
	
	@Override
	public void set(final int index, final double value) {
		array[index] = value;
	}
	
	@Override
	public int size() {
		return array.length;
	}
	
	@Override
	public int capacity() {
		return array.length;
	}

	@Override
	public void accept(final Visitor visitor) {
		for (int i = 0; i < array.length; ++i)
			if (array[i] != 0.0)
				visitor.visit(i, array[i]);
	}

	@Override
	public Iterator<Element> iterator() {
		return new Iterator<Element>() {
			private int index = 0;
			private int pindex = -1;
			
			@Override
			public boolean hasNext() {
				return index < array.length;
			}

			@Override
			public Element next() {
				pindex = index;
				final Element result = new Element() {
					@Override
					public int index() {
						return pindex;
					}

					@Override
					public double value() {
						return array[index];
					}
				};
				for (; index < array.length; ++index) {
					if (array[index] != 0.0)
						break;
				}
				return result;
			}

			@Override
			public void remove() {
				if (pindex < 0)
					throw new IllegalStateException();
				array[pindex] = 0.0;
			}
		};
	}
}