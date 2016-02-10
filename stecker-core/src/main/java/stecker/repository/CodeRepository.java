package stecker.repository;

import org.springframework.util.concurrent.ListenableFuture;

import stecker.domain.CodeDescriptor;

/**
 * Defines type for any SCM repository implementation.
 */
public interface CodeRepository {

    /**
     * Attempt to retrieve source code from SCM asynchronously.
     * 
     * @param id unique identifier of source code in SCM
     * @return a Spring {@codeListenableFuture} that will return a
     *         {@code CodeDescriptor} when retrieval attempt is complete
     */
    ListenableFuture<CodeDescriptor> findById(String id);

}
