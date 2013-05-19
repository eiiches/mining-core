package net.thisptr.string.matcher;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public final class WuManberPattern implements StringPattern {
	public static final int DEFAULT_BLOCK_SIZE = 3;
	public static final int DEFAULT_TABLE_SIZE = 8192;
	
	private int blockSize;
	private int defaultShift;
	private int lmin;
	private int lmax;
	
	private Int2IntMap shiftTable;
	private Int2ObjectMap<List<String>> hashTable;
	
	private int hash(final String block) {
		// FIXME: should use more appropreate hash function.
		return block.hashCode();
	}

	private WuManberPattern(final Collection<String> patterns) {
		if (patterns.isEmpty())
			throw new IllegalArgumentException("Specify at least one pattern.");
		
		this.lmax = 0;
		this.lmin = Integer.MAX_VALUE;
		for (final String p : patterns) {
			final int l = p.length();
			if (l == 0)
				continue;
			
			if (l > lmax) lmax = l;
			if (l < lmin) lmin = l;
		}
		
		this.blockSize = Math.min(lmin, DEFAULT_BLOCK_SIZE);
		this.defaultShift = lmin - blockSize + 1;
		
		this.shiftTable = new Int2IntOpenHashMap(DEFAULT_TABLE_SIZE);
		this.shiftTable.defaultReturnValue(defaultShift);
		for (final String p : patterns) {
			final int l = p.length();
			for (int j = 0; j < l - blockSize + 1; ++j) {
				final int shift = l - (j + blockSize);
				final int key = hash(p.substring(j, j + blockSize));
				final int current = this.shiftTable.get(key);
				if (current > shift)
					this.shiftTable.put(key, shift);
			}
		}
		
		this.hashTable = new Int2ObjectOpenHashMap<List<String>>(DEFAULT_TABLE_SIZE);
		this.hashTable.defaultReturnValue(null);
		for (final String p : patterns) {
			final int key = hash(p.substring(p.length() - blockSize, p.length()));
			List<String> ps = this.hashTable.get(key);
			if (ps == null) {
				ps = new ArrayList<String>();
				this.hashTable.put(key, ps);
			}
			ps.add(p);
		}
	}
	
	private class WuManberMatcher implements StringMatcher {
		private final CharSequence seq;
		
		private int i = blockSize;
		private Queue<String> toVerify = null;
		
		private int at = -1;
		private String text = null;
		
		public WuManberMatcher(final CharSequence seq) {
			this.seq = seq;
		}
		
		private boolean verifyMatch(final int beginIndex, final String p) {
			if (beginIndex < 0)
				return false;
			
			for (int i = 0; i < p.length(); ++i)
				if (seq.charAt(beginIndex + i) != p.charAt(i))
					return false;
			return true;
		}

		@Override
		public boolean find() {
			if (toVerify != null) {
				while (!toVerify.isEmpty()) {
					final String p = toVerify.remove();
					if (verifyMatch(i - p.length(), p)) {
						at = i - p.length();
						text = p;
						return true;
					}
				}
				++i;
				toVerify = null;
			}
			
			while (i <= seq.length()) {
				final int key = hash(seq.subSequence(i - blockSize, i).toString());
				final int shift = shiftTable.get(key);
				if (shift == 0) {
					toVerify = new LinkedList<String>(hashTable.get(key));
					return find();
				}
				i += shift;
			}
			
			at = -1;
			text = null;
			return false;
		}

		@Override
		public int at() {
			if (at < 0)
				throw new IllegalStateException("This method can be called only when previous invocation of find() returns true");
			return at;
		}

		@Override
		public String text() {
			if (text == null)
				throw new IllegalStateException("This method can be called only when previous invocation of find() returns true");
			return text;
		}
	}
	
	public static WuManberPattern compile(final Collection<String> patterns) {
		return new WuManberPattern(patterns);
	}

	@Override
	public StringMatcher matcher(final CharSequence seq) {
		return new WuManberMatcher(seq);
	}
}
