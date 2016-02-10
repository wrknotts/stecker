package stecker.controller.stash;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UriComponentsBuilder;

import stecker.controller.AbstractHighlightingController;
import stecker.domain.FileDescriptor;
import stecker.environment.ScmEnvironment;
import stecker.repository.CodeRepository;
import stecker.environment.stash.StashEnvironment;

@Controller
@RequestMapping("/stash/snippet")
@ConditionalOnExpression("'${stash.enabled}'=='true' && '${stash.snippetsEnabled}'=='true'")
@EnableConfigurationProperties(value = StashEnvironment.class)
public class SnippetController extends AbstractHighlightingController {

    private static final String VIEW_ADDRESS_SEGMENT = "snippets";
    private static final String VIEW_ADDRESS_FRAGMENT = "file-%s";

    @Override
    @Autowired
    @Qualifier("snippetRepository")
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

        return UriComponentsBuilder.fromUri(scmEnv.getBaseAddress())
                .pathSegment(VIEW_ADDRESS_SEGMENT).build().toUri();

    }

    @Override
    protected URI buildFileLevelViewRawUri(String id, FileDescriptor fd) {

        return buildUri(baseViewUri, new String[] { RAW, id, fd.getName() }, null, null);
    }

    @Override
    protected URI buildFileLevelViewUri(String id, FileDescriptor fd) {
        return buildUri(baseViewUri, new String[] { id }, String.format(VIEW_ADDRESS_FRAGMENT, id),
                null);
    }

    @Override
    protected String extractIdFromRequest(HttpServletRequest request) {

        String requestedPath = new AntPathMatcher()
                .extractPathWithinPattern(getRequestMappingPath() + "/script/**", (String) request
                .getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE));

        return requestedPath;

    }

}