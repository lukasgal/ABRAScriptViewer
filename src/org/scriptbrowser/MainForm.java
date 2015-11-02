/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scriptbrowser;

import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLEditorKit;
import javax.xml.xpath.XPathExpressionException;
import jsyntaxpane.DefaultSyntaxKit;
import jsyntaxpane.util.Configuration;
import org.w3c.dom.Document;

/**
 *
 * @author Euronics
 */
public class MainForm extends javax.swing.JFrame {

    private final HTMLEditorKit htmlKit = new HTMLEditorKit();

    private Script selectedScript;

    private final Packages root;

    private class LoadFile extends Thread {

        private File file;

        public LoadFile(String name) {
            super(name);
        }

        public LoadFile(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            super.run();
            try {
                if (root.isPackageInRows(file.toURI().toURL().toString())) {
                    //JOptionPane.showMessageDialog(null, "Tento balíček je již načtený");
                    
                    return;
                }

                XMLReader reader = null;
                reader = new XMLReader(file.toURI().toURL());
                reader.load();
                if (reader.getXmlDoc() == null) {
                    return;
                }
                ScriptPackage sPackage = new ScriptPackage((Document) reader.getXmlDoc());
                sPackage.setFullPath(file.toURI().toURL().toString());
                try {
                    
                    root.addRow(sPackage);
                    PackageTree.setModel(new PackageTreeModel(root, false));
                    PackageTree.updateUI();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Neočekávaná výjimka při zpracování souboru\n"+file.toURI().toURL().toString()+"\n" + ex);
                }

            } catch (XPathExpressionException ex) {
                JOptionPane.showMessageDialog(null, "Špatný formát souboru.");
                ex.printStackTrace();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex);
            }

        }

    }

    /**
     * Creates new form MainForm
     */
    public MainForm() {
        root = new Packages("Balíčky skriptů");
        initComponents();
        //methodCode.setEditorKit(htmlKit);
        init();
        //StyleSheet styl = htmlKit.getStyleSheet();
        DefaultSyntaxKit.initKit();
        DefaultSyntaxKit.getConfig(DefaultSyntaxKit.class).put("CaretColor", "0x000000");
        PackageTree.setCellRenderer(new PackageTreeCellRenderer());
        //methodCode.setBackground(new Color(224, 224, 224));
        Configuration config = DefaultSyntaxKit.getConfig(DefaultSyntaxKit.class);

        methodCode.setContentType("text/java");
        tfNote.setContentType("text/sql");
        //styl.importStyleSheet(getClass().getResource("/resources/Formatter.css"));
        //htmlKit.setStyleSheet(styl);
    }

    public void init() {

        PackageTree.setModel(new PackageTreeModel(root, false));
        PackageTree.addKeyListener(new KeyAdapter() {

            @Override
            @SuppressWarnings("empty-statement")
            public void keyPressed(KeyEvent ke) {
                super.keyPressed(ke);
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    DefaultInterface element = (DefaultInterface) PackageTree.getLastSelectedPathComponent();
                    if ((element instanceof Script)) {
                        tabs.setSelectedIndex(1);
                    }
                    if ((element instanceof MethodDef)) {
                        try {
                            methodCode.setCaretPosition(methodCode.getDocument().getDefaultRootElement().getElement(((MethodDef) element).getLineNumber() - 1).getStartOffset());
                            Rectangle rec = methodCode.modelToView(((MethodDef) element).getLineNumber());
                            rec.setSize((int) rec.getWidth(), (int) rec.getHeight() - ((MethodDef) element).getCodeLength());
                            tabs.setSelectedIndex(1);

                        } catch (BadLocationException ex) {
                            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                if (ke.getKeyCode() == KeyEvent.VK_DELETE) {
                    DefaultInterface element = (DefaultInterface) PackageTree.getLastSelectedPathComponent();
                    if ((element instanceof ScriptPackage)){
                        Packages objParent = (Packages)((ScriptPackage)element).getParent();
                        Packages pkgParent = null;
                        if (objParent.getRows().size()==1)
                            pkgParent = objParent.getParent();
                        //JOptionPane.showMessageDialog(null, objParent.getParent());
                        if(pkgParent!=null)
                            pkgParent.getRows().remove(objParent);
                        else 
                            objParent.getRows().remove(element);
                        //PackageTree.setModel(new PackageTreeModel(root, false));
                        PackageTree.updateUI();
                    }
                }
            }

        });
        PackageTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent tse) {
                DefaultInterface element = (DefaultInterface) PackageTree.getLastSelectedPathComponent();

                if (element instanceof ScriptPackage) {
                    tfName.setText(((ScriptPackage) element).getName());
                    tfPath.setText(((ScriptPackage) element).getFullPath());
                    tfNote.setText(((ScriptPackage) element).getNote());
                    tfDescription.setText(((ScriptPackage) element).getDescription());
                    tfHasPassword.setSelected(((ScriptPackage) element).isHasPassword());
                }
                if (element instanceof Script) {
                    methodCode.setText(((Script) element).getCode());
                    methodCode.setCaretPosition(0);
                    selectedScript = (Script) element;

                }
                if (element instanceof MethodDef) {
                    if (((Script) (((MethodDef) element).getParent())) != selectedScript) {
                        methodCode.setText(((Script) (((MethodDef) element).getParent())).getCode());
                        methodCode.setCaretPosition(0);

                    }
                    jLabel1.setText(((MethodDef) element).getParams());

                }
            }
        });

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        tabs = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        tfName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tfVersion = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tfPath = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        lbDetail = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tfNote = new javax.swing.JEditorPane();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tfDescription = new javax.swing.JTextField();
        tfHasPassword = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        methodCode = new javax.swing.JEditorPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        PackageTree = new javax.swing.JTree();
        searchTree = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnOpen = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setLayout(new java.awt.BorderLayout());

        jSplitPane2.setDividerLocation(350);
        jSplitPane2.setDividerSize(2);

        jPanel5.setLayout(new java.awt.BorderLayout());

        tfName.setEditable(false);

        jLabel2.setLabelFor(tfName);
        jLabel2.setText("Název balíčku:");

        jLabel3.setText("Datum verze:");

        tfVersion.setEditable(false);
        tfVersion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfVersionActionPerformed(evt);
            }
        });

        jLabel4.setLabelFor(tfPath);
        jLabel4.setText("Název souboru:");

        tfPath.setEditable(false);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 36, Short.MAX_VALUE)
        );

        lbDetail.setBackground(new java.awt.Color(0, 0, 0));
        lbDetail.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lbDetail.setForeground(new java.awt.Color(0, 51, 153));
        lbDetail.setText("Detail");

        tfNote.setEditable(false);
        jScrollPane2.setViewportView(tfNote);

        jLabel5.setText("Poznámka:");

        jLabel6.setText("Popis:");

        tfDescription.setEditable(false);
        tfDescription.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfDescriptionActionPerformed(evt);
            }
        });

        tfHasPassword.setText("Zaheslován");
        tfHasPassword.setEnabled(false);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfName))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfPath))
                            .addComponent(lbDetail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addComponent(tfVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(tfHasPassword)
                                        .addGap(0, 330, Short.MAX_VALUE))
                                    .addComponent(jScrollPane2)))))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfDescription)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(lbDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(5, 5, 5)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(tfHasPassword))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(26, 26, 26)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel6, java.awt.BorderLayout.CENTER);

        tabs.addTab("Detail", jPanel5);

        jScrollPane1.setViewportView(methodCode);

        tabs.addTab("Zdroj", jScrollPane1);

        jSplitPane2.setRightComponent(tabs);

        jSplitPane1.setDividerLocation(450);
        jSplitPane1.setDividerSize(2);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jPanel2.setLayout(new java.awt.BorderLayout(5, 5));

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        PackageTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane3.setViewportView(PackageTree);

        jPanel2.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        searchTree.setToolTipText("Hledat metodu");
        searchTree.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                searchTreeKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchTreeKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                searchTreeKeyTyped(evt);
            }
        });
        jPanel2.add(searchTree, java.awt.BorderLayout.PAGE_START);

        jSplitPane1.setLeftComponent(jPanel2);

        jLabel1.setText("jLabel1");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel3.setPreferredSize(new java.awt.Dimension(347, 50));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        btnOpen.setText("Otevřít");
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });
        jPanel3.add(btnOpen);

        jButton1.setText("Rozbalit");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton1);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(98, 98, 98)
                .addComponent(jLabel1))
        );

        jSplitPane1.setBottomComponent(jPanel7);

        jSplitPane2.setLeftComponent(jSplitPane1);

        jPanel1.add(jSplitPane2, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1);

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @SuppressWarnings("empty-statement")
    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed

        JFileChooser jfch = new JFileChooser();
        jfch.setSelectedFile(new File("Tanaka.VyrPlanning.xml"));
        jfch.setMultiSelectionEnabled(true);
        FileFilter ff = new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.getPath().contains(".xml");

            }

            @Override
            public String getDescription() {
                return ".xml";
            }
        };
        jfch.setFileFilter(ff);

        if (jfch.showOpenDialog(this) == 0) {
            LoadFile lf;
            for (File f : jfch.getSelectedFiles()) {
                lf = new LoadFile(f);
                lf.start();
            }
            /*
             try {
             if (root.isPackageInRows(jfch.getSelectedFile().toURI().toURL().toString())) {
             JOptionPane.showMessageDialog(this, "Tento balíček je již načtený");
             return;
             }

             XMLReader reader = null;
             try {
             reader = new XMLReader(jfch.getSelectedFile().toURI().toURL());
             reader.load();
             } catch (MalformedURLException ex) {

             return;
             }

             ScriptPackage sPackage = new ScriptPackage((Document) reader.getXmlDoc());
             sPackage.setFullPath(jfch.getSelectedFile().toURI().toURL().toString());
             try {
             this.setTitle(sPackage.getName());
             root.addRow(sPackage);
             PackageTree.setModel(new PackageTreeModel(root, false));
             PackageTree.updateUI();

             } catch (Exception ex) {
             JOptionPane.showMessageDialog(this, "Neočekávaná výjimka\n" + ex);
             }

             } catch (XPathExpressionException ex) {
             JOptionPane.showMessageDialog(this, "Špatný formát souboru.");
             } catch (Exception ex) {
             JOptionPane.showMessageDialog(this, ex);
             }
             */
        }
    }//GEN-LAST:event_btnOpenActionPerformed

    private void tfVersionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfVersionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfVersionActionPerformed

    private void searchTreeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchTreeKeyTyped

    }//GEN-LAST:event_searchTreeKeyTyped

    private void searchTreeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchTreeKeyPressed

    }//GEN-LAST:event_searchTreeKeyPressed

    private void searchTreeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchTreeKeyReleased
        if (searchTree.getCaretPosition() > 1) {
            searchTree.setCaretPosition(searchTree.getCaretPosition() - 1);
            searchTree.setCaretPosition(searchTree.getCaretPosition() + 1);
        }
        ((PackageTreeModel) PackageTree.getModel()).setFilter(searchTree.getText());
        PackageTree.updateUI();
    }//GEN-LAST:event_searchTreeKeyReleased

    private void tfDescriptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfDescriptionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfDescriptionActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        for (int i = 0; i < PackageTree.getRowCount(); i++) {
            PackageTree.collapseRow(i);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTree PackageTree;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JLabel lbDetail;
    private javax.swing.JEditorPane methodCode;
    private javax.swing.JTextField searchTree;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTextField tfDescription;
    private javax.swing.JCheckBox tfHasPassword;
    private javax.swing.JTextField tfName;
    private javax.swing.JEditorPane tfNote;
    private javax.swing.JTextField tfPath;
    private javax.swing.JTextField tfVersion;
    // End of variables declaration//GEN-END:variables
}
