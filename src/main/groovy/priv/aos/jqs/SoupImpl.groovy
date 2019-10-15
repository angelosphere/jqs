package priv.aos.jqs;

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.JsonPointer

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath

class Loc {
    def path, file, json, parsed, text
    String toString () { return "${path}, ${file}, ${json}" }
} 

/** 
 * inspired by the newton environment
 * everything is part in a big soup of objects :D
 * @author angelosphere
 */
class Soup {
    static dirs = []
    static files = [:] // set of Loc's
    static developDir = "/Users/angelos/develop/workspace-2019/jqs/src/test/json/"
    static ObjectMapper mapper = new ObjectMapper()

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
            files[it.absolutePath] = new Loc(path: it.absolutePath, file:it, text:it.text)
        }
    }     
    
    static def help() {
        println """
Issue queries in path notation:
    \$.store.book[0].title
or in bracket notation:
    \$['store']['book'][0]['title']
"""
    }
    
    static def query(String query) {
        files.each { key, loc -> 
            loc.parsed ?= JsonPath.parse(loc.text);
            List<String> result = loc.parsed.read(query);
            if (result != null) {
                println "Result:"
                result.each { println it }
            }
        }
        "Done!"
    }
    
    static def peek(path) {
        JsonPointer ptr = null;
        if (path instanceof JsonPointer) {
            ptr = path
        } else { // assuming String
            ptr = JsonPointer.compile(path)
        }
        
        files.each { key, loc ->
            loc.json ?= mapper.readTree(loc.text)
            println "found: " + loc.json.at(ptr)
        }
    }
}

binding.setProperty("addDir", { Soup.addDir(it) })
binding.setProperty("help", { Soup.help() } )
binding.setProperty("query", { Soup.query(it) } )
binding.setProperty("peek", { Soup.peek(it) } )
binding.setProperty("Soup", Soup)
addDir "/Users/angelos/develop/workspace-2019/jqs/src/test/json/"
// query "[?(@.example)]"
// query "[?(@.when)]" // [?(@.description =~ /cat.*/i)]
query "[?(@.when == 'never' || @.date == 'today')]" // [?(@.description =~ /cat.*/i)]

peek "/file"