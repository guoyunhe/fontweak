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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Guo Yunhe <guoyunhebrave@gmail.com>
 */
public class SchemeManager {
    
    private final String appConfigDir;
    private final String schemeDir;
    private final File appConfigFile;
    private final Properties configProp;
    
    public SchemeManager () {
        String userhome = System.getProperty("user.home");
        appConfigDir = userhome + "/.config/fontweak";
        schemeDir = appConfigDir + "/scheme";
        appConfigFile = new File(appConfigDir + "/config.properties");
        
        configProp = new Properties();
        
        checkAppConfig();
        readAppConfig();
    }
    
    /**
     * Create application config files and directories if not exist.
     */
    private void checkAppConfig() {
        try {
            if (!Files.exists(Paths.get(appConfigDir))) {
                Files.createDirectory(Paths.get(appConfigDir));
                Files.createDirectory(Paths.get(schemeDir));
                Files.createFile(appConfigFile.toPath());

            } else {
                if (!Files.exists(Paths.get(schemeDir))) {
                    Files.createDirectory(Paths.get(schemeDir));
                }
                if (!appConfigFile.exists()) {
                    Files.createFile(appConfigFile.toPath());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SchemeManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void readAppConfig() {
	InputStream input;
        try {
            input = new FileInputStream(appConfigFile);
            // load a properties file
            configProp.load(input);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SchemeManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SchemeManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void writeAppConfig() {
        OutputStream output;
        try {
            output = new FileOutputStream(appConfigFile);
            // save properties to project root folder
            configProp.store(output, null);
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(SchemeManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String[] getSchemeList() {
        File schemeDirFile = new File(schemeDir);
        FilenameFilter filter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith(".xml")) {
                    return true;
                } else {
                    return false;
                }
                    
            }
        };
        String[] schemeFiles = schemeDirFile.list(filter);
        if (schemeFiles.length > 0) {
            String[] schemeList = new String[schemeFiles.length];
            for (int i = 0; i < schemeFiles.length; i++) {
                String filename = schemeFiles[i];
                schemeList[i] = filename.substring(0, filename.length() - 4);
            }
            return schemeList;
        } else {
            return null;
        }
    }
    
    public String getCurrentSchemeName() {        
        return configProp.getProperty("currentscheme");
    }
    
    public void setCurrentSchemeName(String scheme) {
        configProp.setProperty("currentscheme", scheme);
        writeAppConfig();
    }

    public void renameScheme(String oldName, String newName) {
        getSchemeFile(oldName).renameTo(getSchemeFile(newName));
    }
    
    public void deleteScheme(String scheme) {
        getSchemeFile(scheme).delete();
    }
    
    public File getSchemeFile(String scheme) {
        return new File(schemeDir + "/" + scheme + ".xml");
    }
}
