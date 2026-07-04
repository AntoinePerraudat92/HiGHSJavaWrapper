package wrapper.model;

import highs.DoubleVector;
import highs.HighsSolution;
import lombok.EqualsAndHashCode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import wrapper.exceptions.ConstraintException;

import java.lang.ref.WeakReference;


@NullMarked
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Constraint {

    @EqualsAndHashCode.Include
    private final long index;
    private final ConstraintType constraintType;
    private final WeakReference<Model> modelWeakReference;

    Constraint(long index, final ConstraintType constraintType, final Model model) {
        this.index = index;
        this.constraintType = constraintType;
        this.modelWeakReference = new WeakReference<>(model);
    }

    @SuppressWarnings("all")
    Constraint(long index, final ConstraintType constraintType) {
        this.index = index;
        this.constraintType = constraintType;
        this.modelWeakReference = new WeakReference<>(null);
    }

    long getIndex() {
        return this.index;
    }

    ConstraintType getConstraintType() {
        return this.constraintType;
    }

    @Nullable Model getModel() {
        return this.modelWeakReference.get();
    }

    public void updateCoefficient(double newCoefficient, final Variable variable) {
        final Model model = getModel();
        throwIfModelNull(model);
        model.updateConstraintCoefficient(newCoefficient, variable, this);
    }

    public void updateRightHandSide(double newRhs) {
        final Model model = getModel();
        throwIfModelNull(model);
        model.updateRightHandSide(newRhs, this);
    }

    public double getValue() {
        final Model model = getModel();
        throwIfModelNull(model);
        final HighsSolution highsSolution = model.getSolution();
        final DoubleVector constraintValues = highsSolution.getRow_value();
        return constraintValues.get((int) this.index);
    }

    public double getDualValue() {
        final Model model = getModel();
        throwIfModelNull(model);
        final HighsSolution highsSolution = model.getSolution();
        final DoubleVector dualValues = highsSolution.getRow_dual();
        return dualValues.get((int) this.index);
    }

    private void throwIfModelNull(@Nullable final Model model) {
        if (model == null) {
            throw new ConstraintException("Related model does not exist");
        }
    }

}
