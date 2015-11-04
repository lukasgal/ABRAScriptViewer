package org.scriptbrowser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Euronics
 */
public final class BusinessObject{

    private final HashMap<String, String> bo = new HashMap<>();

    public BusinessObject() {
        load();
    }

    private void load() {
        File f = null;
        try {
            System.out.println(getClass().getResource("/resources/BusinessObjects.txt").toURI());
            f = new File(getClass().getResource("/resources/BusinessObjects.txt").toURI());
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        try (FileReader fr = new FileReader(f)) {
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] s = line.split(";");
                bo.put(s[1].trim(), s[0].trim());
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public String getBOName(String CLSID) {
        
        return bo.get(CLSID);

    }

}
