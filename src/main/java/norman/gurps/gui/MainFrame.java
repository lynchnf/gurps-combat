package norman.gurps.gui;

import norman.gurps.Application;
import norman.gurps.LoggingException;
import norman.gurps.model.GameChar;
import norman.gurps.service.GameCharService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.WindowConstants;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class MainFrame extends JFrame implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainFrame.class);
    private ResourceBundle bundle;
    private ClassLoader loader;
    private final Properties appProps;
    private JDesktopPane desktop;
    private JMenuItem optionsFileItem;
    private JMenuItem exitFileItem;
    private JMenuItem createCharItem;
    private JMenuItem updateCharItem;
    private JMenuItem deleteCharItem;
    private JMenuItem createBattleItem;
    private int frameCount = 0;

    public MainFrame(Properties appProps) throws HeadlessException {
        super();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.appProps = appProps;
        Locale.setDefault(Locale.forLanguageTag(appProps.getProperty("main.frame.language")));

        initComponents();
        int width = Integer.parseInt(appProps.getProperty("main.frame.width"));
        int height = Integer.parseInt(appProps.getProperty("main.frame.height"));
        setSize(width, height);
        int x = Integer.parseInt(appProps.getProperty("main.frame.location.x"));
        int y = Integer.parseInt(appProps.getProperty("main.frame.location.y"));
        setLocation(x, y);
    }

    private void initComponents() {
        LOGGER.debug("Initializing window components. Locale = " + Locale.getDefault());
        bundle = ResourceBundle.getBundle("message");
        loader = Thread.currentThread().getContextClassLoader();
        setTitle(bundle.getString("main.frame.title"));
        desktop = new JDesktopPane();
        desktop.setOpaque(false);
        setContentPane(desktop);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = createMenu(null, "main.file", null, menuBar);
        createBattleItem = createMenuItem(null, "main.battle", null, this, fileMenu);
        optionsFileItem = createMenuItem(null, "main.options", null, this, fileMenu);
        fileMenu.add(new JSeparator());
        exitFileItem = createMenuItem(null, "main.exit", null, this, fileMenu);

        JMenu charMenu = createMenu(null, "main.char", null, menuBar);
        createCharItem = createMenuItem(null, "main.char.create", null, this, charMenu);
        updateCharItem = createMenuItem(null, "main.char.update", null, this, charMenu);
        deleteCharItem = createMenuItem(null, "main.char.delete", null, this, charMenu);
    }

    @Override
    protected void processWindowEvent(WindowEvent windowEvent) {
        if (windowEvent.getSource() == this && windowEvent.getID() == WindowEvent.WINDOW_CLOSING) {

            // Get current size and position of UI and remember it.
            Dimension size = getSize();
            appProps.setProperty("main.frame.width", Integer.toString(size.width));
            appProps.setProperty("main.frame.height", Integer.toString(size.height));
            Point location = getLocation();
            appProps.setProperty("main.frame.location.x", Integer.toString(location.x));
            appProps.setProperty("main.frame.location.y", Integer.toString(location.y));

            // Save the properties file to a local file.
            try {
                Application.storeProps(appProps);
            } catch (LoggingException e) {
                JOptionPane.showMessageDialog(this, bundle.getString("error.message.saving.window.size.and.location"),
                        bundle.getString("error.title"), JOptionPane.ERROR_MESSAGE);
            }
        }
        super.processWindowEvent(windowEvent);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(exitFileItem)) {
            LOGGER.debug("Processing exit menu item.");
            processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        } else if (actionEvent.getSource().equals(createBattleItem)) {
            createBattle();
        } else if (actionEvent.getSource().equals(optionsFileItem)) {
            options();
        } else if (actionEvent.getSource().equals(createCharItem)) {
            createChar();
        } else if (actionEvent.getSource().equals(updateCharItem)) {
            updateChar();
        } else if (actionEvent.getSource().equals(deleteCharItem)) {
            deleteChar();
        } else {
            LOGGER.warn("Unknown actionEvent=\"" + ((AbstractButton) actionEvent.getSource()).getText() + "\"");
        }
    }

    private void createBattle() {
        BattleFrame battleFrame = new BattleFrame();
        desktop.add(battleFrame);
        try {
            battleFrame.setMaximum(true);
            battleFrame.setSelected(true);
        } catch (PropertyVetoException e) {
            LOGGER.warn("Unable to maximize and/or select battle frame.", e);
        }
    }

    private void options() {
        JFrame optionsFrame = new JFrame();
        optionsFrame.setTitle(bundle.getString("main.options.dialog.title"));
        optionsFrame.setResizable(false);
        JPanel optionsPanel = new JPanel();
        optionsFrame.add(optionsPanel);
        optionsPanel.setOpaque(false);

        JLabel langLabel = new JLabel(bundle.getString("main.options.language"));
        optionsPanel.add(langLabel);

        LocaleWrapper[] locales = {new LocaleWrapper(Locale.ENGLISH)};
        JComboBox<LocaleWrapper> langComboBox = new JComboBox<>(locales);
        optionsPanel.add(langComboBox);
        langComboBox.setSelectedItem(new LocaleWrapper());

        MainFrame mainFrame = this;
        langComboBox.addActionListener(actionEvent -> {
            LocaleWrapper newLang = (LocaleWrapper) langComboBox.getSelectedItem();
            assert newLang != null;
            appProps.setProperty("main.frame.language", newLang.getLocale().toLanguageTag());
            try {
                Application.storeProps(appProps);
            } catch (LoggingException e) {
                JOptionPane.showMessageDialog(mainFrame,
                        bundle.getString("error.message.saving.window.size.and.location"),
                        bundle.getString("error.title"), JOptionPane.ERROR_MESSAGE);
            }

            Locale.setDefault(newLang.getLocale());
            initComponents();
            optionsFrame.dispose();
        });
        optionsFrame.pack();

        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int optionsWidth = optionsFrame.getWidth();
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        int optionsHeight = optionsFrame.getHeight();
        optionsFrame.setLocation((screenWidth - optionsWidth) / 2, (screenHeight - optionsHeight) / 2);
        optionsFrame.setVisible(true);
    }

    private void createChar() {
        showCharEditFrame(new GameChar());
    }

    private void updateChar() {
        GameChar gameChar = showSelectCharDialog("images/character32.png", "main.char.update.dialog.title",
                "main.char.update.dialog.message", desktop);
        if (gameChar != null) {
            showCharEditFrame(gameChar);
        }
    }

    private void deleteChar() {
        GameChar gameChar = showSelectCharDialog("images/remove32.png", "main.char.delete.dialog.title",
                "main.char.delete.dialog.message", desktop);
        if (gameChar != null) {
            showCharViewFrame(gameChar);
        }
    }

    private void showCharViewFrame(GameChar gameChar) {
        CharViewFrame charViewFrame = new CharViewFrame(gameChar, frameCount++);
        desktop.add(charViewFrame);
        try {
            charViewFrame.setSelected(true);
        } catch (PropertyVetoException e) {
            LOGGER.warn("Unable to select CharViewFrame. gameChar=\"" + gameChar + "\"", e);
        }
    }

    private void showCharEditFrame(GameChar gameChar) {
        CharEditFrame charEditFrame = new CharEditFrame(gameChar, frameCount++);
        desktop.add(charEditFrame);
        try {
            charEditFrame.setSelected(true);
        } catch (PropertyVetoException e) {
            LOGGER.warn("Unable to select CharEditFrame. gameChar=\"" + gameChar + "\"", e);
        }
    }

    // COMMON METHODS // TODO Refactor these someday.

    private JMenu createMenu(String imagePath, String textKey, String toolTipKey, JMenuBar bar) {
        JMenu menu = new JMenu();
        if (imagePath != null) {
            URL url = loader.getResource(imagePath);
            ImageIcon icon = new ImageIcon(url);
            menu.setIcon(icon);
        }
        if (textKey != null) {
            String text = bundle.getString(textKey);
            menu.setText(text);
        }
        if (toolTipKey != null) {
            String toolTip = bundle.getString(toolTipKey);
            menu.setToolTipText(toolTip);
        }
        if (bar != null) {
            bar.add(menu);
        }
        return menu;
    }

    private JMenuItem createMenuItem(String imagePath, String textKey, String toolTipKey, ActionListener listener,
            JMenu menu) {
        JMenuItem item = new JMenuItem();
        if (imagePath != null) {
            URL url = loader.getResource(imagePath);
            ImageIcon icon = new ImageIcon(url);
            item.setIcon(icon);
        }
        if (textKey != null) {
            String text = bundle.getString(textKey);
            item.setText(text);
        }
        if (toolTipKey != null) {
            String toolTip = bundle.getString(toolTipKey);
            item.setToolTipText(toolTip);
        }
        if (listener != null) {
            item.addActionListener(listener);
        }
        if (menu != null) {
            menu.add(item);
        }
        return item;
    }

    private GameChar showSelectCharDialog(String imagePath, String titleKey, String messageKey,
            Component parentComponent) {
        Icon icon = null;
        if (imagePath != null) {
            URL url = loader.getResource(imagePath);
            icon = new ImageIcon(url);
        }
        String title = null;
        if (titleKey != null) {
            title = bundle.getString(titleKey);
        }
        Object message = null;
        if (messageKey != null) {
            message = bundle.getString(messageKey);
        }
        List<GameChar> allGameChars = GameCharService.findAll();
        allGameChars.sort(Comparator.comparing(GameChar::getName));
        GameChar[] selectionValues = allGameChars.toArray(new GameChar[0]);
        return (GameChar) JOptionPane.showInternalInputDialog(parentComponent, message, title,
                JOptionPane.PLAIN_MESSAGE, icon, selectionValues, null);
    }
}
