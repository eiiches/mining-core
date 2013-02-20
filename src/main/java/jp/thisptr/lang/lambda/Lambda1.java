package jp.thisptr.lang.lambda;

public abstract class Lambda1<R, A1> extends LambdaBase<R, Lambda1<R, A1>> {
	public abstract R invoke(A1 arg1);
	
	public <ChainResultType> Lambda1<ChainResultType, A1> chain(final Lambda1<ChainResultType, ? super R> g) {
		final Lambda1<R, A1> dependant = this;
		return new Lambda1<ChainResultType, A1>() {
			public ChainResultType invoke(final A1 arg1) {
				return g.invoke(dependant.invoke(arg1));
			}
		};
	}
	
	public Lambda0<R> bind(final A1 arg1) {
		final Lambda1<R, A1> original = this;
		return new Lambda0<R>() {
			public R invoke() {
				return original.invoke(arg1);
			}
		};
	}
}
