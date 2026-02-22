package wrapper.model.variable;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.function.BiConsumer;
import java.util.function.DoubleConsumer;

@EqualsAndHashCode
public class Variable {

    @Getter
    private final long index;
    @EqualsAndHashCode.Exclude
    private final DoubleConsumer onCostUpdatedCallback;
    @EqualsAndHashCode.Exclude
    private final BiConsumer<Double, Double> onBoundsUpdateCallback;

    public Variable(long index) {
        this(index, _ -> {
        }, (_, _) -> {
        });
    }

    public Variable(long index, final DoubleConsumer onCostUpdatedCallback, final BiConsumer<Double, Double> onBoundsUpdateCallback) {
        this.index = index;
        this.onCostUpdatedCallback = onCostUpdatedCallback;
        this.onBoundsUpdateCallback = onBoundsUpdateCallback;
    }

    public void updateCost(double newCost) {
        this.onCostUpdatedCallback.accept(newCost);
    }

    public void updateBounds(double lb, double ub) {
        this.onBoundsUpdateCallback.accept(lb, ub);
    }

}
