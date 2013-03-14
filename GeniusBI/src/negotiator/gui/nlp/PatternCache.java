package negotiator.gui.nlp;
import java.util.*;
import java.util.regex.*;

/**
 * A cache for compiled regular expression patterns.
 * @see {@link #main}
 * @author Erel Segal
 * @since 01/12/2011
 */
public class PatternCache {
	
	public static Pattern get(String regexp) {
		Pattern pattern = cache.get(regexp);
		if (pattern==null) {
			pattern = Pattern.compile(regexp);
			cache.put(regexp, pattern);
		}
		return pattern;
	}
	
	public static Matcher matcher(String regexp, String input) {
		return get(regexp).matcher(input);
	}
	
	protected static Map<String,Pattern> cache = new HashMap<String,Pattern>();

	
	/**
	 * demo program
	 */
	public static void main(String[] args) {
		String input = "a=1 b=2 c=3";
		
		Matcher matcher;
		if ((matcher=PatternCache.matcher(".*b=(\\d+).*", input)).matches()) {
			System.out.println("The value of b is "+matcher.group(1));
		}
	}

}
