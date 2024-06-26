package toy.slick.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.config.annotation.DelegatingWsConfiguration;

@Configuration
public class WsConfig {

    @Bean
    public DelegatingWsConfiguration delegatingWsConfiguration() {
        return new DelegatingWsConfiguration();
    }
}
