package jp.thisptr.instance;

import java.util.List;

import jp.thisptr.math.structure.vector.Vector;

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