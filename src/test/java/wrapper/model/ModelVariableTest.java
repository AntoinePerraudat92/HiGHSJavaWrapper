package wrapper.model;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertEquals(3, model.addSemicontinuousVariable(2.0, 7.0, 8.0).getIndex());
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
    void updateVariableBoundsMustChangeObjectiveValue() {
        final Model model = createModel();
        final Variable x1 = model.addContinuousVariable(1.0, 2.0, 1.0);

        final Solution firstSolution = model.minimize().orElseThrow();
        assertEquals(1.0, firstSolution.getObjectiveValue(), EPSILON);

        x1.updateBounds(15.0, 35.0);

        final Solution secondSolution = model.minimize().orElseThrow();
        assertEquals(15.0, secondSolution.getObjectiveValue(), EPSILON);
    }

    @Test
    void requestingVariableValuesForInfeasibleModelMustBePossible() {
        final Model model = createModel();
        final Variable x1 = model.addBinaryVariable(1.0);
        final Variable x2 = model.addBinaryVariable(1.0);
        final Variable x3 = model.addBinaryVariable(1.0);
        model.addEqualityConstraint(
                LinearExpression.of(
                        new LinearExpression.Term(x1, 1.0),
                        new LinearExpression.Term(x2, 1.0),
                        new LinearExpression.Term(x3, 1.0)
                ), 5.0
        );

        model.maximize().orElseThrow();

        assertDoesNotThrow(x1::getValue);
        assertDoesNotThrow(x2::getValue);
        assertDoesNotThrow(x3::getValue);
    }

}
