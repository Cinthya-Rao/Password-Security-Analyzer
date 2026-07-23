package model;

public enum StrengthLevel {
    VERY_WEAK("Very Weak", "strength-very-weak"),
    WEAK("Weak", "strength-weak"),
    FAIR("Fair", "strength-fair"),
    STRONG("Strong", "strength-strong"),
    VERY_STRONG("Very Strong", "strength-very-strong");

    private final String label;
    private final String cssClass;

    StrengthLevel(String label, String cssClass) {
        this.label = label;
        this.cssClass = cssClass;
    }

    public String getLabel() {
        return label;
    }

    public String getCssClass() {
        return cssClass;
    }
}
