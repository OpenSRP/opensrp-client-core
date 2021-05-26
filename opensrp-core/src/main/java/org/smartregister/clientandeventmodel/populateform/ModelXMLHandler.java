package org.smartregister.clientandeventmodel.populateform;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuelgithengi on 1/23/18.
 */

public class ModelXMLHandler extends DefaultHandler {
    private Boolean currentElement = false;
    private String currentValue = "";
    private List<Model> tags;
    private Model model;
    private String eventType;

    // Called when tag starts
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        currentElement = true;
        currentValue = "";
        if ("instance".equals(localName))
            tags = new ArrayList<>();
        else if (tags != null && tags.isEmpty() && StringUtils.isNotEmpty(attributes.getValue("encounter_type"))) {
            eventType = attributes.getValue("encounter_type");
        } else if (tags != null) {
            model = new Model(localName, attributes.getValue("openmrs_entity"),
                    attributes.getValue("openmrs_entity_id"), attributes.getValue("openmrs_entity_parent"));
        }

    }

    // Called when tag closing
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        currentElement = false;
        if ("instance".equals(localName)) {
            throw new SAXTerminationException("Finished processing model");
        } else {
            tags.add(model);
        }

    }

    // Called to get tag characters
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {

        if (currentElement) {
            currentValue = currentValue + new String(ch, start, length);
        }

    }

    public List<Model> getTags() {
        return tags;
    }

    public String getEventType() {
        return eventType;
    }
}
