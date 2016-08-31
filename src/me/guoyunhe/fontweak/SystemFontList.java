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

import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Guo Yunhe <guoyunhebrave@gmail.com>
 */
public class SystemFontList {

    private final GraphicsEnvironment env;

    private List<String> list;
    private List<String> ignoreList;

    public SystemFontList() {
        env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        buildIgnoreList();
        updateList();
    }

    private void updateList() {
        // Return English names of fonts, easier for match
        String[] originalList = env.getAvailableFontFamilyNames();
        list = new ArrayList();
        for (String font : originalList) {
            font = removeFontWeight(font);
            if (!isIgnored(font) && !list.contains(font)) {
                list.add(font);
            }
        }
    }

    private String removeFontWeight(String font) {
        String fi = font.toLowerCase();
        String[] weights = {
            " demilight",
            " light",
            " thin",
            " normal",
            " regular",
            " medium",
            " semibold",
            " bold",
            " extrabold",
            " black"
        };
        for(String weight: weights) {
            if(fi.endsWith(weight)) {
                font = font.substring(0, font.length() - weight.length());
            }
        }
        return font;
    }

    private void buildIgnoreList() {
        ignoreList = new ArrayList();
        ignoreList.add("Sans Serif");
        ignoreList.add("SansSerif");
        ignoreList.add("Serif");
        ignoreList.add("Monospace");
        ignoreList.add("Monospaced");
    }

    private boolean isIgnored(String font) {
        return ignoreList.contains(font);
    }

    public String[] get() {
        String[] array = new String[list.size()];
        list.toArray(array);
        return array;
    }

    public void refresh() {
        updateList();
    }

    public boolean contains(String font) {
        return list.contains(font);
    }
}
