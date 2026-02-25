package wrapper.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(access = AccessLevel.PACKAGE)
public class Constraint {

    @EqualsAndHashCode.Include
    private final long index;
    private BiConsumer<Variable, Double> onCoefficientUpdatedCallback;
    private Consumer<Double> onConstraintRightHandSideUpdatedCallback;
    private DoubleSupplier onGetValueCallback;
    private DoubleSupplier onGetDualValueCallback;

    long getIndex() {
        return this.index;
    }

    enum ConstraintType {
        EQUALITY,
        GREATER_THAN_OR_EQUAL_TO,
        LESS_THAN_OR_EQUAL_TO
    }

    public void updateCoefficient(double newCoefficient, @NonNull final Variable variable) {
        this.onCoefficientUpdatedCallback.accept(variable, newCoefficient);
    }

    public void updateRightHandSide(double newRhs) {
        this.onConstraintRightHandSideUpdatedCallback.accept(newRhs);
    }

    public double getValue() {
        return this.onGetValueCallback.getAsDouble();
    }

    public double getDualValue() {
        return this.onGetDualValueCallback.getAsDouble();
    }

}
