package stecker.adapter.stash;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import stecker.adapter.AbstractListenableFutureCodeDescriptorAdapter;
import stecker.domain.CodeDescriptor;
import stecker.domain.FileDescriptor;
import stecker.domain.stash.File;
import stecker.domain.stash.FileLine;

/**
 * Concrete implementation of
 * {@code AbstractListenableFutureCodeDescriptorAdapter} that adapts (maps)
 * responses for retrieval of a repository file from Stash to a
 * {@code CodeDescriptor}.
 * 
 * @see stecker.domain.stash.File
 * @see stecker.domain.stash.FileLine
 */
public class FileAdapter extends AbstractListenableFutureCodeDescriptorAdapter<File> {

    private static final String LF = "\n";
    private static final Logger LOGGER = LoggerFactory.getLogger(FileAdapter.class);

    private final String path;

    /**
     * Construct a new {@code FileAdapter} using the provided file, id and path.
     * 
     * @param file {@code ListenableFuture} that represents the asynchronous
     *            result of retrieving a repository source file
     * @param id simple name of repository file in Stash
     * @param path full path of repository file in Stash
     */
    public FileAdapter(ListenableFuture<ResponseEntity<File>> file, String id, String path) {

        super(file, id);
        this.path = path;

    }

    /**
     * @see stecker.adapter.AbstractListenableFutureCodeDescriptorAdapter#adaptToCodeDescriptor(org.springframework.http.ResponseEntity)
     */
    @Override
    protected CodeDescriptor adaptToCodeDescriptor(ResponseEntity<File> adapteeResult) {

        File file = adapteeResult.getBody();

        LOGGER.debug("retrieved file. Value: '{}'", file);

        CodeDescriptor descriptor = new CodeDescriptor();
        descriptor.setIdentifier(path);
        descriptor.setName(id);

        List<FileDescriptor> fileDescriptors = new ArrayList<FileDescriptor>(1);

        FileDescriptor fd = new FileDescriptor();
        fd.setIdentifier(path);
        
        (25 <= highlightedCode.length() ? highlightedCode.substring(0, 24).replace(
                "\n", " ")
                + "..." : highlightedCode.replace("\n", " "));
        fd.setName(path);
        fd.setRawCode(processLines(file.getLines()));

        fileDescriptors.add(fd);

        descriptor.setFileDescriptors(fileDescriptors);

        return descriptor;
    }

    /**
     * Performs any processing needed for file lines retrieved from Stash
     * 
     * @param lines
     * @return
     */
    private String processLines(List<FileLine> lines) {

        StringBuilder sb = new StringBuilder();
        for (FileLine line : lines) {
            sb.append(line.getText()).append(LF);
        }

        return sb.substring(0, sb.lastIndexOf(LF)).toString();

    }
}
