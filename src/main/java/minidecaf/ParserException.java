package minidecaf;

public class ParserException extends RuntimeException {

    public ParserException(String message, int line, int col) {
        this(message, line, col, null);
    }

    public ParserException(String message, int line, int col, Throwable cause) {
        throw new RuntimeException(
                "Parse Error (" + line + "," + col + "): " + message, cause);
    }
}
