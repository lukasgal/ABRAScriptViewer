/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scriptbrowser;

import java.util.ArrayList;

/**
 *
 * @author Euronics
 */
public interface DefaultInterface {
    
    public String getShortName();
    
    public ArrayList<DefaultInterface> getRows();
    
    public ArrayList<DefaultInterface> getFilteredRows(String s);
    
    public boolean equals(String s);
    
    
    
}
