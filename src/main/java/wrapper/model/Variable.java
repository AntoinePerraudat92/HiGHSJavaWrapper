package wrapper.model;

import highs.DoubleVector;
import highs.HighsSolution;
import lombok.EqualsAndHashCode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import wrapper.exceptions.VariableException;

import java.lang.ref.WeakReference;

@NullMarked
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Variable {

    @EqualsAndHashCode.Include
    private final long index;
    private final WeakReference<Model> modelWeakReference;

    Variable(long index, final Model model) {
        this.index = index;
        this.modelWeakReference = new WeakReference<>(model);
    }

    @SuppressWarnings("all")
    Variable(long index) {
        this.index = index;
        this.modelWeakReference = new WeakReference<>(null);
    }

    long getIndex() {
        return this.index;
    }

    @Nullable Model getModel() {
        return this.modelWeakReference.get();
    }

    public void updateCost(double newCost) {
        final Model model = getModel();
        throwIfModelNull(model);
        model.updateVariableCost(newCost, this);
    }

    public void updateBounds(double newLb, double newUb) {
        final Model model = getModel();
        throwIfModelNull(model);
        model.updateVariableBounds(newLb, newUb, this);
    }

    public double getValue() {
        final Model model = getModel();
        throwIfModelNull(model);
        final HighsSolution highsSolution = model.getSolution();
        final DoubleVector variableValues = highsSolution.getCol_value();
        return variableValues.get((int) this.index);
    }

    public double getDualValue() {
        final Model model = getModel();
        throwIfModelNull(model);
        final HighsSolution highsSolution = model.getSolution();
        final DoubleVector dualValues = highsSolution.getCol_dual();
        return dualValues.get((int) this.index);
    }

    private void throwIfModelNull(@Nullable final Model model) {
        if (model == null) {
            throw new VariableException("Related model does not exist");
        }
    }

}
