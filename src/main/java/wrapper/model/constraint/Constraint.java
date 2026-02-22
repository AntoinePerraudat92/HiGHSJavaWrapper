package wrapper.model.constraint;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import wrapper.model.variable.Variable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@EqualsAndHashCode
public class Constraint {

    @Getter
    private final long index;
    @Getter
    private final ConstraintType constraintType;
    @Getter
    private double lhs;
    @Getter
    private double rhs;
    @EqualsAndHashCode.Exclude
    private BiConsumer<Variable, Double> onCoefficientUpdated;
    @EqualsAndHashCode.Exclude
    private Consumer<Double> onConstraintRightHandSideUpdated;
    @EqualsAndHashCode.Exclude
    private Consumer<Double> onConstraintLeftHandSideUpdated;

    public Constraint(long index, @NonNull final ConstraintType constraintType, double lhs, double rhs) {
        this.index = index;
        this.constraintType = constraintType;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public void onConstraintCoefficientUpdated(@NonNull final BiConsumer<Variable, Double> onCoefficientUpdated) {
        this.onCoefficientUpdated = onCoefficientUpdated;
    }

    public void updateConstraintCoefficient(double newCoefficient, @NonNull final Variable variable) {
        if (this.onCoefficientUpdated != null) {
            this.onCoefficientUpdated.accept(variable, newCoefficient);
        }
    }

    public void onConstraintRightHandSideUpdated(@NonNull final Consumer<Double> onConstraintRightHandSideUpdated) {
        this.onConstraintRightHandSideUpdated = onConstraintRightHandSideUpdated;
    }

    public void updateConstraintRightHandSide(double rhs) {
        if (this.onConstraintRightHandSideUpdated != null) {
            this.rhs = rhs;
            this.onConstraintRightHandSideUpdated.accept(this.rhs);
        }
    }

    public void onConstraintLeftHandSideUpdated(@NonNull final Consumer<Double> onConstraintLeftHandSideUpdated) {
        this.onConstraintLeftHandSideUpdated = onConstraintLeftHandSideUpdated;
    }

    /**
     * Has no effect for general constraints. updateConstraintSides must be called instead.
     */
    public void updateConstraintLeftHandSide(double lhs) {
        if (this.onConstraintLeftHandSideUpdated != null) {
            this.lhs = lhs;
            this.onConstraintLeftHandSideUpdated.accept(this.lhs);
        }
    }

}
