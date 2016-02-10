package stecker.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import stecker.domain.CodeDescriptor;
import stecker.domain.FileDescriptor;
import stecker.environment.ApplicationEnvironment;
import stecker.environment.ScmEnvironment;
import stecker.repository.CodeRepository;
import stecker.service.SyntaxHighlighterService;

/**
 * Base type for any Spring {@code Controller} implementation that responds to
 * request for highlighted source code. Concrete implementations are required to
 * assign a class-level Spring {@code RequestMapping} annotation that ensures
 * requests are routed properly, the value assigned to this annotation must only
 * define the path up to either the '/html' or '/script' segment of a Stecjer
 * URL.
 * 
 * For example, given that by convention a {@code Controller} that will handle
 * requests for an SCM known as 'Stash' could define their annotation as
 * follows:
 * 
 * <pre>
 * {@literal @}Controller
 * {@literal @}RequestMapping("/stash")
 * public class StashController extends AbstractHighlightingController {
 * ...
 * </pre>
 * 
 * This would result in a concrete implementation supporting the following
 * endpoints for returning highlighted source code:
 * 
 * <pre>
 * /stash/html/{id}
 * /stash/script/{id}
 * </pre>
 * 
 * @see #REQUEST_MAPPING_HTML
 * @see #getHtml(HttpServletRequest)
 * @see #REQUEST_MAPPING_SCRIPT
 * @see #getScript(HttpServletRequest, Map)
 */
@EnableConfigurationProperties(value = ApplicationEnvironment.class)
public abstract class AbstractHighlightingController {

    public static final String REQUEST_MAPPING_HTML = "/html/**";
    public static final String REQUEST_MAPPING_SCRIPT = "/script/**";

    protected static final String MODEL_ATTRIBUTE_SCM_ENVIRONMENT = "scmEnvironment";
    protected static final String MODEL_ATTTRIBUTE_NAME_SCRIPT_URI = "scriptUri";
    protected static final String MODEL_ATTTRIBUTE_NAME_SERVER_BASE_ADDRESS = "serverBaseAddress";
    protected static final String RAW = "raw";
    protected static final String VIEW_NAME_HOME_TEMPLATE = "%s/home";
    protected static final String VIEW_NAME_HTML = "html-script";

    protected ApplicationEnvironment appEnv;
    protected URI baseViewUri;
    protected SyntaxHighlighterService highlighter;
    protected String homeViewName;
    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected CodeRepository repository;
    private String requestMappingPath;
    protected ScmEnvironment scmEnv;

    private static final String PATH_SEPARATOR = "/";
    private static final String PATH_SEGMENT_SCRIPT = "/script";

    /**
     * Construct a new {@code AbstractHighlightingController}
     */
    public AbstractHighlightingController() {

        this.requestMappingPath = AnnotationUtils.findAnnotation(this.getClass(),
                RequestMapping.class).path()[0];

        this.homeViewName = String.format(VIEW_NAME_HOME_TEMPLATE, requestMappingPath
                .startsWith("/") ? requestMappingPath.substring(1) : requestMappingPath);

    }

