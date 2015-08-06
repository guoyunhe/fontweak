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

/**
 *
 * @author Guo Yunhe <guoyunhebrave@gmail.com>
 */
public class FontList {
    
    private String list[];

    public FontList() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] originalList = env.getAvailableFontFamilyNames();
        this.list = new String[originalList.length + 1];
        this.list[0] = "";
        System.arraycopy(originalList, 0, this.list, 1, originalList.length);
    }
    
    public String[] getList() {
        return this.list;
    }
}
