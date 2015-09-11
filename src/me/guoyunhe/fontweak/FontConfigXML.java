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
package me.guoyunhe.fontweak;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Read and write fontconfig XML
 * @author Guo Yunhe <guoyunhebrave@gmail.com>
 */
public class FontConfigXML {
    // XML
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private File configFile;
    private Document doc;
    private Node root;
    
    // fontconfig options
    private boolean antialias = false;
    private boolean hinting = false;
    
    public static final int HINT_NONE   = 0;
    public static final int HINT_SLIGHT = 1;
    public static final int HINT_MEDIUM = 2;
    public static final int HINT_FULL   = 3;
    private static final String[] hintstyleOptions = {"hintnone", "hintslight", "hintmedium", "hintfull"};
    private int hintstyle = HINT_NONE;
    
    public static final int RGBA_NONE = 0;
    public static final int RGBA_RGB  = 1;
    public static final int RGBA_BGR  = 2;
    public static final int RGBA_VRGB = 3;
    public static final int RGBA_VBGR = 4;
    private static final String[] rgbaOptions = {"none", "rgb", "bgr", "vrgb", "vbgr"};
    private int rgba = RGBA_NONE;
    
    private static final String[] langOptions = {"en", "zh-cn", "zh-tw", "zh-hk", "ja", "ko"};
    public static final int EN    = 0;
    public static final int ZH_CN = 1;
    public static final int ZH_TW = 2;
    public static final int ZH_HK = 3;
    public static final int JA    = 4;
    public static final int KO    = 5;
    
    private static final String[] familyOptions = {"sans-serif", "serif", "monospace"};
    public static final int SANS  = 0;
    public static final int SERIF = 1;
    public static final int MONO  = 2;
    
    private final String[][] fontArray;
    
    private ArrayList<String[]> aliasList; // <alias>
    
    // System locale
    private Locale sysLocale;

    public FontConfigXML() {
        factory = DocumentBuilderFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();
            // Empty DTD reference, since it is missing in most system
            builder.setEntityResolver(new EntityResolver() {
                @Override
                public InputSource resolveEntity(String publicId, String systemId) {
                    if (systemId.contains("fonts.dtd")) {
                        return new InputSource(new StringReader(""));
                    } else {
                        return null;
                    }
                }
            });
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(FontConfigXML.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        fontArray = new String[langOptions.length][familyOptions.length];
        aliasList = new ArrayList();
        
        cleanConfigFiles();
        
        sysLocale = Locale.getDefault();
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
        } catch (SAXException ex) {
            Logger.getLogger(FontConfigXML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
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

        // Empty aliasList, load new values
        aliasList.clear();
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
        String name = edit.getAttribute("name");
        if (name.equals("rgba")) {
            rgba = rgbaDecode(parseConst(edit));
        } else if (name.equals("hinting")) {
            hinting = parseBool(edit);
        } else if (name.equals("hintstyle")) {
            hintstyle = hintstyleDecode(parseConst(edit));
        } else if (name.equals("antialias")) {
            antialias = parseBool(edit);
        }
    }
    
    /**
     * Find font family pattern in "match" element.
     * @param node The "match" XML element to be analyzed.
     */
    private void findPattern(Node node) {
        int testFamily = SANS;
        int testLanguage = EN;
        String editFamily = "";
        
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).hasAttributes()) {
                Element child = (Element) children.item(i);
                String nodeName = child.getNodeName();
                String name = child.getAttribute("name");
                if (nodeName.equals("test")) {
                    if (name.equals("family")) {
                        testFamily = familyDecode(parseString(child));
                    } else if (name.equals("lang")) {
                        testLanguage = langDecode(parseString(child));
                    }
                } else if (nodeName.equals("edit")) {
                    editFamily = parseString(child);
                }
            }
        }
        
