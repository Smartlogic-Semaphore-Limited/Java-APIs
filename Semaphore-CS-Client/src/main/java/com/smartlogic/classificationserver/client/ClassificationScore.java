package com.smartlogic.classificationserver.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Data holder for classification results
 *
 * @author Smartlogic Semaphore
 */
public class ClassificationScore implements Comparable<ClassificationScore> {

	protected ClassificationScore(String rulebaseClass, String name, float score) {
		this.rulebaseClass = rulebaseClass;
		this.name = name;
		this.score = score;
	}

	protected ClassificationScore(String rulebaseClass, String name, String score) {
		this.rulebaseClass = rulebaseClass;
		this.name = name;
		this.score = Float.parseFloat(score);
	}

	protected ClassificationScore(String rulebaseClass, String name, float score, String id) {
		this.rulebaseClass = rulebaseClass;
		this.name = name;
		this.score = score;
		this.id = id;
	}

	protected ClassificationScore(String rulebaseClass, String name, String score, String id) {
		this.rulebaseClass = rulebaseClass;
		this.name = name;
		this.score = Float.parseFloat(score);
		this.id = id;
	}

	public ClassificationScore(String data) throws NotAScoreException {
		/* The score can be returned in one of two formats
		   RULEBASE_CLASS.TERMNAME(TERM_ID):score
		   or
		   RULEBASE_CLASS.TERMNAME:score
		   */

		int dotPos = findChar('.', data, 0);
		int braPos = findChar('(', data, 0);
		int ketPos = findChar(')', data, 0);
		int colPos = findChar(':', data, 0);
		if ((dotPos == -1) || (colPos == -1)) {
			throw new NotAScoreException(data);
		}
		if (ketPos < colPos - 1) { // brackets are part of term's name
			ketPos = -1;
			braPos = -1;
		}
		rulebaseClass = unprotect(data.substring(0, dotPos));

		try {
			score = Float.parseFloat(unprotect(data.substring(colPos + 1)));
		} catch (NumberFormatException e) {
			throw new NotAScoreException(data);
		}

		if ((braPos != -1) && (ketPos != -1) && (ketPos > braPos)) {
			this.name = unprotect(data.substring(dotPos + 1, braPos));
			this.id = unprotect(data.substring(braPos + 1, ketPos));
		} else {
			this.name = unprotect(data.substring(dotPos + 1, colPos));
		}
	}

	public static int findChar(char charToFind, String data, int startPos) {
		if (startPos >= data.length()) return -1;

		int position = data.indexOf(charToFind, startPos);
		if (position == -1) return -1; // The character isn't in the string

		int count = 0;
		for (int p = position - 1; ((p >= 0) && (data.charAt(p) == '\\')); p--) count++;
		if (count % 2 == 1) return findChar(charToFind, data, position + 1);

		return position;
	}

	public static String unprotect(String protectedString) {
		return protectedString.replace("\\\\", "\\").replace("\\(", "(").replace("\\)", ")").replace("\\:", ":");
	}

	private String rulebaseClass;

	/**
	 * The rulebase class for this results
	 *
	 * @return the name of the rulebase class
	 */
	public String getRulebaseClass() {
		return rulebaseClass;
	}

	private String name;

	/**
	 * The name of the rulebase (ontology term) that was triggered
	 *
	 * @return The name of the term
	 */
	public String getName() {
		return name;
	}

	private float score;

	/**
	 * Score for this category (in the range 0-1)
	 *
	 * @return the value of the score
	 */
	public float getScore() {
		return score;
	}

	private String id;

	protected void setId(String id) {
		this.id = id;
	}

	/**
	 * The id of the rulebase (ontology term) that was triggered
	 *
	 * @return The id of the term
	 */
	public String getId() {
		return id;
	}


	/**
	 * Doing this manually for now. We don't have many of these. But this is a ton of work
	 * otherwise. I'd use google guava in a heartbeat for these.
	 *
	 * @param other - the object to which to compare this object
	 * @return 0 if equal, -1 if less than, 1 if more than.
	 */
	@Override
	public int compareTo(ClassificationScore other) {
		final int LESS_THAN = -1;
		final int EQUAL = 0;
		final int MORE_THAN = 1;
		if (this == other) return EQUAL;

		// we want scores DESCENDING. That's why these are reversed from the normal ASC case.
		if (this.score < other.score) return MORE_THAN;
		if (this.score > other.score) return LESS_THAN;

		// "null is less than not null" -- Philosopher Steve
		if (this.rulebaseClass == null && other.rulebaseClass != null)
			return LESS_THAN;
		if (this.rulebaseClass != null && other.rulebaseClass == null)
			return MORE_THAN;
		if (this.rulebaseClass != null) {
			int comparison = this.rulebaseClass.compareTo(other.rulebaseClass);
			if (comparison != EQUAL) return comparison;
		}

		if (this.name == null && other.name != null)
			return LESS_THAN;
		if (this.name != null && other.name == null)
			return MORE_THAN;
		if (this.name != null) {
			int comparison = this.name.compareTo(other.name);
			if (comparison != EQUAL) return comparison;
		}

		if (this.id == null && other.id != null)
			return LESS_THAN;
		if (this.id != null && other.id == null)
			return MORE_THAN;
		if (this.id != null)
			return this.id.compareTo(other.id);
		return EQUAL;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ClassificationScore that = (ClassificationScore) o;

		if (Float.compare(that.score, score) != 0) return false;
		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		return !(rulebaseClass != null ? !rulebaseClass.equals(that.rulebaseClass) : that.rulebaseClass != null);

	}

	@Override
	public int hashCode() {
		int result = rulebaseClass != null ? rulebaseClass.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (score != +0.0f ? Float.floatToIntBits(score) : 0);
		result = 31 * result + (id != null ? id.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(this.getClass().getCanonicalName() + ":");
		for (Method method : this.getClass().getMethods()) {
			if (method.getName().startsWith("get") && method.getParameterTypes().length == 0) {
				try {
					sb.append(method.getName() + ": '" + method.invoke(this, new Object[0]) + "', ");
				} catch (IllegalAccessException e) {
					sb.append("IllegalAccessException invoking method: " + method.getName() + ",");
				} catch (IllegalArgumentException e) {
					sb.append("IllegalArgumentException invoking method: " + method.getName() + ",");
				} catch (InvocationTargetException e) {
					sb.append("InvocationTargetException invoking method: " + method.getName() + ",");
				}
			}
		}
		return sb.toString();
	}

}
