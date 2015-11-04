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
    private static final Agenda agenda = new Agenda();
    private static final BusinessObject bo = new BusinessObject();
    private ArrayList<DefaultInterface> rows;
    private static final String[] scriptKindName = new String[]{"Knihovna","Business objekt","Agenda","Číselník","Importovací manager","Aplikační modul"};

    public Script() {
    }

    public String getScriptID() {
        return scriptID;
    }

    public void setScriptID(String scriptID) {
        this.scriptID = scriptID.trim();
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
        String ret = "";
        switch(this.scriptKind){
            case 1:{
                ret = bo.getBOName(getScriptID());break;}
            case 2:{ret = agenda.getAgendaName(getScriptID());break;}
            default: ret = getScriptID();
        }
        return ret;
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
    
    public String getScriptKindName(){
        return scriptKindName[getScriptKind()];
    }
}
