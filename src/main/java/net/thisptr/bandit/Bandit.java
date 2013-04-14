package net.thisptr.bandit;

import java.util.ArrayList;
import java.util.List;

public class Bandit<T> {
	private List<T> arms = new ArrayList<T>();
	
	public Bandit() { }
	
	@SafeVarargs
	public Bandit(final T... arms) {
		for (final T arm : arms)
			this.arms.add(arm);
	}

	public List<T> getArms() {
		return arms;
	}

	public void setArms(final List<T> arms) {
		this.arms = arms;
	}

	public void addArm(final T arm) {
		arms.add(arm);
	}
	
	public int size() {
		return arms.size();
	}
}
