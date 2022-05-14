package norman.gurps.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.LoggingException;
import norman.gurps.model.equipment.Shield;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShieldService {
    private static Logger LOGGER = LoggerFactory.getLogger(ShieldService.class);
    private static ClassLoader loader = Thread.currentThread().getContextClassLoader();
    private static ObjectMapper mapper = new ObjectMapper();
    private static String SHIELDS_RESOURCE = "data/shields.json";

    public static List<Shield> findAll() {
        return loadShields();
    }

    public static Shield getShield(String shieldName) {
        Shield returnShield = null;
        List<Shield> shields = loadShields();
        for (Shield shield : shields) {
            if (shield.getName().equals(shieldName)) {
                returnShield = shield;
                break;
            }
        }
        return returnShield;
    }

    private static List<Shield> loadShields() {
        List<Shield> shieldList = new ArrayList<>();
        try {
            InputStream stream = loader.getResourceAsStream(SHIELDS_RESOURCE);
            Shield[] shieldArray = mapper.readValue(stream, Shield[].class);
            shieldList.addAll(Arrays.asList(shieldArray));
        } catch (IOException e) {
            throw new LoggingException(LOGGER, "Error loading shields from resource " + SHIELDS_RESOURCE + ".", e);
        }
        return shieldList;
    }
}
