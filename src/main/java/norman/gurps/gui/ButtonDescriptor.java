package norman.gurps.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.net.URL;
import java.util.ResourceBundle;

public class ButtonDescriptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ButtonDescriptor.class);
    private final ResourceBundle bundle;
    private final ClassLoader loader;
    private Icon icon;
    private String text;
    private String toolTip;

    public ButtonDescriptor(String imagePath, String textKey, String toolTipKey) {
        bundle = ResourceBundle.getBundle("message");
        loader = Thread.currentThread().getContextClassLoader();
        if (imagePath != null) {
            URL url = loader.getResource(imagePath);
            icon = new ImageIcon(url);
        }
        if (textKey != null) {
            String text = bundle.getString(textKey);
        }
        if (toolTipKey != null) {
            String toolTip = bundle.getString(toolTipKey);
        }
    }

    public Icon getIcon() {
        return icon;
    }

    public String getText() {
        return text;
    }

    public String getToolTip() {
        return toolTip;
    }
}
