/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scriptbrowser;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

/**
 *
 * @author Euronics
 */
public class PackageTreeModel implements javax.swing.tree.TreeModel {

    private final DefaultInterface scriptPackage;
    private String filter;
    boolean showParameters;

    public PackageTreeModel(DefaultInterface scriptPackage,boolean showParams) {
        this.scriptPackage = scriptPackage;
        this.showParameters = showParams;
    }

    @Override
    public Object getRoot() {
        return scriptPackage;
    }

    @Override
    public Object getChild(Object o, int i) {
        if (o instanceof MethodDef)
            ((MethodDef)o).setShowFullName(showParameters);
        if (filter != null) {
            if (!filter.isEmpty()) {
                return ((DefaultInterface) o).getFilteredRows(filter).get(i);
            }
            return ((DefaultInterface) o).getRows().get(i);
        }
        
        
        return ((DefaultInterface) o).getRows().get(i);

    }

    @Override
    public int getChildCount(Object o) {
        if (filter != null) {
            if (!filter.isEmpty()) {
                return ((DefaultInterface) o).getFilteredRows(filter).size();
            } else {
                return ((DefaultInterface) o).getRows().size();
            }
        }
        return ((DefaultInterface) o).getRows().size();
    }

    @Override
    public boolean isLeaf(Object o) {
        if (filter != null) {
            if (!filter.isEmpty()) {
                return ((DefaultInterface) o).getFilteredRows(filter).isEmpty();
            } else {
                return (((DefaultInterface) o).getRows().isEmpty());
            }
        }
        return (((DefaultInterface) o).getRows().isEmpty());

    }

    @Override
    public void valueForPathChanged(TreePath tp, Object o) {

    }

    @Override
    public int getIndexOfChild(Object o, Object o1) {
        return ((DefaultInterface) o).getRows().indexOf(o1);
    }

    @Override
    public void addTreeModelListener(TreeModelListener tl) {

    }

    @Override
    public void removeTreeModelListener(TreeModelListener tl) {

    }

    public void setFilter(String s) {
        this.filter = s;
    }

    public String getFilter() {
        return filter;
    }

    public boolean isShowParameters() {
        return showParameters;
    }

    public void setShowParameters(boolean showParameters) {
        this.showParameters = showParameters;
    }

}