    /**
     * Responds to HTTP GET requests for the home page of this repository
     * plugin.
     * 
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getHome() {

        URI serverBaseUri = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUri();

        ModelAndView mav = new ModelAndView(homeViewName,
                MODEL_ATTTRIBUTE_NAME_SERVER_BASE_ADDRESS, serverBaseUri);
        mav.addObject(MODEL_ATTRIBUTE_SCM_ENVIRONMENT, scmEnv);

        logger.debug("created model and view. View Name: '{}', {}: '{}', {}: '{}'", homeViewName,
                MODEL_ATTTRIBUTE_NAME_SERVER_BASE_ADDRESS, serverBaseUri,
                MODEL_ATTRIBUTE_SCM_ENVIRONMENT, scmEnv);

        return mav;

    }

    /**
     * Responds to HTTP GET requests for a complete HTML page containing
     * highlighted source code for the requested identifier.
     * 
     * @param request represents complete request from the client
     * @return a Spring {@code ModelAndView} representing the response to be
     *         returned
     */
    @RequestMapping(method = RequestMethod.GET, value = REQUEST_MAPPING_HTML)
    public ModelAndView getHtml(HttpServletRequest request) {

        final String id = extractIdFromRequest(request);

        UriComponentsBuilder scriptUriBuilder = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .pathSegment(
                        (requestMappingPath.startsWith(PATH_SEPARATOR) ? requestMappingPath.substring(
                                1, requestMappingPath.length())
                                : requestMappingPath)
                                + PATH_SEGMENT_SCRIPT, id).query(request.getQueryString());

        URI scriptUri = scriptUriBuilder.build().toUri();

        ModelAndView mav = new ModelAndView(VIEW_NAME_HTML, MODEL_ATTTRIBUTE_NAME_SCRIPT_URI,
                scriptUri.toString());

        logger.debug("created model and view. View Name: '{}', {}: '{}'", VIEW_NAME_HTML,
                MODEL_ATTTRIBUTE_NAME_SCRIPT_URI, scriptUri.toString());

        return mav;
    }

    /**
     * Returns the 'path' value of the {@code RequestMapping} annotation
     * assigned to the concrete class.
     * 
     * @return value of {@code RequestMapping} 'path' attribute
     */
    public String getRequestMappingPath() {
        return requestMappingPath;
    }

    /**
     * Responds to HTTP GET requests for a JavaScript file that can modify the
     * DOM within a web browser in order to include highlighted source code for
     * the requested identifier within the page.
     * 
     * @param request represents complete request from the client
     * @param params parsed query parameters from request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = REQUEST_MAPPING_SCRIPT)
    public DeferredResult<ModelAndView> getScript(HttpServletRequest request,
            @RequestParam Map<String, String> params) {

        final String id = extractIdFromRequest(request);

        HighlightingListenableFutureCallback callback = new AbstractHighlightingListenableFutureCallback(
                highlighter, buildTopLevelViewUri(id), params, appEnv.getStyleSheetName()) {

            @Override
            public void addViewUrisToFileDescriptors(List<FileDescriptor> fileDescriptors) {

                for (FileDescriptor fd : fileDescriptors) {
                    fd.setViewUri(buildFileLevelViewUri(id, fd));
                    fd.setViewRawUri(buildFileLevelViewRawUri(id, fd));
                }

            }

        };

        return doGetScript(id, callback);

    }

    /**
     * Automatically invoked after dependency injection is done to perform any
     * additional initialization.
     */
    @PostConstruct
    public void postConstruct() {

        this.baseViewUri = buildBaseViewUri();

    }

    /**
     * Establishes the {@code ApplicationEnvironment} that represents the
     * Stecker-specific application configuration.
     * 
     * @param appEnv represents current application configuration
     */
    @Autowired
    public void setAppEnv(ApplicationEnvironment appEnv) {
        this.appEnv = appEnv;
    }

