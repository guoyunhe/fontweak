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
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
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
 * Read and write FontConfig XML document.
 *
 * File path: "~/fonts.conf" (legacy) or "~/.config/fontconfig/fonts.conf"
 *
 * @author Guo Yunhe guoyunhebrave@gmail.com
 */
public class FontConfig {
    // XML
    private DocumentBuilder builder;
    private File file;
    private Document doc;
    private Element root;

    public List<FontMatch> matchList;
    public FontMatch sansMatch;
    public FontMatch serifMatch;
    public FontMatch monoMatch;
    public List<FontAlias> aliasList;

    // fontconfig options
    public boolean antialias = true;
    public boolean hinting = true;
    public String hintstyle = "hintfull";
    public String rgba = "none";
    public String lcdfilter = "lcddefault";
    public boolean embeddedbitmap = true;

    public static final String[] HINTSTYLE_OPTIONS = {"hintnone", "hintslight", "hintmedium", "hintfull"};
    public static final String[] RGBA_OPTIONS = {"none", "rgb", "bgr", "vrgb", "vbgr"};
    public static final String[] LCDFILTER_OPTIONS = {"lcdnone", "lcddefault", "lcdlight", "lcdlegacy"};
    public static final String[] LANGUAGES = {
        "aa", "ab", "af", "ak", "am", "an", "ar", "as", "ast", "av", "ay", "az-az", "az-ir", "ba", "be", "ber-dz", "ber-ma", "bg", "bho", "bh", "bin", "bi", "bm", "bn", "bo", "br", "brx", "bs", "bua", "byn", "ca", "ce", "chm", "ch", "chr", "co", "crh", "csb", "cs", "cu", "cv", "cy", "da", "de", "doi", "dv", "dz", "ee", "el", "en", "eo", "es", "et", "eu", "fa", "fat", "ff", "fil", "fi", "fj", "fo", "fr", "fur", "fy", "ga", "gd", "gez", "gl", "gn", "gu", "gv", "ha", "haw", "he", "hi", "hne", "ho", "hr", "hsb", "ht", "hu", "hy", "hz", "ia", "id", "ie", "ig", "ii", "ik", "io", "is", "it", "iu", "ja", "jv", "kaa", "kab", "ka", "ki", "kj", "kk", "kl", "km", "kn", "kok", "ko", "kr", "ks", "ku-am", "ku-iq", "ku-ir", "kum", "ku-tr", "kv", "kwm", "kw", "ky", "lah", "la", "lb", "lez", "lg", "li", "ln", "lo", "lt", "lv", "mai", "mg", "mh", "mi", "mk", "ml", "mn-cn", "mni", "mn-mn", "mo", "mr", "ms", "mt", "my", "na", "nb", "nds", "ne", "ng", "nl", "nn", "no", "nqo", "nr", "nso", "nv", "ny", "oc", "om", "or", "os", "ota", "pa", "pap-an", "pap-aw", "pa-pk", "pl", "ps-af", "ps-pk", "pt", "qu", "quz", "rm", "rn", "ro", "ru", "rw", "sah", "sa", "sat", "sco", "sc", "sd", "sel", "se", "sg", "sh", "shs", "sid", "si", "sk", "sl", "sma", "smj", "smn", "sm", "sms", "sn", "so", "sq", "sr", "ss", "st", "su", "sv", "sw", "syr", "ta", "te", "tg", "th", "ti-er", "ti-et", "tig", "tk", "tl", "tn", "to", "tr", "ts", "tt", "tw", "ty", "tyv", "ug", "uk", "ur", "uz", "ve", "vi", "vo", "vot", "wal", "wa", "wen", "wo", "xh", "yap", "yi", "yo", "za", "zh-cn", "zh-hk", "zh-mo", "zh-sg", "zh-tw", "zu"
    };

