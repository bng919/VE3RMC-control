package utils.enums;

public enum Verbosity {
    DEBUG(3),
    INFO(2),
    WARN(1),
    ERROR(0);

    private int printLevel;

    Verbosity(int printLevel) {
        this.printLevel = printLevel;
    }

    public boolean isEqOrHigher(Verbosity other) {
        return this.printLevel >= other.printLevel;
    }
}