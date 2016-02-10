package stecker.environment;

import java.net.URI;
import java.util.List;

import stecker.domain.Bookmarklet;

/**
 * Defines type for any SCM configuration implementation. Used to discover
 * available configurations in runtime environment.
 * 
 * @see stecker.controller.HomeController
 */
public interface ScmEnvironment {

    URI getBaseAddress();

    List<Bookmarklet> getBookmarklets();

    URI getRepositoryImage();

    String getScmIdentifier();

    boolean isEnabled();
}