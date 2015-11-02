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
class Script implements DefaultInterface {

    private String scriptID;
    private int scriptKind;
    private int position;
    private String code;

    private ArrayList<DefaultInterface> rows;

    public Script() {
    }

    public String getScriptID() {
        return scriptID;
    }

    public void setScriptID(String scriptID) {
        this.scriptID = scriptID;
    }

    public int getScriptKind() {
        return scriptKind;
    }

    public void setScriptKind(int scriptKind) {
        this.scriptKind = scriptKind;
    }

    public ArrayList<DefaultInterface> getRows() {
        return rows;
    }

    public void setRows(ArrayList<DefaultInterface> methods) {
        for (DefaultInterface md : methods) {
            ((MethodDef) md).setParent(this);
        }

        this.rows = methods;
    }

    @Override
    public String getShortName() {
        return getScriptID();
    }

    @Override
    public String toString() {
        return getShortName();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(String c) {

        return getShortName().toUpperCase().contains(c.toUpperCase());

    }

    @Override
    public ArrayList<DefaultInterface> getFilteredRows(String s) {
        ArrayList<DefaultInterface> fRows = new ArrayList<>();
        for (DefaultInterface di : getRows()) {
            if (di.equals(s)) {
                fRows.add(di);
            }
        }
        return fRows;
    }
}
