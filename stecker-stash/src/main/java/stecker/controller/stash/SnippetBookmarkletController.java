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
 * Represents the endpoint used to retrieve the Stash Snippet bookmarklet.
 */
@Controller
@RequestMapping(method = RequestMethod.GET, value = "/stash/bookmarklet/snippet")
@ConditionalOnProperty(name = "stash.enabled")
@EnableConfigurationProperties(value = StashEnvironment.class)
public class SnippetBookmarkletController extends AbstractBookmarkletController {

    /**
     * Automatically invoked after dependency injection is done to perform any
     * additional initialization.
     */
    @PostConstruct
    public void postConstruct() {

        setViewName("stash/bookmarklet-snippet");

    }

}