    /**
     * Establish the specific implementation of {@code ScmConfiguration} to be
     * used by this instance. This is most easily accomplished by the concrete
     * class providing its own implementation that delegates to this method and
     * is annotated with both {@code Autowire} and {@code Qualifier} in order to
     * control the specific instance of {@code ScmEnvironment} that should be
     * provided. For example:
     * <p>
     * 
     * <pre>
     * 
     * {@literal @}Override
     * {@literal @}Autowired
     * {@literal @}Qualifier("&lt;name&gt;.CONFIGURATION_PROPERTIES")
     * public void setScmEnv(ScmEnvironment scmEnv) {
     *   super.setScmEnv(scmEnv);
     * }
     * </pre>
     * 
     * </p>
     * Where <i>&lt;name&gt</i> is the bean name assigned by Spring to the
     * specific instance of {@code @ConfigurationProperties} that should be
     * provided.
     * 
     * @param scmEnv concrete instance of {@code ScmEnvironment} required by
     *            this implementation
     */
    public void setScmEnv(ScmEnvironment scmEnv) {
        this.scmEnv = scmEnv;
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
     * Concrete classes must implement this method to provide the appropriate
     * URI to be used as the base for constructing all subsequent URI's that
     * point to back to the SCM for viewing the source file in that environment.
     * 
     * @return implementation-specific base View URI
     */
    protected abstract URI buildBaseViewUri();

    /**
     * Concrete classes must implement this method in order to provide a URI
     * that points back to the SCM to view the source file in it's raw,
     * undecorated state.
     * 
     * @param id unique id of source file in SCM
     * @param fd represents the information retrieved from the SCM about the
     *            source file
     * @return implementation-specific raw view URI for file
     */
    protected abstract URI buildFileLevelViewRawUri(String id, FileDescriptor fd);

    /**
     * Concrete classes must implement this method in order to provide a URI
     * that points back to the SCM to view the source file.
     * 
     * @param id unique id of source file in SCM
     * @param fd represents the information retrieved from the SCM about the
     *            source file
     * @return implementation-specific view URI for file
     */
    protected abstract URI buildFileLevelViewUri(String id, FileDescriptor fd);

    protected URI buildTopLevelViewUri(String id) {

        return buildUri(baseViewUri, new String[] { id }, null, null);
    }

    /**
     * Utility method for constructing URIs.
     * 
     * @param base starting URI
     * @param segments any segments to be added to the base, can be null
     * @param fragment a URL fragment to be added, can be null
     * @param params any query parameters to be added, can be null
     * @return
     */
    protected URI buildUri(URI base, String[] segments, String fragment,
            Map<String, Object[]> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(base);

        if (null != segments) {
            builder.pathSegment(segments);
        }

        if (null != fragment) {
            builder.fragment(fragment);
        }

        if (null != params) {
            for (String k : params.keySet()) {
                builder.queryParam(k, params.get(k));
            }
        }

        UriComponents linkUriComponents = builder.build();

        logger.debug("built URI. Value: '{}'", linkUriComponents.toUriString());

        return linkUriComponents.toUri();
    }

    protected abstract String extractIdFromRequest(HttpServletRequest request);

    /**
     * Establish the specific {@code CodeRepository} implementation to be used
     * by this instance. This is most easily accomplished by the concrete class
     * providing its own implementation that delegates to this method and is
     * annotated with both {@code Autowire} and {@code Qualifier} in order to
     * control the specific instance of {@code ScmEnvironment} that should be
     * provided. For example:
     * <p>
     * 
     * <pre>
     * 
     * {@literal @}Override
     * {@literal @}Autowired
     * {@literal @}Qualifier("&lt;repository-bean-name&gt;")
     * public void setRepository(CodeRepository repository) {
     *   super.setRepository(repository);
     * }
     * </pre>
     * 
     * </p>
     * Where <i>&lt;repository-bean-name&gt</i> is the bean name assigned to the
     * specific instance of {@code CodeRepository} that should be provided. You
     * can let Spring determine this name or assign it yourself.
     * 
     * 
     * @param repository concrete instance of {@code CodeRepository} required by
     *            this implementation
     */
    protected void setRepository(CodeRepository repository) {
        this.repository = repository;
    }

    /**
     * Coordinates ansynchronous retrieval of the source code from an SCM.
     * 
     * @param id unique identifier of source code within an SCM
     * @param callback handles asynchronous callbacks resulting from the
     *            retrieval attempt
     * @return a {@code DeferredResult} that will provide a populated
     *         {@code ModelAndView} when the retrieval attempt completes
     */
    @SuppressWarnings("unchecked")
    private DeferredResult<ModelAndView> doGetScript(final String id,
            HighlightingListenableFutureCallback callback) {

        logger.debug("handling request for source file. Id: '{}'", id);

        ListenableFuture<CodeDescriptor> future = repository.findById(id);

        future.addCallback((ListenableFutureCallback<CodeDescriptor>) callback);

        return callback.getDeferredResult();
    }
}
