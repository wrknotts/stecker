package stecker.environment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.style.ToStringCreator;

/**
 * Represents the Stecker-specific configurations in the current environment.
 */
@ConfigurationProperties(locations = "classpath:application.yml", prefix = "stecker")
public class ApplicationEnvironment {

    private int cacheMinutes = 0;
    private String styleSheetName = "pygments";

    public int getCacheMinutes() {
        return cacheMinutes;
    }

    public String getStyleSheetName() {
        return styleSheetName;
    }

    public void setCacheMinutes(int cacheMinutes) {
        this.cacheMinutes = cacheMinutes;
    }

    public void setStyleSheetName(String styleSheetName) {
        this.styleSheetName = styleSheetName;
    }

    @Override
    public String toString() {

        ToStringCreator tsc = new ToStringCreator(this).append("cacheMinutes", cacheMinutes)
                .append("styleSheetName", styleSheetName);

        return tsc.toString();
    }

}
