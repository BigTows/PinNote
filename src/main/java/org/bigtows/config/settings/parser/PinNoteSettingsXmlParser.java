package org.bigtows.config.settings.parser;

import org.bigtows.config.settings.EvernoteSettings;
import org.bigtows.config.settings.PinNoteSettings;
import org.bigtows.config.settings.parser.exception.ParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Class for parsing XML file to Object
 * <pr>
 * If you love XML, and know how parse XML better.
 * Please pull request me :)
 * I am lazy about XML...
 * </pr>
 */
public class PinNoteSettingsXmlParser implements PinNoteSettingsParser {

    private static final String ROOT_BLOCK_NAME = "PinNote";

    private Node rootNode;

    private PinNoteSettings pinNoteSettings;

    public PinNoteSettingsXmlParser(String settingsFile) throws ParserException {
        this.initializeRootNote(settingsFile);
    }


    private void initializeRootNote(String settingsFile) throws ParserException {
        Document document = this.tryGetDocument(settingsFile);
        rootNode = document.getDocumentElement();
    }

    /**
     * Try get DOM
     *
     * @param settingsFile file
     * @return Document
     * @throws ParserException on trouble with parsing
     */
    private Document tryGetDocument(String settingsFile) throws ParserException {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return documentBuilder.parse(settingsFile);
        } catch (Exception e) {
            throw new ParserException("Failed parse file: " + settingsFile, e);
        }
    }

    @Override
    public PinNoteSettings getPinNoteSettings() throws ParserException {
        if (null == pinNoteSettings) {
            pinNoteSettings = this.parseDom();
        }
        return pinNoteSettings;
    }


    private PinNoteSettings parseDom() throws ParserException {
        if (!rootNode.getNodeName().equals(ROOT_BLOCK_NAME)) {
            throw new ParserException("Invalid structure XML");
        }

        NodeList list = rootNode.getChildNodes();
        for (int index = 0; index < list.getLength(); index++) {
            Node node = list.item(index);
            if (node.getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            if (!node.getNodeName().equals("settings")) {
                //That's because, now ONLY one block can be place in there
                throw new ParserException("Invalid structure XML");
            }
            return this.parseSettingsBlock(node);
        }
        throw new ParserException("Invalid structure XML");
    }

    private PinNoteSettings parseSettingsBlock(Node settingsBlockNode) throws ParserException {
        NodeList list = settingsBlockNode.getChildNodes();
        //TODO create Builder
        Boolean isDebug = null;
        EvernoteSettings settings = null;

        for (int index = 0; index < list.getLength(); index++) {
            Node node = list.item(index);

            if (node.getNodeType() == Node.TEXT_NODE) {
                continue;
            }

            //Letter create good routing
            if (node.getNodeName().equals("debug")) {
                isDebug = Boolean.parseBoolean(node.getAttributes().getNamedItem("enable").getNodeValue());
            } else if (node.getNodeName().equals("Evernote")) {
                //This dirty code
                settings = new EvernoteSettings(
                        node.getChildNodes().item(1).getAttributes().getNamedItem("url").getNodeValue()
                );
            } else {
                throw new ParserException("Invalid structure XML, Settings block");
            }


        }
        return new PinNoteSettings(isDebug, settings);
    }

}
