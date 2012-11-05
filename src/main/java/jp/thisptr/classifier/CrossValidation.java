package jp.thisptr.classifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.thisptr.classifier.logisticregression.BinaryLogisticRegression;
import jp.thisptr.core.tuple.Pair;
import jp.thisptr.math.vector.d.Vector;

public class CrossValidation {
	private BinaryLogisticRegression classifier;
	private List<Pair<Vector, Boolean>> dataset;
	
	public CrossValidation(final BinaryLogisticRegression classifier, final List<Pair<Vector, Boolean>> dataset) {
		this.classifier = classifier;
		this.dataset = new ArrayList<Pair<Vector, Boolean>>(dataset);
	}
	
	public ConfusionMatrix<Boolean> fold(final int n) {
		final ConfusionMatrix<Boolean> cv = new ConfusionMatrix<Boolean>();
		final List<Pair<Vector, Boolean>> dataset = new ArrayList<Pair<Vector, Boolean>>(this.dataset);
		Collections.shuffle(dataset);
		for (int leave = 0; leave < n; ++leave) {
			final List<Pair<Vector, Boolean>> learnset = new ArrayList<Pair<Vector, Boolean>>();
			final List<Pair<Vector, Boolean>> testset = new ArrayList<Pair<Vector, Boolean>>();
			final double blockSize = dataset.size() / (double) n;
			final double begin = blockSize * leave;
			final double end = blockSize * (leave + 1);
			for (int i = 0; i < dataset.size(); ++i) {
				if (begin <= i && i < end) {
					testset.add(dataset.get(i));
					continue;
				}
				learnset.add(dataset.get(i));
			}
			classifier.learn(learnset);
			for (final Pair<Vector, Boolean> d : testset) {
				boolean predicted = classifier.predict(d.getFirst());
				cv.add(d.getSecond(), predicted);
			}
		}
		return cv;
	}
	
	public ConfusionMatrix<Boolean> loocv() {
		final ConfusionMatrix<Boolean> cv = new ConfusionMatrix<Boolean>();
		for (int leave = 0; leave < dataset.size(); ++leave) {
			final List<Pair<Vector, Boolean>> learnset = new ArrayList<Pair<Vector, Boolean>>();
			final List<Pair<Vector, Boolean>> testset = new ArrayList<Pair<Vector, Boolean>>();
			for (int i = 0; i < dataset.size(); ++i) {
				if (leave == i) {
					testset.add(dataset.get(i));
					continue;
				}
				learnset.add(dataset.get(i));
			}
			classifier.learn(learnset);
			for (final Pair<Vector, Boolean> d : testset) {
				boolean predicted = classifier.predict(d.getFirst());
				cv.add(d.getSecond(), predicted);
			}
		}
		return cv;
	}
}