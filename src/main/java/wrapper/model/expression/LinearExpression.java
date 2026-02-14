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
    private final Map<Variable, ExpressionMember> variables = new HashMap<>();

    public LinearExpression() {
        this(0.0);
    }

    public LinearExpression(double constant) {
        this.constant = constant;
    }

    public static LinearExpression of(final ExpressionMember... expressionMembers) {
        return LinearExpression.of(0.0, expressionMembers);
    }

    public static LinearExpression of(double constant, final ExpressionMember... expressionMembers) {
        final LinearExpression expression = new LinearExpression(constant);
        for (final ExpressionMember expressionMember : expressionMembers) {
            expression.addVariable(expressionMember.variable(), expressionMember.coefficient());
        }
        return expression;
    }

    public void consumeExpression(@NonNull final Consumer<ExpressionMember> consumer) {
        this.variables.values().forEach(consumer);
    }

    public void addVariable(final Variable variable, double coefficient) {
        this.variables.putIfAbsent(variable, new ExpressionMember(variable, coefficient));
    }

    public LinearExpression minus(@NonNull final LinearExpression otherExpression) {
        final LinearExpression newLinearExpression = new LinearExpression(constant - otherExpression.constant);
        consumeExpression(member -> newLinearExpression.addVariable(member.variable(), member.coefficient()));
        for (final ExpressionMember member : otherExpression.variables.values()) {
            final Variable variable = member.variable();
            double coefficient = member.coefficient();
            newLinearExpression.variables.computeIfPresent(variable, (_, otherMember) -> new ExpressionMember(variable, otherMember.coefficient() - coefficient));
            newLinearExpression.variables.putIfAbsent(variable, new ExpressionMember(variable, -coefficient));
        }
        return newLinearExpression;
    }

    public int getNmbVariables() {
        return this.variables.size();
    }

}
