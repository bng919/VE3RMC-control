package utils;

public class ResultUtils {

    private final boolean status;

    private ResultUtils(boolean status) {
        this.status=status;
    }

    public static ResultUtils createSuccessfulResult() {
        return new ResultUtils(true);
    }

    public static ResultUtils createFailedResult() {
        return new ResultUtils(false);
    }

    public static ResultUtils createResult(boolean result) {
        return new ResultUtils(result);
    }

    public ResultUtils and(ResultUtils other) {
        return new ResultUtils(this.status & other.isSuccessful());
    }

    public boolean isSuccessful() {
        return status;
    }

}
