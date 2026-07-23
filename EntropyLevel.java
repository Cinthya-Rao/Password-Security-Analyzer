package model;

public enum EntropyLevel {
    LOW("Low entropy", "Easy to guess or crack with basic automated attacks."),
    MEDIUM("Medium entropy", "Better than simple passwords, but still risky for important accounts."),
    HIGH("High entropy", "A strong password for most everyday use cases."),
    VERY_HIGH("Very high entropy", "Excellent complexity for long-term account protection.");

    private final String label;
    private final String explanation;

    EntropyLevel(String label, String explanation) {
        this.label = label;
        this.explanation = explanation;
    }

    public String getLabel() {
        return label;
    }

    public String getExplanation() {
        return explanation;
    }
}
