package jp.thisptr.core.lambda;

public abstract class Lambda2<R, A1, A2> extends LambdaBase<R, Lambda2<R, A1, A2>> {
	public abstract R invoke(A1 arg1, A2 arg2);
	
	public <ChainResultType> Lambda2<ChainResultType, A1, A2> chain(final Lambda1<ChainResultType, ? super R> g) {
		final Lambda2<R, A1, A2> dependant = this;
		return new Lambda2<ChainResultType, A1, A2>() {
			public ChainResultType invoke(final A1 arg1, final A2 arg2) {
				return g.invoke(dependant.invoke(arg1, arg2));
			}
		};
	}
	
	public Lambda1<R, A2> bind(final A1 arg1) {
		final Lambda2<R, A1, A2> original = this;
		return new Lambda1<R, A2>() {
			public R invoke(final A2 arg2) {
				return original.invoke(arg1, arg2);
			}
		};
	}
}
