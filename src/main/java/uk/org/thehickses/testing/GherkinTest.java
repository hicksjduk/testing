package uk.org.thehickses.testing;

import java.util.function.Consumer;

/**
 * A utility class to support the expression of unit tests in Gherkin-like syntax. To use it:
 * <ol>
 * <li>Create an instance of the class, initialising it with an appropriate test fixture.
 * <li>Express each test step using a lambda expression, and register it using one of the methods
 * {@link #given(TestStep)}, {@link #when(TestStep)}, {@link #then(TestStep)}, {@link #and(TestStep)},
 * {@link #but(TestStep)}. Note that all these methods are entirely interchangeable, being syntactic sugar for the
 * operation of adding a step to the test. <br>
 * It is <b>strongly</b> advised that the implementation of each test step be created in a method that clearly expresses
 * the intent of the step (as in the example below).
 * <li>Run the test by calling the {@link #runTest()} method.
 * </ol>
 * <p>
 * Example of a test that uses this package (from the test class for a number puzzle solver):
 * 
 * <pre>
 * // Create the test
 * new GherkinTest<>(new SolveTestFixture())
 *         // Steps to store prerequisite data in the test fixture
 *         .given(inputNumbersToSolveAre(50, 7, 4, 3, 2, 1))
 *         .and(targetNumberIs(378))
 *         // Step to run the function under test and store the result in the test fixture
 *         .when(solve())
 *         // Step to check the result
 *         .then(closestSolutionIs(378))
 *         // Run the test
 *         .runTest();
 * </pre>
 * 
 * @author Jeremy Hicks
 *
 * @param <T>
 *            the type of the test fixture that is passed to all test steps.
 */
public class GherkinTest<T>
{
    public final T testFixture;

    public GherkinTest(T testFixture)
    {
        this.testFixture = testFixture;
    }

    private Consumer<T> testSequence = null;

    public GherkinTest<T> given(TestStep<T> step)
    {
        return addStep(step);
    }

    public GherkinTest<T> when(TestStep<T> step)
    {
        return addStep(step);
    }

    public GherkinTest<T> then(TestStep<T> step)
    {
        return addStep(step);
    }

    public GherkinTest<T> and(TestStep<T> step)
    {
        return addStep(step);
    }

    public GherkinTest<T> but(TestStep<T> step)
    {
        return addStep(step);
    }

    private GherkinTest<T> addStep(TestStep<T> step)
    {
        Consumer<T> item = createTestSequenceItem(step);
        testSequence = testSequence == null ? item : testSequence.andThen(item);
        return this;
    }

    private Consumer<T> createTestSequenceItem(TestStep<T> step)
    {
        return fixture -> runStep(fixture, step);
    }

    private T runStep(T fixture, TestStep<T> step) throws TestException
    {
        try
        {
            step.run(fixture);
        }
        catch (Exception ex)
        {
            throw new TestException(ex);
        }
        return fixture;
    }

    public void runTest() throws Exception
    {
        try
        {
            testSequence.accept(testFixture);
        }
        catch (TestException ex)
        {
            throw (Exception) ex.getCause();
        }
    }

    @FunctionalInterface
    public static interface TestStep<T>
    {
        void run(T fixture) throws Exception;
    }

    @SuppressWarnings("serial")
    private static class TestException extends RuntimeException
    {
        public TestException(Exception cause)
        {
            super(cause);
        }
    }
}
