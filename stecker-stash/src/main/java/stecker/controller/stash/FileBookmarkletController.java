package stecker.controller.stash;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import stecker.controller.AbstractBookmarkletController;
import stecker.environment.stash.StashEnvironment;

/**
 * Represents the endpoint used to retrieve the Stash Repository File bookmarklet.
 */
@Controller
@RequestMapping(method = RequestMethod.GET, value = "/stash/bookmarklet/file")
@ConditionalOnProperty(name = "stash.enabled")
@EnableConfigurationProperties(value = StashEnvironment.class)
public class FileBookmarkletController extends AbstractBookmarkletController {

    /**
     * Automatically invoked after dependency injection is done to perform any
     * additional initialization.
     */
    @PostConstruct
    public void postConstruct() {

        setViewName("stash/bookmarklet-file");

    }

}