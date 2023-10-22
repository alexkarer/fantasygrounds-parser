package at.karer.fantasygroundsparser.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class JacksonConfig {

    private static final XmlMapper xmlMapper = new XmlMapper.Builder(new XmlMapper())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();

    public static XmlMapper getXmlMapperInstance() {
        return xmlMapper;
    }
}
