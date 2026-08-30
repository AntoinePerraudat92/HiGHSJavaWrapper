package wrapper.model;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static wrapper.util.Constants.EPSILON;
import static wrapper.util.ObjectCreator.createModel;

class LinearExpressionTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    @Test
    void getNmbVariables() {
        final Model model = createModel();

        final LinearExpression linearExpression = new LinearExpression();
        linearExpression.addVariable(model.addBinaryVariable(2.1), 1.0);
        linearExpression.addVariable(model.addBinaryVariable(0.91), 0.5);

        assertEquals(2, linearExpression.getNmbVariables());
    }

    @Test
    void merge() {
        final Model model = createModel();
        final Variable x1 = model.addSemicontinuousVariable(0.0, 15.0, 7.2);
        final Variable x2 = model.addBinaryVariable(5.6);
        model.addContinuousVariable(0.0, 7.0, 10.4);
        model.addContinuousVariable(0.0, 2.0, 9.9);
        final Variable x5 = model.addIntegerVariable(0.0, 100.0, 0.7);

        final LinearExpression firstExpression = new LinearExpression(5.0);
        firstExpression.addVariable(x1, 1.0);
        firstExpression.addVariable(x2, 3.0);
        final LinearExpression secondExpression = new LinearExpression(3.0);
        secondExpression.addVariable(x1, 2.0);
        secondExpression.addVariable(x5, 1.0);

        final LinearExpression expression = firstExpression.merge(secondExpression);

        final Map<Long, Double> computedCoefficients = new HashMap<>();
        expression.consumeVariables((variable, value) -> computedCoefficients.put(variable.getIndex(), value));
        final Map<Long, Double> expectedCoefficient = Map.of(0L, -1.0, 1L, 3.0, 4L, -1.0);
        assertEquals(expectedCoefficient, computedCoefficients);
        assertEquals(2.0, expression.getConstant(), EPSILON);
    }

}