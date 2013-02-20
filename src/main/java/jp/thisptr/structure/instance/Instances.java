package jp.thisptr.structure.instance;

import java.util.List;

import jp.thisptr.math.vector.Vector;

public final class Instances {
	private Instances() { }
	
	public static <InstanceType extends Instance<? extends Vector>> int getDimension(final List<InstanceType> instances) {
		int result = 0;
		for (final InstanceType instance : instances) {
			final int size = instance.getVector().size();
			if (size > result)
				result = size;
		}
		return result;
	}
}