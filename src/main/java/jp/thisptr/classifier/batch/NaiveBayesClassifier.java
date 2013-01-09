package jp.thisptr.classifier.batch;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jp.thisptr.classifier.instance.Instances;
import jp.thisptr.classifier.BatchLearner;
import jp.thisptr.core.collection.DefaultMap;
import jp.thisptr.core.generator.Generators;
import jp.thisptr.core.lambda.Lambda0;
import jp.thisptr.core.lambda.alias.Lambda;
import jp.thisptr.core.lambda.util.Lambdas;
import jp.thisptr.core.util.Cache;
import jp.thisptr.core.util.CollectionUtils;
import jp.thisptr.math.structure.vector.SparseMapVector;
import jp.thisptr.math.structure.vector.SparseVector;

public class NaiveBayesClassifier<CategoryType> extends BatchLearner<SparseMapVector, CategoryType, Instances<SparseMapVector, CategoryType>> implements Serializable {
	private static final long serialVersionUID = 8569021296844788072L;
	
	private Set<WordType> vocabularies = new HashSet<WordType>();
	private Map<CategoryType, DefaultMap<WordType, Integer>> wordCount
		= CollectionUtils.defualtMap(new HashMap<CategoryType, DefaultMap<WordType, Integer>>(), new Lambda0<DefaultMap<WordType, Integer>>() {
			public DefaultMap<WordType, Integer> invoke() {
				return CollectionUtils.defualtMap(new HashMap<WordType, Integer>(), 0);
			}
	});
	
	private Map<CategoryType, Integer> categoryCount = CollectionUtils.defualtMap(new HashMap<CategoryType, Integer>(), 0);
	
	private Cache<Map<CategoryType, Integer>> denominator = new Cache<>(new Lambda0<Map<CategoryType, Integer>>() {
		public Map<CategoryType, Integer> invoke() {
			Map<CategoryType, Integer> result = new HashMap<CategoryType, Integer>();
			for (Map.Entry<CategoryType, DefaultMap<WordType, Integer>> entry : wordCount.entrySet()) {
				Map<WordType, Integer> wordCategoryCount = entry.getValue();
				int sum = CollectionUtils.sum(wordCategoryCount.values())
						// + wordCategoryCount.size();
						+ vocabularies.size();
				result.put(entry.getKey(), sum);
			}
			return result;
		}
	});
	
	public void train(final SparseVector sv, final CategoryType category) {
		vocabularies.addAll(sv.indices());

		categoryCount.put(category, categoryCount.get(category) + 1);
		
		Map<WordType, Integer> wordCategoryCount = wordCount.get(category);
		for (Map.Entry<WordType, Integer> w : sv.entrySet())
			wordCategoryCount.put(w.getKey(), wordCategoryCount.get(w.getKey()) + w.getValue());

		denominator.invalidate();
	}
	
	public CategoryType classify(final SparseVector<WordType> document) {
		return CollectionUtils.maximize(categoryCount.keySet(), new Lambda<Double, CategoryType>() {
			public Double invoke(final CategoryType category) {
				return log_P_cat_doc(document, category);
			}
		});
	}
	
	public double P_word_cat(final WordType word, final CategoryType category) {
		Integer count = wordCount.get(category).find(word);
		if (count == null)
			count = 0;
		return (count + 1) / (double)denominator.get().get(category);
	}

	public double log_P_cat_doc(final SparseVector<WordType> document, final CategoryType category) {
		double total = CollectionUtils.sum(categoryCount.values());
		return Generators.array(document.keySet()).map(new Lambda<Double, WordType>() {
			public Double invoke(final WordType word) {
				return Math.log(P_word_cat(word, category));
			}
		}).foldl(Lambdas.add(Double.class), Math.log(categoryCount.get(category) / total)).eval();
	}
	
	public void save(final String filename) throws IOException {
		// SerializationUtils.saveObject(this, new File(filename));
	}
	
	public static <WordType, CategoryType> NaiveBayesClassifier<WordType, CategoryType> load(final String filename) throws IOException, ClassNotFoundException {
		// return SerializationUtils.loadObject(new File(filename));
		return null;
	}
}
