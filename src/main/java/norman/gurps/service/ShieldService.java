package norman.gurps.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.LoggingException;
import norman.gurps.model.Shield;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShieldService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShieldService.class);
    public static final String SHIELDS_RESOURCE = "data/shields.json";
    private static final ClassLoader loader = Thread.currentThread().getContextClassLoader();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<Shield> findAll() {
        return loadShields();
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
