package stecker.domain;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.springframework.core.style.ToStringCreator;

/**
 * Represents source code retrieved from an SCM.
 * {@code AbstractListenableFutureCodeDescriptorAdapter} implementations are
 * responsible for mapping the actual response from an SCM to this common
 * representation.
 */
public class CodeDescriptor {

    private List<FileDescriptor> fileDescriptors = Collections.<FileDescriptor> emptyList();
    private String identifier = null;
    private String lastUpdated = null;
    private String name = null;
    private URI viewUri;

    public List<FileDescriptor> getFileDescriptors() {
        return fileDescriptors;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public String getName() {
        return name;
    }

    public URI getViewUri() {
        return viewUri;
    }

    public void setFileDescriptors(List<FileDescriptor> fileDescriptors) {
        this.fileDescriptors = fileDescriptors;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setViewUri(URI viewUri) {
        this.viewUri = viewUri;
    }

    @Override
    public String toString() {

        ToStringCreator tsc = new ToStringCreator(this).append("identifier", identifier)
                .append("name", name).append("lastUpdated", lastUpdated).append("viewUri", viewUri)
                .append("fileDescriptors", fileDescriptors);

        return tsc.toString();
    }

}
