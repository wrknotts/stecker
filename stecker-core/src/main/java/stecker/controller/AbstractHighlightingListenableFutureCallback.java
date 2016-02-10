package stecker.controller;

import static stecker.util.QueryParameterSupport.extractAndNormalizeParameters;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import stecker.domain.CodeDescriptor;
import stecker.domain.FileDescriptor;
import stecker.service.SyntaxHighlighterService;

public abstract class AbstractHighlightingListenableFutureCallback implements
        ListenableFutureCallback<CodeDescriptor>, HighlightingListenableFutureCallback {

    protected Logger logger = LoggerFactory
            .getLogger(AbstractHighlightingListenableFutureCallback.class);

    private static final String ERROR_FILENAME = "error.txt";
    private static final String MODEL_ATTRIBUTE_NAME_STYLESHEET = "stylesheet";
    private static final String MODEL_ATTTRIBUTE_NAME_SERVER_BASE_ADDRESS = "serverBaseAddress";
    private static final String MODEL_NAME = "descriptor";
    private static final String VIEW_NAME = "hilite-script";

    @SuppressWarnings("serial")
    private static final Map<Integer, List<String>> ERROR_HIGHLIGHT_LINE_NUMBERS = new HashMap<Integer, List<String>>() {

        {
            put(0, new ArrayList<String>() {

                {
                    add("1");
                }
            });
        }
    };

    private final SyntaxHighlighterService highlighter;
    private final DeferredResult<ModelAndView> deferredResult;
    private final Map<String, String> params;
    private final URI serverBaseUri;
    private final String styleSheetName;
    private final URI viewUri;

    public AbstractHighlightingListenableFutureCallback(SyntaxHighlighterService highlighter,
            URI viewUri, Map<String, String> params, String styleSheetName) {

        this.highlighter = highlighter;
        this.deferredResult = new DeferredResult<ModelAndView>();
        this.serverBaseUri = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUri();
        this.params = params;
        this.viewUri = viewUri;
        this.styleSheetName = styleSheetName;

    }

    /**
     * @see stecker.controller.HighlightingListenableFutureCallback#addViewUrisToFileDescriptors(java.util.List)
     */
    @Override
    public abstract void addViewUrisToFileDescriptors(List<FileDescriptor> fileDescriptors);

    /**
     * @see stecker.controller.HighlightingListenableFutureCallback#getDeferredResult()
     */
    @Override
    public DeferredResult<ModelAndView> getDeferredResult() {
        return deferredResult;
    }

    /**
     * @see org.springframework.util.concurrent.FailureCallback#onFailure(java.lang.Throwable)
     */
    @Override
    public void onFailure(Throwable t) {

        if (logger.isWarnEnabled()) {
            logger.warn("An error occurred when attempting to retrieve a source file.", t);
        }

        ModelAndView mav = createModelAndView(buildFailureDescriptor(t), serverBaseUri, styleSheetName);

        deferredResult.setResult(mav);
    }

    /**
     * @see org.springframework.util.concurrent.SuccessCallback#onSuccess(java.lang.Object)
     */
    @Override
    public void onSuccess(CodeDescriptor result) {

        highlighter.highlightSourceCode(result, extractAndNormalizeParameters(params));

        result.setViewUri(viewUri);

        addViewUrisToFileDescriptors(result.getFileDescriptors());

        logger.debug("created code descriptor (SUCCESS). Value: '{}'", result.toString());

        ModelAndView mav = createModelAndView(result, serverBaseUri, styleSheetName);

        logger.debug("completed handling request for source file. Id: '{}'", result.getIdentifier());

        deferredResult.setResult(mav);
    }

    private CodeDescriptor buildFailureDescriptor(Throwable t) {

        CodeDescriptor descriptor = new CodeDescriptor();
        List<FileDescriptor> fileDescriptors = new ArrayList<FileDescriptor>(1);

        FileDescriptor fd = new FileDescriptor();
        fd.setRawCode(String.format("%s: %s", t.getClass().getSimpleName(), t.getMessage()));
        fd.setName(ERROR_FILENAME); // provide name for use by highlighter
        fileDescriptors.add(fd);

        descriptor.setFileDescriptors(fileDescriptors);

        highlighter.highlightSourceCode(descriptor, ERROR_HIGHLIGHT_LINE_NUMBERS);
        fd.setName(null); // reset name so it doesn't show in view

        logger.debug("created code descriptor (FAILURE). Value: '{}'", descriptor.toString());

        return descriptor;

    }

    private ModelAndView createModelAndView(CodeDescriptor descriptor, URI serverBaseUri,
            String styleSheetName) {

        ModelAndView mav = new ModelAndView(VIEW_NAME, MODEL_NAME, descriptor);
        mav.addObject(MODEL_ATTTRIBUTE_NAME_SERVER_BASE_ADDRESS, serverBaseUri);
        mav.addObject(MODEL_ATTRIBUTE_NAME_STYLESHEET, styleSheetName);

        logger.debug(
                "created model and view. View Name: '{}', Stylesheet: '{}', Code Descriptor: '{}'",
                VIEW_NAME, styleSheetName, descriptor);

        return mav;
    }

}