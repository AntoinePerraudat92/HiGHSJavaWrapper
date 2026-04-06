package wrapper.model;

import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.function.ObjDoubleConsumer;


@NullMarked
public class LinearExpression {

    private final double constant;
    private final Map<Long, Term> terms = new HashMap<>();

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
            expression.addTerm(term);
        }
        return expression;
    }

    public void addVariable(final Variable variable, double coefficient) {
        addTerm(new Term(variable, coefficient));
    }

    public void addTerm(final Term term) {
        this.terms.putIfAbsent(term.variable.getIndex(), term);
    }

    void consumeVariables(final ObjDoubleConsumer<Variable> consumer) {
        this.terms.values().forEach(term -> consumer.accept(term.variable(), term.scalar()));
    }

    LinearExpression minus(final LinearExpression otherExpression) {
        final LinearExpression newLinearExpression = new LinearExpression(this.constant - otherExpression.constant);
        consumeVariables(newLinearExpression::addVariable);
        for (final Term term : otherExpression.terms.values()) {
            final Variable variable = term.variable();
            final long variableIndex = variable.getIndex();
            final double scalar = term.scalar();
            newLinearExpression.terms.computeIfPresent(variableIndex, (index, otherTerm) -> new Term(variable, otherTerm.scalar() - scalar));
            newLinearExpression.terms.putIfAbsent(variableIndex, new Term(variable, -scalar));
        }
        return newLinearExpression;
    }

    double getConstant() {
        return this.constant;
    }

    int getNmbVariables() {
        return this.terms.size();
    }

    public record Term(Variable variable, double scalar) {
    }

}
