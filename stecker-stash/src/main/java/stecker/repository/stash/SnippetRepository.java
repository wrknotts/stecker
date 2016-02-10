package stecker.repository.stash;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

import stecker.config.CacheConfiguration;
import stecker.domain.CodeDescriptor;
import stecker.repository.CodeRepository;
import stecker.adapter.stash.SnippetAdapter;
import stecker.domain.stash.Snippet;
import stecker.environment.stash.StashEnvironment;

@Service
@ConditionalOnExpression("'${stash.enabled}'=='true' && '${stash.snippetsEnabled}'=='true'")
@EnableConfigurationProperties(value = StashEnvironment.class)
public class SnippetRepository implements CodeRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SnippetRepository.class);
    private static final String SNIPPET_URL_FRAGMENT_TEMPLATE = "/rest/snippets/1.0/snippets/{guid}";

    private AsyncRestTemplate asyncRestTemplate;
    private String snippetUrlTemplate;
    private StashEnvironment stashEnv;

    @Cacheable(CacheConfiguration.SOURCE_CACHE_NAME)
    @Override
    public ListenableFuture<CodeDescriptor> findById(String guid) {

        LOGGER.debug("retrieving snippet. Guid: '{}'", guid);

        LOGGER.debug("using URI to retrieve snippet from repository. Value: '{}'",
                asyncRestTemplate.getUriTemplateHandler().expand(snippetUrlTemplate, guid));

        ListenableFuture<ResponseEntity<Snippet>> snippet = asyncRestTemplate.getForEntity(
                snippetUrlTemplate, Snippet.class, guid);

        return new SnippetAdapter(snippet, guid);
    }

    /**
     * Automatically invoked after dependency injection is done to perform any
     * additional initialization.
     */
    @PostConstruct
    public void postConstruct() {
        this.snippetUrlTemplate = stashEnv.getBaseAddress().toString()
                + SNIPPET_URL_FRAGMENT_TEMPLATE;
        LOGGER.debug("using URL template. Value: '{}'", snippetUrlTemplate);
    }

    @Autowired
    @Qualifier("snippetAsyncRestTemplate")
    public void setAsyncRestTemplate(AsyncRestTemplate asyncRestTemplate) {
        this.asyncRestTemplate = asyncRestTemplate;
    }

    @Autowired
    public void setStashEnv(StashEnvironment stashEnv) {
        this.stashEnv = stashEnv;
    }

}
