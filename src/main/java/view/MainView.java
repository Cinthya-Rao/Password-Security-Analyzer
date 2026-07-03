package view;

import controller.MainController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainView {
    private final BorderPane root = new BorderPane();
    private final PasswordField passwordField = new PasswordField();
    private final TextField visiblePasswordField = new TextField();
    private final CheckBox showPasswordCheckBox = new CheckBox("Show");
    private final Button analyzeButton = new Button("Analyze");
    private final Button clearButton = new Button("Clear");
    private final ToggleButton darkModeToggle = new ToggleButton("Dark");
    private final ProgressBar strengthBar = new ProgressBar(0);
    private final Label strengthLabel = new Label("Very Weak");
    private final Label scoreLabel = new Label("0/100");
    private final Label entropyLabel = new Label("0.0 bits");
    private final Label entropyDescriptionLabel = new Label("Low entropy - Enter a password to begin.");
    private final Label crackTimeLabel = new Label("Instant");
    private final VBox warningsBox = new VBox(8);
    private final VBox checklistBox = new VBox(8);
    private final VBox statisticsBox = new VBox(8);
    private final VBox patternsBox = new VBox(8);
    private final VBox suggestionsBox = new VBox(8);
    private final VBox historyBox = new VBox(8);
    private final Button copyReportButton = new Button("Copy Report");
    private final Button copyScoreButton = new Button("Copy Score");
    private final Button copySuggestionsButton = new Button("Copy Tips");
    private final Slider lengthSlider = new Slider(8, 64, 20);
    private final Label lengthValueLabel = new Label("20");
    private final CheckBox generatorUppercase = new CheckBox("A-Z");
    private final CheckBox generatorLowercase = new CheckBox("a-z");
    private final CheckBox generatorNumbers = new CheckBox("0-9");
    private final CheckBox generatorSymbols = new CheckBox("Symbols");
    private final CheckBox generatorExcludeAmbiguous = new CheckBox("No ambiguous");
    private final Button generateButton = new Button("Generate");
    private final Button copyGeneratedButton = new Button("Copy");
    private final TextField generatedPasswordField = new TextField();
    private final Label statusLabel = new Label("All analysis runs locally. Passwords are never saved or transmitted.");

    public MainView() {
        buildLayout();
        new MainController(this);
    }

    public Parent getRoot() {
        return root;
    }

    private void buildLayout() {
        root.getStyleClass().add("app-root");
        root.setTop(createHeader());
        root.setCenter(createContent());
        root.setBottom(createFooter());
    }

    private Parent createHeader() {
        Label title = new Label("Password Security Analyzer");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("Local password analysis for security awareness and portfolio demonstration.");
        subtitle.getStyleClass().add("app-subtitle");

        VBox titleBox = new VBox(4, title, subtitle);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        darkModeToggle.setSelected(true);
        darkModeToggle.setTooltip(new Tooltip("Switch between dark and light mode"));
        HBox header = new HBox(18, titleBox, spacer, darkModeToggle);
        header.getStyleClass().add("app-header");
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private Parent createContent() {
        VBox content = new VBox(18);
        content.getStyleClass().add("content");
        content.getChildren().addAll(createPasswordPanel(), createResultsGrid(), createGeneratorPanel(), createHistoryPanel());

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("app-scroll");
        return scrollPane;
    }

    private Parent createPasswordPanel() {
        Label sectionTitle = sectionTitle("Password Input");
        passwordField.setPromptText("Enter a password to analyze locally");
        visiblePasswordField.setPromptText("Enter a password to analyze locally");
        passwordField.setAccessibleText("Password input");
        visiblePasswordField.setAccessibleText("Visible password input");

        StackPane passwordStack = new StackPane(passwordField, visiblePasswordField);
        HBox.setHgrow(passwordStack, Priority.ALWAYS);
        analyzeButton.setDefaultButton(true);
        analyzeButton.setTooltip(new Tooltip("Analyze current password"));
        clearButton.setTooltip(new Tooltip("Clear the password field"));

        HBox row = new HBox(10, passwordStack, showPasswordCheckBox, analyzeButton, clearButton);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("input-row");

        VBox panel = panel(sectionTitle, row);
        return panel;
    }

    private Parent createResultsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(18);
        grid.setVgap(18);
        grid.getStyleClass().add("results-grid");
        grid.add(createStrengthPanel(), 0, 0);
        grid.add(createWarningsPanel(), 1, 0);
        grid.add(createChecklistPanel(), 0, 1);
        grid.add(createStatisticsPanel(), 1, 1);
        grid.add(createSuggestionsPanel(), 0, 2);
        grid.add(createPatternsPanel(), 1, 2);

        for (int column = 0; column < 2; column++) {
            javafx.scene.layout.ColumnConstraints constraints = new javafx.scene.layout.ColumnConstraints();
            constraints.setPercentWidth(50);
            constraints.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(constraints);
        }
        return grid;
    }

    private Parent createStrengthPanel() {
        strengthBar.setMaxWidth(Double.MAX_VALUE);
        strengthBar.setPrefHeight(16);

        Label scoreTitle = new Label("Score");
        scoreTitle.getStyleClass().add("metric-label");
        scoreLabel.getStyleClass().add("score-value");
        strengthLabel.getStyleClass().add("strength-pill");

        HBox scoreRow = new HBox(12, scoreTitle, scoreLabel, strengthLabel);
        scoreRow.setAlignment(Pos.CENTER_LEFT);

        Label entropyTitle = new Label("Entropy");
        entropyTitle.getStyleClass().add("metric-label");
        entropyLabel.getStyleClass().add("metric-value");
        HBox entropyRow = new HBox(12, entropyTitle, entropyLabel);
        entropyRow.setAlignment(Pos.CENTER_LEFT);

        Label crackTitle = new Label("Estimated Crack Time");
        crackTitle.getStyleClass().add("metric-label");
        crackTimeLabel.getStyleClass().add("metric-value");
        HBox crackRow = new HBox(12, crackTitle, crackTimeLabel);
        crackRow.setAlignment(Pos.CENTER_LEFT);

        Label note = new Label("Estimate based on brute-force complexity; real-world attacks vary.");
        note.getStyleClass().add("muted");
        note.setWrapText(true);
        entropyDescriptionLabel.setWrapText(true);

        return panel(sectionTitle("Strength Meter"), scoreRow, strengthBar, entropyRow, entropyDescriptionLabel, crackRow, note,
                createCopyButtons());
    }

    private Parent createWarningsPanel() {
        return panel(sectionTitle("Warnings"), warningsBox);
    }

    private Parent createChecklistPanel() {
        return panel(sectionTitle("Security Checklist"), checklistBox);
    }

    private Parent createStatisticsPanel() {
        return panel(sectionTitle("Password Statistics"), statisticsBox);
    }

    private Parent createSuggestionsPanel() {
        return panel(sectionTitle("Recommendations"), suggestionsBox);
    }

    private Parent createPatternsPanel() {
        return panel(sectionTitle("Pattern Detection"), patternsBox);
    }

    private Parent createCopyButtons() {
        copyReportButton.setTooltip(new Tooltip("Copy the full report without the password"));
        copyScoreButton.setTooltip(new Tooltip("Copy only the score"));
        copySuggestionsButton.setTooltip(new Tooltip("Copy only recommendations"));
        HBox row = new HBox(10, copyReportButton, copyScoreButton, copySuggestionsButton);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Parent createGeneratorPanel() {
        Label title = sectionTitle("Secure Password Generator");
        lengthSlider.setShowTickLabels(true);
        lengthSlider.setShowTickMarks(true);
        lengthSlider.setMajorTickUnit(14);
        lengthSlider.setBlockIncrement(1);
        lengthSlider.valueProperty().addListener((observable, oldValue, newValue) ->
                lengthValueLabel.setText(String.valueOf(newValue.intValue())));

        generatorUppercase.setSelected(true);
        generatorLowercase.setSelected(true);
        generatorNumbers.setSelected(true);
        generatorSymbols.setSelected(true);
        generatorExcludeAmbiguous.setSelected(true);

        HBox lengthRow = new HBox(12, new Label("Length"), lengthSlider, lengthValueLabel);
        HBox.setHgrow(lengthSlider, Priority.ALWAYS);
        lengthRow.setAlignment(Pos.CENTER_LEFT);

        HBox optionRow = new HBox(14, generatorUppercase, generatorLowercase, generatorNumbers,
                generatorSymbols, generatorExcludeAmbiguous);
        optionRow.setAlignment(Pos.CENTER_LEFT);

        generatedPasswordField.setEditable(false);
        generatedPasswordField.setPromptText("Generated password appears here");
        generatedPasswordField.setAccessibleText("Generated password output");
        HBox outputRow = new HBox(10, generatedPasswordField, generateButton, copyGeneratedButton);
        HBox.setHgrow(generatedPasswordField, Priority.ALWAYS);
        outputRow.setAlignment(Pos.CENTER_LEFT);

        return panel(title, lengthRow, optionRow, outputRow);
    }

    private Parent createHistoryPanel() {
        Label note = new Label("Session-only history stores score summaries, never passwords.");
        note.getStyleClass().add("muted");
        return panel(sectionTitle("Session History"), note, historyBox);
    }

    private Parent createFooter() {
        HBox footer = new HBox(statusLabel);
        footer.getStyleClass().add("app-footer");
        footer.setAlignment(Pos.CENTER_LEFT);
        return footer;
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }

    private VBox panel(Parent... children) {
        VBox panel = new VBox(12);
        panel.getStyleClass().add("panel");
        panel.setPadding(new Insets(18));
        panel.getChildren().addAll(children);
        return panel;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public TextField getVisiblePasswordField() {
        return visiblePasswordField;
    }

    public CheckBox getShowPasswordCheckBox() {
        return showPasswordCheckBox;
    }

    public Button getAnalyzeButton() {
        return analyzeButton;
    }

    public Button getClearButton() {
        return clearButton;
    }

    public ToggleButton getDarkModeToggle() {
        return darkModeToggle;
    }

    public ProgressBar getStrengthBar() {
        return strengthBar;
    }

    public Label getStrengthLabel() {
        return strengthLabel;
    }

    public Label getScoreLabel() {
        return scoreLabel;
    }

    public Label getEntropyLabel() {
        return entropyLabel;
    }

    public Label getEntropyDescriptionLabel() {
        return entropyDescriptionLabel;
    }

    public Label getCrackTimeLabel() {
        return crackTimeLabel;
    }

    public VBox getWarningsBox() {
        return warningsBox;
    }

    public VBox getChecklistBox() {
        return checklistBox;
    }

    public VBox getStatisticsBox() {
        return statisticsBox;
    }

    public VBox getPatternsBox() {
        return patternsBox;
    }

    public VBox getSuggestionsBox() {
        return suggestionsBox;
    }

    public VBox getHistoryBox() {
        return historyBox;
    }

    public Button getCopyReportButton() {
        return copyReportButton;
    }

    public Button getCopyScoreButton() {
        return copyScoreButton;
    }

    public Button getCopySuggestionsButton() {
        return copySuggestionsButton;
    }

    public Slider getLengthSlider() {
        return lengthSlider;
    }

    public CheckBox getGeneratorUppercase() {
        return generatorUppercase;
    }

    public CheckBox getGeneratorLowercase() {
        return generatorLowercase;
    }

    public CheckBox getGeneratorNumbers() {
        return generatorNumbers;
    }

    public CheckBox getGeneratorSymbols() {
        return generatorSymbols;
    }

    public CheckBox getGeneratorExcludeAmbiguous() {
        return generatorExcludeAmbiguous;
    }

    public Button getGenerateButton() {
        return generateButton;
    }

    public Button getCopyGeneratedButton() {
        return copyGeneratedButton;
    }

    public TextField getGeneratedPasswordField() {
        return generatedPasswordField;
    }

    public Label getStatusLabel() {
        return statusLabel;
    }
}
