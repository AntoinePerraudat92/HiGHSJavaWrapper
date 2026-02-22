package wrapper.model.expression;

import lombok.Getter;
import lombok.NonNull;
import wrapper.model.util.Term;
import wrapper.model.variable.Variable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.ObjDoubleConsumer;


public class LinearExpression {

    @Getter
    private final double constant;
    private final Map<Variable, Term> terms = new HashMap<>();

    public LinearExpression() {
        this(0.0);
    }

    public LinearExpression(double constant) {
        this.constant = constant;
    }

    public static LinearExpression of(final Term... terms) {
        return LinearExpression.of(0.0, terms);
    }

    public static LinearExpression of(double constant, final Term... terms) {
        final LinearExpression expression = new LinearExpression(constant);
        for (final Term term : terms) {
            expression.addVariable(term.variable(), term.scalar());
        }
        return expression;
    }

    public void consumeVariables(@NonNull final ObjDoubleConsumer<Variable> consumer) {
        this.terms.values().forEach(term -> consumer.accept(term.variable(), term.scalar()));
    }

    public void addVariable(@NonNull final Variable variable, double coefficient) {
        this.terms.putIfAbsent(variable, new Term(variable, coefficient));
    }

    public LinearExpression minus(@NonNull final LinearExpression otherExpression) {
        final LinearExpression newLinearExpression = new LinearExpression(this.constant - otherExpression.constant);
        consumeVariables(newLinearExpression::addVariable);
        for (final Term term : otherExpression.terms.values()) {
            final Variable variable = term.variable();
            double coefficient = term.scalar();
            newLinearExpression.terms.computeIfPresent(variable, (_, otherTerm) -> new Term(variable, otherTerm.scalar() - coefficient));
            newLinearExpression.terms.putIfAbsent(variable, new Term(variable, -coefficient));
        }
        return newLinearExpression;
    }

    public int getNmbVariables() {
        return this.terms.size();
    }

}
