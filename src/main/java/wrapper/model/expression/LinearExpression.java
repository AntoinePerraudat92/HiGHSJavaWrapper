package wrapper.model.expression;

import lombok.Getter;
import lombok.NonNull;
import wrapper.model.variable.Variable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public class LinearExpression {

    @Getter
    private final double constant;
    private final Map<Variable, ExpressionCoefficient> coefficients = new HashMap<>();

    public LinearExpression() {
        this(0.0);
    }

    public LinearExpression(double constant) {
        this.constant = constant;
    }

    public static LinearExpression of(final ExpressionCoefficient... coefficients) throws LinearExpressionException {
        return LinearExpression.of(0.0, coefficients);
    }

    public static LinearExpression of(double constant, final ExpressionCoefficient... coefficients) throws LinearExpressionException {
        final LinearExpression expression = new LinearExpression(constant);
        for (final ExpressionCoefficient coefficient : coefficients) {
            expression.addNewVariable(coefficient.variable(), coefficient.value());
        }
        return expression;
    }

    public void consumeExpression(@NonNull final Consumer<ExpressionCoefficient> consumer) {
        this.coefficients.values().forEach(consumer);
    }

    public void addNewVariable(@NonNull final Variable variable, double coefficient) throws LinearExpressionException {
        if (this.coefficients.containsKey(variable)) {
            throw new LinearExpressionException(String.format("Variable with index %d is already in linear expression", variable.index()));
        }
        addVariable(variable, coefficient);
    }

    public LinearExpression minus(@NonNull final LinearExpression otherExpression) {
        final LinearExpression newLinearExpression = new LinearExpression(constant - otherExpression.constant);
        consumeExpression(expressionCoefficient -> newLinearExpression.addVariable(expressionCoefficient.variable(), expressionCoefficient.value()));
        for (final ExpressionCoefficient expressionCoefficient : otherExpression.coefficients.values()) {
            final Variable variable = expressionCoefficient.variable();
            double coefficient = expressionCoefficient.value();
            newLinearExpression.coefficients.computeIfPresent(variable, (_, otherExpressionCoefficient) -> new ExpressionCoefficient(variable, otherExpressionCoefficient.value() - coefficient));
            newLinearExpression.coefficients.putIfAbsent(variable, new ExpressionCoefficient(variable, -coefficient));
        }
        return newLinearExpression;
    }

    public int getNmbCoefficients() {
        return this.coefficients.size();
    }

    private void addVariable(final Variable variable, double coefficient) {
        this.coefficients.put(variable, new ExpressionCoefficient(variable, coefficient));
    }

}
