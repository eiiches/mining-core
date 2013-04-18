package net.thisptr.bandit;

public class BanditArm<T> {
	private final T item;
	private int rewardCount;
	private int pullCount;
	
	public BanditArm(final T item) {
		this(item, 1, 1);
	}

	public BanditArm(final T item, final int rewardCount, final int pullCount) {
		this.item = item;
		this.rewardCount = rewardCount;
		this.pullCount = pullCount;
	}

	public T getItem() {
		return item;
	}

	public int getRewardCount() {
		return rewardCount;
	}

	public int getPullCount() {
		return pullCount;
	}
	
	public void setRewardCount(final int rewardCount) {
		this.rewardCount = rewardCount;
	}

	public void setPullCount(final int pullCount) {
		this.pullCount = pullCount;
	}
	
	/**
	 * Increment pull count.
	 */
	public void pull() {
		++pullCount;
	}
	
	/**
	 * Increment reward count.
	 */
	public void reward() {
		++rewardCount;
	}
}
