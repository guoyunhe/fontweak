/*
 * Copyright (C) 2015 Guo Yunhe <guoyunhebrave@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.guoyunhe.fcm;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Read and write fontconfig XML
 * @author Guo Yunhe <guoyunhebrave@gmail.com>
 */
public class FontConfigXML {
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private File configFile;
    private Document doc;
    
    private boolean antialias;
    private boolean hinting;
    private String hintstyle;
    private String rgba;
    
    private String sans;
    private String serif;
    private String mono;
    private String zhSans;
    private String zhSerif;
    private String zhMono;
    private String jaSans;
    private String jaSerif;
    private String jaMono;
    private String koSans;
    private String koSerif;
    private String koMono;

    public FontConfigXML() {
        factory = DocumentBuilderFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(FontConfigXML.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.cleanConfigFiles();
        
        this.readConfig();
    }
    
    /**
     * Clean up old fontconfig configuration files in other positions, avoid
     * confusing and conflicts.
     */
    private void cleanConfigFiles() {
        String userHome = System.getProperty("user.home");
        File oldConfigFile = new File(userHome + "/.fonts.conf"); // Old path
        File newConfigFile = new File(userHome + "/.config/fontconfig/fonts.conf");
        
        if (newConfigFile.exists()) {
            oldConfigFile.delete();
        } else if (oldConfigFile.exists()) {
            oldConfigFile.renameTo(newConfigFile);
        }
        // TODO When user configuration file does not exist, create standard
        // configuration file.
        configFile = newConfigFile;
    }
    
    /**
     * Read configuration XML.
     */
    private void readConfig() {
        // Empty DTD reference, since it is missing in most system
        builder.setEntityResolver((String publicId, String systemId) -> {
            if (systemId.contains("fonts.dtd")) {
                return new InputSource(new StringReader(""));
            } else {
                return null;
            }
        });

        // Parse DOM from XML
        try {
            doc = builder.parse(configFile);
        } catch (SAXException | IOException ex) {
            Logger.getLogger(FontConfigXML.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Read elements
        NodeList list = doc.getElementsByTagName("match");
        
        // Debug information
        System.out.println(list.getLength());
        
        for (int i = 0; i < list.getLength(); i++) {
            // Debug information
            System.out.println(i);
            
            Element element = (Element) list.item(i);
            if (element.hasAttribute("target")) {
                this.findOption(element);
            } else {
                this.findPattern(element);
            }
        }
    }
    
    private void findOption(Node node) {
        if (!node.hasAttributes()) {
            return;
        }
        Element element = (Element)node;
        Element edit = (Element)element.getElementsByTagName("edit").item(0);
        switch (edit.getAttribute("name")) {
            case "rgba":
                this.rgba = this.parseConst(edit);
                break;
            case "hinting":
                this.hinting = this.parseBool(edit);
                break;
            case "hintstyle":
                this.hintstyle = this.parseConst(edit);
                break;
            case "antialias":
                this.antialias = this.parseBool(edit);
                break;
        }
        
        // Debug information
        System.out.println(edit.getAttribute("name"));
    }
    
    /**
     * Find font family pattern in "match" element.
     * @param node The "match" XML element to be analyzed.
     */
    private void findPattern(Node node) {
        String testFamily = "";
        String testLanguage = "";
        String editFamily = "";
        
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).hasAttributes()) {
                Element child = (Element) children.item(i);
                switch (child.getNodeName()) {
                    case "test":
                        switch (child.getAttribute("name")) {
                            case "family":
                                testFamily = this.parseString(child);
                                break;
                            case "lang":
                                testLanguage = this.parseString(child);
                                break;
                        }
                        break;
                    case "edit":
                        if (child.getAttribute("name").equals("family")) {
                            editFamily = this.parseString(child);
                        }
                        break;
                }
            }
        }
        
        switch (testFamily) {
            case "sans-serif":
            case "sans":
                switch (testLanguage) {
                    case "zh":
                    case "zh-cn":
                    case "zh-tw":
                        this.zhSans = editFamily;
                        break;
                    case "ja":
                        this.jaSans = editFamily;
                        break;
                    case "ko":
                        this.koSans = editFamily;
                        break;
                    default:
                        this.sans = editFamily;
                        break;
                }
                break;
            case "serif":
                switch (testLanguage) {
                    case "zh":
                    case "zh-cn":
                    case "zh-tw":
                        this.zhSerif = editFamily;
                        break;
                    case "ja":
                        this.jaSerif = editFamily;
                        break;
                    case "ko":
                        this.koSerif = editFamily;
                        break;
                    default:
                        this.serif = editFamily;
                        break;
                }
                break;
            case "mono":
            case "monospace":
                switch (testLanguage) {
                    case "zh":
                    case "zh-cn":
                    case "zh-tw":
                        this.zhMono = editFamily;
                        break;
                    case "ja":
                        this.jaMono = editFamily;
                        break;
                    case "ko":
                        this.koMono = editFamily;
                        break;
                    default:
                        this.mono = editFamily;
                        break;
                }
                break;
        }
        // Debug information
        System.out.println(testFamily + " " + testLanguage + ": " + editFamily);
    }
    
    /**
     * Paser the boolean value inside of "test" or "edit" tags.
     * @param node An edit or test node that contains boolean value.
     * @return Boolean value of fontconfig XML tag.
     */
    private boolean parseBool(Node node) {
        NodeList children = node.getChildNodes();
        boolean value = false;
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if(child.getNodeName().equals("bool")) {
                value = Boolean.parseBoolean(child.getTextContent());
                break;
            }
        }
        return value;
    }
    
    private String parseConst(Node node) {
        String value = "";
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if(child.getNodeName().equals("const")) {
                value = child.getTextContent();
                break;
            }
        }
        return value;
    }
    
    private String parseString(Node node) {
        String value = "";
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if(child.getNodeName().equals("string")) {
                value = child.getTextContent();
                break;
            }
        }
        return value;
    }
}
