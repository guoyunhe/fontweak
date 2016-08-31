/*
 * Copyright (C) 2016 Guo Yunhe <guoyunhebrave@gmail.com>
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Guo Yunhe <guoyunhebrave@gmail.com>
 */
public class FontAlias {
    public String family;
    public String prefer;

    /**
     * Initialize with null content.
     */
    public FontAlias() {
        this.family = null;
        this.prefer = null;
    }

    /**
     * Initialize with data.
     * @param family Condition font family.
     * @param prefer Result font family.
     */
    public FontAlias(String family, String prefer) {
        this.family = family;
        this.prefer = prefer;
    }

    /**
     * Read data from XML DOM.
     * @param element The XML node to be analyzed.
     */
    public void parseDOM(Element element) {
        this.family = null;
        this.prefer = null;

        this.family = element.getElementsByTagName("family").item(0).getTextContent();
        Element preferElement = (Element) element.getElementsByTagName("prefer").item(0);
        this.prefer = preferElement.getElementsByTagName("family").item(0).getTextContent();

        element.getParentNode().removeChild(element);
    }

    /**
     * Create DOM and insert into document.
     *
     * @param doc Document to create elements
     */
    public void createDOM(Document doc) {
        if (isEmpty()) {
            return;
        }

        // fontconfig --> alias
        Element aliasElement = doc.createElement("alias");
        Node root = doc.getElementsByTagName("fontconfig").item(0);
        root.appendChild(aliasElement);

        // fontconfig --> alias --> family
        Element familyElement = doc.createElement("family");
        familyElement.setTextContent(this.family);
        aliasElement.appendChild(familyElement);

        // fontconfig --> alias --> prefer
        Element preferElement = doc.createElement("prefer");
        aliasElement.appendChild(preferElement);

        // fontconfig --> alias --> prefer --> family
        Element preferFamilyElement = doc.createElement("family");
        preferFamilyElement.setTextContent(this.prefer);
        preferElement.appendChild(preferFamilyElement);
    }

    /**
     * Check if the match contains necessary data.
     *
     * @return If the node contains test node and edit node for font family.
     */
    public boolean isEmpty() {
        return this.family == null || this.prefer == null;
    }
}
