package uj.wmii.pwj.anns;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MyTestEngine {

    private final String className;
    private final List<TestReport> allReports = new ArrayList<>();

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please specify test class name");
            System.exit(-1);
        }
        String className = args[0].trim();
        MyTestEngine engine = new MyTestEngine(className);
        engine.runTests();
    }

    public MyTestEngine(String className) {
        this.className = className;
    }

    public void runTests() {
        printAsciiArt();
        final Object unit = getObject(className);
        List<Method> testMethods = getTestMethods(unit);


        int totalTests = 0;
        for (Method m : testMethods) {
            MyTest annotation = m.getAnnotation(MyTest.class);
            int testRuns = Math.max(1, annotation.params().length);
            System.out.printf("  - %s: %d test runs scheduled.\n", m.getName(), testRuns);
            totalTests += testRuns;
        }
        System.out.printf("Total tests to run: %d\n", totalTests);

        int executedTests = 0;
        for (Method m : testMethods) {
            List<TestReport> reports = launchSingleMethod(m, unit);
            allReports.addAll(reports);
            executedTests += reports.size();
            System.out.printf("  (Progress: %d/%d executed)\r", executedTests, totalTests);
        }
        System.out.println("\n Test Execution Finished");

        displayTestResults();
    }



    private List<TestReport> launchSingleMethod(Method m, Object unit) {
        List<TestReport> reports = new ArrayList<>();
        MyTest annotation = m.getAnnotation(MyTest.class);
        String[] params = annotation.params();
        String[] expectedResults = annotation.expectedResults();


        if (params.length == 0) {
            reports.add(runSingleTest(m, unit, null, expectedResults.length > 0 ? expectedResults[0] : null));
        } else {

            for (int i = 0; i < params.length; i++) {
                String param = params[i];
                String expected = (i < expectedResults.length) ? expectedResults[i] : null;
                reports.add(runSingleTest(m, unit, param, expected));
            }
        }
        return reports;
    }

    private TestReport runSingleTest(Method m, Object unit, String param, String expected) {
        String currentTestName = m.getName() + (param != null ? " with param: '" + param + "'" : "");
        String message = "";
        TestStatus status;
        Object result = null;

        try {
            if (param == null) {
                result = m.invoke(unit);
            } else {
                result = m.invoke(unit, param);
            }

            if (m.getReturnType() != void.class && expected != null) {
                String actualResult = result != null ? result.toString() : "null";
                if (actualResult.equals(expected)) {
                    status = TestStatus.PASS;
                } else {
                    status = TestStatus.FAIL;
                    message = String.format("Expected: '%s', Actual: '%s'", expected, actualResult);
                }
            } else {
                status = TestStatus.PASS;
            }
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Exception) {
                status = TestStatus.ERROR;
                message = cause.getClass().getSimpleName() + ": " + cause.getMessage();
            } else {
                status = TestStatus.ERROR;
                message = "Unexpected serious error: " + cause.getClass().getSimpleName();
            }
        } catch (Exception e) {
            status = TestStatus.ERROR;
            message = "Reflection Error: " + e.getClass().getSimpleName();
        }

        return new TestReport(m, param, status, message);
    }

    private void displayTestResults() {
        System.out.println("\n--- Final Test Results ---");

        long passCount = allReports.stream().filter(r -> r.getStatus() == TestStatus.PASS).count();
        long failCount = allReports.stream().filter(r -> r.getStatus() == TestStatus.FAIL).count();
        long errorCount = allReports.stream().filter(r -> r.getStatus() == TestStatus.ERROR).count();
        long totalCount = allReports.size();

        System.out.printf("Total Tests Run: %d\n", totalCount);
        System.out.printf(" PASS: %d\n", passCount);
        System.out.printf(" FAIL: %d\n", failCount);
        System.out.printf(" 🚨ERROR: %d\n", errorCount);
        System.out.println("--------------------------------\n");

        if (failCount > 0 || errorCount > 0) {
            System.out.println("--- 📄 Detailed Failure/Error Reports ---");
            allReports.stream()
                    .filter(r -> r.getStatus() != TestStatus.PASS)
                    .forEach(System.out::println);
            System.out.println("----------------------------------------\n");
        }

    }

    private static List<Method> getTestMethods(Object unit) {
        Method[] methods = unit.getClass().getDeclaredMethods();
        return Arrays.stream(methods)
                .filter(m -> m.getAnnotation(MyTest.class) != null)
                .collect(Collectors.toList());
    }

    private static Object getObject(String className) {
        try {
            Class<?> unitClass = Class.forName(className);
            return unitClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            System.err.println("Could not instantiate class: " + className);
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    private void printAsciiArt() {
        System.out.println(
                " ______         _   _                   \n"
                        + "|  ____|       | | | |                  \n"
                        + "| |__ ___ _ __ | |_| | ___   ___  _ __  \n"
                        + "|  __/ _ \\ '_ \\| __| |/ _ \\ / _ \\| '_ \\ \n"
                        + "| | |  __/ | | | |_| | (_) | (_) | | | |\n"
                        + "|_|  \\___|_| |_|\\__|_|\\___/ \\___/|_| |_|\n"
                        + "                                       \n"
        );
    }
}