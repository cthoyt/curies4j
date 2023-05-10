package org.biopragmatics.curies;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit test for the Converter
 */
public class ConverterTest {
    private Converter converter;

    @Before
    public void setUp() {
        Map<String, String> map = new HashMap<>();
        // Adding elements to map
        map.put("CHEBI", "http://purl.obolibrary.org/obo/CHEBI_");
        map.put("MONDO", "http://purl.obolibrary.org/obo/MONDO_");
        map.put("GO", "http://purl.obolibrary.org/obo/GO_");
        map.put("OBO", "http://purl.obolibrary.org/obo/");

        converter = new Converter(map);
    }


    @Test
    public void testParseCURIE() {
        Reference reference = converter.parseCURIE("CHEBI:1234");
        assertEquals("CHEBI", reference.getPrefix());
        assertEquals("1234", reference.getIdentifier());
        assertEquals("CHEBI:1234", reference.getCURIE());
    }

    @Test
    public void testParseURI() {
        Reference reference = converter.parseURI("http://purl.obolibrary.org/obo/CHEBI_1234");
        assertEquals("CHEBI", reference.getPrefix());
        assertEquals("1234", reference.getIdentifier());
        assertEquals("CHEBI:1234", reference.getCURIE());
    }

    @Test
    public void testParseURIMissing() {
        // Test an invalid example
        assertNull(converter.parseURI("http://nopenope"));
    }


    @Test
    public void testCompress() {
        assertEquals("CHEBI:1234", converter.compress("http://purl.obolibrary.org/obo/CHEBI_1234"));
    }

    @Test
    public void testCompressMissing() {
        assertNull(converter.compress("http://example.org/nope_nope"));
    }

    @Test
    public void testExpand() {
        assertEquals("http://purl.obolibrary.org/obo/CHEBI_1234", converter.expand("CHEBI:1234"));
    }

    @Test
    public void testExpandMissing() {
        assertNull(converter.expand("NOPE:NOPE"));
    }
}
