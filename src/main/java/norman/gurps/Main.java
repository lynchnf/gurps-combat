package norman.gurps;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.debug("Starting Application");
        Main me = new Main();
        me.doIt();
        LOGGER.debug("Finished Application");
    }

    private void doIt() {
        String abc = StringUtils.trimToNull("  ABC  ");
        LOGGER.debug("abc=\"" + abc + "\"");
    }
}
