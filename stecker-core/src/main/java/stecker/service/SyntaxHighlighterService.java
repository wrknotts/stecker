package stecker.service;

import static stecker.util.EncodingSupport.postProcessHighlightedSourceCode;
import static stecker.util.EncodingSupport.preProcessRawSourceCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.python.core.PyException;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyTuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stecker.domain.CodeDescriptor;
import stecker.domain.FileDescriptor;
import stecker.pygments.SyntaxHighlighter;
import stecker.pygments.SyntaxHighlighterException;

/**
 * Encapsulates all interaction with Pygments using the provided (
 * {@code SyntaxHighlighter}.
 */
@Service
public class SyntaxHighlighterService {

    private static final String DEFAULT_TYPE = "text";
    private static final String PYGMENTS_UTIL_CLASS_NOT_FOUND = "<class 'pygments.util.ClassNotFound'>";

    private Map<String, List<String>> availableLexers = Collections
            .<String, List<String>> emptyMap();
    private SyntaxHighlighter highlighter;

    /**
     * Returns {@code Map} of all available Pygments lexers, keyed by lexer name
     * with each entry being a {@code List} of recognized file extensions for
     * the lexer.
     * 
     * @return {@code Map} of lexers supported by Pygments
     */
    public Map<String, List<String>> getLexers() {
        return availableLexers;
    }

    /**
     * Highlight source code
     * 
     * @param descriptor describes source code to be highlighted
     * @return highlighted code
     */
    public void highlightSourceCode(CodeDescriptor descriptor) {

        highlightSourceCode(descriptor, Collections.<Integer, List<String>> emptyMap(), true, true);

    }
    
    /**
     * Highlight source code
     * 
     * @param descriptor describes source code to be highlighted
     * @param indexedLineNumbers an array of line numbers to be highlighted by
     *            Pygments. The index of the array corresponds to the position
     *            of the files retrieved from an SCM
     * @return highlighted code
     */
    public void highlightSourceCode(CodeDescriptor descriptor,
            Map<Integer, List<String>> indexedLineNumbers) {
        highlightSourceCode(descriptor, indexedLineNumbers, true, true);
    }

    /**
     * Highlight source code
     * 
     * @param descriptor describes source code to be highlighted
     * @param indexedLineNumbers an array of line numbers to be highlighted by
     *            Pygments. The index of the array corresponds to the position
     *            of the files retrieved from an SCM
     * @return highlighted code
     */
    public void highlightSourceCode(CodeDescriptor descriptor,
            Map<Integer, List<String>> indexedLineNumbers, boolean escapeForJavaScript, boolean escapeDoubleQuotes) {

        List<FileDescriptor> fileDescriptors = descriptor.getFileDescriptors();

        // attempt to ensure predictable ordering
        Collections.sort(fileDescriptors);

        for (int i = 0; i < fileDescriptors.size(); i++) {

            FileDescriptor fd = fileDescriptors.get(i);

            String preprocessed = preProcessRawSourceCode(fd.getRawCode());

            List<String> types = guessSourceCodeType(fd.getName(), preprocessed);

            // use default type if nothing guessed otherwise use first entry in
            // list
            String type = (((null == types) || (0 == types.size())) ? DEFAULT_TYPE : types.get(0));

            String highlighted = highlight(
                    preprocessed,
                    type,
                    (indexedLineNumbers.containsKey(i) ? indexedLineNumbers.get(i) : Collections
                            .<String> emptyList()), escapeForJavaScript);

            fd.setHighlightedCode(postProcessHighlightedSourceCode(highlighted, escapeDoubleQuotes));
        }

    }

    /**
     * Automatically invoked after dependency injection is done to perform any
     * additional initialization.
     */
    @PostConstruct
    public void postConstruct() {

        this.availableLexers = buildLexerMap();

    }

    /**
     * Establish the {@code SyntaxHighlighter} to be used by this service.
     * 
     * @param highlighter
     */
    @Autowired
    public void setSyntaxHighlighter(SyntaxHighlighter highlighter) {
        this.highlighter = highlighter;
    }

    /**
     * Utility method that translates the Jython types returned from Pygments
     * that represent the available lexers into something more usable
     * 
     * @return {@code Map} of lexers supported by Pygments
     */
    @SuppressWarnings("unchecked")
    private Map<String, List<String>> buildLexerMap() {

        Map<String, List<String>> result = new TreeMap<String, List<String>>(
                String.CASE_INSENSITIVE_ORDER);

        for (PyTuple pt : ((List<PyTuple>) highlighter.getLexerInfo())) {

            PyObject[] pa = pt.getArray();
            String name = pa[0].asString();

            Set<String> available = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

            for (String s : pa[2].asString().split(",")) {
                if (!s.isEmpty()) {
                    available.add(s.trim());
                }
            }

            if (0 != available.size()) {
                result.put(name, Collections.unmodifiableList(new ArrayList<String>(available)));
            }

        }

        return Collections.unmodifiableMap(result);

    }

    /**
     * Returns the name of the source code type based upon the provided filename
     * and source code.
     * 
     * @param filename name of file containing source code
     * @param code source code
     * @return name of source code type or null if unable to determine type
     * @throws SyntaxHighlighterException if any error occurs
     */
    private List<String> guessSourceCodeType(String filename, String code)
            throws SyntaxHighlighterException {
        try {
            List<PyList> lexers = highlighter.guessLexerForFilename(filename, code);
            String[] types = lexers.toArray(new String[lexers.size()]);
            return Arrays.asList(types);
        }
        catch (PyException e) {
            if (PYGMENTS_UTIL_CLASS_NOT_FOUND.equals(e.type.toString())) {
                return null;
            }
            throw new SyntaxHighlighterException(
                    "An error occurred while attempting to highlight source code.", e);
        }
    }

    /**
     * Returns syntax highlighted representation of provided code, using the
     * provided type, provides option to highlight individual source lines.
     * 
     * @param code source code to be highlighted
     * @param type indicates Pygments-derived type of of provided source code
     * @param lineNbrsToHighlight list of line numbers in source code to
     *            highlight
     * @param escapeNewlinesForJavaScript true to escape newlines in source for
     *            processing by Javascript, otherwise false
     * @return highlighted representation
     * @throws SyntaxHighlighterException if any error occurs
     */
    private String highlight(String code, String type, List<String> lineNbrsToHighlight, boolean escapeForJavaScript)
            throws SyntaxHighlighterException {

        try {
            return highlighter.highlightCode(code, type, new PyList(lineNbrsToHighlight), escapeForJavaScript);
        }
        catch (Exception e) {
            throw new SyntaxHighlighterException(
                    "An error occurred while attempting to highlight source code.", e);
        }
    }

}
