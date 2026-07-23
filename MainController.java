package controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Clipboard;
import javafx.util.Duration;
import model.ChecklistItem;
import model.PasswordAnalysis;
import model.PasswordStatistics;
import service.PasswordAnalyzer;
import service.PasswordGenerator;
import util.ClipboardUtil;
import util.ThemeManager;
import view.MainView;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;

public class MainController {
    private final MainView view;
    private final PasswordAnalyzer analyzer = new PasswordAnalyzer();
    private final PasswordGenerator generator = new PasswordGenerator();
    private final ThemeManager themeManager = new ThemeManager();
    private final Deque<String> sessionHistory = new ArrayDeque<>();
    private PasswordAnalysis lastAnalysis;

    public MainController(MainView view) {
        this.view = view;
        initialize();
    }

    private void initialize() {
        applyTheme(true);
        view.getPasswordField().textProperty().bindBidirectional(view.getVisiblePasswordField().textProperty());
        view.getVisiblePasswordField().visibleProperty().bind(view.getShowPasswordCheckBox().selectedProperty());
        view.getVisiblePasswordField().managedProperty().bind(view.getShowPasswordCheckBox().selectedProperty());
        view.getPasswordField().visibleProperty().bind(view.getShowPasswordCheckBox().selectedProperty().not());
        view.getPasswordField().managedProperty().bind(view.getShowPasswordCheckBox().selectedProperty().not());

        view.getPasswordField().textProperty().addListener((observable, oldValue, newValue) -> analyzePassword(false));
        view.getAnalyzeButton().setOnAction(event -> analyzePassword(true));
        view.getClearButton().setOnAction(event -> clear());
        view.getDarkModeToggle().selectedProperty().addListener((observable, oldValue, darkMode) ->
                applyTheme(darkMode));
        view.getCopyReportButton().setOnAction(event -> copyReport());
        view.getCopyScoreButton().setOnAction(event -> copyScore());
        view.getCopySuggestionsButton().setOnAction(event -> copySuggestions());
        view.getGenerateButton().setOnAction(event -> generatePassword());
        view.getCopyGeneratedButton().setOnAction(event -> copyGeneratedPassword());

        analyzePassword(false);
    }

    private void applyTheme(boolean darkMode) {
        themeManager.applyTheme(view.getRoot(), darkMode);
        view.getDarkModeToggle().setText(darkMode ? "Dark" : "Light");
        view.getDarkModeToggle().setAccessibleText(darkMode ? "Dark mode is active" : "Light mode is active");
        view.getStatusLabel().setText((darkMode ? "Dark" : "Light")
                + " mode active. All analysis runs locally.");
    }

    private void analyzePassword(boolean addToHistory) {
        String password = view.getPasswordField().getText();
        lastAnalysis = analyzer.analyze(password);
        updateAnalysis(lastAnalysis);

        if (addToHistory && password != null && !password.isBlank()) {
            addHistoryEntry(lastAnalysis);
        }
    }

    private void updateAnalysis(PasswordAnalysis analysis) {
        view.getScoreLabel().setText(analysis.score() + "/100");
        view.getStrengthLabel().setText(analysis.strengthLevel().getLabel());
        view.getStrengthLabel().getStyleClass().setAll("strength-pill", analysis.strengthLevel().getCssClass());
        animateStrengthBar(view.getStrengthBar(), analysis.score() / 100.0, analysis.strengthLevel().getCssClass());

        PasswordStatistics stats = analysis.statistics();
        view.getEntropyLabel().setText(String.format("%.1f bits", stats.entropyBits()));
        view.getEntropyDescriptionLabel().setText(analysis.entropyLevel().getLabel() + " - "
                + analysis.entropyLevel().getExplanation());
        view.getCrackTimeLabel().setText(analysis.crackTimeEstimate());

        view.getWarningsBox().getChildren().clear();
        if (analysis.warnings().isEmpty()) {
            view.getWarningsBox().getChildren().add(new Label("No urgent warnings detected."));
        } else {
            analysis.warnings().forEach(warning -> {
                Label label = new Label("! " + warning);
                label.getStyleClass().add("warning-label");
                view.getWarningsBox().getChildren().add(label);
            });
        }

        view.getChecklistBox().getChildren().setAll(analysis.checklist().stream()
                .map(this::createChecklistLabel)
                .collect(Collectors.toList()));

        view.getStatisticsBox().getChildren().setAll(
                statLabel("Length", String.valueOf(stats.length())),
                statLabel("Uppercase", String.valueOf(stats.uppercaseCount())),
                statLabel("Lowercase", String.valueOf(stats.lowercaseCount())),
                statLabel("Numbers", String.valueOf(stats.numberCount())),
                statLabel("Symbols", String.valueOf(stats.symbolCount())),
                statLabel("Unique characters", String.valueOf(stats.uniqueCharacterCount())),
                statLabel("Variety", String.format("%.0f%%", stats.characterVarietyPercent()))
        );

        view.getPatternsBox().getChildren().clear();
        if (analysis.patternReport().detectedPatterns().isEmpty()) {
            view.getPatternsBox().getChildren().add(new Label("No obvious repeated, sequential, keyboard, date, or phone patterns found."));
        } else {
            analysis.patternReport().detectedPatterns().forEach(pattern ->
                    view.getPatternsBox().getChildren().add(new Label("! " + pattern)));
        }

        view.getSuggestionsBox().getChildren().clear();
        if (analysis.suggestions().isEmpty()) {
            view.getSuggestionsBox().getChildren().add(new Label("Looks solid. Use a unique password for every account."));
        } else {
            analysis.suggestions().forEach(suggestion -> view.getSuggestionsBox().getChildren().add(new Label("- " + suggestion)));
        }
    }

