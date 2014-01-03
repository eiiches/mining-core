mining-core
===========

A data mining library in java.

WARNING: This library is still in alpha, and the APIs can be drastically changed.

Usage
-----

If you use maven, add the following snippet for your pom.
```xml
<repositories>
	<repository>
		<id>thisptr.net</id>
		<name>thisptr.net</name>
		<url>http://nexus.thisptr.net/content/groups/public</url>
	</repository>
</repositories>
<dependency>
	<groupId>net.thisptr</groupId>
	<artifactId>mining-core</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

Implemented Data Structure and Algorithm
----------------------------------------

### Math

- Vector and Matrix
  - Pure-java implementation
  - ByteBuffer-backed implementations, which can achieve native performance if used with [mining-core-native](https://github.com/eiiches/mining-core-native)

- Probability distributions
  - Gaussian distribution
  - Exponential distribution
  - Uniform distribution
  - Gibbs multivariate distrubution

- Special functions

- Optimizers
  - Steepest Descent
  - Limited Memory BFGS (optinally with a L1 regularizer)
  - Line search

### Classifiers

- Online Learning
  - Perceptron
  - Passive Aggressive
  - Confidence Weighted
  - AROW
  - Soft-Confidence Weighted

- Batch Learning
  - Logistic Regression

- Evaluation
  - Cross Validation

### Neural Networks

- Restricted Boltzmann Machine

### Bandit Algorithms

- UCB1
- KL-UCB
- epsilon-Greedy
- Softmax

### Hashing

- MurMurHash
- Locality Sensitive Hashing
  - SimHash

### Graph

- Random Walk with Restart

### String algorithms

- Pattern Matching
  - Wu-Manber

- Suffix Arrays
  - SAIS, linear time suffix array construction algorithm
  - Maximal substring extraction

- Burrows Wheeler Transform

### Others

- Utilities for building feature vectors
  - IdMapper<T> interface
- Tokenizers
  - N-gram
- Enumerator interface like java8 Stream (will be removed, once java8 is released)
- Useful structures for machine learning
  - ScoredItem
  - Range
  - Instance/LabeledInstance
- Union-Find
- etc.


