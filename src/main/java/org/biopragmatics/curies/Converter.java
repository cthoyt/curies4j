package org.biopragmatics.curies;

import org.apache.commons.collections4.trie.PatriciaTrie;

import java.io.IOException;
import java.util.*;

public class Converter {
    PatriciaTrie<Record> trie;
    Map<String, Record> prefixMap;

    public Converter(List<Record> records) {
        trie = new PatriciaTrie<>();
        prefixMap = new HashMap<>();
        for (Record record : records) {
            prefixMap.put(record.getPrefix(), record);
            for (String prefixSynonym : record.getPrefixSynonyms())
                prefixMap.put(prefixSynonym, record);
            trie.put(record.getUriPrefix(), record);
            for (String uriPrefixSynonym : record.getUriPrefixSynonyms())
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
        Map.Entry<String, Record> entry = trie.select(uri);
        // TODO it appears select() always returns the root if nothing else available
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
        String[] parts = curie.split(":", 2);
        if (parts.length != 2)
            return null;
        return new Reference(standardizePrefix(parts[0]), parts[1]);
    }

    /**
     * Compress a URI into a compact URI (CURIE).
     * The inverse of this operation is {@link org.biopragmatics.curies.Converter#expand}.
     *
     * @param uri A string representation of a URI
     * @return A string representation of a compact URI (CURIE)
     *
     * <h2>Usage</h2>
     *
     * <pre>
     * {@code
     * Converter converter = Converter.getExampleConverter();
     * String curie = converter.compress("http://purl.obolibrary.org/obo/CHEBI_1234");
     * // "CHEBI:1234"
     * }
     * </pre>
     */
    public String compress(String uri) {
        Reference reference = parseURI(uri);
        if (reference == null)
            return null;
        return reference.getCURIE();
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

    /**
     * Expand a compact URI (CURIE) into a URI.
     * The inverse of this operation is {@link org.biopragmatics.curies.Converter#compress}.
     * @param curie A string representation of a compact URI (CURIE)
     * @return A string representation of a URI
     *
     * <h2>Usage</h2>
     *
     * <pre>
     * {@code
     * Converter converter = Converter.getExampleConverter();
     * String uri = converter.expand("CHEBI:1234");
     * // "http://purl.obolibrary.org/obo/CHEBI_1234"
     * }
     * </pre>
     */
    public String expand(String curie) {
        Reference reference = parseCURIE(curie);
        if (reference == null)
            return null;
        return expand(reference);
    }

    /**
     * Serialize a reference into a URI.
     * The inverse of this operation is {@link org.biopragmatics.curies.Converter#parseURI}.
     *
     * @param reference A representation of a prefix/identifier pair
     * @return A string representation of a URI
     *
     * <h2>Usage</h2>
     *
     * <pre>
     * {@code
     * Converter converter = Converter.getExampleConverter();
     * Reference reference = new Reference("CHEBI", "1234");
     * String uri = converter.expand(reference);
     * // "http://purl.obolibrary.org/obo/CHEBI_1234"
     * }
     * </pre>
     */
    public String expand(Reference reference) {
        Record record = getRecord(reference);
        if (record == null)
            return null;
        return record.getURI(reference);
    }

    /**
     * Serialize a reference into a URI
     *
     * @param prefix     The prefix for an entity
     * @param identifier The local unique identifier for an entity in the semantic space defined by the given prefix
     * @return A string representation of a URI
     *
     * <h2>Usage</h2>
     *
     * <pre>
     * {@code
     * Converter converter = Converter.getExampleConverter();
     * String uri = converter.expand("CHEBI", "1234");
     * // "http://purl.obolibrary.org/obo/CHEBI_1234"
     * }
     * </pre>
     */
    public String expand(String prefix, String identifier) {
        return expand(new Reference(prefix, identifier));
    }

    public String standardizePrefix(String prefix) {
        Record record = getRecord(prefix);
        if (record == null)
            return null;
        return record.getPrefix();
    }

    public String standardizeCURIE(String curie) {
        Reference reference = parseCURIE(curie);
        if (reference == null)
            return null;
        Reference referenceStandard = standardizeReference(reference);
        if (referenceStandard == null)
            return null;
        return referenceStandard.getCURIE();
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

    /**
     * Get an example converter that contains entries for ChEBI, GO, and OBO.
     * This is good enough to demonstrate how synonyms for prefixes and URIs work
     * as well as the possibility of overlapping URI prefixes.
     *
     * @return An instantiated Converter object suitable for examples, but not for
     * actual use.
     */
    public static Converter getExampleConverter() {
        List<String> chebiPrefixSynonyms = Collections.singletonList("CHEBI");
        List<String> chebiURIPrefixSynonyms = Collections.singletonList("https://bioregistry.io/chebi:");
        Record chebi = new Record("chebi", "http://purl.obolibrary.org/obo/CHEBI_", chebiPrefixSynonyms, chebiURIPrefixSynonyms);
        List<Record> records = new ArrayList<>();
        records.add(chebi);
        return new Converter(records);
    }

    /**
     * Get a converter over the web with the Bioregistry data inside it.
     *
     * @return A converter with Bioregistry records.
     * @throws IOException if there's an issue getting the web content from GitHub
     */
    public static Converter loadBioregistry() throws IOException {
        String url = "https://github.com/biopragmatics/bioregistry/raw/main/exports/contexts/bioregistry.epm.json";
        return new Converter(Loader.getRecords(url));
    }
}
