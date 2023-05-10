package org.biopragmatics.curies;

import org.apache.commons.collections4.trie.PatriciaTrie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Converter {
    String sep;
    PatriciaTrie<Record> trie;
    Map<String, Record> prefixMap;

    public Converter(List<Record> records) {
        sep = ":";
        trie = new PatriciaTrie<>();
        prefixMap = new HashMap<>();
        for (Record record : records) {
            prefixMap.put(record.prefix, record);
            for (String prefixSynonym : record.prefixSynonyms)
                prefixMap.put(prefixSynonym, record);
            trie.put(record.uriPrefix, record);
            for (String uriPrefixSynonym: record.uriPrefixSynonyms)
                trie.put(uriPrefixSynonym, record);
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

    public PatriciaTrie<Record> getTrie() {
        return trie;
    }

    public Reference parseURI(String uri) {
        Map.Entry<String, Record> entry = trie.select(uri);
        if (entry == null)
            return null;
        String key = entry.getKey();
        if (!uri.startsWith(key))
            return null;
        String identifier = uri.substring(key.length());
        Record record = entry.getValue();
        return record.getReference(identifier);
    }

    public Reference parseCURIE(String curie) {
        String[] parts = curie.split(sep, 2);
        if (parts.length != 2)
            return null;
        return new Reference(parts[0], parts[1]);
    }

    /**
     * Compress a URI into a compact URI (CURIE)
     *
     * @param uri A string representation of a URI
     * @return A compact URI (CURIE)
     */
    public String compress(String uri) {
        Reference reference = parseURI(uri);
        if (reference == null)
            return null;
        return reference.getCURIE(this.sep);
    }

    /**
     * Get the record for the given prefix, if it exists.
     *
     * @param prefix The prefix of the resource to get
     * @return The record associated with the prefix
     */
    public Record getRecord(String prefix) {
        return prefixMap.get(prefix);
    }

    public String expand(String curie) {
        Reference reference = parseCURIE(curie);
        Record record = getRecord(reference.getPrefix());
        if (record == null)
            return null;
        return record.getURI(reference.getIdentifier());
    }
}
