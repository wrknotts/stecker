package stecker.adapter.stash;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import stecker.adapter.AbstractListenableFutureCodeDescriptorAdapter;
import stecker.domain.CodeDescriptor;
import stecker.domain.FileDescriptor;
import stecker.domain.stash.Snippet;
import stecker.domain.stash.SnippetFile;
import stecker.util.DateTimeSupport;

/**
 * Concrete implementation of
 * {@code AbstractListenableFutureCodeDescriptorAdapter} that adapts (maps)
 * responses for retrieval of a code snippet from Stash to a
 * {@code CodeDescriptor}.
 * 
 * @see stecker.domain.stash.Snippet
 * @see stecker.domain.stash.SnippetFile
 */
public class SnippetAdapter extends AbstractListenableFutureCodeDescriptorAdapter<Snippet> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SnippetAdapter.class);

    /**
     * Construct a new {@code FileAdapter} using the provided snippet and id.
     * 
     * @param stashSnippet {@code ListenableFuture} that represents the
     *            asynchronous result of retrieving a snippet
     * @param id unique identifier of snippet in Stash
     */
    public SnippetAdapter(ListenableFuture<ResponseEntity<Snippet>> stashSnippet, String id) {
        super(stashSnippet, id);
    }

    /**
     * @see stecker.adapter.AbstractListenableFutureCodeDescriptorAdapter#adaptToCodeDescriptor(org.springframework.http.ResponseEntity)
     */
    @Override
    protected CodeDescriptor adaptToCodeDescriptor(ResponseEntity<Snippet> adapteeResult) {

        Snippet snippet = adapteeResult.getBody();

        LOGGER.debug("retrieved snippet. Value: '{}'", snippet);

        CodeDescriptor descriptor = new CodeDescriptor();
        descriptor.setIdentifier(snippet.getGuid());
        descriptor.setName(snippet.getName());
        descriptor
        .setLastUpdated(DateTimeSupport.formatDateAsAgo(new Date(snippet.getUpdatedAt())));

        List<SnippetFile> files = snippet.getFiles();
        List<FileDescriptor> fileDescriptors = new ArrayList<FileDescriptor>(files.size());

        for (SnippetFile sf : files) {
            FileDescriptor fd = new FileDescriptor();
            fd.setIdentifier(sf.getGuid());
            fd.setName(sf.getName());
            fd.setRawCode(sf.getContent());

            fileDescriptors.add(fd);
        }

        descriptor.setFileDescriptors(fileDescriptors);

        return descriptor;
    }

}
