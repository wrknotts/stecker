package stecker.environment;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.style.ToStringCreator;

import stecker.domain.Bookmarklet;

/**
 * Base type that describes the configuration of an SCM.
 */
public abstract class AbstractScmEnvironment implements ScmEnvironment {

    protected URI baseAddress = null;
    protected List<Bookmarklet> bookmarklets = new ArrayList<Bookmarklet>();
    protected boolean enabled = false;
    protected URI repositoryImage = null;

    @Override
    public URI getBaseAddress() {
        return baseAddress;
    }

    @Override
    public List<Bookmarklet> getBookmarklets() {
        return bookmarklets;
    }

    @Override
    public URI getRepositoryImage() {
        return repositoryImage;
    }

    @Override
    public String getScmIdentifier() {
        return AnnotationUtils.findAnnotation(this.getClass(), ConfigurationProperties.class)
                .prefix();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setBaseAddress(URI baseAddress) {
        this.baseAddress = baseAddress;
    }

    public void setBookmarklets(List<Bookmarklet> bookmarklets) {
        this.bookmarklets = bookmarklets;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setRepositoryImage(URI repositoryImage) {
        this.repositoryImage = repositoryImage;
    }

    @Override
    public String toString() {

        ToStringCreator tsc = new ToStringCreator(this).append("enabled", enabled)
                .append("baseAddress", baseAddress).append("repositoryImage", repositoryImage)
                .append("bookmarklets", bookmarklets);

        return tsc.toString();
    }

}
