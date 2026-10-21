package fake.lang;

/**
 * TeaVM classlib lacks java.lang.ExceptionInInitializerError. Referenced by
 * blade-ink's Json static completeness check (never actually thrown there).
 */
public class ExceptionInInitializerError extends Error {

    public ExceptionInInitializerError() {
    }

    public ExceptionInInitializerError(String message) {
        super(message);
    }

    public ExceptionInInitializerError(Throwable throwable) {
        super(throwable);
    }
}
