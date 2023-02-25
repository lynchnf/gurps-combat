package norman.gurps.combat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GurpsCombatConfig {
    private static Logger LOGGER = LoggerFactory.getLogger(GurpsCombatConfig.class);

    //@Bean
    //public ClassLoader classLoader() {
    //    return Thread.currentThread().getContextClassLoader();
    //}

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
