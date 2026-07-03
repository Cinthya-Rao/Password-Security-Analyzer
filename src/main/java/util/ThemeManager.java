package util;

import javafx.scene.Parent;

public class ThemeManager {
    private boolean darkMode = true;

    public void applyTheme(Parent root, boolean darkMode) {
        this.darkMode = darkMode;
        root.getStyleClass().removeAll("theme-dark", "theme-light");
        root.getStyleClass().add(darkMode ? "theme-dark" : "theme-light");
    }

    public boolean isDarkMode() {
        return darkMode;
    }
}
