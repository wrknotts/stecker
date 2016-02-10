package stecker.util.pygments;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import stecker.config.SyntaxHighlighterConfiguration;
import stecker.service.SyntaxHighlighterService;

public class SyntaxHighlighterTest {

    private static SyntaxHighlighterService tested;

    @Test
    public void testGetLexerInfo() {
        Map<String, List<String>> result = tested.getLexers();
        
        assertTrue(result.size() > 0);

    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        tested = new SyntaxHighlighterService();
        tested.setSyntaxHighlighter(new SyntaxHighlighterConfiguration().getSyntaxHighlighter());
        tested.postConstruct();
    }

}
