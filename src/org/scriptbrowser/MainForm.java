/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scriptbrowser;

import java.awt.Event;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
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
public final class MainForm extends javax.swing.JFrame {

    private final HTMLEditorKit htmlKit = new HTMLEditorKit();

    private Script selectedScript;
    private int chF;

    private final Packages root;

    private class LoadFile extends SwingWorker<Object, Object> {

        private final File file;

        public LoadFile(File file) {
            this.file = file;
        }

        @Override
        protected void process(List<Object> chunks) {
            super.process(chunks);
        }

        @Override
        protected Object doInBackground() throws Exception {
            try {
                if (root.isPackageInRows(file.toURI().toURL().toString())) {
                    JOptionPane.showMessageDialog(null, "Tento balíček je již načtený");
                }

                XMLReader reader = null;
                reader = new XMLReader(file.toURI().toURL());
                reader.load();
                if (reader.getXmlDoc() == null) {

                }
                ScriptPackage sPackage = new ScriptPackage((Document) reader.getXmlDoc());
                sPackage.setFullPath(file.toURI().toURL().toString());

                root.addRow(sPackage);

                setProgress(chF++);

            } catch (XPathExpressionException ex) {
                JOptionPane.showMessageDialog(null, "Špatný formát souboru.");
                ex.printStackTrace();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
            return null;
        }

        @Override
        protected void done() {
            super.done();
            PackageTree.setModel(new PackageTreeModel(root, false));
            PackageTree.updateUI();
        }

    }

