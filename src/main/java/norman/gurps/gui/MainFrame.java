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

        JMenu fileMenu = new JMenu(bundle.getString("menu.file"));
        menuBar.add(fileMenu);
        optionsFileItem = new JMenuItem(bundle.getString("menu.file.options"));
        fileMenu.add(optionsFileItem);
        optionsFileItem.addActionListener(this);
        fileMenu.add(new JSeparator());
        exitFileItem = new JMenuItem(bundle.getString("menu.file.exit"));
        fileMenu.add(exitFileItem);
        exitFileItem.addActionListener(this);

        JMenu charMenu = new JMenu(bundle.getString("menu.char"));
        menuBar.add(charMenu);
        createCharItem = new JMenuItem(bundle.getString("menu.char.create"));
        charMenu.add(createCharItem);
        updateCharItem = new JMenuItem(bundle.getString("menu.char.update"));
        charMenu.add(updateCharItem);
        deleteCharItem = new JMenuItem(bundle.getString("menu.char.delete"));
        charMenu.add(deleteCharItem);

        JMenu battleMenu = new JMenu(bundle.getString("menu.battle"));
        menuBar.add(battleMenu);
        createBattleItem = new JMenuItem(bundle.getString("menu.battle.create"));
        battleMenu.add(createBattleItem);
        addCharBattleItem = new JMenuItem(bundle.getString("menu.battle.add.char"));
        battleMenu.add(addCharBattleItem);
        removeCharBattleItem = new JMenuItem(bundle.getString("menu.battle.remove.char"));
        battleMenu.add(removeCharBattleItem);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(exitFileItem)) {
            LOGGER.debug("Processing exit menu item.");
            processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        } else if (actionEvent.getSource().equals(optionsFileItem)) {
            options();
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

    private void options() {
        // Put up an options panel to change the number of moves per second
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
}