    private void animateStrengthBar(ProgressBar progressBar, double progress, String styleClass) {
        progressBar.getStyleClass().removeAll("strength-very-weak", "strength-weak", "strength-fair",
                "strength-strong", "strength-very-strong");
        progressBar.getStyleClass().add(styleClass);
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(320),
                new KeyValue(progressBar.progressProperty(), progress)));
        timeline.play();
    }

    private Label createChecklistLabel(ChecklistItem item) {
        Label label = new Label((item.passed() ? "OK  " : "NO  ") + item.label());
        label.getStyleClass().add(item.passed() ? "check-pass" : "check-fail");
        return label;
    }

    private Label statLabel(String name, String value) {
        Label label = new Label(name + ": " + value);
        label.getStyleClass().add("stat-line");
        return label;
    }

    private void addHistoryEntry(PasswordAnalysis analysis) {
        sessionHistory.addFirst(analysis.strengthLevel().getLabel() + " - " + analysis.score()
                + "/100 - " + analysis.crackTimeEstimate());
        while (sessionHistory.size() > 6) {
            sessionHistory.removeLast();
        }
        view.getHistoryBox().getChildren().setAll(sessionHistory.stream().map(Label::new).collect(Collectors.toList()));
    }

    private void clear() {
        view.getPasswordField().clear();
        view.getVisiblePasswordField().clear();
        analyzePassword(false);
    }

    private void copyReport() {
        ensureAnalysis();
        ClipboardUtil.copyText(lastAnalysis.toSecurityReport());
        notify("Security report copied without the password.");
    }

    private void copyScore() {
        ensureAnalysis();
        ClipboardUtil.copyText("Password score: " + lastAnalysis.score() + "/100 (" + lastAnalysis.strengthLevel().getLabel() + ")");
        notify("Score copied.");
    }

    private void copySuggestions() {
        ensureAnalysis();
        String text = lastAnalysis.suggestions().isEmpty()
                ? "No critical improvements needed."
                : String.join(System.lineSeparator(), lastAnalysis.suggestions());
        ClipboardUtil.copyText(text);
        notify("Recommendations copied.");
    }

    private void generatePassword() {
        try {
            String password = generator.generate(
                    view.getLengthSlider().valueProperty().intValue(),
                    view.getGeneratorUppercase().isSelected(),
                    view.getGeneratorLowercase().isSelected(),
                    view.getGeneratorNumbers().isSelected(),
                    view.getGeneratorSymbols().isSelected(),
                    view.getGeneratorExcludeAmbiguous().isSelected()
            );
            view.getGeneratedPasswordField().setText(password);
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void copyGeneratedPassword() {
        String generated = view.getGeneratedPasswordField().getText();
        if (generated == null || generated.isBlank()) {
            showError("Generate a password before copying.");
            return;
        }
        ClipboardContent content = new ClipboardContent();
        content.putString(generated);
        Clipboard.getSystemClipboard().setContent(content);
        notify("Generated password copied.");
    }

    private void ensureAnalysis() {
        if (lastAnalysis == null) {
            analyzePassword(false);
        }
    }

    private void notify(String message) {
        view.getStatusLabel().setText(message);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Password Security Analyzer");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
