package wrapper.model;


import org.junit.jupiter.api.Test;
import wrapper.solution.Solution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static wrapper.util.Constants.EPSILON;

class ModelVariableTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    @Test
    void addVariable() {
        final Model model = new Model();

        assertEquals(0, model.addContinuousVariable(14.2, 18.5, 15.0).getIndex());
        assertEquals(1, model.addIntegerVariable(0.0, 5.2, 1.0).getIndex());
        assertEquals(2, model.addBinaryVariable(0.0).getIndex());
    }

    @Test
    void updateVariableCostMustChangeObjectiveValue() {
        final Model model = new Model();
        model.addContinuousVariable(1.2, 18.5, 2.3);
        final Variable x2 = model.addContinuousVariable(0.0, 10.0, 1.0);

        final Solution firstSolution = model.maximize().orElseThrow();
        assertEquals(52.55, firstSolution.getObjectiveValue(), EPSILON);

        x2.updateCost(2.3);

        final Solution secondSolution = model.maximize().orElseThrow();
        assertEquals(65.55, secondSolution.getObjectiveValue(), EPSILON);
    }

    @Test
    void updateVariableBoundsMustChangeObjectiveValue() {
        final Model model = new Model();
        final Variable x1 = model.addContinuousVariable(1.0, 2.0, 1.0);

        final Solution firstSolution = model.minimize().orElseThrow();
        assertEquals(1.0, firstSolution.getObjectiveValue(), EPSILON);

        x1.updateBounds(15.0, 35.0);

        final Solution secondSolution = model.minimize().orElseThrow();
        assertEquals(15.0, secondSolution.getObjectiveValue(), EPSILON);
    }

}
