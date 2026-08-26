package wrapper.model;

import lombok.AccessLevel;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;


@NullMarked
public class Constraint extends ModelObject {

    @Getter(AccessLevel.PACKAGE)
    private final ConstraintType constraintType;

    Constraint(long index, final Model model, final ConstraintType constraintType) {
        super(index, model);
        this.constraintType = constraintType;
    }

    public void updateCoefficient(double newCoefficient, final Variable variable) {
        final Model model = getModel();
        model.updateConstraintCoefficient(newCoefficient, variable, this);
    }

    public void updateRightHandSide(double newRhs) {
        final Model model = getModel();
        model.updateRightHandSide(newRhs, this);
    }

    enum ConstraintType {
        EQUALITY,
        GREATER_THAN_OR_EQUAL_TO,
        LESS_THAN_OR_EQUAL_TO
    }

}
