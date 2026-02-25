package wrapper.model;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static wrapper.util.Constants.EPSILON;

class LinearExpressionTest {

    @Test
    void getNmbVariables() {
        final LinearExpression linearExpression = new LinearExpression();
        linearExpression.addVariable(MockObjectCreator.createVariable(4), 1.0);
        linearExpression.addVariable(MockObjectCreator.createVariable(12), 0.5);

        assertEquals(2, linearExpression.getNmbVariables());
    }

    @Test
    void minus() {
        final LinearExpression firstExpression = new LinearExpression(5.0);
        firstExpression.addVariable(MockObjectCreator.createVariable(0), 1.0);
        firstExpression.addVariable(MockObjectCreator.createVariable(1), 3.0);
        final LinearExpression secondExpression = new LinearExpression(3.0);
        secondExpression.addVariable(MockObjectCreator.createVariable(0), 2.0);
        secondExpression.addVariable(MockObjectCreator.createVariable(4), 1.0);

        final LinearExpression expression = firstExpression.minus(secondExpression);

        final Map<Long, Double> computedCoefficients = new TreeMap<>();
        expression.consumeVariables((variable, value) -> computedCoefficients.put(variable.getIndex(), value));
        final Map<Long, Double> expectedCoefficient = new TreeMap<>(Map.of(0L, -1.0, 1L, 3.0, 4L, -1.0));
        assertEquals(expectedCoefficient, computedCoefficients);
        assertEquals(2.0, expression.getConstant(), EPSILON);
    }

}