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
public final class Agenda{

    private final HashMap<String, String> agends = new HashMap<>();

    public Agenda() {
        load();
    }

    private void load() {
        File f = null;
        try {
            System.out.println(getClass().getResource("/resources/Agendy.txt").toURI());
            f = new File(getClass().getResource("/resources/Agendy.txt").toURI());
        } catch (URISyntaxException ex) {
            Logger.getLogger(Agenda.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (FileReader fr = new FileReader(f)) {
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] s = line.split("\t");
                agends.put(s[1].trim(), s[0].trim());
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Agenda.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Agenda.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String getAgendaName(String CLSID) {
        
        return agends.get(CLSID);

    }

}
