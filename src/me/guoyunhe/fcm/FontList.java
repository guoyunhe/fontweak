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

import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Guo Yunhe <guoyunhebrave@gmail.com>
 */
public class FontList {
    
    private final GraphicsEnvironment env;
    
    private List<String> list;
    private List<String> ignoreList;

    public FontList() {
        env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        buildIgnoreList();
        updateList();
    }
    
    private void updateList() {
        String[] originalList = env.getAvailableFontFamilyNames();
        list = new ArrayList<>();
        list.add("");
        for (String font : originalList) {
            if (!isIgnored(font)) {
                list.add(font);
            }
        }
    }
    
    private void buildIgnoreList() {
        ignoreList = new ArrayList<>();
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
        return false;
    }
}
