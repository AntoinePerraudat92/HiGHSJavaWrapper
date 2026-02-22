package wrapper.model;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static wrapper.util.Constants.EPSILON;

class LinearExpressionTest {

    @Test
    void getNmbVariables() {
        final LinearExpression linearExpression = new LinearExpression();
        linearExpression.addVariable(new Variable(4), 1.0);
        linearExpression.addVariable(new Variable(12), 0.5);

        assertEquals(2, linearExpression.getNmbVariables());
    }

    @Test
    void minus() {
        final LinearExpression firstExpression = new LinearExpression(5.0);
        firstExpression.addVariable(new Variable(0), 1.0);
        firstExpression.addVariable(new Variable(1), 3.0);
        final LinearExpression secondExpression = new LinearExpression(3.0);
        secondExpression.addVariable(new Variable(0), 2.0);
        secondExpression.addVariable(new Variable(4), 1.0);

        final LinearExpression expression = firstExpression.minus(secondExpression);

        final Map<Variable, Double> computedCoefficientByVariable = new HashMap<>();
        expression.consumeVariables(computedCoefficientByVariable::put);
        assertEquals(Map.of(new Variable(0), -1.0, new Variable(1), 3.0, new Variable(4), -1.0), computedCoefficientByVariable);
        assertEquals(2.0, expression.getConstant(), EPSILON);
    }

}