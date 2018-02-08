package com.smartlogic.classificationserver.client;

import static java.util.Collections.sort;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.testng.annotations.Test;

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
public class ClassificationScoreIdentityComparisonTest  {


	/**
	 * Test all the variants of equality we care about
	 */
	@Test
	public void testIdentity() {

		// use nomenclature A_B where stuff with same A are same by value semantics
		// defined for class.
		ClassificationScore cs1_1 = new ClassificationScore("Test", "Name1", "1.0");
		ClassificationScore cs1_2 = new ClassificationScore("Test", "Name1", "1.0");

		assertEquals(cs1_1, cs1_1);
		assertEquals(cs1_1.hashCode(), cs1_1.hashCode());

		assertEquals(cs1_2, cs1_2);
		assertEquals(cs1_2.hashCode(), cs1_2.hashCode());

		assertEquals(cs1_1, cs1_2);
		assertEquals(cs1_1.hashCode(), cs1_2.hashCode());

		ClassificationScore cs3_1 = new ClassificationScore("Test", "Name1", "1.0", "id1");
		assertEquals(cs3_1, cs3_1);
		assertEquals(cs3_1.hashCode(), cs3_1.hashCode());

		assertNotEquals(cs3_1, cs1_1);

		ClassificationScore cs3_2 = new ClassificationScore("Test", "Name1", "1.0", "id1");
		assertEquals(cs3_2, cs3_2);
		assertEquals(cs3_2.hashCode(), cs3_2.hashCode());

		assertEquals(cs3_2, cs3_1);
		assertEquals(cs3_2.hashCode(), cs3_1.hashCode());

		assertNotEquals(cs3_2, cs1_1);


		ClassificationScore cs5 = new ClassificationScore("Test", "Name1", "1.0", "id2");

		assertNotEquals(cs5, cs3_1);
		assertNotEquals(cs5, cs1_1);


	}

	public void testCompareTo() {
		// use nomenclature A_B where stuff with same A are same by value semantics
		// defined for class.
		ClassificationScore cs1_1 = new ClassificationScore("Test", "Name1", "1.0");
		ClassificationScore cs1_2 = new ClassificationScore("Test", "Name1", "1.0");

		assertEquals(cs1_1.compareTo(cs1_2), 0);
		assertEquals(cs1_2.compareTo(cs1_1), 0);

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
		assertNotEquals(cs5_1, cs4_1);

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

		assertEquals(theSet.size(), 2);
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

		assertEquals(theList.get(0).getId(), "id1");
		assertEquals(theList.get(1).getId(), "id2");
		assertEquals(theList.get(2).getId(), "id3");
		assertEquals(theList.get(3).getId(), "id4");

		sort(theList);
		assertEquals(theList.get(0).getId(), "id1");
		assertEquals(theList.get(1).getId(), "id2");
		assertEquals(theList.get(2).getId(), "id3");
		assertEquals(theList.get(3).getId(), "id4");

		Collections.sort(theList, new Comparator<ClassificationScore>() {
			@Override
			public int compare(ClassificationScore o1, ClassificationScore o2) {
				return -o1.compareTo(o2);
			}
		});

		assertEquals(theList.get(3).getId(), "id1");
		assertEquals(theList.get(2).getId(), "id2");
		assertEquals(theList.get(1).getId(), "id3");
		assertEquals(theList.get(0).getId(), "id4");

	}
}
