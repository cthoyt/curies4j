package org.biopragmatics.curies;

import java.util.ArrayList;
import java.util.List;

public class Record {
    private final String prefix;
    private final String uriPrefix;
    private final List<String> prefixSynonyms;
    private final List<String> uriPrefixSynonyms;

    public Record(String prefix, String uriPrefix, List<String> prefixSynonyms, List<String> uriPrefixSynonyms) {
        this.prefix = prefix;
        this.uriPrefix = uriPrefix;
        this.prefixSynonyms = prefixSynonyms;
        this.uriPrefixSynonyms = uriPrefixSynonyms;
    }

    public Record(String prefix, String uriPrefix) {
        this(prefix, uriPrefix, new ArrayList<>(), new ArrayList<>());
    }

    public String getPrefix() {
        return prefix;
    }

    public List<String> getPrefixSynonyms() {
        return prefixSynonyms;
    }

    public List<String> getUriPrefixSynonyms() {
        return uriPrefixSynonyms;
    }

    public String getUriPrefix() {
        return uriPrefix;
    }

    public Reference getReference(String identifier) {
        return new Reference(prefix, identifier);
    }

    /**
     * Get a URI from the record.
     *
     * @param identifier The local unique identifier in the semantic
     *                   space represented by this record
     * @return A string representing the canonical URI for the identifier
     * in the semantic space represented by this record
     */
    public String getURI(String identifier) {
        return uriPrefix + identifier;
    }

    public String getURI(Reference reference) {
        if (!reference.getPrefix().equals(prefix))
            throw new RuntimeException("prefix mismatch");
        return getURI(reference.getIdentifier());
    }
}
