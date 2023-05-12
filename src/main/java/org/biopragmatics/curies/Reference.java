package org.biopragmatics.curies;

public class Reference {
    private final String prefix;
    private final String identifier;

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

    public String getCURIE() {
        return this.prefix + ":" + this.identifier;
    }
}
