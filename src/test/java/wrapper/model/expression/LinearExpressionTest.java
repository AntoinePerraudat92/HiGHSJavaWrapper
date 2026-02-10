package wrapper.model.expression;

import org.junit.jupiter.api.Test;
import wrapper.model.variable.Variable;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static wrapper.util.Constants.EPSILON;

class LinearExpressionTest {

    @Test
    void getNmbCoefficients() throws LinearExpressionException {
        final LinearExpression linearExpression = new LinearExpression();
        linearExpression.addNewVariable(new Variable(4), 1.0);
        linearExpression.addNewVariable(new Variable(12), 0.5);

        assertEquals(2, linearExpression.getNmbCoefficients());
    }

    @Test
    void addNewVariableMustThrowIfSameVariableIndexAddedTwice() throws LinearExpressionException {
        final LinearExpression linearExpression = new LinearExpression();
        linearExpression.addNewVariable(new Variable(99), 1.0);

        final LinearExpressionException exception = assertThrows(LinearExpressionException.class, () -> linearExpression.addNewVariable(new Variable(99), 1.2));
        assertEquals("Variable with index 99 is already in linear expression", exception.getMessage());
    }

    @Test
    void minus() throws LinearExpressionException {
        final LinearExpression firstExpression = new LinearExpression(5.0);
        firstExpression.addNewVariable(new Variable(0), 1.0);
        firstExpression.addNewVariable(new Variable(1), 3.0);
        final LinearExpression secondExpression = new LinearExpression(3.0);
        secondExpression.addNewVariable(new Variable(0), 2.0);
        secondExpression.addNewVariable(new Variable(4), 1.0);

        final LinearExpression expression = firstExpression.minus(secondExpression);

        final Map<Variable, Double> computedCoefficientByVariable = new HashMap<>();
        expression.consumeExpression(expressionCoefficient -> computedCoefficientByVariable.put(expressionCoefficient.variable(), expressionCoefficient.value()));
        assertEquals(Map.of(new Variable(0), -1.0, new Variable(1), 3.0, new Variable(4), -1.0), computedCoefficientByVariable);
        assertEquals(2.0, expression.getConstant(), EPSILON);
    }

}