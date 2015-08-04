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
import org.w3c.dom.NamedNodeMap;
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
        
        for (int i = 0; i < list.getLength(); i++) {
            Element element = (Element) list.item(i);
            if (element.hasAttribute("target")
                    && element.getAttribute("target").equals("font")) {
                System.out.println("render option");

                Element editElement = (Element) element.getElementsByTagName("edit").item(0);
                String name = editElement.getAttribute("name");
                
                // Checkout option value
                switch(name) {
                    case "antialias":
                        this.antialias = parseBool(editElement);
                        System.out.println("antialias: " + this.antialias);
                        break;
                }
                
            } else {
                System.out.println("font family");
            }
        }
    }
    
    private boolean parseBool(Node node) {
        NodeList children = node.getChildNodes();
        boolean value = false;
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if(child.getNodeName().equals("bool")) {
                value = Boolean.parseBoolean(child.getTextContent());
            }
        }
        return value;
    }
}
