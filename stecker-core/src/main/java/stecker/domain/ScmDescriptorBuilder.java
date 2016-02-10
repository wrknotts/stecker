package stecker.domain;

import static stecker.util.EncodingSupport.JAVASCRIPT_ESCAPER;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.io.CharStreams;

import stecker.config.CacheConfiguration;
import stecker.environment.ScmEnvironment;

/**
 * Discovers available implementations of {@code ScmEnvironment}, prepares any
 * bookmarklets defined and creates an {@code ScmDescriptor} for each instance
 * to be used when rendering views.
 */
public class ScmDescriptorBuilder {

    private static final String BOOKMARKLET_TEMPLATE = "javascript:((function(){%s})())";
    private static final Logger LOGGER = LoggerFactory.getLogger(ScmDescriptorBuilder.class);
    private static final String PLACEHOLDER_SERVER_BASE_ADDRESS = "_serverBaseAddress_";

    private ApplicationContext appCtx;
    private ResourceLoader resourceLoader;
    private Map<String, ScmDescriptor> scmDescriptors = new TreeMap<String, ScmDescriptor>();

    @Cacheable(CacheConfiguration.SCM_DESCRIPTOR_CACHE_NAME)
    public Map<String, ScmDescriptor> getScmDescriptors(URI serverBaseUri) {

        String escapedServerBaseUri = JAVASCRIPT_ESCAPER.escape(serverBaseUri.toString());

        for (ScmDescriptor scm : scmDescriptors.values()) {
            // construct home page URI
            scm.setHomePageAddress(UriComponentsBuilder.fromUri(serverBaseUri)
                    .path(scm.getScmIdentifier()).build().toUri());
            // final preparation for bookmarklet JavaScript function
            List<Bookmarklet> bl = scm.getBookmarklets();
            for (Bookmarklet b : bl) {
                b.setJavaScriptFunction(b.getJavaScriptFunction().replace(
                        PLACEHOLDER_SERVER_BASE_ADDRESS, escapedServerBaseUri));
            }
        }

        return scmDescriptors;
    }

    /**
     * Automatically invoked after dependency injection is done to perform any
     * additional initialization.
     * 
     * @throws IOException
     */
    @PostConstruct
    public void postConstruct() throws IOException {

        // discover all registered beans that define an ScmEnvironment
        Map<String, ScmEnvironment> providers = appCtx.getBeansOfType(ScmEnvironment.class);

        LOGGER.debug("discovered '{}' instance{} of {}", providers.size(),
                (1 == providers.size() ? "" : "s"), ScmEnvironment.class.getName());

        for (ScmEnvironment p : providers.values()) {

            if (p.isEnabled()) {

                LOGGER.debug("processing '{}'", p.getClass().getName());

                List<Bookmarklet> provided = p.getBookmarklets();
                // load javascript for each bookmarklet and assign it to
                // instance then construct valid bookmarklet function and assign
                // it as well
                List<Bookmarklet> prepared = new ArrayList<Bookmarklet>(provided.size());
                for (Bookmarklet b : provided) {

                    Resource resource = resourceLoader.getResource("classpath:/bookmarklets/"
                            + b.getRelativeFilePath());

                    String rawJavaScript = CharStreams.toString(new InputStreamReader(resource
                            .getInputStream(), StandardCharsets.UTF_8));

                    Bookmarklet pb = new Bookmarklet();
                    pb.setDescription(b.getDescription());
                    pb.setLinkText(b.getLinkText());
                    pb.setRawJavaScript(rawJavaScript);
                    // initial preparation of bookmarklet JavaScript function,
                    // final preparation will be performed on first retrieval of
                    // resulting Map
                    pb.setJavaScriptFunction(String.format(BOOKMARKLET_TEMPLATE,
                            JAVASCRIPT_ESCAPER.escape(rawJavaScript)));
                    pb.setRelativeFilePath(b.getRelativeFilePath());
                    prepared.add(pb);

                }

                ScmDescriptor scm = new ScmDescriptor();
                scm.setScmIdentifier(p.getScmIdentifier());
                scm.setRepositoryBaseAddress(p.getBaseAddress());
                scm.setRepositoryImage(p.getRepositoryImage());
                scm.setBookmarklets(prepared);

                scmDescriptors.put(p.getClass().getSimpleName(), scm);

            }
            else {
                LOGGER.debug("'{}' is disabled and will not be processed", p.getClass().getName());

            }

        }

    }

    /**
     * Establishes the {@code ApplicationEnvironment} that represents to core
     * application configuration.
     * 
     * @param appEnv represents current application configuration
     */
    @Autowired
    public void setAppCtx(ApplicationContext appCtx) {
        this.appCtx = appCtx;
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

}