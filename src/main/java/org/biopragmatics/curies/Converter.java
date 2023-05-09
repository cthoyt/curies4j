package org.biopragmatics.curies;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Converter {
    String sep;
    Trie<String, Record> trie;
    Map<String, Record> prefixMap;

    public Converter(List<Record> records) {
        sep = ":";
        trie = new PatriciaTrie<>();
        prefixMap = new HashMap<>();
        for (Record record : records) {
            prefixMap.put(record.prefix, record);
            trie.put(record.uriPrefix, record);
            // add uri prefix synonyms later
        }
    }

    public Converter(Map<String, String> prefixMap) {
        this(consumePrefixMap(prefixMap));
    }

    private static List<Record> consumePrefixMap(Map<String, String> prefixMap) {
        List<Record> records = new ArrayList<>();
        for (Map.Entry<String, String> entry : prefixMap.entrySet()) {
            records.add(new Record(entry.getKey(), entry.getValue()));
        }
        return records;
    }

    public Reference parseURI(String uri) {
        return null;
    }

    public Reference parseCURIE(String curie) {
        String[] parts = curie.split(sep);
        if (parts.length != 2)
            return null;
        return new Reference(parts[0], parts[1]);
    }

    public String compress(String uri) {
        Reference reference = parseURI(uri);
        if (reference == null)
            return null;
        return reference.getCURIE(this.sep);
    }

    public String expand(String curie) {
        Reference reference = parseCURIE(curie);
        Record record = prefixMap.get(reference.prefix);
        return record.uriPrefix + reference.identifier;
    }
}
