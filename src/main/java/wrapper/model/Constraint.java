package wrapper.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@EqualsAndHashCode
public class Constraint {

    @Getter
    private final long index;
    private final ConstraintType constraintType;
    private double lhs;
    private double rhs;
    @EqualsAndHashCode.Exclude
    private BiConsumer<Variable, Double> onCoefficientUpdated;
    @EqualsAndHashCode.Exclude
    private Consumer<Double> onConstraintRightHandSideUpdated;
    @EqualsAndHashCode.Exclude
    private Consumer<Double> onConstraintLeftHandSideUpdated;

    Constraint(long index, final ConstraintType constraintType, double lhs, double rhs) {
        this.index = index;
        this.constraintType = constraintType;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    void onConstraintCoefficientUpdated(final BiConsumer<Variable, Double> callback) {
        this.onCoefficientUpdated = callback;
    }

    void onConstraintRightHandSideUpdated(final Consumer<Double> callback) {
        this.onConstraintRightHandSideUpdated = callback;
    }

    void onConstraintLeftHandSideUpdated(final Consumer<Double> callback) {
        this.onConstraintLeftHandSideUpdated = callback;
    }

    double getRhs() {
        return this.rhs;
    }

    double getLhs() {
        return this.lhs;
    }

    ConstraintType getConstraintType() {
        return this.constraintType;
    }

    enum ConstraintType {
        EQUALITY,
        GREATER_THAN_OR_EQUAL_TO,
        LESS_THAN_OR_EQUAL_TO,
        GENERAL
    }

    public void updateConstraintCoefficient(double newCoefficient, @NonNull final Variable variable) {
        if (this.onCoefficientUpdated != null) {
            this.onCoefficientUpdated.accept(variable, newCoefficient);
        }
    }

    public void updateConstraintRightHandSide(double rhs) {
        if (this.onConstraintRightHandSideUpdated != null) {
            this.rhs = rhs;
            this.onConstraintRightHandSideUpdated.accept(this.rhs);
        }
    }

    public void updateConstraintLeftHandSide(double lhs) {
        if (this.onConstraintLeftHandSideUpdated != null) {
            this.lhs = lhs;
            this.onConstraintLeftHandSideUpdated.accept(this.lhs);
        }
    }

}
