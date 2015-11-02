/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scriptbrowser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.Highlighter;

/**
 *
 * @author Euronics
 */
public final class CodeFormatter {

    private static final String HTML_HEADER = "<html><head><meta content-type=\"text/html\" charset=\"windows-1250\"></head><body>";
    private static final String HTML_FOOT = "</body></html>";
    
    public CodeFormatter() {
    }

    public static String formatToHTML(String s) {
        String resText;
        String[] lines = s.split("\n");
        
        resText = HTML_HEADER + "";
        resText += "<pre>";
        for (String line : lines) {
            Pattern r = Pattern.compile("(procedure|function)");
            Matcher m = r.matcher(line);
            if (m.find()) {
                line = m.replaceAll("<span class=\"keyword\">" + m.group(0) + "</span>");
                m.reset();
                
            }
            r = Pattern.compile(CodeParser.REGEX_BEGIN_BLOCK);
            m = r.matcher(line);
            if (m.find()) {
                line = m.replaceAll("<span class=\"keyword\">" + m.group(0) + "</span>");
                m.reset();
            }
            
            r = Pattern.compile(CodeParser.REGEX_KEYWORDS_BLOCK);
            m = r.matcher(line);
            if (m.find()) {
                line = m.replaceAll("<span class=\"keyword\">" + m.group(0) + "</span>");
                m.reset();
                
            }
            resText += line;
        }
        /*s = s.replaceAll("function ", "<span class=\"keyword\">function</span> ");
         s = s.replaceAll("procedure ", "<span class=\"keyword\">procedure</span> ");
         s = s.replaceAll("begin", "<span class=\"keyword\">begin</span>");
         s = s.replaceAll("end;", "<span class=\"keyword\">end;</span>");
         s = s.replaceAll("try", "<span class=\"keyword\">try</span>");
         s = s.replaceAll("for ", "<span class=\"keyword\">for</span> ");
         s = s.replaceAll("with ", "<span class=\"keyword\">with</span> ");*/

        
        /*int i = 1;
         for(String line:lines){
         resText+=line;
         i++;
         }*/
        resText += "</pre>";
        resText += "" + HTML_FOOT;

        return resText;

    }
}
