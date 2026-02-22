package wrapper.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.function.BiConsumer;
import java.util.function.DoubleConsumer;

@EqualsAndHashCode
public class Variable {

    @Getter
    private final long index;
    @EqualsAndHashCode.Exclude
    private DoubleConsumer onCostUpdatedCallback;
    @EqualsAndHashCode.Exclude
    private BiConsumer<Double, Double> onBoundsUpdateCallback;

    Variable(long index) {
        this.index = index;
    }

    void onCostUpdated(final DoubleConsumer callback) {
        this.onCostUpdatedCallback = callback;
    }

    void onBoundsUpdated(final BiConsumer<Double, Double> callback) {
        this.onBoundsUpdateCallback = callback;
    }

    public void updateCost(double newCost) {
        if (this.onCostUpdatedCallback != null) {
            this.onCostUpdatedCallback.accept(newCost);
        }
    }

    public void updateBounds(double newLb, double newUb) {
        if (this.onBoundsUpdateCallback != null) {
            this.onBoundsUpdateCallback.accept(newLb, newUb);
        }
    }

}
