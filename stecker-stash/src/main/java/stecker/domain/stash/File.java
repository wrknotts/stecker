package stecker.domain.stash;

import java.util.List;

import org.springframework.core.style.ToStringCreator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class File {

    @JsonProperty("isLastPage")
    private boolean isLastPage;

    @JsonProperty("lines")
    private List<FileLine> lines;

    @JsonProperty("size")
    private int size;

    @JsonProperty("start")
    private int start;

    public List<FileLine> getLines() {
        return lines;
    }

    public int getSize() {
        return size;
    }

    public int getStart() {
        return start;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    @Override
    public String toString() {

        ToStringCreator tsc = new ToStringCreator(this).append("start", this.start)
                .append("size", this.size).append("isLastPage", this.isLastPage)
                .append("# of lines", this.lines.size());

        if (!this.lines.isEmpty()) {
            tsc.append("first line", this.lines.get(0));
        }

        return tsc.toString();
    }

}