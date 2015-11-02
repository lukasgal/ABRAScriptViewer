/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scriptbrowser;

import java.util.ArrayList;

/**
 *
 * @author Lukáš Gál
 */
class Packages implements DefaultInterface {

    private final ArrayList<DefaultInterface> rows = new ArrayList<>();
    private String name;
    private Packages parent;

    public Packages(String name) {
        this.name = name;
    }

    
    @Override
    public ArrayList<DefaultInterface> getRows() {
        return rows;
    }

    @Override
    public String getShortName() {
        return this.name;
    }

    @Override
    public String toString() {
        return getShortName();
    }

    @Override
    public boolean equals(String c) {

        return getShortName().toUpperCase().contains(c.toUpperCase());

    }
   
    
    @Override
    public ArrayList<DefaultInterface> getFilteredRows(String s) {
        ArrayList<DefaultInterface> fRows = new ArrayList<>();
        for (DefaultInterface di : getRows()) {
            if (!di.getFilteredRows(s).isEmpty()) {
                fRows.add(di);
            }
        }
        return fRows;
    }
    
    public boolean isPackageInRows(String path) {
        
        for (DefaultInterface di : getRows()) {
            if(di instanceof ScriptPackage){
                if(((ScriptPackage)(di)).getFullPath().equals(path))
                    return true;
            }else return (((Packages)di).isPackageInRows(path)) ;                
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void addRow(ScriptPackage add){
        for (DefaultInterface di : getRows()) {
            if (((Packages)di).getName().equals(add.getName())) {
                add.setParent((Packages)di);
                ((Packages)di).getRows().add(add);
                return;
            }
        }
        Packages newPackage = new Packages(add.getName());
        newPackage.setParent(this);
        newPackage.getRows().add(add);
        add.setParent((Packages)newPackage);
        this.getRows().add(newPackage);
    }

    public Packages getParent() {
        return parent;
    }

    public void setParent(Packages parent) {
        this.parent = parent;
    }
    
}
