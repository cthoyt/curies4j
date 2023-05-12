package org.biopragmatics.curies;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit test for the Converter
 */
public class ConverterTest {
    private Converter simpleConverter;
    private Converter complexConverter;

    @Before
    public void setUp() {
        Map<String, String> map = new HashMap<>();
        // Adding elements to map
        map.put("CHEBI", "http://purl.obolibrary.org/obo/CHEBI_");
        map.put("MONDO", "http://purl.obolibrary.org/obo/MONDO_");
        map.put("GO", "http://purl.obolibrary.org/obo/GO_");
        map.put("OBO", "http://purl.obolibrary.org/obo/");
        simpleConverter = new Converter(map);

        complexConverter = Converter.getExampleConverter();
    }


    @Test
    public void testParseCURIE() {
        Reference reference = simpleConverter.parseCURIE("CHEBI:1234");
        assertEquals("CHEBI", reference.getPrefix());
        assertEquals("1234", reference.getIdentifier());
        assertEquals("CHEBI:1234", reference.getCURIE());
    }

    @Test
    public void testParseURI() {
        Reference reference = simpleConverter.parseURI("http://purl.obolibrary.org/obo/CHEBI_1234");
        assertEquals("CHEBI", reference.getPrefix());
        assertEquals("1234", reference.getIdentifier());
        assertEquals("CHEBI:1234", reference.getCURIE());
    }

    @Test
    public void testParseURIMissing() {
        // Test an invalid example
        assertNull(simpleConverter.parseURI("http://nopenope"));
    }


    @Test
    public void testCompress() {
        assertEquals("CHEBI:1234", simpleConverter.compress("http://purl.obolibrary.org/obo/CHEBI_1234"));
    }

    @Test
    public void testCompressMissing() {
        assertNull(simpleConverter.compress("http://example.org/nope_nope"));
    }

    @Test
    public void testExpand() {
        assertEquals("http://purl.obolibrary.org/obo/CHEBI_1234", simpleConverter.expand("CHEBI:1234"));
        assertEquals("http://purl.obolibrary.org/obo/CHEBI_1234", simpleConverter.expand("CHEBI", "1234"));
        assertNull(simpleConverter.expand("notacurie"));
    }

    @Test
    public void testExpandMissing() {
        assertNull(simpleConverter.expand("NOPE:NOPE"));
    }

    @Test
    public void testStandardizePrefix() {
        assertEquals("CHEBI", simpleConverter.standardizePrefix("CHEBI"));
        assertNull(simpleConverter.standardizePrefix("chebi"));

        assertEquals("chebi", complexConverter.standardizePrefix("CHEBI"));
        assertEquals("chebi", complexConverter.standardizePrefix("chebi"));
    }

    @Test
    public void testStandardizeCURIE() {
        assertEquals("chebi:1234", complexConverter.standardizeCURIE("CHEBI:1234"));
        assertEquals("chebi:1234", complexConverter.standardizeCURIE("chebi:1234"));
        assertNull(complexConverter.standardizeCURIE("nope:nope"));
        assertNull(complexConverter.standardizeCURIE("notacurie"));
    }

    @Test
    public void testStandardizeURI() {
        assertEquals("http://purl.obolibrary.org/obo/CHEBI_1234", complexConverter.standardizeURI("http://purl.obolibrary.org/obo/CHEBI_1234"));
        assertEquals("http://purl.obolibrary.org/obo/CHEBI_1234", complexConverter.standardizeURI("https://bioregistry.io/chebi:1234"));
        assertNull(complexConverter.standardizeURI("https://nope.nope/chebi:1234"));
    }
}
