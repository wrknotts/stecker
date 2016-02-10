package stecker.controller;

import java.util.List;

import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.ModelAndView;

import stecker.domain.FileDescriptor;

public interface HighlightingListenableFutureCallback {

    public abstract DeferredResult<ModelAndView> getDeferredResult();

    public abstract void addViewUrisToFileDescriptors(List<FileDescriptor> fileDescriptors);

}