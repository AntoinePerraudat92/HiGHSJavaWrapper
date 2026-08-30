package wrapper.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static wrapper.util.Constants.EPSILON;
import static wrapper.util.ObjectCreator.createModel;

class ModelSolveTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    @Test
    void minimize() {
        final Model model = createModel();
        model.addContinuousVariable(1.2, 7.0, 1.0);
        model.addContinuousVariable(0.5, 4.0, 1.0);

        final Solution solution = model.minimize().orElseThrow();

        assertTrue(solution.isFeasible());
        assertEquals(1.7, solution.getObjectiveValue(), EPSILON);
    }

    @Test
    void maximize() {
        final Model model = createModel();
        model.addContinuousVariable(0.0, 3.0, 1.0);
        model.addContinuousVariable(0.0, 2.9, 1.0);

        final Solution solution = model.maximize().orElseThrow();

        assertTrue(solution.isFeasible());
        assertEquals(5.9, solution.getObjectiveValue(), EPSILON);
    }

    @Test
    void maximizeWithSimpleConstraint() {
        final Model model = createModel();
        final LinearExpression linearExpression = LinearExpression.of(
                new LinearExpression.Term(
                        model.addContinuousVariable(
                                0.0,
                                Double.POSITIVE_INFINITY,
                                5.5
                        ), 1.0
                ),
                new LinearExpression.Term(model.addContinuousVariable(0.5, Double.POSITIVE_INFINITY, 1.0), 1.0)
        );
        model.addEqualityConstraint(linearExpression, 1.0);

        final Solution solution = model.maximize().orElseThrow();

        assertTrue(solution.isFeasible());
        assertEquals(3.25, solution.getObjectiveValue(), EPSILON);
    }

    @Test
    void maximizeWithThreeConstraints() {
        final Model model = createModel();
        final Variable x1 = model.addContinuousVariable(0.0, 1.0, 1.0);
        final Variable x2 = model.addContinuousVariable(0.0, 12.0, 5.0);
        final Variable x3 = model.addContinuousVariable(0.0, 5.0, 14.0);
        model.addLessThanOrEqualToConstraint(
                LinearExpression.of(
                        new LinearExpression.Term(x1, 0.5),
                        new LinearExpression.Term(x3, 14.0)
                ), 7.0
        );
        model.addEqualityConstraint(
                LinearExpression.of(
                        new LinearExpression.Term(x2, 12.4),
                        new LinearExpression.Term(x3, 0.2)
                ), 2.0
        );

        final Solution solution = model.maximize().orElseThrow();

        assertTrue(solution.isFeasible());
        assertEquals(8.2690092166, solution.getObjectiveValue(), EPSILON);
    }

    @Test
    void maximizeWithBinaryVariables() {
        final Model model = createModel();
        final Variable x1 = model.addBinaryVariable(1.2);
        final Variable x2 = model.addBinaryVariable(1.3);
        model.addLessThanOrEqualToConstraint(
                LinearExpression.of(
                        new LinearExpression.Term(x1, 1.0),
                        new LinearExpression.Term(x2, 1.0)
                ), 1.2
        );

        final Solution solution = model.maximize().orElseThrow();

        assertTrue(solution.isFeasible());
        assertEquals(1.3, solution.getObjectiveValue(), EPSILON);
    }

    @Test
    void maximizeMustFailDueToInfeasibilityOnIntegralityConstraints() {
        final Model model = createModel();
        final Variable x1 = model.addBinaryVariable(1.0);
        final Variable x2 = model.addBinaryVariable(1.0);
        model.addEqualityConstraint(
                LinearExpression.of(
                        new LinearExpression.Term(x1, 1.0),
                        new LinearExpression.Term(x2, 1.0)
                ), 1.5
        );

        final Solution solution = model.maximize().orElseThrow();

        assertFalse(solution.isFeasible());
    }

    @Test
    void binaryVariablesMustHaveTheExpectedValues() {
        final Model model = createModel();
        final Variable x1 = model.addIntegerVariable(0.0, 3.0, 2.0);
        final Variable x2 = model.addIntegerVariable(0.0, Double.POSITIVE_INFINITY, 8.0);
        final Variable x3 = model.addIntegerVariable(2.0, Double.POSITIVE_INFINITY, 15.0);
        model.addGreaterThanOrEqualToConstraint(
                LinearExpression.of(
                        new LinearExpression.Term(x1, 0.5),
                        new LinearExpression.Term(x2, 1.0),
                        new LinearExpression.Term(x3, 1.0)
                ), 4.5
        );

        final Solution solution = model.minimize().orElseThrow();

        assertTrue(solution.isFeasible());
        assertEquals(3.0, x1.getValue(), EPSILON);
        assertEquals(1.0, x2.getValue(), EPSILON);
        assertEquals(2.0, x3.getValue(), EPSILON);
    }

    @Test
    void successiveCallsToSolverMustLeadToDifferentSolutions() {
        final Model model = createModel();
        final Variable x1 = model.addContinuousVariable(0.0, Double.POSITIVE_INFINITY, 2.0);
        final Variable x2 = model.addIntegerVariable(0.0, Double.POSITIVE_INFINITY, 1.0);
        model.addLessThanOrEqualToConstraint(
                LinearExpression.of(
                        new LinearExpression.Term(x1, 1.0),
                        new LinearExpression.Term(x2, 1.0)
                ), 5.0
        );

        final Solution firstSolution = model.maximize().orElseThrow();
        assertTrue(firstSolution.isFeasible());
        assertEquals(10.0, firstSolution.getObjectiveValue(), EPSILON);
        assertEquals(5.0, x1.getValue(), EPSILON);
        assertEquals(0.0, x2.getValue(), EPSILON);

        final Variable x3 = model.addIntegerVariable(1.0, Double.POSITIVE_INFINITY, 1.0);
        model.addEqualityConstraint(
                LinearExpression.of(
                        new LinearExpression.Term(x1, 1.0),
                        new LinearExpression.Term(x2, 1.0),
                        new LinearExpression.Term(x3, 1.0)
                ), 3.0
        );

        final Solution secondSolution = model.maximize().orElseThrow();
        assertTrue(secondSolution.isFeasible());
        assertEquals(5.0, secondSolution.getObjectiveValue(), EPSILON);
        assertEquals(2.0, x1.getValue(), EPSILON);
        assertEquals(0.0, x2.getValue(), EPSILON);
        assertEquals(1.0, x3.getValue(), EPSILON);
    }

    @Test
    void successiveCallsToSolverWithoutModelChangeMustLeadToSameSolutions() {
        final Model model = createModel();
        final Variable x1 = model.addContinuousVariable(0.0, Double.POSITIVE_INFINITY, 2.0);
        final Variable x2 = model.addIntegerVariable(0.0, Double.POSITIVE_INFINITY, 1.0);
        model.addLessThanOrEqualToConstraint(
                LinearExpression.of(
                        new LinearExpression.Term(x1, 1.0),
                        new LinearExpression.Term(x2, 1.0)
                ), 5.0
        );

        final Solution firstSolution = model.maximize().orElseThrow();
        assertTrue(firstSolution.isFeasible());
        assertEquals(10.0, firstSolution.getObjectiveValue(), EPSILON);

        final Solution secondSolution = model.maximize().orElseThrow();
        assertTrue(secondSolution.isFeasible());
        assertEquals(10.0, secondSolution.getObjectiveValue(), EPSILON);
    }

    @Test
    void maximizeWithConstraintsUsingLinearExpressionsForBothSides() {
        final Model model = createModel();
        final Variable x1 = model.addIntegerVariable(0.0, Double.POSITIVE_INFINITY, 0.0);
        final Variable x2 = model.addIntegerVariable(0.0, Double.POSITIVE_INFINITY, 0.0);
        final Variable x3 = model.addIntegerVariable(0.0, Double.POSITIVE_INFINITY, 1.0);
        model.addEqualityConstraint(
                LinearExpression.of(new LinearExpression.Term(x1, 3.0), new LinearExpression.Term(x2, 1.0)),
                LinearExpression.of(2.0, new LinearExpression.Term(x3, 1.0))
        );
        model.addLessThanOrEqualToConstraint(
                LinearExpression.of(new LinearExpression.Term(x1, 1.0), new LinearExpression.Term(x2, 1.0)),
                LinearExpression.of(1.0)
        );

        final Solution solution = model.maximize().orElseThrow();

        assertTrue(solution.isFeasible());
        assertEquals(1.0, x3.getValue(), EPSILON);
    }

    @Test
    void minimizeWithConstraintsUsingLinearExpressionsForBothSides() {
        final Model model = createModel();
        final Variable x1 = model.addIntegerVariable(0.0, Double.POSITIVE_INFINITY, 0.0);
        final Variable x2 = model.addIntegerVariable(0.0, Double.POSITIVE_INFINITY, 0.0);
        model.addGreaterThanOrEqualToConstraint(
                LinearExpression.of(5.0, new LinearExpression.Term(x2, 1.0)),
                LinearExpression.of(10.0, new LinearExpression.Term(x1, 1.0))
        );

        final Solution solution = model.minimize().orElseThrow();

        assertTrue(solution.isFeasible());
        assertEquals(0.0, x1.getValue(), EPSILON);
        assertEquals(5.0, x2.getValue(), EPSILON);
    }

    @Test
    void maximizeWithInfiniteCostMustGiveFeasibleSolution() {
        final Model model = createModel();
        model.addBinaryVariable(Double.POSITIVE_INFINITY);

        final Solution solution = model.maximize().orElseThrow();

        assertTrue(solution.isFeasible());
    }

    @Test
    void unboundedModelMustGiveInfeasibleSolution() {
        final Model model = createModel();
        model.addContinuousVariable(0.0, Double.POSITIVE_INFINITY, 1.0);

        final Solution solution = model.maximize().orElseThrow();

        assertFalse(solution.isFeasible());
    }

}
