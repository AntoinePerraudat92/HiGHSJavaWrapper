package wrapper.model;


import highs.HighsModelStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

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
        final Solution solution = new Solution(modelStatus, 0.0);

        assertTrue(solution.isFeasible());
    }

    @ParameterizedTest
    @MethodSource("getInfeasibleModelStatuses")
    void isFeasibleMustBeFalse(final HighsModelStatus modelStatus) {
        final Solution solution = new Solution(modelStatus, 0.0);

        assertFalse(solution.isFeasible());
    }


    @ParameterizedTest
    @ValueSource(doubles = {0.1, -5.6, 14.5})
    void getObjectiveValue(final double objectiveValue) {
        final Solution solution = new Solution(HighsModelStatus.kOptimal, objectiveValue);

        assertEquals(objectiveValue, solution.getObjectiveValue(), EPSILON);
    }

}