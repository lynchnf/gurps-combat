package foo.bar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;

public class MyButton extends JButton {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyFrame.class);
    public MyButton() {
        LOGGER.debug("MyButton constructed");
    }
}
