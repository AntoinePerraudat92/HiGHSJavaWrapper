package wrapper.model.variable;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.function.DoubleConsumer;

@EqualsAndHashCode
public class Variable {

    @Getter
    private final long index;
    @EqualsAndHashCode.Exclude
    private final DoubleConsumer onCostUpdatedCallback;

    public Variable(long index) {
        this.index = index;
        this.onCostUpdatedCallback = _ -> {
        };
    }

    public Variable(long index, final DoubleConsumer onCostUpdatedCallback) {
        this.index = index;
        this.onCostUpdatedCallback = onCostUpdatedCallback;
    }

    public void updateCost(double newCost) {
        this.onCostUpdatedCallback.accept(newCost);
    }

}
