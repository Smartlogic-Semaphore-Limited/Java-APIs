package com.smartlogic.classificationserver.client;

import junit.framework.TestCase;

import java.util.*;

import static java.util.Collections.sort;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by stevenbiondi on 1/9/14.
 * <p/>
 * We found a bug out in the wild caused by bad compareTo politics in the
 * ClassificationScore object. The compareTo implementation was based
 * upon only the tuple [name,score] to be unique for all objects
 * which is not correct. There are cases where name and score are identical
 * but CS returns a term ID as well, and the two objects should not be equal.
 * There's also going to be cases where IDs are the same, but names are different.
 * <p/>
 * This showed up because we use a TreeSet<ClassificationScore> instance
 * to store the scores.
 * This set type requires the Comparable<ClassificationScore>.compareTo method
 * to order objects within the tree (TreeMap).
 * <p/>
 * So we need to take into account [score, id, name, rulebaseClass]
 * in our compareTo implementation.
 * <p/>
 * While we're at it, we might as well add equals and hashCode to make them consistent
 * with value semantics. (But this is not technically required to fix the bug we found)
 */
public class ClassificationScoreIdentityComparisonTest extends TestCase {

	public void setUp() {

	}

	/**
	 * Test all the variants of equality we care about
	 */
	public void testIdentity() {

		// use nomenclature A_B where stuff with same A are same by value semantics
		// defined for class.
		ClassificationScore cs1_1 = new ClassificationScore("Test", "Name1", "1.0");
		ClassificationScore cs1_2 = new ClassificationScore("Test", "Name1", "1.0");

		assertThat(cs1_1, is(cs1_1));
		assertThat(cs1_1.hashCode(), is(cs1_1.hashCode()));

		assertThat(cs1_2, is(cs1_2));
		assertThat(cs1_2.hashCode(), is(cs1_2.hashCode()));

		assertThat(cs1_1, is(cs1_2));
		assertThat(cs1_1.hashCode(), is(cs1_2.hashCode()));

		ClassificationScore cs3_1 = new ClassificationScore("Test", "Name1", "1.0", "id1");
		assertThat(cs3_1, is(cs3_1));
		assertThat(cs3_1.hashCode(), is(cs3_1.hashCode()));

		assertThat(cs3_1, is(not(cs1_1)));

		ClassificationScore cs3_2 = new ClassificationScore("Test", "Name1", "1.0", "id1");
		assertThat(cs3_2, is(cs3_2));
		assertThat(cs3_2.hashCode(), is(cs3_2.hashCode()));

		assertThat(cs3_2, is(cs3_1));
		assertThat(cs3_2.hashCode(), is(cs3_1.hashCode()));

		assertThat(cs3_2, is(not(cs1_1)));


		ClassificationScore cs5 = new ClassificationScore("Test", "Name1", "1.0", "id2");

		assertThat(cs5, is(not(cs3_1)));
		assertThat(cs5, is(not(cs1_1)));


	}

	public void testCompareTo() {
		// use nomenclature A_B where stuff with same A are same by value semantics
		// defined for class.
		ClassificationScore cs1_1 = new ClassificationScore("Test", "Name1", "1.0");
		ClassificationScore cs1_2 = new ClassificationScore("Test", "Name1", "1.0");

		assertThat(cs1_1.compareTo(cs1_2), is(0));
		assertThat(cs1_2.compareTo(cs1_1), is(0));

		ClassificationScore cs2_1 = new ClassificationScore("Test", "Name1", ".99");
		assertTrue(cs2_1.compareTo(cs1_1) > 0);

		ClassificationScore cs3_1 = new ClassificationScore("Test", "Name1", ".98");
		assertTrue(cs3_1.compareTo(cs2_1) > 0);
		assertTrue(cs3_1.compareTo(cs1_1) > 0);

		ClassificationScore cs4_1 = new ClassificationScore("Test", "Name1", ".50", "id1");
		ClassificationScore cs5_1 = new ClassificationScore("Test", "Name1", ".50", "id2");
		assertTrue(cs4_1.compareTo(cs1_1) > 0);
		assertTrue(cs4_1.compareTo(cs5_1) < 0);
		assertTrue(cs5_1.compareTo(cs4_1) > 0);
		assertThat(cs5_1, is(not(cs4_1)));

		ClassificationScore cs6_1 = new ClassificationScore("A", null, "1.0", null);
		ClassificationScore cs7_1 = new ClassificationScore("B", null, "1.0", null);
		assertTrue(cs6_1.compareTo(cs7_1) < 0);
	}

	public void testTreeSetCompareToBugFixed() {
		ClassificationScore cs1 = new ClassificationScore("Test", "Test", "1.0");
		cs1.setId("id1");
		ClassificationScore cs2 = new ClassificationScore("Test", "Test", "1.0");
		cs2.setId("id2");

		Set<ClassificationScore> theSet = new TreeSet<ClassificationScore>();
		theSet.add(cs1);
		theSet.add(cs2);

		assertThat(theSet.size(), is(2));
	}

	public void testHigherScoresFirst() {

		TreeSet<ClassificationScore> theSet = new TreeSet<ClassificationScore>();
		ClassificationScore cs1 = new ClassificationScore("Test", "Test", "1.0", "id1");
		ClassificationScore cs2 = new ClassificationScore("Test", "Test", ".99", "id2");
		ClassificationScore cs3 = new ClassificationScore("Test", "Test", ".98", "id3");
		ClassificationScore cs4 = new ClassificationScore("Test", "Test", ".97", "id4");

		theSet.add(cs4);
		theSet.add(cs2);
		theSet.add(cs3);
		theSet.add(cs1);

		List<ClassificationScore> theList = new ArrayList<ClassificationScore>();
		Iterator<ClassificationScore> it = theSet.iterator();
		while (it.hasNext())
			theList.add(it.next());

		assertThat(theList.get(0).getId(), is("id1"));
		assertThat(theList.get(1).getId(), is("id2"));
		assertThat(theList.get(2).getId(), is("id3"));
		assertThat(theList.get(3).getId(), is("id4"));

		sort(theList);
		assertThat(theList.get(0).getId(), is("id1"));
		assertThat(theList.get(1).getId(), is("id2"));
		assertThat(theList.get(2).getId(), is("id3"));
		assertThat(theList.get(3).getId(), is("id4"));

		Collections.sort(theList, new Comparator<ClassificationScore>() {
			@Override
			public int compare(ClassificationScore o1, ClassificationScore o2) {
				return -o1.compareTo(o2);
			}
		});

		assertThat(theList.get(3).getId(), is("id1"));
		assertThat(theList.get(2).getId(), is("id2"));
		assertThat(theList.get(1).getId(), is("id3"));
		assertThat(theList.get(0).getId(), is("id4"));

	}
}
