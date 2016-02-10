package stecker.environment.stash;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.style.ToStringCreator;

import stecker.environment.AbstractScmEnvironment;

@ConfigurationProperties(prefix = "stash")
public class StashEnvironment extends AbstractScmEnvironment {

    private boolean filesEnabled = false;
    private int maxRepositoryThreads = 10;
    private boolean snippetsEnabled = false;

    public int getMaxRepositoryThreads() {
        return maxRepositoryThreads;
    }

    public boolean isFilesEnabled() {
        return (enabled && filesEnabled);
    }

    public boolean isSnippetsEnabled() {
        return (enabled && snippetsEnabled);
    }

    public void setFilesEnabled(boolean filesEnabled) {
        this.filesEnabled = filesEnabled;
    }

    public void setMaxRepositoryThreads(int maxRepositoryThreads) {
        this.maxRepositoryThreads = maxRepositoryThreads;
    }

    public void setSnippetsEnabled(boolean snippetsEnabled) {
        this.snippetsEnabled = snippetsEnabled;
    }

    @Override
    public String toString() {

        ToStringCreator tsc = new ToStringCreator(this).append("enabled", enabled)
                .append("baseAddress", baseAddress).append("repositoryImage", repositoryImage)
                .append("bookmarklets", bookmarklets).append("filesEnabled", filesEnabled)
                .append("snippetsEnabled", snippetsEnabled)
                .append("maxRepositoryThreads", maxRepositoryThreads);

        return tsc.toString();
    }

}
