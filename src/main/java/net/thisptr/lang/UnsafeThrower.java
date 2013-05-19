package net.thisptr.lang;

public class UnsafeThrower {
	public static void doThrow(final Exception e) {
		UnsafeThrower.<RuntimeException> doThrow0(e);
	}
	
	@SuppressWarnings("unchecked")
	private static <E extends Exception> void doThrow0(final Exception e) throws E {
		throw (E) e;
	}
}