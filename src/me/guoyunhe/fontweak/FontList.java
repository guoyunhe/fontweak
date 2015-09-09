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
import java.util.Locale;

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
        // Return English names of fonts, easier for match
        Locale en = new Locale("en", "US");
        String[] originalList = env.getAvailableFontFamilyNames(en);
        list = new ArrayList();
        list.add("");
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

    /**
     * Find installed from fallback font list. Only first installed font will be
     * returned. Note: in different language environment, names of fonts may
     * differ.
     * @param fonts A list of fonts to be search.
     * @return The first font found, or null if nothing found.
     */
    public String fontFallback(String[] fonts) {
        for (String font : fonts) {
            if (list.contains(font)) {
                return font;
            }
        }
        return null;
    }
    
    public String sansFallback() {
        // This fallback list only contains Free and Open Source fonts
        String[] fonts = {
            "Ubuntu", // Ubuntu pre-install
            "Nimbus Sans L",
            "DejaVu Sans",
            "Liberation Sans",
            "Droid Sans"
        };
        return fontFallback(fonts);
    }
    
    public String serifFallback() {
        // This fallback list only contains Free and Open Source fonts
        String[] fonts = {
            "Nimbus Roman No9 L",
            "DejaVu Serif",
            "Liberation Serif",
            "Droid Serif"
        };
        return fontFallback(fonts);
    }
    
    public String monoFallback() {
        // This fallback list only contains Free and Open Source fonts
        String[] fonts = {
            "Ubuntu Mono", // Ubuntu pre-install
            "DejaVu Sans Mono",
            "Libration Mono",
            "Droid Sans Mono",
        };
        return fontFallback(fonts);
    }
    
    public String zhCNSansFallback() {
        // This fallback list only contains Free and Open Source fonts
        String[] fonts = {
            "WenQuanYi Micro Hei",
            "Droid Sans Fallback",
            "WenQuanYi Zen Hei"
        };
        return fontFallback(fonts);
    }
    
    public String zhCNSerifFallback() {
        // This fallback list only contains Free and Open Source fonts
        String[] fonts = {
            "AR PL UMing CN"
        };
        return fontFallback(fonts);
    }
    
    public String zhTWSansFallback() {
        // This fallback list only contains Free and Open Source fonts
        String[] fonts = {
            "WenQuanYi Micro Hei",
            "Droid Sans Fallback",
            "WenQuanYi Zen Hei"
        };
        return fontFallback(fonts);
    }
    
    public String zhTWSerifFallback() {
        // This fallback list only contains Free and Open Source fonts
        String[] fonts = {
            "AR PL UMing TW"
        };
        return fontFallback(fonts);
    }
    
    public String zhHKSansFallback() {
        // This fallback list only contains Free and Open Source fonts
        String[] fonts = {
            "WenQuanYi Micro Hei",
            "Droid Sans Fallback",
            "WenQuanYi Zen Hei"
        };
        return fontFallback(fonts);
    }
    
    public String zhHKSerifFallback() {
        // This fallback list only contains Free and Open Source fonts
        String[] fonts = {
            "AR PL UMing HK"
        };
        return fontFallback(fonts);
    }
    
    public String jaSansFallback() {
        // This fallback list only contains Free and Open Source fonts
        String[] fonts = {
            "Droid Sans Japanese",
            "WenQuanYi Micro Hei",
            "WenQuanYi Zen Hei"
        };
        return fontFallback(fonts);
    }
    
    public String jaSerifFallback() {
        // This fallback list only contains Free and Open Source fonts
        String[] fonts = {
            "AR PL UMing TW"
        };
        return fontFallback(fonts);
    }
    
    public String koSansFallback() {
        // This fallback list only contains Free and Open Source fonts
        String[] fonts = {
            "WenQuanYi Micro Hei",
            "Droid Sans Fallback",
            "WenQuanYi Zen Hei"
        };
        return fontFallback(fonts);
    }
    
    public String koSerifFallback() {
        // This fallback list only contains Free and Open Source fonts
        String[] fonts = {
            "AR PL UMing TW"
        };
        return fontFallback(fonts);
    }
}
