package stecker.config.stash;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import stecker.environment.stash.StashEnvironment;

/**
 * Handles configuration of the instances of a Spring {@code AsyncRestTemplate} used to interact with Stash.
 */
@Configuration
@ConditionalOnProperty(name = "stash.enabled")
@EnableConfigurationProperties(value = StashEnvironment.class)
public class AsyncRestTemplateConfiguration {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AsyncRestTemplateConfiguration.class);
    private static final String MESSAGE_KEY_FILE_UNAUTHORIZED = "stash.file.unauthorized";
    private static final String MESSAGE_KEY_NOT_FOUND = "stash.not.found";
    private static final String MESSAGE_KEY_SNIPPET_UNAUTHORIZED = "stash.snippet.unauthorized";

    private int maxThreads;
    private StashEnvironment stashEnv;

    @Autowired
    private MessageSource messageSource;

    /**
     * Automatically invoked after dependency injection is done to perform any
     * additional initialization.
     */
    @PostConstruct
    public void postConstruct() {

        this.maxThreads = stashEnv.getMaxRepositoryThreads();

        LOGGER.debug("maximum number of threads for retrieving source code set to '{}'", maxThreads);

    }

    /**
     * Establishes the {@code StashEnvironment} that represents the Stash-specific
     * configuration.
     * 
     * @param stashEnv represents current Stash configuration
     */
    @Autowired
    public void setStashEnv(StashEnvironment stashEnv) {
        this.stashEnv = stashEnv;
    }

    /**
     * Returns a configured instance of {@code AsyncRestTemplate} used to retrieve repository source files from Stash.
     * 
     * @return
     */
    @Bean(name = "fileAsyncRestTemplate")
    AsyncRestTemplate fileAsyncRestTemplate() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(maxThreads);
        executor.setMaxPoolSize(maxThreads);
        executor.initialize();

        AsyncRestTemplate template = new AsyncRestTemplate(executor);
        template.setErrorHandler(new StatusCheckingResponseErrorHandler() {

            @Override
            public void handleClientError(HttpStatus statusCode) {

                if (HttpStatus.NOT_FOUND.equals(statusCode)) {
                    throw new RestClientException(messageSource.getMessage(MESSAGE_KEY_NOT_FOUND,
                            null, "Not Found", LocaleContextHolder.getLocale()));
                }

                if (HttpStatus.UNAUTHORIZED.equals(statusCode)) {
                    throw new RestClientException(messageSource.getMessage(
                            MESSAGE_KEY_FILE_UNAUTHORIZED, null, "Unauthorized or Not Found",
                            LocaleContextHolder.getLocale()));
                }
            }
        });

        return template;
    }

    /**
     * Returns a configured instance of {@code AsyncRestTemplate} used to retrieve snippet source files from Stash.
     * 
     * @return
     */
    @Bean(name = "snippetAsyncRestTemplate")
    AsyncRestTemplate snippetAsyncRestTemplate() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(maxThreads);
        executor.setMaxPoolSize(maxThreads);
        executor.initialize();

        AsyncRestTemplate template = new AsyncRestTemplate(executor);
        template.setErrorHandler(new StatusCheckingResponseErrorHandler() {

            @Override
            public void handleClientError(HttpStatus statusCode) {
                if (HttpStatus.NOT_FOUND.equals(statusCode)) {
                    throw new RestClientException(messageSource.getMessage(MESSAGE_KEY_NOT_FOUND,
                            null, "Not Found", LocaleContextHolder.getLocale()));
                }
                if (HttpStatus.UNAUTHORIZED.equals(statusCode)) {
                    throw new RestClientException(messageSource.getMessage(
                            MESSAGE_KEY_SNIPPET_UNAUTHORIZED, null, "Unauthorized",
                            LocaleContextHolder.getLocale()));
                }

            }

        });

        return template;
    }

    /**
     * Overrides default Spring response error handling behavior.
     */
    private static class StatusCheckingResponseErrorHandler extends DefaultResponseErrorHandler {

        /**
         * @see org.springframework.web.client.DefaultResponseErrorHandler#handleError(org.springframework.http.client.ClientHttpResponse)
         */
        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            HttpStatus statusCode = getHttpStatusCode(response);
            switch (statusCode.series()) {
            case CLIENT_ERROR:

                handleClientError(statusCode);

                super.handleError(response);

            case SERVER_ERROR:
                super.handleError(response);
            default:
                throw new RestClientException("Unknown status code [" + statusCode + "]");
            }
        }

        protected void handleClientError(HttpStatus statusCode) {
        }

        private HttpStatus getHttpStatusCode(ClientHttpResponse response) throws IOException {
            HttpStatus statusCode;
            try {
                statusCode = response.getStatusCode();
            }
            catch (IllegalArgumentException ex) {
                throw new HttpServerErrorException(HttpStatus.valueOf(response.getRawStatusCode()),
                        response.getStatusText());
            }
            return statusCode;
        }
    }
}
