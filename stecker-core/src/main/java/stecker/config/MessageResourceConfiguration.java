package stecker.config;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * Handles configuration of application message catalog.
 */
@Configuration
public class MessageResourceConfiguration {

    /**
     * Returns {@code LocalResolver} initialized with default locale of JVM.
     * 
     * @return configured {@code LocaleResolver}
     */
    @Bean
    public LocaleResolver localeResolver() {

        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.getDefault());

        return resolver;
    }

    /**
     * Returns {@code MessageSource} configured with location of file containing
     * application messages.
     * 
     * @return configured {@code MessageSource}
     */
    @Bean
    public MessageSource messageSource() {

        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("locale/messages");

        return source;
    }

}
