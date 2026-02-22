package wrapper.model.variable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

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

    public Variable(long index) {
        this.index = index;
    }

    public void onCostUpdated(@NonNull final DoubleConsumer callback) {
        this.onCostUpdatedCallback = callback;
    }

    public void onBoundsUpdated(@NonNull final BiConsumer<Double, Double> callback) {
        this.onBoundsUpdateCallback = callback;
    }

    public void updateCost(double newCost) {
        if (this.onCostUpdatedCallback != null) {
            this.onCostUpdatedCallback.accept(newCost);
        }
    }

    public void updateBounds(double lb, double ub) {
        if (this.onBoundsUpdateCallback != null) {
            this.onBoundsUpdateCallback.accept(lb, ub);
        }
    }

}
