package norman.gurps.combat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.IOUtils;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JsonTestHelper {
    protected ClassLoader loader;
    protected ObjectMapper mapper;

    public void initialize() {
        loader = Thread.currentThread().getContextClassLoader();
        mapper = new ObjectMapper();
    }

    public <T> void doTheTest(Class<T> valueType, String resourceName)
            throws URISyntaxException, IOException, JSONException {
        T object = getObject(valueType, resourceName);
        assertNotNull(object, String.format("Object of type %s, read from file %s, is null.", valueType, resourceName));

        String jsonActual = mapper.writeValueAsString(object);
        assertNotNull(jsonActual,
                String.format("Object of type %s, read from file %s, could not be written as a non-null JSON string.",
                        valueType, resourceName));

        InputStream stream = loader.getResourceAsStream(resourceName);
        String jsonExpected = IOUtils.toString(stream, StandardCharsets.UTF_8);
        JSONAssert.assertEquals(
                String.format("JSON string converted from object of type %s did not match JSON string in file %s.",
                        valueType, resourceName), jsonExpected, jsonActual, true);
    }

    public <T> T getObject(Class<T> valueType, String resourceName) throws URISyntaxException, IOException {
        URL resource = loader.getResource(resourceName);
        String path = resource.toURI().getPath();
        File file = new File(path);
        return mapper.readValue(file, valueType);
    }
}
