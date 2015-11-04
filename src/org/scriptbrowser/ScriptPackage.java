/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scriptbrowser;

import java.io.File;
import java.util.ArrayList;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Lukáš Gál
 */
class ScriptPackage extends XMLParser implements DefaultInterface {

    private final ArrayList<DefaultInterface> rows = new ArrayList<>();
    private String fullPath;
    private boolean isNew;
    private Packages parent;
    private String encoding;
    
    public ScriptPackage(Document doc) throws XPathExpressionException, Exception {
        super(doc);
        this.setScripts();
        this.isNew = true;
        this.encoding = doc.getXmlEncoding();
    }

    private void setScripts() throws XPathExpressionException, Exception {

        String expression = "ScriptPackageExport/Rows/Row";
        NodeList rows = (NodeList) xPath.compile(expression).evaluate(this.doc, XPathConstants.NODESET);

        for (int i = 0; i < rows.getLength(); i++) {
            try {
                Script script = new Script();
                script.setScriptKind(Integer.parseInt(((Element) rows.item(i)).getElementsByTagName("ScriptKind").item(0).getTextContent()));
                if(!(((Element) rows.item(i)).getAttribute("Position").isEmpty()))
                    script.setPosition(Integer.parseInt(((Element) rows.item(i)).getAttribute("Position")));
                else
                    script.setPosition(-1);
                script.setScriptID(((Element) rows.item(i)).getElementsByTagName("ScriptID").item(0).getTextContent());
                script.setCode(this.getScriptData(((script.getPosition()==-1)?(-1):(i + 1))));

                script.setRows(CodeParser.parse(script.getCode()));
                
                this.rows.add(script);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
                System.out.println(ex.getLocalizedMessage());
            }
        }
    }

    /**
     *
     * @param row
     * @return @throws XPathExpressionException
     */
    private String getScriptData(int row) throws XPathExpressionException {
        StringBuilder sb = new StringBuilder();
        String expression = "ScriptPackageExport/Rows/Row"+((row==-1)?"":"[@Position='" + row + "']")+"/ScriptData";
        NodeList rows = (NodeList) xPath.compile(expression).evaluate(this.doc, XPathConstants.NODESET);

        for (int i = 0; i < rows.getLength(); i++) {
            sb.append(rows.item(i).getFirstChild().getNodeValue());
        }

        return sb.toString();

    }

    @Override
    public ArrayList<DefaultInterface> getRows() {
        return rows;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
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

    public boolean isNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public Packages getParent() {
        return parent;
    }

    public void setParent(Packages parent) {
        this.parent = parent;
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

    String getFileName() {
        return new File(getFullPath()).getName().replace(".xml", "");
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
    
}
