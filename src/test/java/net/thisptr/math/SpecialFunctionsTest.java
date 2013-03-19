package net.thisptr.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SpecialFunctionsTest {
	@Test
	public void testGammLn() {
		assertTrue(Double.isNaN(SpecialFunctions.gammaLn(-1.0)));
		assertTrue(Double.isNaN(SpecialFunctions.gammaLn(0.0)));
		assertEquals(Math.log(99999.42279423), SpecialFunctions.gammaLn(1e-5), 1e-5);
		assertEquals(Math.log(9.51350770), SpecialFunctions.gammaLn(0.1), 1e-5);
		assertEquals(Math.log(1.06862870), SpecialFunctions.gammaLn(0.9), 1e-5);
		assertEquals(Math.log(24), SpecialFunctions.gammaLn(5), 1e-5);
	}
	
	@Test
	public void testRegularizedIncompleteGamma() {
		// normal case 1
		assertEquals(0.32084720, SpecialFunctions.regularizedUpperGamma(3, 3.5), 1e-5);
		assertEquals(1.0 - 0.32084720, SpecialFunctions.regularizedLowerGamma(3, 3.5), 1e-5);
		
		// normal case 2
		assertEquals(0.08837643, SpecialFunctions.regularizedUpperGamma(3, 5.5), 1e-5);
		assertEquals(1.0 - 0.08837643, SpecialFunctions.regularizedLowerGamma(3, 5.5), 1e-5);
		
		// x is zero
		assertEquals(1.0, SpecialFunctions.regularizedUpperGamma(3.0, 0.0), 1e-5);
		assertEquals(1.0 - 1.0, SpecialFunctions.regularizedLowerGamma(3.0, 0.0), 1e-5);
		
		// a is zero
		assertTrue(Double.isNaN(SpecialFunctions.regularizedUpperGamma(0.0, 1.0)));
		assertTrue(Double.isNaN(SpecialFunctions.regularizedLowerGamma(0.0, 1.0)));
		
		// a is negative
		assertTrue(Double.isNaN(SpecialFunctions.regularizedUpperGamma(-1.0, 1.0)));
		assertTrue(Double.isNaN(SpecialFunctions.regularizedLowerGamma(-1.0, 1.0)));
		
		// x is negative
		assertTrue(Double.isNaN(SpecialFunctions.regularizedUpperGamma(1.0, -1.0)));
		assertTrue(Double.isNaN(SpecialFunctions.regularizedLowerGamma(1.0, -1.0)));
	}
	
	@Test
	public void testInvErf() {
		// normal cases
		assertEquals(0.47693628, SpecialFunctions.invErf(0.5), 1e-5);
		assertEquals(0.0, SpecialFunctions.invErf(0.0), 1e-5);
		assertEquals(-0.90619380, SpecialFunctions.invErf(-0.8), 1e-5);
		assertEquals(0.90619380, SpecialFunctions.invErf(0.8), 1e-5);
		assertEquals(1.16308715, SpecialFunctions.invErf(0.9), 1e-5);
		assertEquals(-1.16308715, SpecialFunctions.invErf(-0.9), 1e-5);
		assertEquals(0.00000886, SpecialFunctions.invErf(0.000000000000001), 1e-5);
		assertEquals(4.812924058944831, SpecialFunctions.invErf(0.99999999999), 1e-5);
		
		// at 1.0, positive infinity
		assertTrue(Double.isInfinite(SpecialFunctions.invErf(1.0)));
		assertTrue(SpecialFunctions.invErf(1.0) > 0);
		
		// at -1.0, positive infinity
		assertTrue(Double.isInfinite(SpecialFunctions.invErf(-1.0)));
		assertTrue(SpecialFunctions.invErf(-1.0) < 0);
		
		// otherwise, NaN
		assertTrue(Double.isNaN(SpecialFunctions.invErf(2.0)));
		assertTrue(Double.isNaN(SpecialFunctions.invErf(-2.0)));
	}
}
