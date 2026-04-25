package wrapper.model;


import org.junit.jupiter.api.Test;
import wrapper.exceptions.VariableException;

import static org.junit.jupiter.api.Assertions.*;
import static wrapper.util.Constants.EPSILON;
import static wrapper.util.ObjectCreator.createModel;

class ModelVariableTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    @Test
    void addVariable() {
        final Model model = createModel();

        assertEquals(0, model.addContinuousVariable(14.2, 18.5, 15.0).getIndex());
        assertEquals(1, model.addIntegerVariable(0.0, 5.2, 1.0).getIndex());
        assertEquals(2, model.addBinaryVariable(0.0).getIndex());
    }

    @Test
    void check() {
        final Model firstModel = createModel();
        final Variable firstVariable = firstModel.addBinaryVariable(1.0);
        final Model secondModel = createModel();
        final Variable secondVariable = secondModel.addIntegerVariable(0.0, 12.0, 6.9);

        assertDoesNotThrow(() -> firstVariable.check(firstModel));
        assertDoesNotThrow(() -> secondVariable.check(secondModel));
        final VariableException firstException = assertThrows(VariableException.class, () -> firstVariable.check(secondModel));
        assertEquals("Trying to access or modify variable associated with wrong model", firstException.getMessage());
        final VariableException secondException = assertThrows(VariableException.class, () -> secondVariable.check(firstModel));
        assertEquals("Trying to access or modify variable associated with wrong model", secondException.getMessage());
        final VariableException thirdException = assertThrows(VariableException.class, () -> secondVariable.check(null));
        assertEquals("Trying to access or modify variable associated with wrong model", thirdException.getMessage());
    }

    @Test
    void updateVariableCostMustChangeObjectiveValue() {
        final Model model = createModel();
        model.addContinuousVariable(1.2, 18.5, 2.3);
        final Variable x2 = model.addContinuousVariable(0.0, 10.0, 1.0);

        final Solution firstSolution = model.maximize().orElseThrow();
        assertEquals(52.55, firstSolution.getObjectiveValue(), EPSILON);

        x2.updateCost(2.3);

        final Solution secondSolution = model.maximize().orElseThrow();
        assertEquals(65.55, secondSolution.getObjectiveValue(), EPSILON);
    }

    @Test
    void updateVariableCostMustThrowIfModelGCed() {
        final Variable x = new Variable(9);

        final VariableException exception = assertThrows(VariableException.class, () -> x.updateCost(1.8));
        assertEquals("Related model does not exist", exception.getMessage());
    }

    @Test
    void updateVariableBoundsMustChangeObjectiveValue() {
        final Model model = createModel();
        final Variable x1 = model.addContinuousVariable(1.0, 2.0, 1.0);

        final Solution firstSolution = model.minimize().orElseThrow();
        assertEquals(1.0, firstSolution.getObjectiveValue(), EPSILON);

        x1.updateBounds(15.0, 35.0);

        final Solution secondSolution = model.minimize().orElseThrow();
        assertEquals(15.0, secondSolution.getObjectiveValue(), EPSILON);
    }

}
