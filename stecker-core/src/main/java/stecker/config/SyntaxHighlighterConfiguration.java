package stecker.config;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PySystemState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import stecker.pygments.SyntaxHighlighter;

/**
 * Handles configuration of {@code SyntaxHighlighter} used by application.
 */
@Configuration
public class SyntaxHighlighterConfiguration {

    /**
     * Returns {@code SyntaxHighlighter} that provides bridge from Java to
     * Jython environment to expose Pygments capabilities to application.
     * 
     * @return configured {@code SyntaxHighlighter}
     */
    @Bean(name = "syntaxHighlighter")
    public SyntaxHighlighter getSyntaxHighlighter() {

        return createSyntaxHighlighter();

    }

    /**
     * Initializes Java-to-Jython bridge specifically matching the
     * {@code SyntaxHighlighter} interface.
     * 
     * @return bridge object between Java/Jython
     */
    private SyntaxHighlighter createSyntaxHighlighter() {

        try (PySystemState state = new PySystemState()) {
            PyObject pythonImporter = state.getBuiltins().__getitem__(Py.newString("__import__"));
            PyObject module = pythonImporter.__call__(Py.newString("PygmentsSyntaxHighlighter"));
            SyntaxHighlighter sh = (SyntaxHighlighter) module
                    .__getattr__("PygmentsSyntaxHighlighter").__call__()
                    .__tojava__(SyntaxHighlighter.class);

            return sh;
        }

    }
}
