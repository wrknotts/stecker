package stecker.adapter;

import java.util.concurrent.ExecutionException;

import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureAdapter;

import stecker.domain.CodeDescriptor;

/**
 * A specialization of the generic Spring {@code ListenableFutureAdapter} to
 * adapt (map) response returned from SCM to a {@code CodeDescriptor}.
 *
 * @param <S> type that represents response from SCM
 */
public abstract class AbstractListenableFutureCodeDescriptorAdapter<S> extends
ListenableFutureAdapter<CodeDescriptor, ResponseEntity<S>> {

    protected String id;

    /**
     * Construct a new {@code AbstractListenableFutureCodeDescriptorAdapter}
     * with the given adaptee and id.
     * 
     * @param adaptee the future to adapt to
     * @param id source code identifier
     */
    public AbstractListenableFutureCodeDescriptorAdapter(
            ListenableFuture<ResponseEntity<S>> adaptee, String id) {

        super(adaptee);
        this.id = id;

    }

    /**
     * @see org.springframework.util.concurrent.FutureAdapter#adapt(java.lang.Object)
     */
    @Override
    protected CodeDescriptor adapt(ResponseEntity<S> adapteeResult) throws ExecutionException {

        return adaptToCodeDescriptor(adapteeResult);

    }

    /**
     * Concrete classes must implement this method to handle their specific
     * mapping needs.
     * 
     * @param adapteeResult represents response from the SCM
     * @return instance of {@code CodeDescriptor} containing the mapped values
     */
    protected abstract CodeDescriptor adaptToCodeDescriptor(ResponseEntity<S> adapteeResult);

}
