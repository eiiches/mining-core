package jp.thisptr.lang.lambda;

public abstract class Lambda0<R> {
	public abstract R invoke();

	public <ChainResultType> Lambda0<ChainResultType> chain(final Lambda1<ChainResultType, ? super R> g) {
		final Lambda0<R> dependant = this;
		return new Lambda0<ChainResultType>() {
			public ChainResultType invoke() {
				return g.invoke(dependant.invoke());
			}
		};
	}
}
