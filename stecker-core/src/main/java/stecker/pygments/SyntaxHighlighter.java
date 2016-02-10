package stecker.pygments;

import java.util.List;

import org.python.core.PyList;

/**
 * Defines type used to create Java/Jython bridge to Pygments.
 * 
 * @see stecker.config.SyntaxHighlighterConfiguration
 */
public interface SyntaxHighlighter {

    PyList getLexerInfo() throws SyntaxHighlighterException;

    List<String> guessLexer(String code) throws SyntaxHighlighterException;

    List<PyList> guessLexerForFilename(String filename, String code)
            throws SyntaxHighlighterException;

    String highlightCode(String code, String type, PyList lineNbrsToHighlight,
            Boolean escapeNewlinesForJavaScript) throws SyntaxHighlighterException;
}
