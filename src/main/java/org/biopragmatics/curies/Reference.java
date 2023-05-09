package org.biopragmatics.curies;

public class Reference {
    String prefix;
    String identifier;

    public Reference(String prefix, String identifier) {
        this.prefix = prefix;
        this.identifier = identifier;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getCURIE(String sep) {
        return this.prefix + sep + this.identifier;
    }

    public String getCURIE() {
        return getCURIE(":");
    }
}
