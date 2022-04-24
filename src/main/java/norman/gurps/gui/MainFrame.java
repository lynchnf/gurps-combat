package norman.gurps.gui;

import norman.gurps.Application;
import norman.gurps.LoggingException;
import norman.gurps.model.GameChar;
import norman.gurps.service.GameCharService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class MainFrame extends JFrame implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainFrame.class);
    private ResourceBundle bundle;
    private Properties appProps;
    private JDesktopPane desktop;
    private JMenuItem optionsFileItem;
    private JMenuItem exitFileItem;
    private JMenuItem createCharItem;
    private JMenuItem updateCharItem;
    private JMenuItem deleteCharItem;
    private JMenuItem createBattleItem;
    private JMenuItem addCharBattleItem;
    private JMenuItem removeCharBattleItem;

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
        bundle = ResourceBundle.getBundle("norman.gurps.gui.MainFrame");
        setTitle(bundle.getString("title"));
        desktop = new JDesktopPane();
        setContentPane(desktop);
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = createMenu("menu.file", menuBar);
        optionsFileItem = createMenuItem("menu.file.options", fileMenu);
        fileMenu.add(new JSeparator());
        exitFileItem = createMenuItem("menu.file.exit", fileMenu);

        JMenu charMenu = createMenu("menu.char", menuBar);
        createCharItem = createMenuItem("menu.char.create", charMenu);
        updateCharItem = createMenuItem("menu.char.update", charMenu);
        deleteCharItem = createMenuItem("menu.char.delete", charMenu);

        JMenu battleMenu = createMenu("menu.battle", menuBar);
        createBattleItem = createMenuItem("menu.battle.create", battleMenu);
        addCharBattleItem = createMenuItem("menu.battle.add.char", battleMenu);
        removeCharBattleItem = createMenuItem("menu.battle.remove.char", battleMenu);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(exitFileItem)) {
            LOGGER.debug("Processing exit menu item.");
            processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        } else if (actionEvent.getSource().equals(optionsFileItem)) {
            options();
        } else if (actionEvent.getSource().equals(createCharItem)) {
            createChar();
        } else if (actionEvent.getSource().equals(updateCharItem)) {
            updateChar();
        } else if (actionEvent.getSource().equals(deleteCharItem)) {
            deleteChar();
        } else {
            LOGGER.debug("Unknown actionEvent=\"" + ((AbstractButton) actionEvent.getSource()).getText() + "\"");
        }
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
                        bundle.getString("error.dialog.title"), JOptionPane.ERROR_MESSAGE);
            }
        }
        super.processWindowEvent(windowEvent);
    }

    private JMenu createMenu(String key, JMenuBar bar) {
        JMenu menu = new JMenu(bundle.getString(key));
        bar.add(menu);
        return menu;
    }

    private JMenuItem createMenuItem(String key, JMenu menu) {
        JMenuItem item = new JMenuItem(bundle.getString(key));
        menu.add(item);
        item.addActionListener(this);
        return item;
    }

    private void options() {
        JFrame optionsFrame = new JFrame();
        optionsFrame.setTitle(bundle.getString("options.title"));
        optionsFrame.setResizable(false);
        JPanel optionsPanel = new JPanel();
        optionsFrame.add(optionsPanel);
        optionsPanel.setOpaque(false);

        JLabel langLabel = new JLabel(bundle.getString("options.language"));
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
                        bundle.getString("error.dialog.title"), JOptionPane.ERROR_MESSAGE);
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
        GameChar choice = showSelectCharDialog("menu.char.update", "char.update.message");
        if (choice != null) {
            showCharEditFrame(choice);
        }
    }

    private void deleteChar() {
        GameChar choice = showSelectCharDialog("menu.char.delete", "char.delete.message");
        if (choice != null) {
            showCharViewFrame(choice);
        }
    }

    private GameChar showSelectCharDialog(String titleKey, String messageKey) {
        String title = bundle.getString(titleKey);
        Object message = bundle.getString(messageKey);
        List<GameChar> allGameChars = GameCharService.findAll();
        GameChar[] selectionValues = allGameChars.toArray(new GameChar[0]);
        return (GameChar) JOptionPane.showInternalInputDialog(desktop, message, title, JOptionPane.PLAIN_MESSAGE, null,
                selectionValues, null);
    }

    private void showCharViewFrame(GameChar gameChar) {
        CharViewFrame charViewFrame = new CharViewFrame(gameChar);
        desktop.add(charViewFrame);
        try {
            charViewFrame.setSelected(true);
        } catch (PropertyVetoException e) {
            LOGGER.warn("Unable to select CharViewFrame. gameChar=\"" + gameChar + "\"", e);
        }
    }

    private void showCharEditFrame(GameChar gameChar) {
        CharEditFrame charEditFrame = new CharEditFrame(gameChar);
        desktop.add(charEditFrame);
        try {
            charEditFrame.setSelected(true);
        } catch (PropertyVetoException e) {
            LOGGER.warn("Unable to select CharEditFrame. gameChar=\"" + gameChar + "\"", e);
        }
    }
}