        fontArray[testLanguage][testFamily] = editFamily;
    }
    
    private void findAlias(Node node) {
        NodeList children = node.getChildNodes();
        String originalFont = null;
        String preferFont = null;
        for(int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            String name = child.getNodeName();
            if (name.equals("family")) {
                originalFont = child.getTextContent().trim();
            } else if (name.equals("prefer")) {
                NodeList grandChildren = child.getChildNodes();
                for (int j = 0; j < grandChildren.getLength(); j++) {
                    Node grandChild = grandChildren.item(j);
                    if (grandChild.getNodeName().equals("family")) {
                        preferFont = grandChild.getTextContent().trim();
                    }
                }
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
        for (int i = 0; i < langOptions.length; i++) {
            for (int j = 0; j < familyOptions.length; j++) {
                String font = fontArray[i][j];
                if (validFont(font)) {
                    String lang = langEncode(i);
                    String family = familyEncode(j);
                    root.appendChild(makeFontFamilyMatch(family, lang, font));
                }
            }
        }
        
        for (String[] aliasPair : aliasList) {
            root.appendChild(makeAliasElement(aliasPair[0], aliasPair[1]));
        }
        
        root.appendChild(makeFontRenderMatch("antialias", "bool", Boolean.toString(antialias)));
        root.appendChild(makeFontRenderMatch("hinting", "bool", Boolean.toString(hinting)));
        root.appendChild(makeFontRenderMatch("hintstyle", "const", hintstyleEncode(hintstyle)));
        root.appendChild(makeFontRenderMatch("rgba", "const", rgbaEncode(rgba)));

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
        // Lower priority of current locale, see #20
        String binding = null;
        Locale locale;
        if (lang.equals("en")) {
            binding = "strong";
        } else if (lang.length() > 2) {
            locale = new Locale(lang.substring(0, 2), lang.substring(3));
            if (!sysLocale.getLanguage().equals(locale.getLanguage()) || !sysLocale.getCountry().equals(locale.getCountry())) {
                binding = "strong";
            }
        } else {
            locale = new Locale(lang);
            if (!sysLocale.getLanguage().equals(locale.getLanguage())) {
                binding = "strong";
            }
        }
        
        Element match = doc.createElement("match");
        match.appendChild(makeTestElement("family", "string", family, null));
        if (!lang.equals("en")) {
            match.appendChild(makeTestElement("lang", "string", lang, "contains"));
        }
        match.appendChild(makeEditElement("family", "string", font, "prepend", binding));
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
    
    private int rgbaDecode(String str) {
        for (int i = 0; i < rgbaOptions.length; i++) {
            if (rgbaOptions[i].equals(str)) {
                return i;
            }
        }
        return RGBA_NONE;
    }
    
    private String rgbaEncode(int num) {
        num = num % rgbaOptions.length;
        String str = rgbaOptions[num];
        return str;
    }
    
    private int hintstyleDecode(String str) {
        for (int i = 0; i < hintstyleOptions.length; i++) {
            if (hintstyleOptions[i].equals(str)) {
                return i;
            }
        }
        return RGBA_NONE;
    }
    
    private String hintstyleEncode(int num) {
        num = num % hintstyleOptions.length;
        String str = hintstyleOptions[num];
        return str;
    }
    
    private int langDecode(String str) {
        for (int i = 0; i < langOptions.length; i++) {
            if (langOptions[i].equals(str)) {
                return i;
            }
        }
        return EN;
    }
    
    private String langEncode(int num) {
        num = num % langOptions.length;
        String str = langOptions[num];
        return str;
    }
    
    private int familyDecode(String str) {
        for (int i = 0; i < familyOptions.length; i++) {
            if (familyOptions[i].equals(str)) {
                return i;
            }
        }
        return SANS;
    }
    
    private String familyEncode(int num) {
        num = num % familyOptions.length;
        String str = familyOptions[num];
        return str;
    }
    
    public String fontFallback(String[] fonts, List<String> list) {
        for (String font : fonts) {
            if (list.contains(font)) {
                return font;
            }
        }
        return null;
    }
    
    public void setFontFamily(int lang, int family, String font) {
        fontArray[lang][family] = font;
    }
    
    public String getFontFamily(int lang, int family) {
        return fontArray[lang][family];
    }
    
    public String[][] getDefaultFontFamilyArray(String[] list) {
        String[][] defaultFontArray = new String[langOptions.length][familyOptions.length];
        List fontList = Arrays.asList(list);

        defaultFontArray[EN][SANS] = fontFallback(new String[]{
            "Ubuntu", // Ubuntu pre-install
            "Nimbus Sans L",
            "DejaVu Sans",
            "Liberation Sans",
            "Droid Sans"
        }, fontList);
        
        defaultFontArray[EN][SERIF] = fontFallback(new String[]{
            "Nimbus Roman No9 L",
            "DejaVu Serif",
            "Liberation Serif",
            "Droid Serif"
        }, fontList);
        
        defaultFontArray[EN][MONO] = fontFallback(new String[]{
            "Ubuntu Mono", // Ubuntu pre-install
            "DejaVu Sans Mono",
            "Libration Mono",
            "Droid Sans Mono",
        }, fontList);
        
        defaultFontArray[ZH_HK][SANS] = fontFallback(new String[]{
            "WenQuanYi Micro Hei",
            "Droid Sans Fallback",
            "WenQuanYi Zen Hei"
        }, fontList);
        
        defaultFontArray[ZH_HK][SERIF] = fontFallback(new String[]{
            "AR PL UMing HK"
        }, fontList);
        
        defaultFontArray[ZH_HK][MONO] = fontFallback(new String[]{
            "WenQuanYi Micro Hei Mono",
        }, fontList);
        
        defaultFontArray[ZH_CN][SANS] = fontFallback(new String[]{
            "WenQuanYi Micro Hei",
            "Droid Sans Fallback",
            "WenQuanYi Zen Hei"
        }, fontList);
        
        defaultFontArray[ZH_CN][SERIF] = fontFallback(new String[]{
            "AR PL UMing CN"
        }, fontList);
        
        defaultFontArray[ZH_CN][MONO] = fontFallback(new String[]{
            "WenQuanYi Micro Hei Mono",
        }, fontList);
        
        defaultFontArray[ZH_TW][SANS] = fontFallback(new String[]{
            "WenQuanYi Micro Hei",
            "Droid Sans Fallback",
            "WenQuanYi Zen Hei"
        }, fontList);
        
        defaultFontArray[ZH_TW][SERIF] = fontFallback(new String[]{
            "AR PL UMing TW"
        }, fontList);
        
        defaultFontArray[ZH_TW][MONO] = fontFallback(new String[]{
            "WenQuanYi Micro Hei Mono",
        }, fontList);
        
        defaultFontArray[JA][SANS] = fontFallback(new String[]{
            "Droid Sans Japanese",
            "WenQuanYi Micro Hei",
            "WenQuanYi Zen Hei"
        }, fontList);
        
        defaultFontArray[JA][SERIF] = fontFallback(new String[]{
            "AR PL UMing TW"
        }, fontList);
        
        defaultFontArray[JA][MONO] = fontFallback(new String[]{
            "Droid Sans Japanese",
            "WenQuanYi Micro Hei Mono"
        }, fontList);
        
        defaultFontArray[KO][SANS] = fontFallback(new String[]{
            "WenQuanYi Micro Hei",
            "Droid Sans Fallback",
            "WenQuanYi Zen Hei"
        }, fontList);
        
        defaultFontArray[KO][SERIF] = fontFallback(new String[]{
            "AR PL UMing TW"
        }, fontList);
        
        defaultFontArray[KO][MONO] = fontFallback(new String[]{
            "WenQuanYi Micro Hei Mono"
        }, fontList);
        
        return defaultFontArray;
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
        hintstyle = style;
    }
    
    public int getHintStyle() {
        return hintstyle;
    }
    
    public void setSubpixel(int subpixel) {
        rgba = subpixel;
    }
    
    public int getSubpixel() {
        return rgba;
    }
    
    public ArrayList<String[]> getAliasList () {
        return aliasList;
    }
    
    public void setAliasList(ArrayList<String[]> aliasList) {
        this.aliasList = aliasList;
    }
}