    public FontConfig() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
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
            Logger.getLogger(FontConfig.class.getName()).log(Level.SEVERE, null, ex);
        }

        String userHome = System.getProperty("user.home");
        file = new File(userHome + "/.config/fontconfig/fonts.conf");
        File legacyConfigFile = new File(userHome + "/.fonts.conf"); // Old path
        if (file.exists() && legacyConfigFile.exists()) {
            legacyConfigFile.delete(); // Delete legacy
        } else if (!file.exists() && legacyConfigFile.exists()) {
            legacyConfigFile.renameTo(file); // Migrate legacy
        } else if (!file.exists() && !legacyConfigFile.exists()) {
            // Create default if no config file found
            InputStream in = getClass().getResourceAsStream("/me/guoyunhe/fontweak/config/default.conf");
            try {
                file.getParentFile().mkdirs(); // Make all parents folders if not exists
                Files.copy(in, file.toPath()); // Copty default config file
            } catch (IOException ex) {
                Logger.getLogger(FontConfig.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        matchList = new ArrayList<FontMatch>();
        aliasList = new ArrayList<FontAlias>();
    }


    /**
     * Read fontconfig configuration file (XML).
     */
    public void readConfig() {
        // Parse DOM
        try {
            doc = builder.parse(file);
        } catch (SAXException ex) {
            Logger.getLogger(FontConfig.class.getName()).log(Level.SEVERE, null, ex);
            // Copy default config file if the XML file is invalid
            InputStream in = getClass().getResourceAsStream("/me/guoyunhe/fontweak/config/default.conf");
            try {
                file.delete();
                Files.copy(in, file.toPath()); // Copty default config file
                doc = builder.parse(file);
            } catch (IOException | SAXException ex1) {
                Logger.getLogger(FontConfig.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (IOException ex) {
            Logger.getLogger(FontConfig.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Read elements
        root = (Element) doc.getElementsByTagName("fontconfig").item(0);
        NodeList matchElements = root.getElementsByTagName("match");
        NodeList aliasElements = root.getElementsByTagName("alias");

        sansMatch = null;
        serifMatch = null;
        monoMatch = null;
        antialias = true;
        hinting = true;
        hintstyle = "hintnone";
        rgba = "none";
        lcdfilter = "lcddefault";
        embeddedbitmap = true;
        matchList.clear();
        aliasList.clear();

        /* When parsing DOM, nodes in matchElements list will be removed one by
         * one. So here we use while loop other than for loop.
        */
        while (matchElements.getLength() > 0) {
            Element element;

            element = (Element) matchElements.item(0);

            if (element.hasAttribute("target") && element.getAttribute("target").equals("font")) {
                parseOptionDOM(element);
            } else {
                FontMatch match;

                match = new FontMatch();
                match.parseDOM(element);
                if (!match.isEmpty()) {
                    matchList.add(match);
                    if (match.langTest == null) {
                        if (match.familyTest.equalsIgnoreCase("sans-serif")) {
                            sansMatch = match;
                        } else if (match.familyTest.equalsIgnoreCase("serif")) {
                            serifMatch = match;
                        } else if (match.familyTest.equalsIgnoreCase("monospace")) {
                            monoMatch = match;
                        }
                    }
                }
            }
        }

        if (this.sansMatch == null) {
            this.sansMatch = new FontMatch("sans-serif", null, null);
            matchList.add(sansMatch);
        }

        if (this.serifMatch == null) {
            this.serifMatch = new FontMatch("serif", null, null);
            matchList.add(serifMatch);
        }

        if (this.monoMatch == null) {
            this.monoMatch = new FontMatch("monospace", null, null);
            matchList.add(monoMatch);
        }

        /* When parsing DOM, nodes in aliasElements list will be removed one by
         * one. So here we use while loop other than for loop.
        */
        while (aliasElements.getLength() > 0) {
            Element element;
            FontAlias alias;

            element = (Element) aliasElements.item(0);
            alias = new FontAlias();
            alias.parseDOM(element);
            if (!alias.isEmpty()) {
                aliasList.add(alias);
            }
        }
    }

    /**
     * Write changes to fontconfig configuration file (XML).
     */
    public void writeConfig() {
        // Clean empty text nodes
        NodeList childList = root.getChildNodes();
        for (int i = 0; i < childList.getLength(); i++) {
            Node n = childList.item(i);
            if (n.getNodeType() == Node.TEXT_NODE) {
                n.getParentNode().removeChild(n);
                i--;
            }
        }

        for (FontMatch match : matchList) {
            match.createDOM(doc);
        }

        for (FontAlias alias : aliasList) {
            alias.createDOM(doc);
        }

        createOptionDOM("antialias", Boolean.toString(antialias), "bool");
        createOptionDOM("hinting", Boolean.toString(hinting), "bool");
        createOptionDOM("hintstyle", hintstyle, "const");
        createOptionDOM("rgba", rgba, "const");
        createOptionDOM("lcdfilter", lcdfilter, "const");
        createOptionDOM("embeddedbitmap", Boolean.toString(embeddedbitmap), "bool");

        // Write document object to XML file.
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Result output = new StreamResult(file);
            Source input = new DOMSource(doc);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(input, output);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(FontConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(FontConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    private void parseOptionDOM(Element element) {
        Element editElement;
        String name;

        editElement = (Element) element.getElementsByTagName("edit").item(0);
        name = editElement.getAttribute("name");

        switch (name) {
            case "rgba":
                if (editElement.getElementsByTagName("const").getLength() > 0) {
                    rgba = editElement.getElementsByTagName("const").item(0).getTextContent();
                }   break;
            case "hinting":
                if (editElement.getElementsByTagName("bool").getLength() > 0) {
                    hinting = Boolean.valueOf(editElement.getElementsByTagName("bool").item(0).getTextContent());
                }   break;
            case "hintstyle":
                if (editElement.getElementsByTagName("const").getLength() > 0) {
                    hintstyle = editElement.getElementsByTagName("const").item(0).getTextContent();
                }   break;
            case "antialias":
                if (editElement.getElementsByTagName("bool").getLength() > 0) {
                    antialias = Boolean.valueOf(editElement.getElementsByTagName("bool").item(0).getTextContent());
                }   break;
            case "lcdfilter":
                if (editElement.getElementsByTagName("const").getLength() > 0) {
                    lcdfilter = editElement.getElementsByTagName("const").item(0).getTextContent();
                }   break;
            case "embeddedbitmap":
                if (editElement.getElementsByTagName("bool").getLength() > 0) {
                    embeddedbitmap = Boolean.valueOf(editElement.getElementsByTagName("bool").item(0).getTextContent());
                }   break;
            default:
                break;
        }

        root.removeChild(element);
    }

    private void createOptionDOM(String name, String value, String type) {
        // fontconfig --> match[target="font"]
        Element matchElement = doc.createElement("match");
        matchElement.setAttribute("target", "font");
        root.appendChild(matchElement);

        // fontconfig --> match[target="font"] --> edit
        Element editElement = doc.createElement("edit");
        editElement.setAttribute("name", name);
        editElement.setAttribute("mode", "assign");
        matchElement.appendChild(editElement);

        // fontconfig --> match[target="font"] --> edit --> const, bool
        Element valueElement = doc.createElement(type);
        valueElement.setTextContent(value);
        editElement.appendChild(valueElement);
    }
}
