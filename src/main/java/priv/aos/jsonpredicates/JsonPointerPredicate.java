package priv.aos.jsonpredicates;

import com.fasterxml.jackson.databind.JsonNode;

public interface JsonPointerPredicate {

	/**
	 * @param node the node to check, this check simply checks if the fragment extracted via the JsonPointer (path) 
	 * is equal to the value
	 * @return true if equal, false otherwise
	 */
	boolean check(JsonNode node);

	// -- helper methods for fluent construction of predicates
	
	static JsonPointerPredicate not(JsonPointerPredicate other) {
		return new JsonNegativePredicate(other);
	}
	
	static JsonPointerPredicate equals(String path, String value) {
		return new JsonTextEqualsPredicate(path, value);
	}
	
	static JsonPointerPredicate or(JsonPointerPredicate lhs, JsonPointerPredicate rhs) {
		JsonPointerPredicate res = node -> { return lhs.check(node) || rhs.check(node); };
		return res;
	}
}
