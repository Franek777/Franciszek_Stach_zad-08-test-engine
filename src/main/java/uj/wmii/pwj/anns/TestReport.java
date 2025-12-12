package uj.wmii.pwj.anns;

import java.lang.reflect.Method;

public class TestReport {
    private final Method method;
    private final String param; // null dla testów bez parametrów
    private final TestStatus status;
    private final String message; // np. szczegóły błędu/wyjątku lub porównanie wyników

    public TestReport(Method method, String param, TestStatus status, String message) {
        this.method = method;
        this.param = param;
        this.status = status;
        this.message = message;
    }

    public Method getMethod() { return method; }
    public String getParam() { return param; }
    public TestStatus getStatus() { return status; }
    public String getMessage() { return message; }

    // Dodatkowa metoda do wyświetlania wyniku
    @Override
    public String toString() {
        String testName = method.getName() + (param != null ? " (" + param + ")" : "()");
        String result = String.format("[%s] %s", status.toString(), testName);
        if (message != null && !message.isEmpty()) {
            result += " - " + message;
        }
        return result;
    }
}