/*
 * Copyright 2015 by KnowledgeWorks. All rights reserved.
 * 
 * This software is the proprietary information of KnowledgeWorks
 * Use is subject to license terms.
 * 
 * http://www.knowledgeworks.pt
 */
package pt.knowledgeworks.regexreplacer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import pt.knowledgeworks.regexreplacer.config.Config;
import pt.knowledgeworks.regexreplacer.config.ReplaceInfo;
import pt.knowledgeworks.regexreplacer.info.MatchFound;

/**
 *
 * @author Ivo Lucas <ivo.lucas@knowledgeworks.pt>
 */
public class MainApp extends javax.swing.JFrame {
    private FinderUtil finderUtil;

    /**
     * Creates new form MainApp
     */
    public MainApp() {
        initComponents();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                if (f.isFile()) {
                    return (f.getName().endsWith(".regrep"));
                }
                return true;
            }

            @Override
            public String getDescription() {
                return "RegRep File";
            }
        });
    }

    final static Logger LOGGER = Logger.getGlobal();

    private List<ReplaceInfo> replaceInfoList = new ArrayList<>();

    List<MatchFound> filesFound = new ArrayList<>();

    private JFileChooser fileChooser = new JFileChooser();

    
    
    DefaultTableModel tableModelMatchFound = new DefaultTableModel() {
        @Override
        public int getRowCount() {
            return filesFound.size();
        }

        @Override
        public int getColumnCount() {
            return replaceInfoList.size() + 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            MatchFound matchFound = filesFound.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return matchFound.getLocation().substring(jTextFieldFolder.getText().length());
                case 1:
                    return matchFound.getIgnore();

                default:
                    Boolean result = null;
                    ReplaceInfo columnreplaceInfoList = getColumnreplaceInfoList(columnIndex);
                    if (columnreplaceInfoList != null) {
                        result = matchFound.getMatchs().get(columnreplaceInfoList);
                    }
                    if (result != null) {
                        return result;
                    } else {
                        return Boolean.FALSE;
                    }
            }
        }

        ReplaceInfo getColumnreplaceInfoList(int columnIndex) {
            int t = columnIndex - 2;
            if (t >= 0 && t < replaceInfoList.size()) {
                return replaceInfoList.get(columnIndex - 2);
            }
            return null;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return "File";
                case 1:
                    return "Error";

                default:
                    ReplaceInfo columnreplaceInfoList = getColumnreplaceInfoList(columnIndex);
                    if (columnreplaceInfoList != null) {
                        return columnreplaceInfoList.getName();
                    }
                    return "";
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return String.class;
                default:
                    return Boolean.class;
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex > 0;

        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            MatchFound matchFound = filesFound.get(rowIndex);
            switch (columnIndex) {
                case 0:

                    return;
                case 1:
                    matchFound.setIgnore((Boolean) aValue);
                    return;

                default:
                    ReplaceInfo columnreplaceInfoList = getColumnreplaceInfoList(columnIndex);
                    if (columnreplaceInfoList != null) {
                        matchFound.getMatchs().put(columnreplaceInfoList, (Boolean) aValue);
                    }
            }
        }

       
    };

    TableModel tableModelReplaceInfo = new TableModel() {
        @Override
        public int getRowCount() {
            return replaceInfoList.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ReplaceInfo replaceInfo = replaceInfoList.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return replaceInfo.getName();
                case 1:
                    return replaceInfo.getRegex();
                case 2:
                    return replaceInfo.getReplacement();
                case 3:
                    return replaceInfo.getEnable();
                default:
                    return "";
            }
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return "Name";
                case 1:
                    return "Replace Regex";
                case 2:
                    return "Replacement";
                case 3:
                    return "Enable";
                default:
                    return "";
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 3:
                    return Boolean.class;
                default:
                    return String.class;
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            ReplaceInfo replaceInfo = replaceInfoList.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    replaceInfo.setName((String) aValue);
                    return;
                case 1:
                    replaceInfo.setRegex((String) aValue);
                    return;
                case 2:
                    replaceInfo.setReplacement((String) aValue);
                    return;
                case 3:
                    replaceInfo.setEnable((Boolean) aValue);
                    return;

                default:

            }
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
//            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
//            throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextFieldFileEncoding = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldFileRegex = new javax.swing.JTextField();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableReplaceInfo = new javax.swing.JTable();
        jButtonAddReplaceInfo = new javax.swing.JButton();
        jButtonRemoveReplaceInfo = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableMatchFound = new javax.swing.JTable();
        jProgressBar1 = new javax.swing.JProgressBar();
        jTextFieldFolder = new javax.swing.JTextField();
        jButtonFind = new javax.swing.JButton();
        jButtonReplace = new javax.swing.JButton();
        jButtonFolder = new javax.swing.JButton();
        jCheckBoxZip = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldZipFileRegex = new javax.swing.JTextField();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("File Enconding");

        jTextFieldFileEncoding.setText("UTF-8");

        jLabel2.setText("File Regex");

        jTextFieldFileRegex.setText(".*\\.java");

        jTableReplaceInfo.setModel(tableModelReplaceInfo);
        jScrollPane1.setViewportView(jTableReplaceInfo);

        jButtonAddReplaceInfo.setText("Add");
        jButtonAddReplaceInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddReplaceInfoActionPerformed(evt);
            }
        });

        jButtonRemoveReplaceInfo.setText("Remove");
        jButtonRemoveReplaceInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveReplaceInfoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 859, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButtonRemoveReplaceInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAddReplaceInfo))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAddReplaceInfo)
                    .addComponent(jButtonRemoveReplaceInfo))
                .addGap(1, 1, 1)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("RegexReplacement", jPanel1);

        jTableMatchFound.setModel(tableModelMatchFound);
        jScrollPane2.setViewportView(jTableMatchFound);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 859, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );

        jTabbedPane2.addTab("Found", jPanel2);

        jTextFieldFolder.setToolTipText("");

        jButtonFind.setText("Find");
        jButtonFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindActionPerformed(evt);
            }
        });

        jButtonReplace.setText("Replace");
        jButtonReplace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReplaceActionPerformed(evt);
            }
        });

        jButtonFolder.setText("Folder");
        jButtonFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFolderActionPerformed(evt);
            }
        });

        jCheckBoxZip.setText("Zip Replace");

        jLabel3.setText("Zip File Regex");

        jTextFieldZipFileRegex.setText(".*\\.java");

        fileMenu.setMnemonic('f');
        fileMenu.setText("File");

        openMenuItem.setMnemonic('o');
        openMenuItem.setText("Open");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openMenuItem);

        saveAsMenuItem.setMnemonic('a');
        saveAsMenuItem.setText("Save As ...");
        saveAsMenuItem.setDisplayedMnemonicIndex(5);
        saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveAsMenuItem);

        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setMnemonic('h');
        helpMenu.setText("Help");

        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("About");
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonFolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextFieldFolder, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonFind)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonReplace))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextFieldFileEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldFileRegex, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBoxZip)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldZipFileRegex, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldFileEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldFileRegex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxZip)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jTextFieldZipFileRegex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldFolder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonFind)
                        .addComponent(jButtonReplace)
                        .addComponent(jButtonFolder))
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jTabbedPane2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (
                    InputStream file = new FileInputStream(selectedFile);
                    InputStream buffer = new BufferedInputStream(file);
                    ObjectInput input = new ObjectInputStream(buffer);) {
                //deserialize the List
                Config config = (Config) input.readObject();
                replaceInfoList = config.getList();
                jTextFieldFileEncoding.setText(config.getFileEnconding());
                jTextFieldFileRegex.setText(config.getFileNameRegex());
                jTextFieldZipFileRegex.setText(config.getZipfileNameRegex());
                jCheckBoxZip.setSelected(config.isZipFiles());
                //display its data
                clearFind();
                jTableReplaceInfo.updateUI();
                this.repaint();
            } catch (ClassNotFoundException ex) {
                LOGGER.log(Level.SEVERE, "Cannot perform input. Class not found.", ex);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Cannot perform input.", ex);
            }

        }
    }//GEN-LAST:event_openMenuItemActionPerformed

    private void saveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsMenuItemActionPerformed
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (
                    OutputStream file = new FileOutputStream(selectedFile);
                    OutputStream buffer = new BufferedOutputStream(file);
                    ObjectOutput output = new ObjectOutputStream(buffer);) {
                Config config = new Config();
                config.setFileEnconding(jTextFieldFileEncoding.getText());
                config.setFileNameRegex(jTextFieldFileRegex.getText());
                config.setZipFiles(jCheckBoxZip.isSelected());
                config.setZipfileNameRegex(jTextFieldZipFileRegex.getText());
                config.setList(replaceInfoList);
                output.writeObject(config);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Cannot perform output.", ex);
            }

        }
    }//GEN-LAST:event_saveAsMenuItemActionPerformed

    private void jButtonAddReplaceInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddReplaceInfoActionPerformed
        int selectedRow = jTableReplaceInfo.getSelectedRow();
        if (selectedRow >= 0) {
            replaceInfoList.add(selectedRow,new ReplaceInfo());
        } else {
             replaceInfoList.add(new ReplaceInfo());
        }
        
        jTableReplaceInfo.updateUI();
        clearFind();

    }//GEN-LAST:event_jButtonAddReplaceInfoActionPerformed

    public void clearFind(){
        filesFound.clear();
        jTableMatchFound.updateUI();
    }
    
    private void jButtonRemoveReplaceInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveReplaceInfoActionPerformed
        int selectedRow = jTableReplaceInfo.getSelectedRow();
        if (selectedRow >= 0) {
            replaceInfoList.remove(selectedRow);
        }
        jTableReplaceInfo.updateUI();
        clearFind();
    }//GEN-LAST:event_jButtonRemoveReplaceInfoActionPerformed

    private void jButtonFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFindActionPerformed
        clearFind();
        finderUtil = new FinderUtil(jTextFieldFileEncoding.getText(), jTextFieldFileRegex.getText(),jCheckBoxZip.isSelected(), jTextFieldZipFileRegex.getText(), replaceInfoList, filesFound);
        finderUtil.process((new File(jTextFieldFolder.getText())).toPath());
        tableModelMatchFound.fireTableStructureChanged();
        jTableMatchFound.doLayout();
        jTableMatchFound.updateUI();
    }//GEN-LAST:event_jButtonFindActionPerformed

    private void jButtonFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFolderActionPerformed
        JFileChooser directoryChooser = new JFileChooser();
        directoryChooser.setDialogTitle("Select base Folder");
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        directoryChooser.setAcceptAllFileFilterUsed(false);
        String text = jTextFieldFolder.getText();
        if (text != null && !text.isEmpty()) {
            directoryChooser.setCurrentDirectory(new File(text));
        } else {
            directoryChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        }
        if (directoryChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {            
            jTextFieldFolder.setText(directoryChooser.getSelectedFile().getPath());
        } else {
            System.out.println("No Selection ");
        }

    }//GEN-LAST:event_jButtonFolderActionPerformed

    private void jButtonReplaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReplaceActionPerformed
        System.out.println("start");
        finderUtil.apply();
        System.out.println("end");
    }//GEN-LAST:event_jButtonReplaceActionPerformed

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
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainApp().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JButton jButtonAddReplaceInfo;
    private javax.swing.JButton jButtonFind;
    private javax.swing.JButton jButtonFolder;
    private javax.swing.JButton jButtonRemoveReplaceInfo;
    private javax.swing.JButton jButtonReplace;
    private javax.swing.JCheckBox jCheckBoxZip;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTableMatchFound;
    private javax.swing.JTable jTableReplaceInfo;
    private javax.swing.JTextField jTextFieldFileEncoding;
    private javax.swing.JTextField jTextFieldFileRegex;
    private javax.swing.JTextField jTextFieldFolder;
    private javax.swing.JTextField jTextFieldZipFileRegex;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem saveAsMenuItem;
    // End of variables declaration//GEN-END:variables

}
