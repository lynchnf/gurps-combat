package norman.gurps.gui;

import norman.gurps.Application;
import norman.gurps.LoggingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
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
        CharFrame charFrame = new CharFrame();
        desktop.add(charFrame);
        try {
            charFrame.setSelected(true);
        } catch (PropertyVetoException e) {
            LOGGER.warn("Unable to set selected on CharFrame for new GameChar.", e);
        }
    }
}
