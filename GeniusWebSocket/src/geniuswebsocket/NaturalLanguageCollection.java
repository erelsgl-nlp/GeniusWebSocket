package geniuswebsocket;

/**
 * <p>A collection of natural language strings with a similar meaning.
 * <p>Allows to choose a string at random.
 *
 * @author Erel Segal Halevi
 * @since 2013
 */
public class NaturalLanguageCollection {
	private String[] strings;
	
	public NaturalLanguageCollection(String... strings) {
		this.strings = strings;
	}
	
	public String randomString() {
		return strings[(int)(Math.random()*strings.length)];
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
