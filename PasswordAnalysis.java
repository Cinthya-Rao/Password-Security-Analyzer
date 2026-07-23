package model;

import java.util.List;

public record PasswordAnalysis(
        int score,
        StrengthLevel strengthLevel,
        PasswordStatistics statistics,
        EntropyLevel entropyLevel,
        String crackTimeEstimate,
        boolean commonPassword,
        PatternReport patternReport,
        List<ChecklistItem> checklist,
        List<String> suggestions,
        List<String> warnings
) {
    public String toSecurityReport() {
        StringBuilder report = new StringBuilder();
        report.append("Password Security Analyzer Report\n");
        report.append("=================================\n\n");
        report.append("Score: ").append(score).append("/100\n");
        report.append("Strength: ").append(strengthLevel.getLabel()).append("\n");
        report.append("Entropy: ").append(String.format("%.1f", statistics.entropyBits())).append(" bits (")
                .append(entropyLevel.getLabel()).append(")\n");
        report.append("Estimated brute-force time: ").append(crackTimeEstimate).append("\n\n");

        report.append("Statistics\n");
        report.append("- Length: ").append(statistics.length()).append("\n");
        report.append("- Uppercase letters: ").append(statistics.uppercaseCount()).append("\n");
        report.append("- Lowercase letters: ").append(statistics.lowercaseCount()).append("\n");
        report.append("- Numbers: ").append(statistics.numberCount()).append("\n");
        report.append("- Symbols: ").append(statistics.symbolCount()).append("\n");
        report.append("- Unique characters: ").append(statistics.uniqueCharacterCount()).append("\n");
        report.append("- Character variety: ").append(String.format("%.0f%%", statistics.characterVarietyPercent())).append("\n\n");

        if (!warnings.isEmpty()) {
            report.append("Warnings\n");
            warnings.forEach(warning -> report.append("- ").append(warning).append("\n"));
            report.append("\n");
        }

        report.append("Checklist\n");
        checklist.forEach(item -> report.append(item.passed() ? "[PASS] " : "[FAIL] ")
                .append(item.label()).append("\n"));
        report.append("\n");

        report.append("Recommendations\n");
        if (suggestions.isEmpty()) {
            report.append("- No critical improvements needed. Keep using unique passwords for every account.\n");
        } else {
            suggestions.forEach(suggestion -> report.append("- ").append(suggestion).append("\n"));
        }

        report.append("\nThis report intentionally excludes the password itself.");
        return report.toString();
    }
}
