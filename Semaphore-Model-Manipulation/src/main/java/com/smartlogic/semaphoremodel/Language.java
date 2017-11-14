package com.smartlogic.semaphoremodel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Language {

	private final String code;
	private final String name;
	
	private Language(String code) {
		this.name = null;
		this.code = code;
	}

	private Language(String name, String code) {
		this.name = name;
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	private static Map<String, Language> languages = new ConcurrentHashMap<String, Language>();
	public static Language getLanguage(String languageCode) {
		Language language = languages.get(languageCode);
		if (language == null) {
			language = new Language(languageCode);
			languages.put(languageCode, language);
		}
		return language;
	}
	
}
