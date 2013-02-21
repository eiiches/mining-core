package jp.thisptr.dataset;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import jp.thisptr.structure.dataframe.DataFrame;
import jp.thisptr.util.IOGenerators;

import org.apache.commons.io.IOUtils;


/**
 * Fischer's iris data set.
 * http://en.wikipedia.org/wiki/Iris_flower_data_set
 */
public class Iris extends DataFrame {
	public static enum Species {
		Setosa,
		Versicolor,
		Virginica
	}
	
	public final ColumnDef<Double> columnSepalLength = new ColumnDefNumerical("sepalLength");
	public final ColumnDef<Double> columnSepalWidth = new ColumnDefNumerical("sepalWidth");
	public final ColumnDef<Double> columnPetalLength = new ColumnDefNumerical("petalLength");
	public final ColumnDef<Double> columnPetalWidth = new ColumnDefNumerical("petalWidth");
	public final ColumnDef<Species> columnSpecies = new ColumnDefNominal<Species>("species");
	
	private static final Pattern TAB = Pattern.compile("\t");
	
	public Iris() {
		try (final InputStream is = getClass().getClassLoader().getResourceAsStream("dataset/iris.dat")) {
			for (final String line : IOGenerators.readLines(is).skip(1)) {
				final DataFrame.RowView row = addRow();
				final String[] values = TAB.split(line);
				row.setValue(columnSepalLength, Double.valueOf(values[0]));
				row.setValue(columnSepalWidth, Double.valueOf(values[1]));
				row.setValue(columnPetalLength, Double.valueOf(values[2]));
				row.setValue(columnPetalWidth, Double.valueOf(values[3]));
				row.setValue(columnSpecies, Species.valueOf(values[4]));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}