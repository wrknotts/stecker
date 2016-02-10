package stecker.util;

import com.google.common.net.PercentEscaper;


/**
 * Provides convenience fields and methods for encoding.
 */
public abstract class EncodingSupport {

    /**
     * Useful for escaping strings for compatibility with JavaScript
     */
    public static final PercentEscaper JAVASCRIPT_ESCAPER = new PercentEscaper("-_.*()", false);

    private static final String BACK_SLASH_DOUBLE_ESCAPED = "\\\\";
    private static final String BACK_SLASH_ESCAPED = "\\";
    private static final String DOUBLE_QUOTE_DOUBLE_ESCAPED = "\\\"";
    private static final String DOUBLE_QUOTE_ESCAPED = "\"";
    private static final String LF = "\n";

    /**
     * Alters provided code after highlighting to allow proper handling by
     * JavaScript in the browser.
     * 
     * @param highlightedCode source code that was highlighted
     * @return
     */
    public static String postProcessHighlightedSourceCode(String highlightedCode, boolean escapeDoubleQuotes) {

        if (highlightedCode.endsWith(LF)) {
            highlightedCode = highlightedCode.substring(0, highlightedCode.length() - 1);
        }

        if (escapeDoubleQuotes) {
            highlightedCode = highlightedCode.replace(DOUBLE_QUOTE_ESCAPED, DOUBLE_QUOTE_DOUBLE_ESCAPED);
        }
        
        return highlightedCode;
    }

    /**
     * Alters provided code prior to highlighting to allow proper handling by
     * JavaScript in the browser.
     * 
     * @param code source code to be highlighted
     * @return
     */
    public static String preProcessRawSourceCode(String code) {

        // escape backslashes embedded within source code
        StringBuilder sb = new StringBuilder(code.length());

        String[] lines = code.split(LF);
        for (String line : lines) {
            sb.append(line.replace(BACK_SLASH_ESCAPED, BACK_SLASH_DOUBLE_ESCAPED));
            sb.append(LF);
        }

        return sb.toString();
    }
}
