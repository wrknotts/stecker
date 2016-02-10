package stecker.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.google.common.io.CharStreams;

import stecker.config.CacheConfiguration;
import stecker.domain.CodeDescriptor;
import stecker.domain.FileDescriptor;
import stecker.service.SyntaxHighlighterService;
import stecker.util.QueryParameterSupport;

public class CodeSampleBuilder {

    private static final String PLACEHOLDER_SERVER_BASE_ADDRESS = "_serverBaseAddress_";

    private Map<String, String> codeSamples = new HashMap<String, String>();
    private SyntaxHighlighterService highlighter;
    private ResourceLoader resourceLoader;

    @Cacheable(CacheConfiguration.CODE_SAMPLE_CACHE_NAME)
    public Map<String, String> getCodeSamples(URI serverBaseUri) throws IOException {

        codeSamples.put(
                "embed_using_script.html",
                highlightSampleCode("embed_using_script.html", "4").replace(PLACEHOLDER_SERVER_BASE_ADDRESS,
                        serverBaseUri.toString()));
        codeSamples.put(
                "return_complete_html_page.txt",
                highlightSampleCode("return_complete_html_page.txt", "1").replace(PLACEHOLDER_SERVER_BASE_ADDRESS,
                        serverBaseUri.toString()));
        codeSamples.put(
                "highlight_single_line.txt",
                highlightSampleCode("highlight_single_line.txt").replace(PLACEHOLDER_SERVER_BASE_ADDRESS,
                        serverBaseUri.toString()));
        codeSamples.put(
                "highlight_multiple_lines.txt",
                highlightSampleCode("highlight_multiple_lines.txt").replace(PLACEHOLDER_SERVER_BASE_ADDRESS,
                        serverBaseUri.toString()));
        codeSamples.put(
                "highlight_line_range.txt",
                highlightSampleCode("highlight_line_range.txt").replace(PLACEHOLDER_SERVER_BASE_ADDRESS,
                        serverBaseUri.toString()));
        codeSamples.put(
                "countdown.R",
                highlightSampleCode("countdown.R", "4,5,7-9,13").replace(PLACEHOLDER_SERVER_BASE_ADDRESS,
                        serverBaseUri.toString()));


        return codeSamples;
    }

    /**
     * Establishes the Spring {@code ResourceLoader} to be used to load
     * bookmarklet JavaScript code.
     * 
     * @param resourceLoader
     */
    @Autowired
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Establish the {@code SyntaxHighlighter} to be used.
     * 
     * @param highlighter
     */
    @Autowired
    public void setSyntaxHighlighter(SyntaxHighlighterService highlighter) {
        this.highlighter = highlighter;
    }

    /**
     * Handles highlighting of code samples for home page.
     * 
     * @param name code sample file name
     * @param highlightLines
     * @return
     * @throws IOException
     */
    @SuppressWarnings("serial")
    private String highlightSampleCode(String name, String... highlightLines) throws IOException {

        Resource resource = resourceLoader
                .getResource(String.format("classpath:/samples/%s", name));

        String fileContents = CharStreams.toString(new InputStreamReader(resource.getInputStream(),
                StandardCharsets.UTF_8));

        final FileDescriptor fd = new FileDescriptor();
        fd.setName(name);
        fd.setRawCode(fileContents);

        CodeDescriptor cd = new CodeDescriptor();
        cd.setName(name);
        cd.setFileDescriptors(new ArrayList<FileDescriptor>() {

            {
                add(fd);
            }
        });
        Map<Integer, List<String>> hl = new HashMap<Integer, List<String>>();

        for (int i = 0; i < highlightLines.length; i++) {
            String[] hll = highlightLines[i].split(",");
            hl.put(i, QueryParameterSupport.normalizeLineNumbers(Arrays.asList(hll)));
        }

        highlighter.highlightSourceCode(cd, hl, false, false);

        return fd.getHighlightedCode();
    }

}