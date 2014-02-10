package net.thisptr.math.vector;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;


public class DenseByteBufferVectorTest {
	
	@Test
	public void testByteBufferVector_int() {
		final DenseByteBufferVector bbv = new DenseByteBufferVector(100);
		bbv.set(0, 1.0);
		bbv.set(99, 1.0);
		assertEquals(1.0, bbv.get(0), 0.0);
		assertEquals(1.0, bbv.get(99), 0.0);
		
		// values may not be initialized to zero.
		// assertEquals(0.0, bbv.get(1), 0.0);
	}
	
	@Test
	public void testByteBufferVector_ByteBuffer() {
		final DenseByteBufferVector bbv = DenseByteBufferVector.wrap(100, VectorShape.Column, ByteBuffer.wrap(new byte[800]));
		bbv.set(0, 1.0);
		bbv.set(99, 1.0);
		assertEquals(1.0, bbv.get(0), 0.0);
		assertEquals(1.0, bbv.get(99), 0.0);
	}
	
	@Test
	public void testByteBufferVector_Vector() {
		final DenseByteBufferVector bbv = new DenseByteBufferVector(new DenseArrayVector(2, VectorShape.Column, new double[] { 1.0, 2.0 }));
		assertEquals(1.0, bbv.get(0), 0.0);
		assertEquals(2.0, bbv.get(1), 0.0);
	}
}
