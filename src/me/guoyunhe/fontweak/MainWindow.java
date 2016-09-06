/*
 * Copyright (C) 2016 Guo Yunhe guoyunhebrave@gmail.com
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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Guo Yunhe guoyunhebrave@gmail.com
 */
public class MainWindow extends javax.swing.JFrame {

    private final ImageIcon appIcon;
    private final FontConfig fontconfig;
    private final SystemFontList sysfonts;
    private final DefaultListModel<String> matchListModel;
    private final DefaultListModel<String> fontListModel;
    private final DefaultTableModel aliasTableModel;

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        appIcon = new ImageIcon(getClass().getResource("/me/guoyunhe/fontweak/img/icon-256.png"));
        sysfonts = new SystemFontList();
        fontconfig = new FontConfig();
        matchListModel = new DefaultListModel<>();
        fontListModel = new DefaultListModel<>();
        aliasTableModel = new DefaultTableModel(
                new String[][]{},
                new String[]{
                    java.util.ResourceBundle.getBundle("me/guoyunhe/fontweak/lang/main").getString("ORIGINAL FONT"), java.util.ResourceBundle.getBundle("me/guoyunhe/fontweak/lang/main").getString("PREFERED FONT")
                }
        ) {
            Class[] types = new Class[]{
                java.lang.String.class, java.lang.String.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };

        initComponents();
        loadConfig();
    }

    private void loadConfig() {
        fontconfig.readConfig();

        if (fontconfig.matchList != null && !fontconfig.matchList.isEmpty()) {
            for (FontMatch match : fontconfig.matchList) {
                if (match.langTest != null) {
                    matchListModel.addElement(match.familyTest + " [" + match.langTest + "]");
                } else {
                    matchListModel.addElement(match.familyTest);
                }
            }

            matchList.setSelectedIndex(0);
        }

        if (fontconfig.aliasList != null && !fontconfig.aliasList.isEmpty()) {
            for (FontAlias alias : fontconfig.aliasList) {
                aliasTableModel.addRow(new String[]{alias.family, alias.prefer});
            }
        }

        // Options
        this.antialiasCheckBox.setSelected(fontconfig.antialias);
        this.hintingCheckBox.setSelected(fontconfig.hinting);
        this.hintstyleComboBox.setSelectedItem(fontconfig.hintstyle);
        this.rgbaComboBox.setSelectedItem(fontconfig.rgba);
        this.lcdfilterComboBox.setSelectedItem(fontconfig.lcdfilter);
        this.embeddedbitmapCheckBox.setSelected(fontconfig.embeddedbitmap);
    }

    private void saveConfig() {

        // Options
        fontconfig.antialias = this.antialiasCheckBox.isSelected();
        fontconfig.hinting = this.hintingCheckBox.isSelected();
        fontconfig.hintstyle = (String) this.hintstyleComboBox.getSelectedItem();
        fontconfig.rgba = (String) this.rgbaComboBox.getSelectedItem();
        fontconfig.lcdfilter = (String) this.lcdfilterComboBox.getSelectedItem();
        fontconfig.embeddedbitmap = this.embeddedbitmapCheckBox.isSelected();

        fontconfig.writeConfig();
    }

    private void selectMatch() {
        fontListModel.removeAllElements();
        int selected = matchList.getSelectedIndex();

        if (selected >= 0) {
            String[] fonts = fontconfig.matchList.get(selected).familyEdit;
            if (fonts != null && fonts.length > 0) {
                for (String font : fonts) {
                    fontListModel.addElement(font);
                }
            }
        }
    }

    private void saveFontList() {
        int selected = matchList.getSelectedIndex();
        String[] fonts = new String[fontListModel.getSize()];
        fontListModel.copyInto(fonts);
        fontconfig.matchList.get(selected).familyEdit = fonts;
    }

