package norman.gurps.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.net.URL;
import java.util.ResourceBundle;

public class ButtonDescriptor {
    private static Logger LOGGER = LoggerFactory.getLogger(ButtonDescriptor.class);
    private ResourceBundle bundle = ResourceBundle.getBundle("message");
    private ClassLoader loader = Thread.currentThread().getContextClassLoader();
    private Icon icon;
    private String text;
    private String toolTip;
    private boolean enabled;

    public ButtonDescriptor(String imagePath, String textKey, String toolTipKey) {
        this(imagePath, textKey, toolTipKey, true);
    }

    public ButtonDescriptor(String imagePath, String textKey, String toolTipKey, boolean enabled) {
        if (imagePath != null) {
            URL url = loader.getResource(imagePath);
            icon = new ImageIcon(url);
        }
        if (textKey != null) {
            text = bundle.getString(textKey);
        }
        if (toolTipKey != null) {
            toolTip = bundle.getString(toolTipKey);
        }
        this.enabled = enabled;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
