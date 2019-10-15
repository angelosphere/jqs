package priv.aos.jsonpredicates;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;

public class JsonTextEqualsPredicate implements JsonPointerPredicate {
	@Nonnull
	private String value;
	private JsonPointer cpt;

	/**
	 * @param path a JsonPointer in text form
	 * @param value a string which should be equal to the fragment in the JsonNode the path is referencing  
	 */
	public JsonTextEqualsPredicate(String path, String value) {
		cpt = JsonPointer.compile(path);
		this.value = value;
	}

	/**
	 * @param node the node to check, this check simply checks if the fragment extracted via the JsonPointer (path) 
	 * is equal to the value
	 * @return true if equal, false otherwise
	 */
	@Override
	public boolean check(JsonNode node) {     // AOS Json.createPointer("/likes/2");, see: https://dzone.com/articles/son-processing-11-the-json-pointer-challenge
		String text = node.at(cpt).asText();  // AOS cash the Pointer, see docu of JsonPointer, oops? How to do that ...
		return value.equals(text);
	}

	@Override
	public String toString() {
		return "JsonTextEqualsPredicate [value=" + value + ", cpt=" + cpt + "]";
	}
}