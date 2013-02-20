package jp.thisptr.structure.dataframe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.thisptr.lang.lambda.Lambda1;
import jp.thisptr.lang.tuple.Pair;
import jp.thisptr.math.vector.SparseMapVector;
import jp.thisptr.structure.instance.Instance;
import jp.thisptr.structure.instance.LabeledInstance;
import jp.thisptr.util.SequencialIdMapper;

import org.apache.commons.lang.NotImplementedException;

/**
 * This class represents a dataset, which later can be converted to 
 * <tt>List&lt;Instance&gt;</tt>. While <tt>List&lt;Instance&gt;</tt> is for 
 * datasets that is represented by math vectors (which is ready 
 * to be used in algorithms), this class can store complex columns 
 * from simple numerical columns or nominal columns to any user-defined 
 * columns.
 *
 */
public class DataFrame implements Iterable<DataFrame.RowView> {
	
	/**
	 * Build a vector from DataFrame.
	 *
	 */
	public static class VectorBuilder {
		private ColumnDef<?>[] columns;
		private SequencialIdMapper<Pair<ColumnDef<?>, Object>> idMapper;
		
		public VectorBuilder(final ColumnDef<?>... columns) {
			this.columns = columns;
			this.idMapper = new SequencialIdMapper<>();
		}
		
		public SparseMapVector build(final RowView row) {
			final SparseMapVector result = new SparseMapVector();
			for (final ColumnDef<?> column : columns)
				column.fillVector(result, row, idMapper);
			return result;
		}
	}
	
	/**
	 * A column type definition. This class implements how the column is converted into vectors.
	 * @author eiichi
	 * @param <ValueType>
	 */
	public abstract static class ColumnDef<ValueType> {
		private final String name;

		public ColumnDef(final String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
		@Override
		public String toString() {
			return "ColumnDef [name=" + name + "]";
		}

		public abstract void fillVector(final SparseMapVector vector, final RowView row, final SequencialIdMapper<Pair<ColumnDef<?>, Object>> idMapper);
	}
	
	public static class ColumnDefNominal<ValueType> extends ColumnDef<ValueType> {
		public ColumnDefNominal(final String name) {
			super(name);
		}

		@Override
		public void fillVector(final SparseMapVector vector, final RowView row,
				final SequencialIdMapper<Pair<ColumnDef<?>, Object>> idMapper) {
			final ValueType value = row.getValue(this);
			if (value == null)
				throw new MissingValueException();
			final int index = idMapper.map(new Pair<ColumnDef<?>, Object>(this, value));
			vector.set(index, 1.0);
		}
	}

	public static class ColumnDefNumerical extends ColumnDef<Double> {
		public ColumnDefNumerical(final String name) {
			super(name);
		}

		@Override
		public void fillVector(final SparseMapVector vector, final RowView row,
				final SequencialIdMapper<Pair<ColumnDef<?>, Object>> idMapper) {
			final Double value = row.getValue(this);
			if (value == null)
				throw new MissingValueException();
			final int index = idMapper.map(new Pair<ColumnDef<?>, Object>(this, 0));
			vector.set(index, value);
		}
	}
	
	public static class ColumnDefMultiNominal<ValueType> extends ColumnDef<List<ValueType>> {
		public ColumnDefMultiNominal(final String name) {
			super(name);
		}

		@Override
		public void fillVector(final SparseMapVector vector, final RowView row,
				final SequencialIdMapper<Pair<ColumnDef<?>, Object>> idMapper) {
			throw new NotImplementedException();
		}
	}
	
	/**
	 * A row view.
	 */
	public class RowView {
		private final int index;
		
		public RowView(final int index) {
			this.index = index;
		}
		
		/**
		 * If a not-previously-seen column instance is specified, it will be implicitly added.
		 * @param column
		 * @param value
		 * @return this row instance to support method chaining.
		 */
		public <T> RowView setValue(final ColumnDef<T> column, final T value) {
			@SuppressWarnings("unchecked")
			List<T> values = (List<T>) columnValues.get(column);
			if (values == null) {
				values = new ArrayList<T>();
				columnValues.put(column, values);
			}
			while (values.size() <= index)
				values.add(null);
			values.set(index, value);
			return this;
		}
		
		public <T> T getValue(final ColumnDef<T> column) {
			@SuppressWarnings("unchecked")
			final List<T> values = (List<T>) columnValues.get(column);
			if (values == null || values.size() <= index)
				return null;
			return values.get(index);
		}
		
		public long getId() {
			return indexIds.get(index);
		}
	}
	
	private int size = 0;
	
	/**
	 * A dataframe values, represented as Map<column, List<value>>.
	 */
	private final Map<ColumnDef<?>, List<?>> columnValues = new HashMap<ColumnDef<?>, List<?>>();
	private final List<Long> indexIds = new ArrayList<Long>();

	public DataFrame() { }
	
	public RowView addRow(final long id) {
		final int index = size++;
		indexIds.add(id);
		return new RowView(index);
	}
	
	public RowView addRow() {
		return addRow(-1);
	}

	@Override
	public Iterator<RowView> iterator() {
		return new Iterator<RowView>() {
			private int nextIndex = 0;
			
			@Override
			public boolean hasNext() {
				return nextIndex < size;
			}
			@Override
			public RowView next() {
				return new RowView(nextIndex++);
			}
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	public DataFrame filter(final Lambda1<Boolean, DataFrame.RowView> predicate) {
		final DataFrame result = new DataFrame();
		for (final DataFrame.RowView row : this) {
			if (predicate.invoke(row)) {
				final DataFrame.RowView resultRow = result.addRow(row.getId());
				for (final ColumnDef<?> key : columnValues.keySet()) {
					@SuppressWarnings("unchecked")
					final ColumnDef<Object> column = (ColumnDef<Object>) key;
					final Object value = row.getValue(column);
					if (value != null)
						resultRow.setValue(column, value);
				}
			}
		}
		return result;
	}
	
	public List<Instance<SparseMapVector>> toInstance(final DataFrame.ColumnDef<?>... columns) {
		final DataFrame.VectorBuilder builder = new DataFrame.VectorBuilder(columns);
		final List<Instance<SparseMapVector>> result = new ArrayList<>();
		for (final DataFrame.RowView row : this) {
			final SparseMapVector vector = builder.build(row);
			result.add(new Instance<SparseMapVector>(vector));
		}
		return result;
	}
	
	public <LabelType> List<LabeledInstance<SparseMapVector, LabelType>> toLabeledInstance(final Lambda1<LabelType, DataFrame.RowView> labeler, final DataFrame.ColumnDef<?>... columns) {
		final DataFrame.VectorBuilder builder = new DataFrame.VectorBuilder(columns);
		final List<LabeledInstance<SparseMapVector, LabelType>> result = new ArrayList<>();
		for (final DataFrame.RowView row : this) {
			final LabelType label = labeler.invoke(row);
			final SparseMapVector vector = builder.build(row);
			result.add(new LabeledInstance<SparseMapVector, LabelType>(vector, label));
		}
		return result;
	}
}