package stecker.controller;

import java.io.IOException;
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
@RequestMapping("/")
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);
    private static final String MODEL_ATTTRIBUTE_NAME_SCM_DESCRIPTORS = "scmDescriptors";
    private static final String VIEW_NAME = "home";

    private CodeSampleBuilder codeSampleBuilder;
    private ScmDescriptorBuilder scmDescriptorBuilder;

    /**
     * Responds to HTTP GET Requests for the home page
     * 
     * @return a Spring {@code ModelAndView} that represents the home page
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getHome() throws IOException {

        URI serverBaseUri = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUri();

        Map<String, ScmDescriptor> scmDescriptors = scmDescriptorBuilder
                .getScmDescriptors(serverBaseUri);

        ModelAndView mav = new ModelAndView(VIEW_NAME,
                AbstractHighlightingController.MODEL_ATTTRIBUTE_NAME_SERVER_BASE_ADDRESS,
                serverBaseUri);
        mav.addObject(MODEL_ATTTRIBUTE_NAME_SCM_DESCRIPTORS, scmDescriptors.entrySet());

        mav.addObject("codeSamples", codeSampleBuilder.getCodeSamples(serverBaseUri));

        LOGGER.debug("created model and view. View Name: '{}', {}: '{}'", VIEW_NAME,
                MODEL_ATTTRIBUTE_NAME_SCM_DESCRIPTORS, scmDescriptors);

        return mav;
    }

    /**
     * Establish the {@code CodeSampleBuilder} to be used.
     * 
     * @param highlighter
     */
    @Autowired
    public void setCodeSampleBuilder(CodeSampleBuilder codeSampleBuilder) {
        this.codeSampleBuilder = codeSampleBuilder;
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