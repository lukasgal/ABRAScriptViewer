/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scriptbrowser;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Euronics
 */
public class PackageTreeCellRenderer extends DefaultTreeCellRenderer {

    private final Icon pckIcon;
    private final Icon scriptIcon;
    private final Icon functionIcon;
    private final Icon procedureIcon;
    private final Icon rootIcon;
    private final Icon pckGroupIcon;
    private final Icon agendaIcon;
    private final Icon libIcon;
    private final Icon boIcon;
    
    public PackageTreeCellRenderer() {
        this.rootIcon = new ImageIcon(getClass().getResource("/resources/packages.png"));
        this.pckGroupIcon = new ImageIcon(getClass().getResource("/resources/packagesGroup.png"));
        this.pckIcon = new ImageIcon(getClass().getResource("/resources/package.png"));
        this.scriptIcon = new ImageIcon(getClass().getResource("/resources/script.png"));
        this.functionIcon = new ImageIcon(getClass().getResource("/resources/function.png"));
        this.procedureIcon = new ImageIcon(getClass().getResource("/resources/procedure.png"));
        this.agendaIcon = new ImageIcon(getClass().getResource("/resources/agenda.png"));
        this.libIcon = new ImageIcon(getClass().getResource("/resources/library.png"));
        this.boIcon = new ImageIcon(getClass().getResource("/resources/bo.png"));
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (value instanceof Packages) {

            setText((String) ((Packages) value).getShortName() + (!((Packages) value).getRows().isEmpty() ? (" (" + ((Packages) value).getRows().size() + ")") : ""));
            if (tree.getModel().getRoot() == value) {
                setIcon(rootIcon);
            } else {
                setIcon(pckGroupIcon);
            }
        }
        if (value instanceof ScriptPackage) {
            if (((ScriptPackage) value).isNew()) //setFont(getFont().deriveFont(Font.ITALIC));
            {
                
            }
            setText(((ScriptPackage)value).getFileName());
            setIcon(pckIcon);
        }
        if (value instanceof Script) {
            switch(((Script)value).getScriptKind()){
                case 0:{setIcon(libIcon);break;}
                case 1:{setIcon(boIcon);break;}
                case 2:{setIcon(agendaIcon);break;}
                default: setIcon(scriptIcon);
            }
            
        }
        if (value instanceof MethodDef) {
            if (((MethodDef) value).getType() == MethodDef.TYPE_FUNCTION) {
                setIcon(functionIcon);
            }
            if (((MethodDef) value).getType() == MethodDef.TYPE_PROCEDURE) {
                setIcon(procedureIcon);
            }

        }
        return this;

    }

}
