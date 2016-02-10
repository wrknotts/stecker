package stecker.domain.stash;

import org.springframework.core.style.ToStringCreator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SnippetFile {

    @JsonProperty("name")
    private String name;

    @JsonProperty("guid")
    private String guid;

    @JsonProperty("content")
    private String content;

    public String getContent() {
        return content;
    }

    public String getGuid() {
        return guid;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {

        ToStringCreator tsc = new ToStringCreator(this)
                .append("guid", this.guid)
                .append("name", this.name)
                .append("content",
                        (25 <= content.length() ? content.substring(0, 24).replace("\n", "")
                                + "..." : content.replace("\n", "")));

        return tsc.toString();
    }

}
