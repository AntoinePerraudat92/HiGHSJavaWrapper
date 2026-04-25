package wrapper.model;

import highs.DoubleVector;
import highs.Highs;
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

    @Nullable
    Model getModel() {
        return this.modelWeakReference.get();
    }

    public void updateCoefficient(double newCoefficient, final Variable variable) {
        final Model model = getModel();
        throwIfModelNull(model);
        variable.check(model);
        model.getHighs().changeCoeff(this.index, variable.getIndex(), newCoefficient);
    }

    public void updateRightHandSide(double newRhs) {
        final Model model = getModel();
        throwIfModelNull(model);
        final Highs highs = model.getHighs();
        switch (getConstraintType()) {
            case EQUALITY -> highs.changeRowBounds(this.index, newRhs, newRhs);
            case GREATER_THAN_OR_EQUAL_TO -> highs.changeRowBounds(this.index, newRhs, Double.MAX_VALUE);
            case LESS_THAN_OR_EQUAL_TO -> highs.changeRowBounds(this.index, -Double.MAX_VALUE, newRhs);
        }
    }

    public double getValue() {
        final Model model = getModel();
        throwIfModelNull(model);
        final HighsSolution highsSolution = model.getHighs().getSolution();
        final DoubleVector constraintValues = highsSolution.getRow_value();
        return constraintValues.get((int) this.index);
    }

    public double getDualValue() {
        final Model model = getModel();
        throwIfModelNull(model);
        final HighsSolution highsSolution = model.getHighs().getSolution();
        final DoubleVector dualValues = highsSolution.getRow_dual();
        return dualValues.get((int) this.index);
    }

    private void throwIfModelNull(@Nullable final Model model) {
        if (model == null) {
            throw new ConstraintException("Related model does not exist");
        }
    }

}
