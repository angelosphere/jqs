package priv.aos.jqs;

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.core.JsonPointer

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath

class Loc {
    def path, file, json, parsed, text
    String toXString () {
        return "${path}, ${file}, ${json}"
    }
}

class Match {
    def query, result = [] // result is a list of tuples, loc + match
    
    def Match(query) {
        this.query = query
    }
    
    def addMatch(loc, result) {
        result << new Tuple(loc, result)
    }
}

/** 
 * inspired by the newton environment
 * everything is part in a big soup of objects :D
 * @author angelosphere, Aos Enkimaru
 */
class Soup {
    static dirs = []
    static locs = [:] // set of Loc's
    static developDir = "/Users/angelos/develop/workspace-2019/jqs/src/test/json/"
    static ObjectMapper mapper = new ObjectMapper()
    static boolean printFilenameOnly = true
    static history = [] // list of Matches
    
    
    static def addDir(String name) {
        if (loadDir(name) > 0)
            dirs << name
        else
            println "Error, ${name} is not a directory"
    }

    static def loadDir(String name) {
        def f = new File(name)
        if (f.isDirectory()) {
            FilenameFilter filter = { dir,  fName -> fName.endsWith(".json") }
            File[] res = f.listFiles (filter)
            loadEachFile(res)
            return res.length
        } else
            return 0
    }

    static def loadEachFile (File[] listOfFiles) {
        listOfFiles.each {
            locs[it.absolutePath] = new Loc(path: it.absolutePath, file:it, text:it.text)
        }
    }

    static def help() {
        println """
query: Issue queries in path notation:
    \$.store.book[0].title
or in bracket notation:
    \$['store']['book'][0]['title']

peek: issue queries in Json path notation

empty: find an empty array using Json path expression

For more help look at the examples/uncommented code in the source code below
"""
    }

    static def printLoc(loc) {
        if (printFilenameOnly)
            println "${loc.file.name}::"
        else
            println "${loc.path} (text size: ${loc.text.length()})::" 
    }
    
    static def query(String query) {
        boolean found = false
        def match = new Match(query)

        println "Query: ${query}"
        locs.each { key, loc ->
            loc.parsed ?= JsonPath.parse(loc.text)
            List<String> result
            def res
            try {
                res = loc.parsed.read(query)
                // super annoying that we either get a list or a single string ...
                if (res instanceof List)
                    result = res
                else {
                    result = new ArrayList<String>()
                    result << res
                }
                if (result.size() != 0) {
                    // match.addMatch(loc, result)
                    found = true
                    printLoc(loc)
                    result.each {  that ->
                        println "\t${that}"
                    }
                }
            } catch (Exception x) {
                // honestly, no idea why a path that does not fit to the json file
                // causes an exception ... sigh
            }
        }
        
        printResult(found)
    }

    static def emptyArray(path) {
        JsonPointer ptr = null
        boolean found = false
         
        if (path instanceof JsonPointer) {
            ptr = path
        } else { // assuming String
            ptr = JsonPointer.compile(path)
        }
        
        println "Empty: ${path}"
        
        locs.each { key, loc ->
            loc.json ?= mapper.readTree(loc.text)
            def array = loc.json.at(ptr)
            if (array instanceof ArrayNode && array.size() == 0) {
                found = true
                printLoc (loc)
                println "\t${ptr}[]"
            }
        }
        printResult(found)
    }
    
    static def printResult(found) {
        if (!found)
            println "NO MATCHES\n"
        else {
            println ""
            println "Done!"
            println ""
        }
    }
    
    static def peek(path) {
        JsonPointer ptr = null
        boolean found = false
         
        if (path instanceof JsonPointer) {
            ptr = path
        } else { // assuming String
            ptr = JsonPointer.compile(path)
        }

        println ("Peek: ${path}")

        locs.each { key, loc ->
            loc.json ?= mapper.readTree(loc.text)
            def text = loc.json.at(ptr).asText()
            if (text != null && text.length() != 0) {
                found = true
                printLoc (loc)
                println "\t${text}"
            }
        }

        printResult(found)
    }
    
    static def hist () {
        history.each {
            println it.querry
        }
    } 
}

def list() {
    println binding.each { k, v ->  println "${k}: ${v}" }
    return null
}

/// set up the binding ... for some reason the groovy console does not find/access the static import/class
/// import static SoupImpl.*binding.setProperty("addDir", { Soup.addDir(it) })

binding.setProperty("help", { Soup.help() } )
binding.setProperty("query", { Soup.query(it) } )
binding.setProperty("peek", { Soup.peek(it) } )
binding.setProperty("empty", { Soup.emptyArray(it) } )

binding.setProperty("history", { Soup.hist() } )

binding.setProperty("Soup", Soup)

/// some commands

addDir "/Users/angelos/develop/workspace-2019/jqs/src/test/json/"
//query "[?(@.example)]"
//query "[?(@.when)]"
//query "[?(@.when == 'never' || @.date == 'today')]"

query '$.tool.jsonpath.creator.location[2]'
// query '$.tool.jsonpath.creator.location[1]'
query "\$['tool']['jsonpath']['creator']['location'][1]"

peek "/file"
peek "/ones/0/example"

emptyArray "/book"