# curies4j

A Java implementation of the [`curies`](https://github.com/cthoyt/curies/) Python package,
which enables idiomatic conversion between URIs and compact URIs (CURIEs).

```java
import org.biopragmatics.curies.Converter;
import org.biopragmatics.curies.Reference;

class CuriesDemo1 {
    public static void main(String[] args) {
        Converter converter = Converter.getExampleConverter();

        String curie = "CHEBI:1234";
        String uri = "http://purl.obolibrary.org/obo/CHEBI_1234";

        String reference = new Reference("CHEBI", "1234");
        String prefix = reference.getPrefix();  // "CHEBI"
        String identifier = reference.getIdentfier();  // "1234"

        // 2 ways to expand a CURIE into a URI
        String uri1 = converter.expand("CHEBI:1234");
        String uri2 = converter.expand("CHEBI", "1234");
        // "http://purl.obolibrary.org/obo/CHEBI_1234"

        // Expand a reference into a URI
        String uri3 = converter.expand(reference);
        // "http://purl.obolibrary.org/obo/CHEBI_1234"

        // Compress a URI to a CURIE string
        String curie1 = converter.compress(uri);
        // CHEBI:1234

        // Parse a URI into a reference
        Reference reference1 = converter.parseURI(uri);
        // new Reference("CHEBI", "1234")

        // Parse a CURIE into a reference
        Reference reference2 = converter.parseCURIE(curie);
        // new Reference("CHEBI", "1234")
    }
}
```

## Bioregistry Integration

The Bioregistry can be loaded over the web.

```java
import org.biopragmatics.curies.Converter;

class CuriesDemo2 {
    public static void main(String[] args) {
        Converter converter = Converter.loadBioregistry();
        converter.compress("https://www.ebi.ac.uk/ols/ontologies/doid/terms?obo_id=DOID:1234");
        // doid:1234
    }
}
```
