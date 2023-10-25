# curies4j

A Java implementation of the [`curies`](https://github.com/cthoyt/curies/) Python package,
which enables idiomatic conversion between URIs and compact URIs (CURIEs).

Note that this package returns `null` when it gets content it can't handle instead of
throwing errors.

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

## Loading a Prefix Map

```java
import java.util.HashMap;
import java.util.Map;
import org.biopragmatics.curies.Converter;
import org.biopragmatics.curies.Loader;

class AdHocResourceDemo {
    public static void main(String[] args) {
        Map<String, String> prefixMap = new HashMap<>();
        prefixMap.put("OMIM", "https://omim.org/entry/");
        prefixMap.put("OMIMPS", "https://omim.org/phenotypicSeries/PS");
        prefixMap.put("Orphanet", "http://www.orpha.net/ORDO/Orphanet_");

        Converter converter = new Converter(prefixMap);
        converter.compress("https://www.ebi.ac.uk/ols/ontologies/doid/terms?obo_id=DOID:1234");
        // doid:1234
    }
}
```

## Loading an Extended Prefix Map

If you have the URL for an extended prefix map, you can do the following:

```java
import org.biopragmatics.curies.Converter;
import org.biopragmatics.curies.Loader;

class RemoteResourceDemo {
    public static void main(String[] args) {
        String url = "https://raw.githubusercontent.com/biopragmatics/bioregistry/main/exports/contexts/bioregistry.epm.json";
        Converter converter = new Converter(Loader.getRecords(url));
        converter.compress("https://www.ebi.ac.uk/ols/ontologies/doid/terms?obo_id=DOID:1234");
        // doid:1234
    }
}
```

Similarly, if you have a file object, you can do:

```java
import java.io.*;
import org.biopragmatics.curies.Converter;
import org.biopragmatics.curies.Loader;

class LocalResourceDemo {
    public static void main(String[] args) {
        File file = new File("/Users/cthoyt/dev/bioregistry/exporst/contexts/bioregistry.epm.json")
        Converter converter = new Converter(Loader.getRecords(file));
        converter.compress("https://www.ebi.ac.uk/ols/ontologies/doid/terms?obo_id=DOID:1234");
        // doid:1234
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

## Installation

See https://central.sonatype.com/artifact/org.biopragmatics.curies/curies

## Acknowledgements

Special thanks to [Jonas Schaub](https://github.com/JonasSchaub) for helping with packaging and deployment to Maven Central.

