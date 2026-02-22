package wrapper.model;


import highs.DoubleVector;
import highs.HighsModelStatus;
import highs.HighsSolution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import wrapper.exceptions.ConstraintException;
import wrapper.exceptions.VariableException;

import static org.junit.jupiter.api.Assertions.*;
import static wrapper.util.Constants.EPSILON;

class SolutionTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    private static HighsModelStatus[] getInfeasibleModelStatuses() {
        return new HighsModelStatus[]{
                highs.HighsModelStatus.kNotset,
                HighsModelStatus.kLoadError,
                HighsModelStatus.kModelError,
                HighsModelStatus.kPresolveError,
                HighsModelStatus.kSolveError,
                HighsModelStatus.kPostsolveError,
                HighsModelStatus.kModelEmpty,
                HighsModelStatus.kInfeasible,
                HighsModelStatus.kUnboundedOrInfeasible,
                HighsModelStatus.kUnbounded,
                HighsModelStatus.kUnknown,
                HighsModelStatus.kMin,
                HighsModelStatus.kMax
        };
    }

    private static HighsModelStatus[] getFeasibleModelStatuses() {
        return new HighsModelStatus[]{highs.HighsModelStatus.kOptimal,
                HighsModelStatus.kObjectiveBound,
                HighsModelStatus.kObjectiveTarget,
                HighsModelStatus.kTimeLimit,
                HighsModelStatus.kIterationLimit,
                HighsModelStatus.kSolutionLimit,
                HighsModelStatus.kMemoryLimit,
                HighsModelStatus.kInterrupt};
    }

    @ParameterizedTest
    @MethodSource("getFeasibleModelStatuses")
    void isFeasibleMustBeTrue(final HighsModelStatus modelStatus) {
        final HighsSolution highsSolution = new HighsSolution();
        final Solution solution = new Solution(highsSolution, modelStatus, 0.0);

        assertTrue(solution.isFeasible());
    }

    @ParameterizedTest
    @MethodSource("getInfeasibleModelStatuses")
    void isFeasibleMustBeFalse(final HighsModelStatus modelStatus) {
        final HighsSolution highsSolution = new HighsSolution();
        final Solution solution = new Solution(highsSolution, modelStatus, 0.0);

        assertFalse(solution.isFeasible());
    }

    @Test
    void getVariableValue() throws VariableException {
        final HighsSolution highsSolution = new HighsSolution();
        highsSolution.setCol_value(new DoubleVector(new double[]{1.0, 5.4, 8.1}));
        final Solution solution = new Solution(highsSolution, HighsModelStatus.kOptimal, 0.0);

        assertEquals(1.0, solution.getVariableValue(MockObjectCreator.mockVariable(0)), EPSILON);
        assertEquals(5.4, solution.getVariableValue(MockObjectCreator.mockVariable(1)), EPSILON);
        assertEquals(8.1, solution.getVariableValue(MockObjectCreator.mockVariable(2)), EPSILON);
    }

    @Test
    void getVariableValueMustThrowForUnknownVariable() {
        final HighsSolution highsSolution = new HighsSolution();
        highsSolution.setCol_value(new DoubleVector(new double[]{1.0, 1.0, 10.1, 5.2}));
        final Solution solution = new Solution(highsSolution, HighsModelStatus.kIterationLimit, 0.0);
        final Variable unknownVariable = MockObjectCreator.mockVariable(7);

        final VariableException exception = assertThrows(VariableException.class, () -> solution.getVariableValue(unknownVariable));
        assertEquals("Variable with index 7 does not exist in the solution", exception.getMessage());
    }

    @Test
    void getDualValue() throws ConstraintException {
        final HighsSolution highsSolution = new HighsSolution();
        highsSolution.setRow_dual(new DoubleVector(new double[]{1.8, 6.9}));
        final Solution solution = new Solution(highsSolution, HighsModelStatus.kOptimal, 0.0);

        assertEquals(1.8, solution.getDualValue(MockObjectCreator.mockConstraint(0)), EPSILON);
        assertEquals(6.9, solution.getDualValue(MockObjectCreator.mockConstraint(1)), EPSILON);
    }

    @Test
    void getDualValueMustThrowForUnknownVariable() {
        final HighsSolution highsSolution = new HighsSolution();
        highsSolution.setRow_dual(new DoubleVector(new double[]{0.0, 3.0, 5.6}));
        final Solution solution = new Solution(highsSolution, HighsModelStatus.kTimeLimit, 0.0);
        final Constraint constraint = MockObjectCreator.mockConstraint(10);

        final ConstraintException exception = assertThrows(ConstraintException.class, () -> solution.getDualValue(constraint));
        assertEquals("Constraint with index 10 does not exist in the solution", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.1, -5.6, 14.5})
    void getObjectiveValue(final double objectiveValue) {
        final HighsSolution highsSolution = new HighsSolution();
        final Solution solution = new Solution(highsSolution, HighsModelStatus.kOptimal, objectiveValue);

        assertEquals(objectiveValue, solution.getObjectiveValue(), EPSILON);
    }

}