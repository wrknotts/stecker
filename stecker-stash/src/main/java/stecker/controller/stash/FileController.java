package stecker.controller.stash;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;

import stecker.controller.AbstractHighlightingController;
import stecker.domain.FileDescriptor;
import stecker.environment.ScmEnvironment;
import stecker.repository.CodeRepository;
import stecker.environment.stash.StashEnvironment;

@Controller
@RequestMapping("/stash")
@ConditionalOnExpression("'${stash.enabled}'=='true' && '${stash.filesEnabled}'=='true'")
@EnableConfigurationProperties(value = StashEnvironment.class)
public class FileController extends AbstractHighlightingController {

    private static final String STASH_API_VERSION_SEGMENT = "1.0";
    private static final String STASH_REST_PATH_SEGMENT = "rest";

    @SuppressWarnings("serial")
    private static final Map<String, Object[]> RAW_QUERY_PARAMETER_MAP = new HashMap<String, Object[]>() {

        {
            put(RAW, null);
        }
    };

    /**
     * @see stecker.controller.AbstractHighlightingController#setRepository(stecker.repository.CodeRepository)
     */
    @Override
    @Autowired
    @Qualifier("fileRepository")
    public void setRepository(CodeRepository repository) {
        super.setRepository(repository);
    }

    @Override
    @Autowired
    @Qualifier("stash.CONFIGURATION_PROPERTIES")
    public void setScmEnv(ScmEnvironment scmEnv) {
        super.setScmEnv(scmEnv);
    }

    @Override
    protected URI buildBaseViewUri() {
        return scmEnv.getBaseAddress();
    }

    @Override
    protected URI buildFileLevelViewRawUri(String id, FileDescriptor fd) {

        return buildUri(scmEnv.getBaseAddress(), new String[] { id }, null, RAW_QUERY_PARAMETER_MAP);
    }

    @Override
    protected URI buildFileLevelViewUri(String id, FileDescriptor fd) {

        return buildUri(scmEnv.getBaseAddress(), new String[] { id }, null, null);

    }

    @Override
    protected String extractIdFromRequest(HttpServletRequest request) {

        String requestedPath = new AntPathMatcher()
                .extractPathWithinPattern((String) request
                        .getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE),
                        (String) request
                                .getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE));

        validatePath(requestedPath);

        return requestedPath;

    }

    private void validatePath(String path) {

        // simple check to stop malicious requests for well known
        // StashEnvironment API
        // patterns, could be better
        String[] parts = path.split("\\/");
        if ((parts[0].equals(STASH_REST_PATH_SEGMENT) && (parts[2]
                .equals(STASH_API_VERSION_SEGMENT)))) {
            throw new RuntimeException("Bad Request");
        }

    }

}