package net.thisptr.string.matcher;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class WuManberTest {
	
	private static class MatchResult {
		public final int at;
		public final String text;
		
		public MatchResult(final String text, final int at) {
			this.text = text;
			this.at = at;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + at;
			result = prime * result + ((text == null) ? 0 : text.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MatchResult other = (MatchResult) obj;
			if (at != other.at)
				return false;
			if (text == null) {
				if (other.text != null)
					return false;
			} else if (!text.equals(other.text))
				return false;
			return true;
		}
	}
	
	private static class Fixture {
		public final String[] needles;
		public final String haystack;
		public final MatchResult[] results;
		public Fixture(final String[] needles, final String haystack, final MatchResult[] results) {
			super();
			this.needles = needles;
			this.haystack = haystack;
			this.results = results;
		}
	}
	
	public static MatchResult r(final String text, final int at) {
		return new MatchResult(text, at);
	}
	
	@DataPoint
	public static final Fixture FIXTURE_1 = new Fixture(
			new String[] { "announce", "annual", "annually" },
			"CPM_annual_conference_announce",
			new MatchResult[] {
					r("annual", 4),
					r("announce", 22),
			});
	
	@DataPoint
	public static final Fixture FIXTURE_2 = new Fixture(
			new String[] { "hoge", "hoge1" },
			"hoge1",
			new MatchResult[] {
					r("hoge", 0),
					r("hoge1", 0),
			});
	
	@DataPoint
	public static final Fixture FIXTURE_3 = new Fixture(
			new String[] { "hoge", "hogehoge" },
			"hogehoge",
			new MatchResult[] {
					r("hoge", 0),
					r("hogehoge", 0),
					r("hoge", 4),
			});

	@Theory
	public void test(final Fixture fixture) {
		final List<String> needles = Arrays.asList(fixture.needles);
		final StringPattern pattern = WuManber.compile(needles);
		final StringMatcher matcher = pattern.matcher(fixture.haystack);
		
		final Set<MatchResult> actual = new HashSet<MatchResult>();
		while (matcher.find())
			actual.add(new MatchResult(matcher.text(), matcher.at()));
		
		final Set<MatchResult> expected = new HashSet<MatchResult>(Arrays.asList(fixture.results));
		assertEquals(expected, actual);
	}

}
