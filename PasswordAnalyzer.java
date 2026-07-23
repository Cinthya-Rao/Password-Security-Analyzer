package service;

import model.ChecklistItem;
import model.EntropyLevel;
import model.PatternReport;
import model.PasswordAnalysis;
import model.PasswordStatistics;
import model.StrengthLevel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PasswordAnalyzer {
    private final EntropyCalculator entropyCalculator = new EntropyCalculator();
    private final DictionaryChecker dictionaryChecker = new DictionaryChecker();
    private final PatternDetector patternDetector = new PatternDetector();
    private final PasswordScoreCalculator scoreCalculator = new PasswordScoreCalculator();
    private final SuggestionGenerator suggestionGenerator = new SuggestionGenerator();

    public PasswordAnalysis analyze(String password) {
        String value = password == null ? "" : password;
        PasswordStatistics statistics = buildStatistics(value);
        PatternReport patterns = patternDetector.detect(value);
        boolean commonPassword = dictionaryChecker.isCommonPassword(value);
        boolean containsCommonWord = dictionaryChecker.containsCommonWord(value);
        int score = scoreCalculator.calculateScore(value, statistics, patterns, commonPassword, containsCommonWord);
        StrengthLevel strengthLevel = scoreCalculator.classify(score);
        EntropyLevel entropyLevel = entropyCalculator.classify(statistics.entropyBits());
        String crackTime = entropyCalculator.estimateCrackTime(statistics.entropyBits());
        List<ChecklistItem> checklist = buildChecklist(statistics, patterns, containsCommonWord);
        List<String> suggestions = suggestionGenerator.generate(statistics, patterns, commonPassword, containsCommonWord);
        List<String> warnings = buildWarnings(commonPassword, patterns);

        return new PasswordAnalysis(score, strengthLevel, statistics, entropyLevel, crackTime, commonPassword,
                patterns, checklist, suggestions, warnings);
    }

    private PasswordStatistics buildStatistics(String password) {
        int uppercase = 0;
        int lowercase = 0;
        int numbers = 0;
        int symbols = 0;
        Set<Integer> uniqueCharacters = new LinkedHashSet<>();

        for (int index = 0; index < password.length(); index++) {
            int character = password.charAt(index);
            uniqueCharacters.add(character);
            if (Character.isUpperCase(character)) {
                uppercase++;
            } else if (Character.isLowerCase(character)) {
                lowercase++;
            } else if (Character.isDigit(character)) {
                numbers++;
            } else {
                symbols++;
            }
        }

        double entropy = entropyCalculator.calculateEntropy(password);
        double variety = password.isEmpty() ? 0 : (uniqueCharacters.size() * 100.0) / password.length();
        return new PasswordStatistics(password.length(), uppercase, lowercase, numbers, symbols,
                uniqueCharacters.size(), entropy, variety);
    }

    private List<ChecklistItem> buildChecklist(PasswordStatistics statistics, PatternReport patterns,
                                               boolean containsCommonWord) {
        return List.of(
                new ChecklistItem("At least 12 characters", statistics.length() >= 12),
                new ChecklistItem("Contains uppercase letters", statistics.uppercaseCount() > 0),
                new ChecklistItem("Contains lowercase letters", statistics.lowercaseCount() > 0),
                new ChecklistItem("Contains numbers", statistics.numberCount() > 0),
                new ChecklistItem("Contains symbols", statistics.symbolCount() > 0),
                new ChecklistItem("No repeated patterns", !patterns.repeatedCharacters() && !patterns.repeatedWords()),
                new ChecklistItem("No common words", !containsCommonWord),
                new ChecklistItem("No obvious sequences", !patterns.sequentialLetters()
                        && !patterns.sequentialNumbers() && !patterns.keyboardWalk())
        );
    }

    private List<String> buildWarnings(boolean commonPassword, PatternReport patterns) {
        List<String> warnings = new ArrayList<>();
        if (commonPassword) {
            warnings.add("This password is commonly used and should be changed immediately.");
        }
        if (patterns.dateLike()) {
            warnings.add("The password looks like it may contain a date.");
        }
        if (patterns.phoneLike()) {
            warnings.add("The password resembles a phone number.");
        }
        return List.copyOf(warnings);
    }
}
