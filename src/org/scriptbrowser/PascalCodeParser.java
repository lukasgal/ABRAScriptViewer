/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scriptbrowser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author Lukáš Gál
 */
public class PascalCodeParser {

    private final String code;
    private final ArrayList defList = new ArrayList<>();
    public final static String REG_PROCEDURE = "^procedure+.*";
    public final static String REG_FUNCTION = "^function+.*";
    public final static String REG_CONST = "^\\bconst\\b+.*(?!\\:)";
    public final static String REG_VAR = "^\\bvar\\b$";
    public final static String REG_BEGIN = "([^ ]*)\\bbegin\\b([^ \\.]*)";
    public final static String REG_END = "([^ ]*)\\bend\\b([^ \\.]*)";
    public final static String REG_LBRAKET = "([^ ]*)\\u0028([^ \\.]*)";
    public final static String REG_RBRAKET = "([^ ]*)\\u0029([^ \\.]*)";    
    
    public PascalCodeParser(String code) {
        this.code = code;
    }

    public String[] parse() {
        StringBuilder str = new StringBuilder();
        String[] split = code.split("\n");
        int countLine = 1;
        int beginEndCount = 0;
        boolean isProcFunc = false;
        int countComment = 0;
        int countParentheses = 0;
        boolean hasParameters = false;
        String line;
        MethodDef method = new MethodDef();
        Parameter param = null;
        for (String split1 : split) {
            
            line = split1.trim();

            if (startCommentBlockDef(line)) {
                countComment++;
            }

            if (endCommentBlockDef(line)) {
                countComment--;
            }

            if (str.length() != 0 && isProcFunc && (hasParameters && (countParentheses > 0 || countParentheses==0)) &&  !(constDef(line) || varDef(line) || beginDef(line) || procedureDef(line) || functionDef(line))) {
                str.append(line.trim());
                
            } else if (str.length() != 0) {
                ArrayList<Parameter> paramList = new ArrayList<>();
                
                if (hasParameters){
                    int leftB = str.toString().indexOf("(");
                    int rightB = str.toString().indexOf(")");
                    String params = str.toString().substring(leftB+1, rightB);
                    String[] params2 = params.split(";");
                    for(String p:params2){
                        String[] p2 = p.split(":");
                        String dType=p2[1].trim();                        
                        String variable =p2[0].trim();
                        String[] v; 
                        int vType = Parameter.TYPE_NULL;
                        if(varAsParamDef(p2[0].trim()) || constDef(p2[0].trim())){
                           v = variable.split(" ");
                           vType = (varDef(v[0].trim())?Parameter.TYPE_VAR:Parameter.TYPE_CONST);
                           variable = v[1].trim();
                        }
                        param.setName(variable);
                        param.setDataType(dType);
                        param.setType(vType);
                        paramList.add(param);
                    }
                }
     //           method.setParams(paramList);
                defList.add(str.toString());
                System.out.println(method);
                str = new StringBuilder();
                hasParameters = false;
            }

            if ((procedureDef(line) || functionDef(line)) && countComment == 0) {
                param = new Parameter();
                method.setType((procedureDef(line))?MethodDef.TYPE_PROCEDURE:MethodDef.TYPE_FUNCTION);
                method.setLineNumber(countLine);
                    
                if (isProcFunc) {
                    str.append("sub");
                }
                if (startBracketDef(line))
                    hasParameters = true;
                
                str.append(countLine).append(" ").append(line.trim());
                isProcFunc = true;
            }
            if (startBracketDef(line) && isProcFunc) {
                countParentheses++;
            }

            if (endBracketDef(line) && isProcFunc) {
                countParentheses--;
            }
            if ((beginDef(line) || keywordsDef(line)) && countComment == 0) {
                beginEndCount++;
            }

            if (endDef(line) && countComment == 0) {
                beginEndCount--;
                if (beginEndCount == 0) {
                    isProcFunc = false;
                    hasParameters = false;
                }
            }
            countLine++;
        }

        String[] s = new String[defList.size()];
        for (int i = 0; i < defList.size(); i++) {
            s[i] = (String) defList.get(i);
        }

        return s;
    }

    private boolean procedureDef(String line) {
        String trim = line.trim().toLowerCase();
        Pattern p = Pattern.compile("^procedure+.*");
        Matcher m = p.matcher(trim);
        return m.find();
    }

    private boolean constDef(String line) {
        String trim = line.trim().toLowerCase();
        Pattern p = Pattern.compile("^\\bconst\\b+.*(?!\\:)");
        Matcher m = p.matcher(trim);
        return m.find();
    }

    private boolean endDef(String line) {
        String trim = line.trim().toLowerCase();
        Pattern p = Pattern.compile("([^ ]*)\\bend\\b([^ \\.]*)");
        Matcher m = p.matcher(trim);
        return m.find();
    }

    private boolean varAsParamDef(String line) {
        String trim = line.trim().toLowerCase();
        Pattern p = Pattern.compile("([^ ]*)\\bvar\\b([^ \\.]*)");
        Matcher m = p.matcher(trim);
        return m.find();
    }
    
    private boolean varDef(String line) {
        String trim = line.trim().toLowerCase();
        Pattern p = Pattern.compile("^\\bvar\\b$");
        Matcher m = p.matcher(trim);
        return m.find();
    }

    private boolean beginDef(String line) {
        String trim = line.trim().toLowerCase();
        Pattern p = Pattern.compile("([^ ]*)\\bbegin\\b([^ \\.]*)");
        Matcher m = p.matcher(trim);
        return m.find();
    }

    private boolean functionDef(String line) {
        String trim = line.trim().toLowerCase();
        Pattern p = Pattern.compile("^function+.*");
        Matcher m = p.matcher(trim);
        return m.find();
    }

    private boolean keywordsDef(String line) {
        String trim = line.trim().toLowerCase();
        Pattern p = Pattern.compile("([^ ]*)(\\btry\\b|\\bcase\\b)([^\\.]*)");
        Matcher m = p.matcher(trim);
        return m.find();
    }

    private boolean startCommentBlockDef(String line) {
        String trim = line.trim().toLowerCase();
        Pattern p = Pattern.compile("([^ ]*)\\u007B([^ \\.]*)");
        Matcher m = p.matcher(trim);
        return m.find();
    }

    private boolean endCommentBlockDef(String line) {
        String trim = line.trim().toLowerCase();
        Pattern p = Pattern.compile("([^ ]*)\\u007D([^ \\.]*)");
        Matcher m = p.matcher(trim);
        return m.find();
    }

    private boolean startBracketDef(String line) {
        String trim = line.trim().toLowerCase();
        Pattern p = Pattern.compile("([^ ]*)\\u0028([^ \\.]*)");
        Matcher m = p.matcher(trim);
        return m.find();
    }

    private boolean endBracketDef(String line) {
        String trim = line.trim().toLowerCase();
        Pattern p = Pattern.compile("([^ ]*)\\u0029([^ \\.]*)");
        Matcher m = p.matcher(trim);
        return m.find();
    }
}
