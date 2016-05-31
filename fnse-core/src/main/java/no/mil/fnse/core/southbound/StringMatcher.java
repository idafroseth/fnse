package no.mil.fnse.core.southbound;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component("StringMatcher")
public class StringMatcher {
	/**
	 * Search for a searchString in a text and return the entire word. return
	 * null if the text does not contain the pattern.
	 * 
	 * @param searchString
	 * @param text
	 * @return the first word containing the pattern
	 */
	public String findFirstWordWithPattern(String searchString, String text) {

		String sPattern = "(?i)\\b\\S*" + searchString + "\\S*\\b";
		Pattern pattern = Pattern.compile(sPattern);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			return matcher.group();
		}
		return null;
	}

	public Collection<String> findAllWordWithPattern(String searchPattern, String text) {

		String sPattern = "(?i)\\b\\S*" + searchPattern + "\\S*\\b";
		Pattern pattern = Pattern.compile(sPattern);
		Matcher matcher = pattern.matcher(text);
		Collection<String> result = new HashSet<String>();
		while (matcher.find()) {
			result.add(matcher.group());
		}
		return result;
	}

	public String findNextWordAfter(String previousPatter, String text) {
		// String result ="";
		String sPattern = "(?<=" + previousPatter + ")\\s*(\\S+)";
		Pattern pattern = Pattern.compile(sPattern);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			return matcher.group();
		}
		return null;

	}

}
