package stecker.controller;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Base type for any Spring {@code Controller} used to handle requests for
 * Bookmarklet JavaScript code.
 */
public abstract class AbstractBookmarkletController {

    protected static final String MODEL_ATTTRIBUTE_NAME_SERVER_BASE_ADDRESS = "serverBaseAddress";

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private String viewName;

    /**
     * Responds to HTTP GET requests for Bookmarklet JavaScript code.
     * 
     * @return instance of Spring {@code ModelAndView} that encapsulates the
     *         response to be sent to the requester
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getBookmarklet() {

        URI serverBaseUri = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUri();

        ModelAndView mav = new ModelAndView(viewName, MODEL_ATTTRIBUTE_NAME_SERVER_BASE_ADDRESS,
                serverBaseUri.toString());

        logger.debug("created model and view. View name: '{}', {}: '{}' to server URI: {}",
                viewName, MODEL_ATTTRIBUTE_NAME_SERVER_BASE_ADDRESS, serverBaseUri);

        return mav;
    }

    /**
     * Concrete classes must call this method to establish the specific Spring
     * {@code View} name for this implementation. This is most easily
     * accomplished by the concrete class providing a method annotated with
     * {@code PostConstruct} so that the Spring lifecycle will invoke it at the
     * proper time.
     * 
     * @param viewName name of the Spring {@code View} that should be returned
     */
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
}