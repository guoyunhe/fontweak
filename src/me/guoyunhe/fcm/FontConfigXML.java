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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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
    private Node root;
    
    private boolean antialias = false;
    private boolean hinting = false;
    private String hintstyle = "hintnone";
    private String rgba = "none";
    
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
    
    private ArrayList<String[]> aliasList;
    
    public static final int HINT_NONE = 0;
    public static final int HINT_SLIGHT = 1;
    public static final int HINT_MEDIUM = 2;
    public static final int HINT_FULL = 3;

    public static final int RGBA_NONE = 0;
    public static final int RGBA_RGB = 1;
    public static final int RGBA_BGR = 2;
    public static final int RGBA_VRGB = 3;
    public static final int RGBA_VBGR = 4;

    public FontConfigXML() {
        factory = DocumentBuilderFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();
            // Empty DTD reference, since it is missing in most system
            builder.setEntityResolver((String publicId, String systemId) -> {
                if (systemId.contains("fonts.dtd")) {
                    return new InputSource(new StringReader(""));
                } else {
                    return null;
                }
            });
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(FontConfigXML.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        aliasList = new ArrayList();
        
        cleanConfigFiles();
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
     * Read fontconfig configuration file (XML).
     * @param xml Fontconfig XML file to be read
     */
    public void readConfig(File xml) {
        // Parse DOM from XML
        try {
            doc = builder.parse(xml);
        } catch (SAXException | IOException ex) {
            Logger.getLogger(FontConfigXML.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Read elements
        root = doc.getElementsByTagName("fontconfig").item(0);
        NodeList matchElements = doc.getElementsByTagName("match");
        NodeList aliasElements = doc.getElementsByTagName("alias");
        
        for (int i = 0; i < matchElements.getLength(); i++) {
            Element element = (Element) matchElements.item(i);
            if (element.hasAttribute("target")) {
                findOption(element);
            } else {
                findPattern(element);
            }
        }

        for (int i = 0; i < aliasElements.getLength(); i++) {
            findAlias(aliasElements.item(i));
        }
    }
    
    /**
     * Read default fontconfig configuration file.
     * Located at `~/.config/fontconfig/fonts.conf`
     */
    public void readConfig() {
        readConfig(configFile);
    }
    
    private void findOption(Node node) {
        if (!node.hasAttributes()) {
            return;
        }
        Element element = (Element)node;
        Element edit = (Element)element.getElementsByTagName("edit").item(0);
        switch (edit.getAttribute("name")) {
            case "rgba":
                rgba = parseConst(edit);
                break;
            case "hinting":
                hinting = parseBool(edit);
                break;
            case "hintstyle":
                hintstyle = parseConst(edit);
                break;
            case "antialias":
                antialias = parseBool(edit);
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
                                testFamily = parseString(child);
                                break;
                            case "lang":
                                testLanguage = parseString(child);
                                break;
                        }
                        break;
                    case "edit":
                        if (child.getAttribute("name").equals("family")) {
                            editFamily = parseString(child);
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
                        zhSans = editFamily;
                        break;
                    case "ja":
                        jaSans = editFamily;
                        break;
                    case "ko":
                        koSans = editFamily;
                        break;
                    default:
                        sans = editFamily;
                        break;
                }
                break;
            case "serif":
                switch (testLanguage) {
                    case "zh":
                    case "zh-cn":
                    case "zh-tw":
                        zhSerif = editFamily;
                        break;
                    case "ja":
                        jaSerif = editFamily;
                        break;
                    case "ko":
                        koSerif = editFamily;
                        break;
                    default:
                        serif = editFamily;
                        break;
                }
                break;
            case "mono":
            case "monospace":
                switch (testLanguage) {
                    case "zh":
                    case "zh-cn":
                    case "zh-tw":
                        zhMono = editFamily;
                        break;
                    case "ja":
                        jaMono = editFamily;
                        break;
                    case "ko":
                        koMono = editFamily;
                        break;
                    default:
                        mono = editFamily;
                        break;
                }
                break;
        }
        // Debug information
        System.out.println(testFamily + " " + testLanguage + ": " + editFamily);
    }
    
    private void findAlias(Node node) {
        NodeList children = node.getChildNodes();
        String originalFont = null;
        String preferFont = null;
        for(int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            switch (child.getNodeName()) {
                case "family":
                    originalFont = child.getTextContent().trim();
                    break;
                case "prefer":
                    NodeList grandChildren = child.getChildNodes();
                    for (int j = 0; j < grandChildren.getLength(); j++) {
                        Node grandChild = grandChildren.item(j);
                        if (grandChild.getNodeName().equals("family")) {
                            preferFont = grandChild.getTextContent().trim();
                        }
                    }
                    break;
            }
        }
        if (originalFont != null && preferFont != null) {
            String pair[] = {originalFont, preferFont};
            aliasList.add(pair);
        }
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
    
    /**
     * Write changes to fontconfig configuration file (XML).
     * @param xml The configuration file to be saved.
     */
    public void writeConfig(File xml) {
        // Clean old nodes
        NodeList list = doc.getElementsByTagName("match");
        while (list.getLength() > 0) {
            Node n = list.item(0);
            n.getParentNode().removeChild(n);
        }
        NodeList list2 = doc.getElementsByTagName("alias");
        while (list2.getLength() > 0) {
            Node n = list2.item(0);
            n.getParentNode().removeChild(n);
        }
        // Clean empty text nodes
        NodeList childList = root.getChildNodes();
        for (int i = 0; i < childList.getLength(); i++) {
            Node n = childList.item(i);
            if (n.getNodeType() == Node.TEXT_NODE) {
                n.getParentNode().removeChild(n);
                i--;
            }
        }

        // Save option values to document elements
        if (validFont(zhSans)) {
            root.appendChild(makeFontFamilyMatch("sans-serif", "zh", zhSans));
        }
        if (validFont(zhSerif)) {
            root.appendChild(makeFontFamilyMatch("serif", "zh", zhSerif));
        }
        if (validFont(zhMono)) {
            root.appendChild(makeFontFamilyMatch("monospace", "zh", zhMono));
        }
        if (validFont(jaSans)) {
            root.appendChild(makeFontFamilyMatch("sans-serif", "ja", jaSans));
        }
        if (validFont(jaSerif)) {
            root.appendChild(makeFontFamilyMatch("serif", "ja", jaSerif));
        }
        if (validFont(jaMono)) {
            root.appendChild(makeFontFamilyMatch("monospace", "ja", jaMono));
        }
        if (validFont(koSans)) {
            root.appendChild(makeFontFamilyMatch("sans-serif", "ko", koSans));
        }
        if (validFont(koSerif)) {
            root.appendChild(makeFontFamilyMatch("serif", "ko", koSerif));
        }
        if (validFont(koMono)) {
            root.appendChild(makeFontFamilyMatch("monospace", "ko", koMono));
        }
        if (validFont(sans)) {
            root.appendChild(makeFontFamilyMatch("sans-serif", null, sans));
        }
        if (validFont(serif)) {
            root.appendChild(makeFontFamilyMatch("serif", null, serif));
        }
        if (validFont(mono)) {
            root.appendChild(makeFontFamilyMatch("monospace", null, mono));
        }
        
        for (String[] aliasPair : aliasList) {
            root.appendChild(makeAliasElement(aliasPair[0], aliasPair[1]));
        }
        
        root.appendChild(makeFontRenderMatch("antialias", "bool", Boolean.toString(antialias)));
        root.appendChild(makeFontRenderMatch("hinting", "bool", Boolean.toString(hinting)));
        root.appendChild(makeFontRenderMatch("hintstyle", "const", hintstyle));
        root.appendChild(makeFontRenderMatch("rgba", "const", rgba));
        
        // Write document object to XML file.
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Result output = new StreamResult(xml);
            Source input = new DOMSource(doc);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(input, output);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(FontConfigXML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(FontConfigXML.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Write changes to default fontconfig configuration file (XML).
     * Located at `~/.config/fontconfig/fonts.conf`.
     */
    public void writeConfig() {
        writeConfig(configFile);
    }
    
    private Element makeFontFamilyMatch(String family, String lang, String font) {
        Element match = doc.createElement("match");
        match.appendChild(makeTestElement("family", "string", family, null));
        if (lang != null && !lang.isEmpty()) {
            match.appendChild(makeTestElement("lang", "string", lang, "contains"));
            match.appendChild(makeEditElement("family", "string", font, "prepend", null));
        } else {
            match.appendChild(makeEditElement("family", "string", font, "prepend", "strong"));
        }
        return match;
    }
    
    private Element makeFontRenderMatch(String name, String type, String value) {
        Element match = doc.createElement("match");
        match.setAttribute("target", "font");
        match.appendChild(makeEditElement(name, type, value, "assign", null));
        return match;
    }
    
    private Element makeEditElement(String name, String type, String value,
            String mode, String binding) {
        Element editElement = doc.createElement("edit");
        editElement.setAttribute("name", name);
        if (mode != null && !mode.isEmpty()) {
            editElement.setAttribute("mode", mode);
        }
        if (binding != null && !binding.isEmpty()) {
            editElement.setAttribute("binding", binding);
        }
        Element valueElement = doc.createElement(type);
        valueElement.setTextContent(value);
        editElement.appendChild(valueElement);
        return editElement;
    }
    
    private Element makeTestElement(String name, String type, String value,
            String compare) {
        Element testElement = doc.createElement("test");
        testElement.setAttribute("name", name);
        if (compare != null && !compare.isEmpty()) {
            testElement.setAttribute("compare", compare);
        }
        Element valueElement = doc.createElement(type);
        valueElement.setTextContent(value);
        testElement.appendChild(valueElement);
        return testElement;
    }
    
    private boolean validFont(String font) {
        return !(font == null || font.isEmpty());
    }
    
    private Element makeAliasElement(String font, String alias) {
        Element aliasElement = doc.createElement("alias");
        aliasElement.setAttribute("binding", "strong");
        Element familyElement = doc.createElement("family");
        familyElement.setTextContent(font);
        aliasElement.appendChild(familyElement);
        Element preferElement = doc.createElement("prefer");
        Element preferFamilyElement = doc.createElement("family");
        preferFamilyElement.setTextContent(alias);
        preferElement.appendChild(preferFamilyElement);
        aliasElement.appendChild(preferElement);
        return aliasElement;
    }
    
    public void setSans(String font) {
        sans = font;
    }
    
    public void setSerif(String font) {
        serif = font;
    }
    
    public void setMono(String font) {
        mono = font;
    }
    
    public void setZhSans(String font) {
        zhSans = font;
    }
    
    public void setZhSerif(String font) {
        zhSerif = font;
    }
    
    public void setZhMono(String font) {
        zhMono = font;
    }
    
    public void setJaSans(String font) {
        jaSans = font;
    }
    
    public void setJaSerif(String font) {
        jaSerif = font;
    }
    
    public void setJaMono(String font) {
        jaMono = font;
    }
    
    public void setKoSans(String font) {
        koSans = font;
    }
    
    public void setKoSerif(String font) {
        koSerif = font;
    }
    
    public void setKoMono(String font) {
        koMono = font;
    }
    
    public String getSans() {
        return sans;
    }
    
    public String getSerif() {
        return serif;
    }
    
    public String getMono() {
        return mono;
    }
    
    public String getZhSans() {
        return zhSans;
    }
    
    public String getZhSerif() {
        return zhSerif;
    }
    
    public String getZhMono() {
        return zhMono;
    }
    
    public String getJaSans() {
        return jaSans;
    }
    
    public String getJaSerif() {
        return jaSerif;
    }
    
    public String getJaMono() {
        return jaMono;
    }
    
    public String getKoSans() {
        return koSans;
    }
    
    public String getKoSerif() {
        return koSerif;
    }
    
    public String getKoMono() {
        return koMono;
    }
    
    public void setAntiAlias(boolean antialias) {
        this.antialias = antialias;
    }
    
    public boolean getAntiAlias() {
        return antialias;
    }
    
    public void setHinting(boolean hinting) {
        this.hinting = hinting;
    }
    
    public boolean getHinting() {
        return hinting;
    }
    
    public void setHintStyle(int style) {
        switch (style) {
            case HINT_NONE:
                hintstyle = "hintnone";
                break;
            case HINT_SLIGHT:
                hintstyle = "hintslight";
                break;
            case HINT_MEDIUM:
                hintstyle = "hintmedium";
                break;
            case HINT_FULL:
                hintstyle = "hintfull";
                break;
            default:
                hintstyle = "hintnone";
                break;
        }
    }
    
    public int getHintStyle() {
        switch (hintstyle) {
            case "hintnone":
                return HINT_NONE;
            case "hintslight":
                return HINT_SLIGHT;
            case "hintmedium":
                return HINT_MEDIUM;
            case "hintfull":
                return HINT_FULL;
            default:
                return HINT_NONE;
        }
    }
    
    public void setSubpixel(int rgba) {
        switch (rgba) {
            case RGBA_NONE:
                this.rgba = "none";
                break;
            case RGBA_RGB:
                this.rgba = "rgb";
                break;
            case RGBA_BGR:
                this.rgba = "bgr";
                break;
            case RGBA_VRGB:
                this.rgba = "vrgb";
                break;
            case RGBA_VBGR:
                this.rgba = "vbgr";
                break;
            default:
                this.rgba = "none";
                break;
        }
    }
    
    public int getSubpixel() {
        switch (rgba) {
            case "none":
                return RGBA_NONE;
            case "rgb":
                return RGBA_RGB;
            case "bgr":
                return RGBA_BGR;
            case "vrgb":
                return RGBA_VRGB;
            case "vbgr":
                return RGBA_VBGR;
            default:
                return RGBA_NONE;
        }
    }
    
    public ArrayList<String[]> getAliasList () {
        return aliasList;
    }
    
    public void setAliasList(ArrayList<String[]> aliasList) {
        this.aliasList = aliasList;
    }
}
