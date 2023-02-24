package norman.gurps.combat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GurpsCombatApplication {
    private static Logger LOGGER = LoggerFactory.getLogger(GurpsCombatApplication.class);

    public static void main(String[] args) {
        LOGGER.info("Starting GurpsCombatApplication.");
        SpringApplication.run(GurpsCombatApplication.class, args);
    }
}
