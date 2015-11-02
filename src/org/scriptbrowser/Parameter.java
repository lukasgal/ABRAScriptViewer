package org.scriptbrowser;

/**
 *
 * @author Lukáš Gál
 */
class Parameter {
    private String name;
    private String dataType;
    private int type;
    
    public static final int TYPE_NULL = 0;
    public static final int TYPE_VAR = 1;
    public static final int TYPE_CONST = 2;
    
    
    Parameter() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    
}