    private void openURL(String url) {
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (URISyntaxException | IOException e) {}
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        createMatchDialog = new javax.swing.JDialog();
        createMatchDialogOptionPanel = new javax.swing.JPanel();
        createMatchDialogFamilyLabel = new javax.swing.JLabel();
        createMatchDialogFamilyComboBox = new javax.swing.JComboBox<>();
        createMatchDialogLanguageLabel = new javax.swing.JLabel();
        createMatchDialogLanguageComboBox = new javax.swing.JComboBox<>();
        createMatchDialogButtonPanel = new javax.swing.JPanel();
        createMatchDialogCancelButton = new javax.swing.JButton();
        createMatchDialogOkButton = new javax.swing.JButton();
        tabs = new javax.swing.JTabbedPane();
        matchPanel = new javax.swing.JPanel();
        matchListPanel = new javax.swing.JPanel();
        matchListScrollPane = new javax.swing.JScrollPane();
        matchList = new javax.swing.JList<String>();
        matchListButtonPanel = new javax.swing.JPanel();
        matchCreateButton = new javax.swing.JButton();
        matchDeleteButton = new javax.swing.JButton();
        matchListLabel = new javax.swing.JLabel();
        matchFontListPanel = new javax.swing.JPanel();
        fontListScrollPane = new javax.swing.JScrollPane();
        fontList = new javax.swing.JList<String>();
        jPanel4 = new javax.swing.JPanel();
        fontUpButton = new javax.swing.JButton();
        fontDownButton = new javax.swing.JButton();
        fontAddButton = new javax.swing.JButton();
        fontRemoveButton = new javax.swing.JButton();
        fontListLabel = new javax.swing.JLabel();
        aliasPanel = new javax.swing.JPanel();
        aliasScrollPane = new javax.swing.JScrollPane();
        aliasTable = new javax.swing.JTable(){
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        aliasButtonPanel = new javax.swing.JPanel();
        aliasTextField = new javax.swing.JTextField();
        aliasComboBox = new javax.swing.JComboBox<String>();
        aliasAddButton = new javax.swing.JButton();
        aliasRemoveButton = new javax.swing.JButton();
        optionPanel = new javax.swing.JPanel();
        antialiasLabel = new javax.swing.JLabel();
        antialiasCheckBox = new javax.swing.JCheckBox();
        hintingLabel = new javax.swing.JLabel();
        hintingCheckBox = new javax.swing.JCheckBox();
        hintstyleLabel = new javax.swing.JLabel();
        rgbaLabel = new javax.swing.JLabel();
        hintstyleComboBox = new javax.swing.JComboBox<String>();
        rgbaComboBox = new javax.swing.JComboBox<String>();
        lcdfilterLabel = new javax.swing.JLabel();
        lcdfilterComboBox = new javax.swing.JComboBox<>();
        embeddedbitmapLabel = new javax.swing.JLabel();
        embeddedbitmapCheckBox = new javax.swing.JCheckBox();
        buttonPanel = new javax.swing.JPanel();
        aboutButton = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        helpButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        cancelButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        okButton = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("me/guoyunhe/fontweak/lang/main"); // NOI18N
        createMatchDialog.setTitle(bundle.getString("CREATE FONT MATCH")); // NOI18N
        createMatchDialog.setIconImage(appIcon.getImage());
        createMatchDialog.setLocationByPlatform(true);
        createMatchDialog.setModal(true);
        createMatchDialog.setSize(new java.awt.Dimension(300, 200));
        createMatchDialog.getContentPane().setLayout(new javax.swing.BoxLayout(createMatchDialog.getContentPane(), javax.swing.BoxLayout.PAGE_AXIS));

        java.awt.GridBagLayout createMatchDialogOptionPanelLayout = new java.awt.GridBagLayout();
        createMatchDialogOptionPanelLayout.columnWidths = new int[] {0, 15, 0};
        createMatchDialogOptionPanelLayout.rowHeights = new int[] {0, 15, 0};
        createMatchDialogOptionPanel.setLayout(createMatchDialogOptionPanelLayout);

        createMatchDialogFamilyLabel.setText(bundle.getString("FONT FAMILY")); // NOI18N
        createMatchDialogFamilyLabel.setPreferredSize(new java.awt.Dimension(100, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        createMatchDialogOptionPanel.add(createMatchDialogFamilyLabel, gridBagConstraints);

        createMatchDialogFamilyComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "sans-serif", "serif", "monospace" }));
        createMatchDialogFamilyComboBox.setPreferredSize(new java.awt.Dimension(150, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        createMatchDialogOptionPanel.add(createMatchDialogFamilyComboBox, gridBagConstraints);

        createMatchDialogLanguageLabel.setText(bundle.getString("LANGUAGE")); // NOI18N
        createMatchDialogLanguageLabel.setPreferredSize(new java.awt.Dimension(100, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        createMatchDialogOptionPanel.add(createMatchDialogLanguageLabel, gridBagConstraints);

        createMatchDialogLanguageComboBox.setModel(new DefaultComboBoxModel(FontConfig.LANGUAGES));
        createMatchDialogLanguageComboBox.setPreferredSize(new java.awt.Dimension(150, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        createMatchDialogOptionPanel.add(createMatchDialogLanguageComboBox, gridBagConstraints);

        createMatchDialog.getContentPane().add(createMatchDialogOptionPanel);

        createMatchDialogButtonPanel.setBorder(null);
        createMatchDialogButtonPanel.setMaximumSize(new java.awt.Dimension(32767, 40));
        createMatchDialogButtonPanel.setMinimumSize(new java.awt.Dimension(100, 40));
        createMatchDialogButtonPanel.setPreferredSize(new java.awt.Dimension(400, 40));

        createMatchDialogCancelButton.setText(bundle.getString("CANCEL")); // NOI18N
        createMatchDialogCancelButton.setMinimumSize(new java.awt.Dimension(80, 0));
        createMatchDialogCancelButton.setPreferredSize(new java.awt.Dimension(80, 30));
        createMatchDialogCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createMatchDialogCancelButtonActionPerformed(evt);
            }
        });
        createMatchDialogButtonPanel.add(createMatchDialogCancelButton);

        createMatchDialogOkButton.setText(bundle.getString("OK")); // NOI18N
        createMatchDialogOkButton.setMinimumSize(new java.awt.Dimension(80, 0));
        createMatchDialogOkButton.setPreferredSize(new java.awt.Dimension(80, 30));
        createMatchDialogOkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createMatchDialogOkButtonActionPerformed(evt);
            }
        });
        createMatchDialogButtonPanel.add(createMatchDialogOkButton);

        createMatchDialog.getContentPane().add(createMatchDialogButtonPanel);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Fontweak");
        setIconImage(appIcon.getImage());
        setLocationByPlatform(true);

        tabs.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 5, 5));
        tabs.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

        matchPanel.setLayout(new javax.swing.BoxLayout(matchPanel, javax.swing.BoxLayout.LINE_AXIS));

        matchListPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 0, 5, 0));
        matchListPanel.setLayout(new java.awt.BorderLayout());

        matchListScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));

        matchList.setModel(matchListModel);
        matchList.setSelectedIndex(0);
        matchList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                matchListValueChanged(evt);
            }
        });
        matchListScrollPane.setViewportView(matchList);

        matchListPanel.add(matchListScrollPane, java.awt.BorderLayout.CENTER);

        matchListButtonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 0, 5, 0));
        matchListButtonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        matchCreateButton.setText(bundle.getString("CREATE")); // NOI18N
        matchCreateButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        matchCreateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                matchCreateButtonActionPerformed(evt);
            }
        });
        matchListButtonPanel.add(matchCreateButton);

        matchDeleteButton.setText(bundle.getString("DELETE")); // NOI18N
        matchDeleteButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        matchDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                matchDeleteButtonActionPerformed(evt);
            }
        });
        matchListButtonPanel.add(matchDeleteButton);

        matchListPanel.add(matchListButtonPanel, java.awt.BorderLayout.PAGE_END);

        matchListLabel.setText(bundle.getString("MATCH LIST")); // NOI18N
        matchListLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        matchListPanel.add(matchListLabel, java.awt.BorderLayout.PAGE_START);

        matchPanel.add(matchListPanel);

        matchFontListPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 0, 5, 0));
        matchFontListPanel.setLayout(new java.awt.BorderLayout());

        fontListScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));

        fontList.setModel(fontListModel);
        fontListScrollPane.setViewportView(fontList);

        matchFontListPanel.add(fontListScrollPane, java.awt.BorderLayout.CENTER);

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 0, 5, 0));
        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        fontUpButton.setText(bundle.getString("MOVE UP")); // NOI18N
        fontUpButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        fontUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontUpButtonActionPerformed(evt);
            }
        });
        jPanel4.add(fontUpButton);

        fontDownButton.setText(bundle.getString("MOVE DOWN")); // NOI18N
        fontDownButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        fontDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontDownButtonActionPerformed(evt);
            }
        });
        jPanel4.add(fontDownButton);

        fontAddButton.setText(bundle.getString("ADD")); // NOI18N
        fontAddButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        fontAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontAddButtonActionPerformed(evt);
            }
        });
        jPanel4.add(fontAddButton);

        fontRemoveButton.setText(bundle.getString("REMOVE")); // NOI18N
        fontRemoveButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        fontRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontRemoveButtonActionPerformed(evt);
            }
        });
        jPanel4.add(fontRemoveButton);

        matchFontListPanel.add(jPanel4, java.awt.BorderLayout.PAGE_END);

        fontListLabel.setText(bundle.getString("FONT LIST")); // NOI18N
        fontListLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        matchFontListPanel.add(fontListLabel, java.awt.BorderLayout.PAGE_START);

        matchPanel.add(matchFontListPanel);

        tabs.addTab(bundle.getString("FONT MATCH"), matchPanel); // NOI18N

        aliasPanel.setLayout(new java.awt.BorderLayout());

        aliasScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        aliasTable.setModel(aliasTableModel);
        aliasTable.setGridColor(new java.awt.Color(199, 199, 199));
        aliasTable.setRowHeight(30);
        aliasScrollPane.setViewportView(aliasTable);

        aliasPanel.add(aliasScrollPane, java.awt.BorderLayout.CENTER);

        aliasButtonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 5, 0));
        aliasButtonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        aliasTextField.setPreferredSize(new java.awt.Dimension(200, 28));
        aliasButtonPanel.add(aliasTextField);

        aliasComboBox.setModel(new DefaultComboBoxModel(sysfonts.get())
        );
        aliasComboBox.setMinimumSize(new java.awt.Dimension(100, 28));
        aliasComboBox.setPreferredSize(new java.awt.Dimension(200, 28));
        aliasButtonPanel.add(aliasComboBox);

        aliasAddButton.setText(bundle.getString("ADD")); // NOI18N
        aliasAddButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        aliasAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aliasAddButtonActionPerformed(evt);
            }
        });
        aliasButtonPanel.add(aliasAddButton);

        aliasRemoveButton.setText(bundle.getString("REMOVE")); // NOI18N
        aliasRemoveButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        aliasRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aliasRemoveButtonActionPerformed(evt);
            }
        });
        aliasButtonPanel.add(aliasRemoveButton);

        aliasPanel.add(aliasButtonPanel, java.awt.BorderLayout.PAGE_END);

        tabs.addTab(bundle.getString("FONT ALIAS"), aliasPanel); // NOI18N

        java.awt.GridBagLayout optionPanelLayout = new java.awt.GridBagLayout();
        optionPanelLayout.columnWidths = new int[] {0, 20, 0};
        optionPanelLayout.rowHeights = new int[] {0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0};
        optionPanel.setLayout(optionPanelLayout);

        antialiasLabel.setText(bundle.getString("ANTIALIAS")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        optionPanel.add(antialiasLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        optionPanel.add(antialiasCheckBox, gridBagConstraints);

        hintingLabel.setText(bundle.getString("HINTING")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        optionPanel.add(hintingLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        optionPanel.add(hintingCheckBox, gridBagConstraints);

        hintstyleLabel.setText(bundle.getString("HINT STYLE")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        optionPanel.add(hintstyleLabel, gridBagConstraints);

        rgbaLabel.setText(bundle.getString("SUBPIXEL RENDER")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        optionPanel.add(rgbaLabel, gridBagConstraints);

        hintstyleComboBox.setModel(new DefaultComboBoxModel(FontConfig.HINTSTYLE_OPTIONS));
        hintstyleComboBox.setMinimumSize(new java.awt.Dimension(150, 28));
        hintstyleComboBox.setPreferredSize(new java.awt.Dimension(150, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        optionPanel.add(hintstyleComboBox, gridBagConstraints);

        rgbaComboBox.setModel(new DefaultComboBoxModel(FontConfig.RGBA_OPTIONS));
        rgbaComboBox.setMinimumSize(new java.awt.Dimension(150, 28));
        rgbaComboBox.setPreferredSize(new java.awt.Dimension(150, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        optionPanel.add(rgbaComboBox, gridBagConstraints);

        lcdfilterLabel.setText(bundle.getString("LCD FILTER")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        optionPanel.add(lcdfilterLabel, gridBagConstraints);

        lcdfilterComboBox.setModel(new DefaultComboBoxModel(FontConfig.LCDFILTER_OPTIONS));
        lcdfilterComboBox.setPreferredSize(new java.awt.Dimension(150, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        optionPanel.add(lcdfilterComboBox, gridBagConstraints);

        embeddedbitmapLabel.setText(bundle.getString("EMBEDDED BITMAP")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        optionPanel.add(embeddedbitmapLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        optionPanel.add(embeddedbitmapCheckBox, gridBagConstraints);

        tabs.addTab(bundle.getString("FONT OPTION"), optionPanel); // NOI18N

        getContentPane().add(tabs, java.awt.BorderLayout.CENTER);

        buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonPanel.setPreferredSize(new java.awt.Dimension(400, 44));
        buttonPanel.setLayout(new javax.swing.BoxLayout(buttonPanel, javax.swing.BoxLayout.LINE_AXIS));

        aboutButton.setText(bundle.getString("ABOUT")); // NOI18N
        aboutButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        aboutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutButtonListener(evt);
            }
        });
        buttonPanel.add(aboutButton);
        buttonPanel.add(filler3);

        helpButton.setText(bundle.getString("HELP")); // NOI18N
        helpButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(helpButton);
        buttonPanel.add(filler1);

        cancelButton.setText(bundle.getString("CANCEL")); // NOI18N
        cancelButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        cancelButton.setMinimumSize(new java.awt.Dimension(60, 30));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(cancelButton);
        buttonPanel.add(filler2);

        okButton.setText(bundle.getString("OK")); // NOI18N
        okButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(okButton);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void aboutButtonListener(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutButtonListener
        openURL("https://github.com/guoyunhe/fontweak");
    }//GEN-LAST:event_aboutButtonListener

    private void fontAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontAddButtonActionPerformed
        String font = (String)JOptionPane.showInputDialog(this,
                java.util.ResourceBundle.getBundle("me/guoyunhe/fontweak/lang/main").getString("CHOOSE FONT"),
                java.util.ResourceBundle.getBundle("me/guoyunhe/fontweak/lang/main").getString("CHOOSE FONT"),
                JOptionPane.PLAIN_MESSAGE, null, sysfonts.get(), appIcon.getImage());
        if (font != null && !font.isEmpty()) {
            fontListModel.addElement(font);
            saveFontList();

            fontList.setSelectedIndex(fontListModel.getSize() - 1);
        }
    }//GEN-LAST:event_fontAddButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        saveConfig();
        System.exit(0);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        System.exit(0);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void matchListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_matchListValueChanged
        selectMatch();
    }//GEN-LAST:event_matchListValueChanged

    private void fontRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontRemoveButtonActionPerformed
        if (fontList.getSelectedIndex() >= 0) {
            int index = fontList.getSelectedIndex();
            fontListModel.remove(index);

            saveFontList();

            if (index > fontListModel.getSize() - 1) {
                index = fontListModel.getSize() - 1;
            }

            if (index >= 0) {
                fontList.setSelectedIndex(index);
            }
        }
    }//GEN-LAST:event_fontRemoveButtonActionPerformed

    private void fontDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontDownButtonActionPerformed
        int index = fontList.getSelectedIndex();
        if (index >= 0 && index < fontListModel.getSize() - 1) {
            String current = fontListModel.get(index);
            String next = fontListModel.get(index + 1);
            fontListModel.set(index, next);
            fontListModel.set(index + 1, current);

            saveFontList();

            fontList.setSelectedIndex(index + 1);
        }
    }//GEN-LAST:event_fontDownButtonActionPerformed

    private void fontUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontUpButtonActionPerformed
        int index = fontList.getSelectedIndex();
        if (index > 0 && index < fontListModel.getSize()) {
            String current = fontListModel.get(index);
            String previous = fontListModel.get(index - 1);
            fontListModel.set(index, previous);
            fontListModel.set(index - 1, current);

            saveFontList();

            fontList.setSelectedIndex(index - 1);
        }
    }//GEN-LAST:event_fontUpButtonActionPerformed

    private void matchCreateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_matchCreateButtonActionPerformed
        createMatchDialog.setVisible(true);
    }//GEN-LAST:event_matchCreateButtonActionPerformed

    private void createMatchDialogCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createMatchDialogCancelButtonActionPerformed
        createMatchDialog.setVisible(false);
    }//GEN-LAST:event_createMatchDialogCancelButtonActionPerformed

    private void createMatchDialogOkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createMatchDialogOkButtonActionPerformed
        FontMatch match = new FontMatch((String) createMatchDialogFamilyComboBox.getSelectedItem(),
                (String) createMatchDialogLanguageComboBox.getSelectedItem(), null);
        fontconfig.matchList.add(match);
        matchListModel.addElement(match.familyTest + " [" + match.langTest + "]");
        createMatchDialog.setVisible(false);
    }//GEN-LAST:event_createMatchDialogOkButtonActionPerformed

    private void matchDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_matchDeleteButtonActionPerformed
        int selected = matchList.getSelectedIndex();
        if (selected >= 0) {
            matchListModel.remove(selected);
            fontconfig.matchList.remove(selected);
            if (selected > matchListModel.getSize() - 1) {
                selected = matchListModel.getSize() - 1;
            }

            if (selected >= 0) {
                matchList.setSelectedIndex(selected);
            }
        }
    }//GEN-LAST:event_matchDeleteButtonActionPerformed

    private void aliasAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aliasAddButtonActionPerformed
        FontAlias alias = new FontAlias(aliasTextField.getText(), (String)aliasComboBox.getSelectedItem());
        fontconfig.aliasList.add(alias);
        aliasTableModel.addRow(new String[]{alias.family, alias.prefer});
        aliasTextField.setText("");
    }//GEN-LAST:event_aliasAddButtonActionPerformed

    private void aliasRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aliasRemoveButtonActionPerformed
        int selected = aliasTable.getSelectedRow();
        if (selected > -1) {
            fontconfig.aliasList.remove(selected);
            aliasTableModel.removeRow(selected);

            if (selected > aliasTableModel.getRowCount() - 1) {
                selected = aliasTableModel.getRowCount() - 1;
            }

            if (selected >= 0) {
                aliasTable.setRowSelectionInterval(selected, selected);
            }
        }
    }//GEN-LAST:event_aliasRemoveButtonActionPerformed

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        openURL("https://github.com/guoyunhe/fontweak/wiki");
    }//GEN-LAST:event_helpButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutButton;
    private javax.swing.JButton aliasAddButton;
    private javax.swing.JPanel aliasButtonPanel;
    private javax.swing.JComboBox<String> aliasComboBox;
    private javax.swing.JPanel aliasPanel;
    private javax.swing.JButton aliasRemoveButton;
    private javax.swing.JScrollPane aliasScrollPane;
    private javax.swing.JTable aliasTable;
    private javax.swing.JTextField aliasTextField;
    private javax.swing.JCheckBox antialiasCheckBox;
    private javax.swing.JLabel antialiasLabel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JDialog createMatchDialog;
    private javax.swing.JPanel createMatchDialogButtonPanel;
    private javax.swing.JButton createMatchDialogCancelButton;
    private javax.swing.JComboBox<String> createMatchDialogFamilyComboBox;
    private javax.swing.JLabel createMatchDialogFamilyLabel;
    private javax.swing.JComboBox<String> createMatchDialogLanguageComboBox;
    private javax.swing.JLabel createMatchDialogLanguageLabel;
    private javax.swing.JButton createMatchDialogOkButton;
    private javax.swing.JPanel createMatchDialogOptionPanel;
    private javax.swing.JCheckBox embeddedbitmapCheckBox;
    private javax.swing.JLabel embeddedbitmapLabel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JButton fontAddButton;
    private javax.swing.JButton fontDownButton;
    private javax.swing.JList<String> fontList;
    private javax.swing.JLabel fontListLabel;
    private javax.swing.JScrollPane fontListScrollPane;
    private javax.swing.JButton fontRemoveButton;
    private javax.swing.JButton fontUpButton;
    private javax.swing.JButton helpButton;
    private javax.swing.JCheckBox hintingCheckBox;
    private javax.swing.JLabel hintingLabel;
    private javax.swing.JComboBox<String> hintstyleComboBox;
    private javax.swing.JLabel hintstyleLabel;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JComboBox<String> lcdfilterComboBox;
    private javax.swing.JLabel lcdfilterLabel;
    private javax.swing.JButton matchCreateButton;
    private javax.swing.JButton matchDeleteButton;
    private javax.swing.JPanel matchFontListPanel;
    private javax.swing.JList<String> matchList;
    private javax.swing.JPanel matchListButtonPanel;
    private javax.swing.JLabel matchListLabel;
    private javax.swing.JPanel matchListPanel;
    private javax.swing.JScrollPane matchListScrollPane;
    private javax.swing.JPanel matchPanel;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel optionPanel;
    private javax.swing.JComboBox<String> rgbaComboBox;
    private javax.swing.JLabel rgbaLabel;
    private javax.swing.JTabbedPane tabs;
    // End of variables declaration//GEN-END:variables
}
