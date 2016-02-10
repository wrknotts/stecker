package stecker.domain;

import java.net.URI;

import org.springframework.core.style.ToStringCreator;

/**
 * Represents a source code file retrieved from an SCM.
 */
public class FileDescriptor implements Comparable<FileDescriptor> {

    private String highlightedCode = null;
    private String identifier = null;
    private String name = null;
    private String rawCode = null;
    private URI viewRawUri;
    private URI viewUri;

    @Override
    public int compareTo(FileDescriptor f) {
        return name.compareTo(f.getName());
    }

    public String getHighlightedCode() {
        return highlightedCode;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public String getRawCode() {
        return rawCode;
    }

    public URI getViewRawUri() {
        return viewRawUri;
    }

    public URI getViewUri() {
        return viewUri;
    }

    public void setHighlightedCode(String highlightedCode) {
        this.highlightedCode = highlightedCode;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRawCode(String rawCode) {
        this.rawCode = rawCode;
    }

    public void setViewRawUri(URI viewRawUri) {
        this.viewRawUri = viewRawUri;
    }

    public void setViewUri(URI viewUri) {
        this.viewUri = viewUri;
    }

    @Override
    public String toString() {

        ToStringCreator tsc = new ToStringCreator(this)
                .append("identifier", identifier)
                .append("name", name)
                .append("viewUri", viewUri)
                .append("viewRawUri", viewRawUri)
                .append("rawCode",
                        (25 <= rawCode.length() ? rawCode.substring(0, 24).replace("\n", " ")
                        + "..." : rawCode.replace("\n", " ")))
                        .append("highlightedCode",
                                (null == highlightedCode ? null :(25 <= highlightedCode.length() ? highlightedCode.substring(0, 24).replace(
                                        "\n", " ")
                                        + "..." : highlightedCode.replace("\n", " "))));

        return tsc.toString();
    }

}