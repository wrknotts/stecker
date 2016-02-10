package stecker.domain.stash;

import org.springframework.core.style.ToStringCreator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileLine {

    @JsonProperty("text")
    private String text;

    public String getText() {
        return text;
    }

    @Override
    public String toString() {

        ToStringCreator tsc = new ToStringCreator(this).append("text",
                (25 <= this.text.length() ? this.text.substring(0, 24).replace("\n", "") + "..."
                        : this.text.replace("\n", "")));

        return tsc.toString();
    }

}
