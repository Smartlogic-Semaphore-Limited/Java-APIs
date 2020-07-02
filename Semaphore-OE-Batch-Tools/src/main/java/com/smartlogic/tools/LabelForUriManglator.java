package com.smartlogic.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.regex.Pattern;

import org.apache.commons.text.WordUtils;
import org.apache.jena.ext.com.google.common.base.CharMatcher;
import org.apache.jena.ext.com.google.common.base.Strings;
import org.apache.jena.ext.com.google.common.collect.ImmutableMap;

/**
 * Utilities for converting labels to part of prefLabel and altLabel URIs.
 */
@SuppressWarnings("deprecation")
public class LabelForUriManglator {

	private static final CharMatcher SPECIAL_ASCII_MATCHER = CharMatcher.ascii()
			.and(CharMatcher.javaLetterOrDigit().negate()).and(CharMatcher.noneOf("._ -"));

	public static String mangle(String label) {
		label = simplify(label);
		label = stripSpecialAsciiCharacters(label);
		label = toCamelCase(label);
		label = applyEndodingWithStripingOfPercents(label);
		label = stripRepeatedHyphens(label);
		label = fixCornerCasesForJena(label);
		return label;
	}

	private static String stripSpecialAsciiCharacters(String label) {
		return SPECIAL_ASCII_MATCHER.replaceFrom(label, "-");
	}

	private static String toCamelCase(String label) {
		if (label.length() >= 1) {
			String first = label.substring(0, 1);
			label = WordUtils.capitalize(label);
			label = first + label.substring(1);
			return label.replaceAll(" ", "");
		} else {
			return label;
		}
	}

	private static String applyEndodingWithStripingOfPercents(String label) {
		label = urlEncode(label);
		return label.replace("%", "");
	}

	private static String stripRepeatedHyphens(String label) {
		return label.replaceAll("[-]+", "-");
	}

	/**
	 * <pre>
	 * 1. leading '-' is prefixed by '_' because of URI compatibility issues
	 * 2. empty labels is converted to '_'
	 * </pre>
	 */
	private static String fixCornerCasesForJena(String label) {
		if (Strings.isNullOrEmpty(label)) {
			return "_";
		}
		if (label.startsWith("-")) {
			return "_" + label.substring(1);
		}
		return label;
	}

	private static String urlEncode(String label) {
		try {
			return URLEncoder.encode(label, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException("Unsupported encoding.", uee);
		}
	}

	private static final ImmutableMap<String, String> NONDIACRITICS = ImmutableMap.<String, String>builder()

			// Replace non-diacritics as their equivalent characters
			.put("\u0141", "l") // BiaLystock
			.put("\u0142", "l") // Bialystock
			.put("ß", "ss").put("æ", "ae").put("ø", "o").put("©", "c").put("\u00D0", "d")
			// All Ð ð
			// from
			// http://de.wikipedia.org/wiki/%C3%90
			.put("\u00F0", "d").put("\u0110", "d").put("\u0111", "d").put("\u0189", "d").put("\u0256", "d")
			.put("\u00DE", "th") // thorn Þ
			.put("\u00FE", "th") // thorn þ
			.build();

	/*
	 * Special regular expression character ranges relevant for simplification ->
	 * see http://docstore.mik.ua/orelly/perl/prog3/ch05_04.htm
	 * InCombiningDiacriticalMarks: special marks that are part of "normal" ä, ö, î
	 * etc.. IsSk: Symbol, Modifier see
	 * http://www.fileformat.info/info/unicode/category/Sk/list.htm IsLm: Letter,
	 * Modifier see http://www.fileformat.info/info/unicode/category/Lm/list.htm
	 */
	private static final Pattern DIACRITICS_AND_FRIENDS = Pattern
			.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

	public static String simplify(String str) {
		str = stripDiacritics(str);
		return stripNonDiacritics(str);
	}

	private static String stripNonDiacritics(String str) {
		StringBuffer ret = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			String source = str.substring(i, i + 1);
			String replace = NONDIACRITICS.get(source);
			if (replace == null) {
				ret.append(source);
			} else {
				ret.append(replace);
			}
		}
		return ret.toString();
	}

	private static String stripDiacritics(String str) {
		str = Normalizer.normalize(str, Normalizer.Form.NFD);
		return DIACRITICS_AND_FRIENDS.matcher(str).replaceAll("");
	}

}
