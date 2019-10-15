package priv.aos.jsonpredicates;

import com.fasterxml.jackson.databind.JsonNode;

/**
*
* A "not" predicate, negating the check of the predicate. E.g. you want if (!"some string".equals("bad choice"));
* 
*/
public class JsonNegativePredicate implements JsonPointerPredicate {

	private JsonPointerPredicate other;

	public JsonNegativePredicate(JsonPointerPredicate other) {
		this.other = other;
		
	}

	/**
	 * works like Java "!" operator, 'note's the wrapped check
	 */
	// AOS this can probably be based on Java 8 Predicates
	@Override
	public boolean check(JsonNode node) {
		return ! other.check(node);
	}

	public String toString() {
		return "not (" + other + ")"; 
	}
}
