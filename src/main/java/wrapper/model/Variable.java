package wrapper.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.util.function.BiConsumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(access = AccessLevel.PACKAGE)
public class Variable {

    @EqualsAndHashCode.Include
    private final long index;
    private DoubleConsumer onCostUpdatedCallback;
    private BiConsumer<Double, Double> onBoundsUpdateCallback;
    private DoubleSupplier onGetValueCallback;
    private DoubleSupplier onGetDualValueCallback;

    long getIndex() {
        return this.index;
    }

    public void updateCost(double newCost) {
        this.onCostUpdatedCallback.accept(newCost);
    }

    public void updateBounds(double newLb, double newUb) {
        this.onBoundsUpdateCallback.accept(newLb, newUb);
    }

    public double getValue() {
        return this.onGetValueCallback.getAsDouble();
    }

    public double getDualValue() {
        return this.onGetDualValueCallback.getAsDouble();
    }

}
