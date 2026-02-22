package wrapper.model;

import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.ObjDoubleConsumer;


public class LinearExpression {

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

    public void addVariable(@NonNull final Variable variable, double coefficient) {
        addTerm(new Term(variable, coefficient));
    }

    public void addTerm(@NonNull final Term term) {
        this.terms.putIfAbsent(term.variable, term);
    }

    public record Term(@NonNull Variable variable, double scalar) {
    }

    void consumeVariables(final ObjDoubleConsumer<Variable> consumer) {
        this.terms.values().forEach(term -> consumer.accept(term.variable(), term.scalar()));
    }

    LinearExpression minus(final LinearExpression otherExpression) {
        final LinearExpression newLinearExpression = new LinearExpression(this.constant - otherExpression.constant);
        consumeVariables(newLinearExpression::addVariable);
        for (final Term term : otherExpression.terms.values()) {
            newLinearExpression.terms.computeIfPresent(term.variable(), (variable, otherTerm) -> new Term(variable, otherTerm.scalar() - term.scalar()));
            newLinearExpression.terms.putIfAbsent(term.variable(), new Term(term.variable(), -term.scalar()));
        }
        return newLinearExpression;
    }

    double getConstant() {
        return this.constant;
    }

    int getNmbVariables() {
        return this.terms.size();
    }

}
