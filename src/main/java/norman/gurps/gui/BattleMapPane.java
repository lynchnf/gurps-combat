package norman.gurps.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class BattleMapPane extends JPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(BattleMapPane.class);

    public BattleMapPane(String imagePath, int width, int height) {
        super();
        initComponents(imagePath, width, height);
    }

    private void initComponents(String imagePath, int width, int height) {
        LOGGER.debug("Initializing battle map pane.");
        setLayout(new BorderLayout());

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(imagePath);
        ImageIcon image = new ImageIcon(url);
        JLabel imageLabel = new JLabel(image);

        JScrollPane scrollPane = new JScrollPane(imageLabel);
        scrollPane.setPreferredSize(new Dimension(width, height));
        this.add(scrollPane);
    }
}
