package stecker.domain.stash;

import java.util.List;

import org.springframework.core.style.ToStringCreator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Snippet {

    @JsonProperty("guid")
    private String guid;

    @JsonProperty("name")
    private String name;

    
    public String getName() {
        return name;
    }

    @JsonProperty("updatedAt")
    private long updatedAt;

    @JsonProperty("files")
    private List<SnippetFile> files;

    public List<SnippetFile> getFiles() {
        return files;
    }

    public String getGuid() {
        return guid;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {

        ToStringCreator tsc = new ToStringCreator(this).append("guid", this.guid)
                .append("name", this.name).append("updatedAt", this.updatedAt)
                .append("files", this.files);
        
        return tsc.toString();
    }

}