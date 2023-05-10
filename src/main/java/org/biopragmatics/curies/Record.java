package org.biopragmatics.curies;

import java.util.ArrayList;
import java.util.List;

public class Record {
    String prefix;
    String uriPrefix;
    List<String> prefixSynonyms;
    List<String> uriPrefixSynonyms;

    public Record(String prefix, String uriPrefix, List<String> prefixSynonyms, List<String> uriPrefixSynonyms) {
        this.prefix = prefix;
        this.uriPrefix = uriPrefix;
        this.prefixSynonyms = prefixSynonyms;
        this.uriPrefixSynonyms = uriPrefixSynonyms;
    }

    public Record(String prefix, String uriPrefix) {
        this(prefix, uriPrefix, new ArrayList<>(), new ArrayList<>());
    }

    public Reference getReference(String identifier) {
        return new Reference(prefix, identifier);
    }

    public String getURI(String identifier) {
        return uriPrefix + identifier;
    }
}
