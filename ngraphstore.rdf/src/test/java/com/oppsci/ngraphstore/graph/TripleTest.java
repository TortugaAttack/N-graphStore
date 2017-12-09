package com.oppsci.ngraphstore.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TripleTest {
	
	@Parameters
	public static Collection<Object[]> data() {
		List<Object[]> testConfigs = new ArrayList<Object[]>();

		testConfigs.add(new Object[] {new String[] {"a", "b", "c", "d"}, new String[] {"a", "b", "c", "d"}, true});
		testConfigs.add(new Object[] {new String[] {"a", "b", "c", "d"}, new String[] {"a", "b", "c", "e"}, false});
		testConfigs.add(new Object[] {new String[] {"a", "b", "c", "d"}, new String[] {"a", "b", "e", "d"}, false});
		testConfigs.add(new Object[] {new String[] {"a", "b", "c", "d"}, new String[] {"a", "e", "c", "d"}, false});
		testConfigs.add(new Object[] {new String[] {"a", "b", "c", "d"}, new String[] {"e", "b", "c", "d"}, false});
		
		return testConfigs;
	}

	private Triple<String> expectedTriple;
	private Triple<String> actualTriple;
	private boolean test;
	
	public TripleTest(String[] expected, String[] actual, boolean test) {
		expectedTriple = new Triple<String>();
		expectedTriple.setSubject(expected[0]);
		expectedTriple.setPredicate(expected[1]);
		expectedTriple.setObject(expected[2]);
		expectedTriple.setGraph(expected[3]);
		
		actualTriple = new Triple<String>();
		actualTriple.setSubject(actual[0]);
		actualTriple.setPredicate(actual[1]);
		actualTriple.setObject(actual[2]);
		actualTriple.setGraph(actual[3]);
		this.test=test;
	}
	
	@Test
	public void equalsTest() {
		assertEquals(test, expectedTriple.equals(actualTriple));
	}
	
	@Test
	public void differentClassCheck() {
		Object obj = "test";
		assertFalse(expectedTriple.equals(obj));
	}

}
