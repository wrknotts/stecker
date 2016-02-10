package stecker.repository.stash;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

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
import stecker.adapter.stash.FileAdapter;
import stecker.domain.stash.File;
import stecker.environment.stash.StashEnvironment;

@Service
@ConditionalOnExpression("'${stash.enabled}'=='true' && '${stash.filesEnabled}'=='true'")
@EnableConfigurationProperties(value = StashEnvironment.class)
public class FileRepository implements CodeRepository {

    private static final String ESCAPED_PATH_SEPARATOR = "\\/";
    private static final Logger LOGGER = LoggerFactory.getLogger(FileRepository.class);
    private static final String PATH_SEPARATOR = "/";
    private static final String RAW_URL_TEMPLATE = "%s?raw";

    private AsyncRestTemplate asyncRestTemplate;

    private String fileUrlTemplate;

    private StashEnvironment stashEnv;

    @Cacheable(CacheConfiguration.SOURCE_CACHE_NAME)
    @Override
    public ListenableFuture<CodeDescriptor> findById(String path) {

        LOGGER.debug("retrieving file. Path: '{}'", path);

        List<String> parts = Arrays.asList(path.split(ESCAPED_PATH_SEPARATOR));

        // get name of file from parsed path
        String fileName = parts.get(parts.size() - 1);

        // build actual path used to retrieve file
        String filePath = buldRetrievalPath(parts);

        URI uri = URI.create(String.format(fileUrlTemplate, filePath));

        LOGGER.debug("using URI to retrieve file from repository. Value: '{}'", uri.toString());

        ListenableFuture<ResponseEntity<File>> file = asyncRestTemplate.getForEntity(uri,
                File.class);

        return new FileAdapter(file, fileName, path);
    }

    /**
     * Automatically invoked after dependency injection is done to perform any
     * additional initialization.
     */
    @PostConstruct
    public void postConstruct() {
        this.fileUrlTemplate = stashEnv.getBaseAddress().toString() + RAW_URL_TEMPLATE;
        LOGGER.debug("using URL template. Value: '{}'", fileUrlTemplate);
    }

    @Autowired
    @Qualifier("fileAsyncRestTemplate")
    public void setAsyncRestTemplate(AsyncRestTemplate asyncRestTemplate) {
        this.asyncRestTemplate = asyncRestTemplate;
    }

    @Autowired
    public void setStashEnv(StashEnvironment stashEnv) {
        this.stashEnv = stashEnv;
    }

    private String buldRetrievalPath(List<String> parts) {

        LOGGER.debug("building retrieval path from segments. Value: '{}'", parts);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.size(); i++) {
            String p = parts.get(i);
            sb.append(PATH_SEPARATOR).append(p);
        }

        LOGGER.debug("built retrieval path. Value: '{}'", sb.toString());

        return sb.toString();
    }

}
