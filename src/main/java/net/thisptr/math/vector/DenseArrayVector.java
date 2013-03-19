package net.thisptr.math.vector;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DenseArrayVector extends DenseVector {
	private final double[] array;
	
	public DenseArrayVector(final Vector src) {
		final int size = src.size();
		array = new double[size];
		for (final Vector.Element e : src)
			array[e.index()] = e.value();
	}
	
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
	public void walk(final Visitor visitor) {
		for (int i = 0; i < array.length; ++i)
			if (array[i] != 0.0)
				visitor.visit(i, array[i]);
	}
	
	public class DenseArrayVectorElement implements Element {
		private final int index;
		
		public DenseArrayVectorElement(final int index) {
			this.index = index;
		}
		
		@Override
		public int index() {
			return index;
		}

		@Override
		public double value() {
			return array[index];
		}
	}

	@Override
	public Iterator<Element> iterator() {
		return new Iterator<Element>() {
			private int index = -1;
			private int pindex = -1;
			
			private int proceed(final int index) {
				int i = index;
				for (++i; i < array.length; ++i) {
					if (array[i] != 0.0)
						break;
				}
				return i;
			}
			
			@Override
			public boolean hasNext() {
				if (index < 0)
					index = proceed(index);
				return index < array.length;
			}

			@Override
			public Element next() {
				if (!hasNext())
					throw new NoSuchElementException();
				pindex = index;
				final Element result = new DenseArrayVectorElement(pindex);
				index = proceed(index);
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