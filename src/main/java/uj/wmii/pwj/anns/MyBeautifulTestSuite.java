package uj.wmii.pwj.anns;

public class MyBeautifulTestSuite {

    @MyTest
    public void testAlwaysPass() {
        System.out.println("-> Test: testAlwaysPass");
    }

    @MyTest
    public String testShouldFail() {
        System.out.println("-> Test: testShouldFail (returning 'B', expecting 'A')");
        return "B";
    }

    @MyTest(expectedResults = "4")
    public String testAdditionPass() {
        System.out.println("-> Test: testAdditionPass (2+2)");
        return String.valueOf(2 + 2);
    }

    @MyTest(expectedResults = "5")
    public String testAdditionFail() {
        System.out.println("-> Test: testAdditionFail (2+2)");
        return String.valueOf(2 + 2);
    }

    @MyTest
    public void testThrowsError() {
        System.out.println("-> Test: testThrowsError");
        throw new NullPointerException("Simulated Exception");
    }

    @MyTest(params = {"1", "2", "3"},
            expectedResults = {"2", "4", "6"})
    public String testWithParam(String param) {
        int val = Integer.parseInt(param);
        System.out.printf("-> Test: testWithParam invoked with: %s\n", param);
        return String.valueOf(val * 2);
    }

    @MyTest(params = {"A", "B"}, expectedResults = {"A", "C"})
    public String testParamFail(String param) {
        System.out.printf("-> Test: testParamFail invoked with: %s\n", param);
        return param;
    }

    public void notATest() {
        System.out.println("I'm not a test and should be ignored.");
    }
}