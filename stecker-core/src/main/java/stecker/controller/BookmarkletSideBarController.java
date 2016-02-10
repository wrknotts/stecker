package stecker.controller;

import static stecker.util.EncodingSupport.JAVASCRIPT_ESCAPER;

import java.net.URI;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import stecker.domain.ScmDescriptor;
import stecker.domain.ScmDescriptorBuilder;

/**
 * Represents the home page for Stecker.
 */
@Controller
@RequestMapping("/bookmarklets")
public class BookmarkletSideBarController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(BookmarkletSideBarController.class);
    private static final String MODEL_ATTTRIBUTE_NAME_ESCAPED_SERVER_BASE_ADDRESS = "escapedServerBaseUri";
    private static final String MODEL_ATTTRIBUTE_NAME_SCM_DESCRIPTORS = "scmDescriptors";
    private static final String VIEW_NAME = "bookmarklets";

    private ScmDescriptorBuilder scmDescriptorBuilder;

    /**
     * Responds to HTTP GET Requests for the home page
     * 
     * @return a Spring {@code ModelAndView} that represents the home page
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getHome() {

        URI serverBaseUri = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUri();
        String escapedServerBaseUri = JAVASCRIPT_ESCAPER.escape(serverBaseUri.toString());

        Map<String, ScmDescriptor> scmDescriptors = scmDescriptorBuilder
                .getScmDescriptors(serverBaseUri);

        ModelAndView mav = new ModelAndView(VIEW_NAME,
                AbstractHighlightingController.MODEL_ATTTRIBUTE_NAME_SERVER_BASE_ADDRESS, serverBaseUri);
        mav.addObject(MODEL_ATTTRIBUTE_NAME_ESCAPED_SERVER_BASE_ADDRESS, escapedServerBaseUri);
        mav.addObject(MODEL_ATTTRIBUTE_NAME_SCM_DESCRIPTORS, scmDescriptors.entrySet());

        LOGGER.debug("created model and view. View Name: '{}', {}: '{}', {}: '{}', {}: '{}'", VIEW_NAME,
                AbstractHighlightingController.MODEL_ATTTRIBUTE_NAME_SERVER_BASE_ADDRESS, serverBaseUri,
                MODEL_ATTTRIBUTE_NAME_ESCAPED_SERVER_BASE_ADDRESS, escapedServerBaseUri,
                MODEL_ATTTRIBUTE_NAME_SCM_DESCRIPTORS, scmDescriptors);

        return mav;
    }

    /**
     * Establishes the Spring {@code ScmDescriptorBuilder} to be used.
     * 
     * @param scmDescriptorBuilder
     */
    @Autowired
    public void setScmDescriptorBuilder(ScmDescriptorBuilder scmDescriptorBuilder) {
        this.scmDescriptorBuilder = scmDescriptorBuilder;
    }

}