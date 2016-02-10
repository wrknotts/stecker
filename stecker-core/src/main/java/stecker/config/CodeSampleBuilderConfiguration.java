package stecker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import stecker.controller.CodeSampleBuilder;

/**
 * Provides configured instance of {@code CodeSampleBuilder}.
 */
@Configuration
public class CodeSampleBuilderConfiguration {

    /**
     * Returns {@code CodeSampleBuilder}.
     * 
     * @return {@code CodeSampleBuilder}.
     */
    @Bean
    public CodeSampleBuilder codeSampleBuilder() {
        return new CodeSampleBuilder();
    }

}