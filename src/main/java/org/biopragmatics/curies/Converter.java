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
            for (String uriPrefixSynonym : record.uriPrefixSynonyms)
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

    public Reference parseURI(String uri) {
        // TODO it appears select() always returns the root if nothing else available
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

    public Record getRecord(Reference reference) {
        return getRecord(reference.getPrefix());
    }

    public String expand(String curie) {
        Reference reference = parseCURIE(curie);
        if (reference == null)
            return null;
        return expand(reference);
    }

    public String expand(Reference reference) {
        Record record = getRecord(reference);
        if (record == null)
            return null;
        return record.getURI(reference);
    }

    public String expand(String prefix, String identifier) {
        return expand(new Reference(prefix, identifier));
    }

    public String standardizePrefix(String prefix) {
        Record record = getRecord(prefix);
        if (record == null)
            return null;
        return record.prefix;
    }

    public String standardizeCURIE(String curie) {
        Reference reference = parseCURIE(curie);
        if (reference == null)
            return null;
        Reference referenceStandard = standardizeReference(reference);
        if (referenceStandard == null)
            return null;
        return referenceStandard.getCURIE(sep);
    }

    public Reference standardizeReference(Reference reference) {
        String normPrefix = standardizePrefix(reference.getPrefix());
        if (normPrefix == null)
            return null;
        return new Reference(normPrefix, reference.getIdentifier());
    }

    public String standardizeURI(String uri) {
        Reference reference = parseURI(uri);
        if (reference == null)
            return null;
        return expand(reference);
    }
}
