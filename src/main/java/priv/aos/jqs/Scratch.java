package priv.aos.jqs;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * scratch pad for java code, because groovy code completion is not working
 * @author angelosphere
 *
 */
public class Scratch {
	public static void main(String[] args) throws IOException {
		ObjectMapper om = new ObjectMapper();
		String text = "{ \"top\": [] }";
		JsonPointer x = JsonPointer.compile("/top");
		JsonNode node = om.readTree(text);
		Object res = node.at(x);
		node.size();
		System.out.print(res);
	}
}
