package utils;

public class ResultHelper {

    private boolean status;

    private ResultHelper(boolean status) {
        this.status=status;
    }

    public static ResultHelper createSuccessfulResult() {
        return new ResultHelper(true);
    }

    public static ResultHelper createFailedResult() {
        return new ResultHelper(false);
    }

    public static ResultHelper createResult(boolean result) {
        return new ResultHelper(result);
    }

    public ResultHelper and(ResultHelper other) {
        return new ResultHelper(this.status & other.isSuccessful());
    }

    public boolean isSuccessful() {
        return status;
    }

}
