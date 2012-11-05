package jp.thisptr.structure.suffixarray;

import static org.junit.Assert.assertArrayEquals;
import jp.thisptr.structure.suffixarray.SuffixArray;
import jp.thisptr.structure.suffixarray.builder.SuffixArrayBuilderNaive;

import org.junit.Test;

public class SuffixArrayTest {

	@Test
	public void test() {
		SuffixArray sa = SuffixArray.build("abracadabra", new SuffixArrayBuilderNaive());
		assertArrayEquals(new int[] {
				10, // a
				 7, // abra
				 0, // abracadabra
				 3, // acadabra
				 5, // adabra
				 8, // bra
				 1, // bracadabra
				 4, // cadabra
				 6, // dabra
				 9, // ra
				 2, // racadabra
		}, sa.getIntArray());
	}

}
