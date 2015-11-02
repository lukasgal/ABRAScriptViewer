package org.scriptbrowser;

import java.util.ArrayList;

/**
 *
 * @author Lukáš Gál
 */
public class MethodDef implements DefaultInterface {

    public static final int TYPE_PROCEDURE = 0;
    public static final int TYPE_FUNCTION = 1;
    public static final String[] METHOD_TYPE_NAME = new String[]{"procedure", "function"};

    private int lineNumber;
    private int codeLength;
    private int type;
    private String name;
    private String params;
    private Script parent;
    private String returnType;
    private boolean isChild;
    private boolean showFullName;

    private final ArrayList<DefaultInterface> rows = new ArrayList<>();

    public MethodDef() {
        this.returnType = null;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    @Override
    public ArrayList<DefaultInterface> getRows() {
        return rows;
    }

    public void addChild(MethodDef md) {
        rows.add(md);
    }

    public boolean isIsChild() {
        return isChild;
    }

    public void setIsChild(boolean isChild) {
        this.isChild = isChild;
    }

    @Override
    public String getShortName() {
        return METHOD_TYPE_NAME[type] + " " + name;
    }

    public String getFullName() {
        return getShortName() + ((!getParams().isEmpty()) ? ("(" + getParams() + ")") : "");
    }

    @Override
    public String toString() {
        if (!isShowFullName()) {
            return getShortName();
        } else {
            return getFullName();
        }
    }

    public int getCodeLength() {
        return codeLength;
    }

    public void setCodeLength(int codeLength) {
        this.codeLength = codeLength;
    }

    public Script getParent() {
        return parent;
    }

    /**
     *
     * @param parent
     */
    public void setParent(Script parent) {
        this.parent = parent;
        for (DefaultInterface md : getRows()) {
            ((MethodDef) md).setParent(this.parent);
        }
    }

    public boolean isShowFullName() {
        return showFullName;
    }

    public void setShowFullName(boolean showFullName) {
        this.showFullName = showFullName;
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
