package wrapper.model;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class Variable extends ModelObject {

    Variable(long index, final Model model) {
        super(index, model);
    }

    public void updateCost(double newCost) {
        final Model model = getModel();
        model.updateVariableCost(newCost, this);
    }

    public void updateBounds(double newLb, double newUb) {
        final Model model = getModel();
        model.updateVariableBounds(newLb, newUb, this);
    }

    public double getValue() {
        final Model model = getModel();
        return model.getValue(this);
    }

    public double getDualValue() {
        final Model model = getModel();
        return model.getDualValue(this);
    }

}
