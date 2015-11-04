/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scriptbrowser;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Lukáš Gál
 */
public class XMLParser  {

    protected Document doc;
    protected XPath xPath;
    protected String name;
    private String description;
    private Integer usageState;
    private Integer runOrder;
    private String note;
    private boolean hasPassword;

    public XMLParser(Document doc) throws XPathExpressionException {
        this.doc = doc;
        this.xPath = XPathFactory.newInstance().newXPath();
        this.setPackageHeader();
    }

    private void setPackageHeader() throws XPathExpressionException {

        String expression = "ScriptPackageExport/Header";
        NodeList rows = (NodeList) xPath.compile(expression).evaluate(this.doc, XPathConstants.NODESET);
        
        this.name = ((Element) rows.item(0)).getElementsByTagName("Name").item(0).getTextContent();
        this.description = ((Element) rows.item(0)).getElementsByTagName("Description").item(0).getTextContent();
        this.usageState = Integer.parseInt(((Element) rows.item(0)).getElementsByTagName("UsageState").item(0).getTextContent());
        this.runOrder = Integer.parseInt(((Element) rows.item(0)).getElementsByTagName("RunOrder").item(0).getTextContent());
        this.note = ((Element) rows.item(0)).getElementsByTagName("Note").item(0).getTextContent();
        this.hasPassword = !((Element) rows.item(0)).getElementsByTagName("HashPassword").item(0).getTextContent().isEmpty();
    }

    public Document getDoc() {
        return doc;
    }

    public XPath getxPath() {
        return xPath;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getUsageState() {
        return usageState;
    }

    public Integer getRunOrder() {
        return runOrder;
    }

    public String getNote() {
        return note;
    }

    public boolean isHasPassword() {
        return hasPassword;
    }

    public void setHasPassword(boolean hasPassword) {
        this.hasPassword = hasPassword;
    }

    
}
