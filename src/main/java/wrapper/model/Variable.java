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

    void check(final Model otherModel) {
        final Model thisModel = this.modelWeakReference.get();
        if (thisModel != otherModel) {
            throw new VariableException("Trying to access or modify variable associated with wrong model");
        }
    }

    long getIndex() {
        return this.index;
    }

    public void updateCost(double newCost) {
        final Model model = this.modelWeakReference.get();
        throwIfModelNull(model);
        model.getHighs().changeColCost(this.index, newCost);
    }

    public void updateBounds(double newLb, double newUb) {
        final Model model = this.modelWeakReference.get();
        throwIfModelNull(model);
        model.getHighs().changeColBounds(this.index, newLb, newUb);
    }

    public double getValue() {
        final Model model = this.modelWeakReference.get();
        throwIfModelNull(model);
        final HighsSolution highsSolution = model.getHighs().getSolution();
        final DoubleVector variableValues = highsSolution.getCol_value();
        return variableValues.get((int) this.index);
    }

    public double getDualValue() {
        final Model model = this.modelWeakReference.get();
        throwIfModelNull(model);
        final HighsSolution highsSolution = model.getHighs().getSolution();
        final DoubleVector dualValues = highsSolution.getCol_dual();
        return dualValues.get((int) this.index);
    }

    private void throwIfModelNull(@Nullable final Model model) {
        if (model == null) {
            throw new VariableException("Related model does not exist");
        }
    }

}