    /**
     * Creates new form MainForm
     */
    public MainForm() {
        root = new Packages("Balíčky skriptů");
        initComponents();
        controlActions();
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
                    if ((element instanceof ScriptPackage)) {
                        Packages objParent = (Packages) ((ScriptPackage) element).getParent();
                        Packages pkgParent = null;
                        if (objParent.getRows().size() == 1) {
                            pkgParent = objParent.getParent();
                        }
                        //JOptionPane.showMessageDialog(null, objParent.getParent());
                        if (pkgParent != null) {
                            pkgParent.getRows().remove(objParent);
                        } else {
                            objParent.getRows().remove(element);
                        }
                        //PackageTree.setModel(new PackageTreeModel(root, false));
                        PackageTree.updateUI();
                    }
                }
            }

        });

        PackageTree.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                try {

                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    LoadFile lf;
                    for (File file : droppedFiles) {
                        chF = 1;
                        lf = new LoadFile(file);
                        lf.execute();

                    }
                } catch (UnsupportedFlavorException | IOException ex) {
                    ex.printStackTrace();
                }
            }

        });

        PackageTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent tse) {
                DefaultInterface element = (DefaultInterface) PackageTree.getLastSelectedPathComponent();
                controlActions();
                if (element instanceof ScriptPackage) {
                    tfName.setText(((ScriptPackage) element).getName());
                    tfPath.setText(((ScriptPackage) element).getFullPath());
                    tfNote.setText(((ScriptPackage) element).getNote());
                    tfDescription.setText(((ScriptPackage) element).getDescription());
                    tfHasPassword.setSelected(((ScriptPackage) element).isHasPassword());
                    tfEncoding.setText(((ScriptPackage) element).getEncoding());
                    closePkg.setEnabled(true);
                    pkgDetail.setVisible(true);
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

    public void controlActions() {
        closePkg.setEnabled(false);
        pkgDetail.setVisible(false);
    }

    private void initIcons() {
        //ImageIcon = new ImageIcon(getClass().getResource("/resources/packages.png"));
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
        pkgDetail = new javax.swing.JPanel();
        tfName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tfVersion = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tfPath = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lbDetail = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tfNote = new javax.swing.JEditorPane();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tfDescription = new javax.swing.JTextField();
        tfHasPassword = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        tfEncoding = new javax.swing.JTextField();
        scriptDetail = new javax.swing.JPanel();
        tfScriptName = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        tfScriptType = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        lbDetail1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        methodCode = new javax.swing.JEditorPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        PackageTree = new javax.swing.JTree();
        searchTree = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jButton2 = new javax.swing.JButton();
        save = new javax.swing.JButton();
        closePkg = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jSplitPane2.setDividerLocation(250);
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

        jLabel1.setText("jLabel1");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
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

        jLabel7.setText("Kódování:");

        tfEncoding.setEditable(false);
        tfEncoding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfEncodingActionPerformed(evt);
            }
        });

        tfScriptName.setEditable(false);

        jLabel8.setLabelFor(tfName);
        jLabel8.setText("Název skriptu:");

        jLabel10.setLabelFor(tfPath);
        jLabel10.setText("Typ skriptu:");

        tfScriptType.setEditable(false);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 307, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 149, Short.MAX_VALUE)
        );

        lbDetail1.setBackground(new java.awt.Color(0, 0, 0));
        lbDetail1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lbDetail1.setForeground(new java.awt.Color(0, 51, 153));
        lbDetail1.setText("Detail");

        javax.swing.GroupLayout scriptDetailLayout = new javax.swing.GroupLayout(scriptDetail);
        scriptDetail.setLayout(scriptDetailLayout);
        scriptDetailLayout.setHorizontalGroup(
            scriptDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scriptDetailLayout.createSequentialGroup()
                .addGroup(scriptDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(scriptDetailLayout.createSequentialGroup()
                        .addGap(251, 251, 251)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 254, Short.MAX_VALUE))
                    .addGroup(scriptDetailLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(scriptDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(scriptDetailLayout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfScriptName))
                            .addGroup(scriptDetailLayout.createSequentialGroup()
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfScriptType))
                            .addComponent(lbDetail1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        scriptDetailLayout.setVerticalGroup(
            scriptDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scriptDetailLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(lbDetail1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(scriptDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfScriptName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(5, 5, 5)
                .addGroup(scriptDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfScriptType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(408, 408, 408)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(128, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pkgDetailLayout = new javax.swing.GroupLayout(pkgDetail);
        pkgDetail.setLayout(pkgDetailLayout);
        pkgDetailLayout.setHorizontalGroup(
            pkgDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pkgDetailLayout.createSequentialGroup()
                .addGroup(pkgDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pkgDetailLayout.createSequentialGroup()
                        .addGap(251, 251, 251)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pkgDetailLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pkgDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pkgDetailLayout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfName))
                            .addGroup(pkgDetailLayout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfPath))
                            .addComponent(lbDetail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(pkgDetailLayout.createSequentialGroup()
                                .addGroup(pkgDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pkgDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pkgDetailLayout.createSequentialGroup()
                                        .addComponent(tfVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(tfHasPassword)
                                        .addGap(0, 453, Short.MAX_VALUE))
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                            .addGroup(pkgDetailLayout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfDescription)))))
                .addContainerGap())
            .addGroup(pkgDetailLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(pkgDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pkgDetailLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(scriptDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        pkgDetailLayout.setVerticalGroup(
            pkgDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pkgDetailLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(lbDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pkgDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(5, 5, 5)
                .addGroup(pkgDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pkgDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(tfHasPassword))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pkgDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pkgDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(34, 34, 34)
                .addGroup(pkgDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(128, Short.MAX_VALUE))
            .addGroup(pkgDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pkgDetailLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(scriptDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jPanel5.add(pkgDetail, java.awt.BorderLayout.CENTER);

        tabs.addTab("Detail", jPanel5);

        jScrollPane1.setViewportView(methodCode);

        tabs.addTab("Zdroj", jScrollPane1);

        jSplitPane2.setRightComponent(tabs);

        jSplitPane1.setDividerLocation(0);
        jSplitPane1.setDividerSize(0);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jPanel2.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setLeftComponent(jPanel2);

        jPanel3.setLayout(new java.awt.BorderLayout());

        PackageTree.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        PackageTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane3.setViewportView(PackageTree);

        jPanel3.add(jScrollPane3, java.awt.BorderLayout.CENTER);

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
        jPanel3.add(searchTree, java.awt.BorderLayout.PAGE_END);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 242, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel3.add(jPanel4, java.awt.BorderLayout.PAGE_START);

        jTabbedPane1.addTab("Balíčky", jPanel3);

        jSplitPane1.setRightComponent(jTabbedPane1);

        jSplitPane2.setLeftComponent(jSplitPane1);

        jPanel1.add(jSplitPane2, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jToolBar1.setRollover(true);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/open.png"))); // NOI18N
        jButton2.setText("Otevřít");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                open(evt);
            }
        });
        jToolBar1.add(jButton2);

        save.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/save.png"))); // NOI18N
        save.setText("Uložit");
        save.setFocusable(false);
        save.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        save.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(save);

        closePkg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/close.png"))); // NOI18N
        closePkg.setText("Zavřít balíček");
        closePkg.setFocusable(false);
        closePkg.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        closePkg.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        closePkg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closePkg(evt);
            }
        });
        jToolBar1.add(closePkg);
        jToolBar1.add(jSeparator1);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        jMenu1.setText("File");

        jMenu3.setText("jMenu3");
        jMenu1.add(jMenu3);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tfVersionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfVersionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfVersionActionPerformed

    private void tfDescriptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfDescriptionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfDescriptionActionPerformed

    private void tfEncodingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfEncodingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfEncodingActionPerformed

    private void open(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_open
        JFileChooser jfch = new JFileChooser();
        jfch.setSelectedFile(new File("Tanaka.VyrPlanning.xml"));
        jfch.setMultiSelectionEnabled(true);
        FileFilter ff = new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.getPath().contains(".xml") || f.isDirectory();

            }

            @Override
            public String getDescription() {
                return ".xml";
            }
        };
        jfch.setFileFilter(ff);

        if (jfch.showOpenDialog(this) == 0) {
            LoadFile lf;
            chF = 1;
            for (File f : jfch.getSelectedFiles()) {
                lf = new LoadFile(f);
                lf.execute();
            }
        }

    }//GEN-LAST:event_open

    private void searchTreeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchTreeKeyTyped

    }//GEN-LAST:event_searchTreeKeyTyped

    private void searchTreeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchTreeKeyReleased
        if (searchTree.getCaretPosition() > 1) {
            searchTree.setCaretPosition(searchTree.getCaretPosition() - 1);
            searchTree.setCaretPosition(searchTree.getCaretPosition() + 1);
        }
        ((PackageTreeModel) PackageTree.getModel()).setFilter(searchTree.getText());
        PackageTree.updateUI();
    }//GEN-LAST:event_searchTreeKeyReleased

    private void searchTreeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchTreeKeyPressed

    }//GEN-LAST:event_searchTreeKeyPressed

    private void closePkg(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closePkg
        DefaultInterface element = (DefaultInterface) PackageTree.getLastSelectedPathComponent();
        if ((element instanceof ScriptPackage)) {
            Packages objParent = (Packages) ((ScriptPackage) element).getParent();
            Packages pkgParent = null;
            if (objParent.getRows().size() == 1) {
                pkgParent = objParent.getParent();
            }
            //JOptionPane.showMessageDialog(null, objParent.getParent());
            if (pkgParent != null) {
                pkgParent.getRows().remove(objParent);
            } else {
                objParent.getRows().remove(element);
            }
            //PackageTree.setModel(new PackageTreeModel(root, false));
            PackageTree.updateUI();
        }

    }//GEN-LAST:event_closePkg

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
    private javax.swing.JButton closePkg;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lbDetail;
    private javax.swing.JLabel lbDetail1;
    private javax.swing.JEditorPane methodCode;
    private javax.swing.JPanel pkgDetail;
    private javax.swing.JButton save;
    private javax.swing.JPanel scriptDetail;
    private javax.swing.JTextField searchTree;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTextField tfDescription;
    private javax.swing.JTextField tfEncoding;
    private javax.swing.JCheckBox tfHasPassword;
    private javax.swing.JTextField tfName;
    private javax.swing.JEditorPane tfNote;
    private javax.swing.JTextField tfPath;
    private javax.swing.JTextField tfScriptName;
    private javax.swing.JTextField tfScriptType;
    private javax.swing.JTextField tfVersion;
    // End of variables declaration//GEN-END:variables
}
