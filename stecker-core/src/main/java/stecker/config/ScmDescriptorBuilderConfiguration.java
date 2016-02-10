package stecker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import stecker.domain.ScmDescriptorBuilder;

/**
 * Provides configured instance of {@code ScmDescriptorBuilder}.
 */
@Configuration
public class ScmDescriptorBuilderConfiguration {

    /**
     * Returns {@code ScmDescriptorBuilder}.
     * 
     * @return {@code ScmDescriptorBuilder}.
     */
    @Bean
    public ScmDescriptorBuilder scmDescriptorBuilder() {
        return new ScmDescriptorBuilder();
    }

}