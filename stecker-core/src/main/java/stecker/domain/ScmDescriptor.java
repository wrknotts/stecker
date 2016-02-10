package stecker.domain;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.style.ToStringCreator;

/**
 * Represents a configured SCM. Used when rendering the home page.
 */
public class ScmDescriptor {

    private List<Bookmarklet> bookmarklets = new ArrayList<Bookmarklet>();
    private URI homePageAddress = null;
    private URI repositoryBaseAddress = null;
    private URI repositoryImage = null;
    private String scmIdentifier;

    public List<Bookmarklet> getBookmarklets() {
        return bookmarklets;
    }

    public URI getHomePageAddress() {
        return homePageAddress;
    }

    public URI getRepositoryBaseAddress() {
        return repositoryBaseAddress;
    }

    public URI getRepositoryImage() {
        return repositoryImage;
    }

    public String getScmIdentifier() {
        return scmIdentifier;
    }

    public void setBookmarklets(List<Bookmarklet> bookmarklets) {
        this.bookmarklets = bookmarklets;
    }

    public void setHomePageAddress(URI homePageAddress) {
        this.homePageAddress = homePageAddress;
    }

    public void setRepositoryBaseAddress(URI repositoryBaseAddress) {
        this.repositoryBaseAddress = repositoryBaseAddress;
    }

    public void setRepositoryImage(URI repositoryImage) {
        this.repositoryImage = repositoryImage;
    }

    public void setScmIdentifier(String scmIdentifier) {
        this.scmIdentifier = scmIdentifier;
    }

    @Override
    public String toString() {

        ToStringCreator tsc = new ToStringCreator(this)
        .append("repositoryBaseAddress", repositoryBaseAddress)
        .append("repositoryImage", repositoryImage).append("bookmarklets", bookmarklets);

        return tsc.toString();
    }

}
