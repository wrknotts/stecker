package stecker.domain;

import org.springframework.core.style.ToStringCreator;

/**
 * Represents bookmarklet definitions provided by the current configuration
 */
public class Bookmarklet {

    private String description;
    private String javaScriptFunction;
    private String linkText;
    private String rawJavaScript;
    private String relativeFilePath;

    public String getDescription() {
        return description;
    }

    public String getJavaScriptFunction() {
        return javaScriptFunction;
    }

    public String getLinkText() {
        return linkText;
    }

    public String getRawJavaScript() {
        return rawJavaScript;
    }

    public String getRelativeFilePath() {
        return relativeFilePath;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setJavaScriptFunction(String javaScriptFunction) {
        this.javaScriptFunction = javaScriptFunction;
    }

    public void setLinkText(String buttonText) {
        this.linkText = buttonText;
    }

    public void setRawJavaScript(String rawJavaScript) {
        this.rawJavaScript = rawJavaScript;
    }

    public void setRelativeFilePath(String relativeFilePath) {
        this.relativeFilePath = relativeFilePath;
    }

    @Override
    public String toString() {

        ToStringCreator tsc = new ToStringCreator(this).append("description", description)
                .append("javaScriptFunction", (null == javaScriptFunction ? null : (25 <= javaScriptFunction.length() ? javaScriptFunction.substring(0, 24).replace("\n", " ")
                        + "..." : javaScriptFunction.replace("\n", " ")))).append("linkText", linkText)
                .append("rawJavaScript", (null == rawJavaScript ? null : (25 <= rawJavaScript.length() ? rawJavaScript.substring(0, 24).replace("\n", " ")
                        + "..." : rawJavaScript.replace("\n", " "))))
                .append("relativeFilePath", relativeFilePath);

        return tsc.toString();
    }

}
