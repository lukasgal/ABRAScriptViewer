package org.scriptbrowser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Lukáš Gál
 */
public final class CodeParser {

    public static final String REGEX_PROCFUN = "((procedure|function)(\\s)*(\\w+?)(\\s)*(\\u0028((.)*)\\u0029)??((\\s)*(:){1}(\\s)*([\\w\\p{Space}])+?)??(\\s)*;)";
    public static final String REGEX_HEADER = "((procedure|function)(\\s)*(([\\p{Alnum}(_)(\\-)])+?)(\\s)*)";
    public static final String REGEX_START_COMMENT = "((\\{)|(\\(\\*))";
    public static final String REGEX_END_COMMENT = "((\\})|(\\*\\)))";
    public static final String REGEX_BEGIN_BLOCK = "((\\bbegin\\b)|(\\btry\\b)|(\\bcase\\b))";
    public static final String REGEX_END_BLOCK = "\\b(end)\\b";
    public static final String REGEX_KEYWORDS_BLOCK = "((\\bwith\\b)|(\\band\\b)|(\\bof\\b)|(\\bor\\b)|(\\bconst\\b)|(\\bvar\\b)|(\\bbegin\\b)|(\\btry\\b)|(\\bcase\\b)|(\\bif\\b)|(\\bthen\\b)(\\belse\\b)|(\\buses\\b)|(\\while\\b)|(\\bfor\\b)|(\\bdo\\b))";
    CodeParser() {

    }

    public static ArrayList<DefaultInterface> parse(String code) throws Exception {
        StringBuilder sb = new StringBuilder();
        ArrayList<DefaultInterface> mdList = new ArrayList<>();
        int countLine = 0;
        int startLine = 0;
        int endLine = 0;
        int top = -1;
        int methodsCounter = 0;
        int blocksCounter = 0;
        ArrayList<Integer> lifo = new ArrayList<>();
        ArrayList<Integer> methodsList = new ArrayList<>();
        int removedElement;
        boolean isStartBlock = false;
        boolean isComment = false;
        boolean isMethod = false;
        boolean isChild = false;
        if (code == null) {
            return mdList;
        }

        String[] split = code.split("\n");

        for (String line : split) {
            countLine++;
            line = line.replaceAll("&#xD;", "");
            line = line.replaceAll("\r", "");
            line = line.replaceAll("\t", "");
            if (match(line, REGEX_START_COMMENT)) {
                isComment = true;
            }
            if (match(line, REGEX_END_COMMENT)) {
                isComment = false;
            }

            line = (line.contains("//")) ? line.substring(0, line.indexOf("//")) : line;
            line = (line.contains("(*")) ? line.substring(0, line.indexOf("{*")) : line;
            line = (line.contains("{")) ? line.substring(0, line.indexOf("{")) : line;
            line = (line.contains("}")) ? line.substring(0, line.indexOf("}")) : line;
            line = (line.contains("*)")) ? line.substring(0, line.indexOf("*)")) : line;

            if (line.length() < 1) {
                continue;
            }

            sb.append(line.trim());

            if (match(sb.toString(), REGEX_HEADER) && !isComment) {
                startLine = countLine;
            }

            try {

                Pattern p = Pattern.compile(REGEX_PROCFUN, Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(sb.toString());
                if (m.find() && !isComment) {
                    MethodDef md = new MethodDef();
                    md.setName(m.group(4));
                    md.setReturnType(m.group(9));
                    md.setLineNumber(startLine);
                    md.setParams(m.group(7));
                    md.setCodeLength(endLine);
                    md.setType((m.group(2).matches("procedure") ? MethodDef.TYPE_PROCEDURE : MethodDef.TYPE_FUNCTION));

                    if (isMethod) {
                        isChild = true;
                        md.setIsChild(isChild);
                        ((MethodDef) (mdList.get(mdList.size() - 1))).addChild(md);
                        //System.out.println("ChildMethod=" + methodsCounter + " " + m.group(0));
                    } else {
                        isChild = false;
                        mdList.add(md);
                        isMethod = true;
                        //System.out.println("Method=" + methodsCounter + " " + m.group(0));
                    }
                    methodsList.add(methodsCounter);
                    lifo.add(methodsCounter++);

                    sb = new StringBuilder();

                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

            if (match(line, REGEX_BEGIN_BLOCK) && !isComment) {
                lifo.add(blocksCounter--);
                //System.out.println("StartBlock=" + blocksCounter);
            }
            if (match(line, REGEX_END_BLOCK) && !isComment) {
                top = lifo.size() - 1;
                removedElement = -1000;

                if (!lifo.isEmpty()) {

                    if (lifo.get(top) < 0) {
                        removedElement = lifo.remove(top);
                        //System.out.println("END=" + removedElement);
                    }
                    top = lifo.size() - 1;
                    if (!lifo.isEmpty()) {
                        if (lifo.get(top) >= 0) {
                            removedElement = lifo.remove(top);
                            if (!methodsList.isEmpty()) {
                                methodsList.remove(methodsList.size() - 1);
                                endLine = countLine;
                            }
                            //System.out.println("ENDMethod=" + removedElement);
                        }
                    }
                }
                if (methodsList.isEmpty()) {
                    isMethod = false;
                    blocksCounter = 0;
                    methodsCounter = 0;
                    //System.out.println("ENDMainMethod=" + removedElement);
                }

            }
        }
        return mdList;
    }

    public static boolean match(String line, String pattern) {
        Pattern p;
        Matcher m;
        p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        m = p.matcher(line);
        return m.find();
    }
}
