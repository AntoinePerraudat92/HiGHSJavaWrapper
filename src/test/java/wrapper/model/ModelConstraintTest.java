package wrapper.model;

import org.junit.jupiter.api.Test;
import wrapper.exceptions.VariableException;
import wrapper.solution.Solution;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static wrapper.util.Constants.EPSILON;

class ModelConstraintTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    @Test
    void updateConstraintCoefficient() {
        final Model model = new Model();
        final Variable x1 = model.addBinaryVariable(1.0);
        final Variable x2 = model.addBinaryVariable(1.0);
        final Constraint constraint = model.addLessThanOrEqualToConstraint(1.0, LinearExpression.of(new LinearExpression.Term(x1, 1.0)));

        final Solution firstSolution = model.maximize().orElseThrow();
        assertEquals(2.0, firstSolution.getObjectiveValue(), EPSILON);

        constraint.updateConstraintCoefficient(1.0, x2);

        final Solution secondSolution = model.maximize().orElseThrow();
        assertEquals(1.0, secondSolution.getObjectiveValue(), EPSILON);
    }

    @Test
    void updateConstraintCoefficientMustThrowForUnknownVariable() {
        final Model model = new Model();
        final Variable x1 = model.addBinaryVariable(1.0);
        final Constraint constraint = model.addLessThanOrEqualToConstraint(4.0, LinearExpression.of(new LinearExpression.Term(x1, 0.5)));
        final Variable unknownVariable = new Variable(12);

        final VariableException exception = assertThrows(VariableException.class, () -> constraint.updateConstraintCoefficient(0.5, unknownVariable));
        assertEquals("Variable with index 12 does not exist in the model", exception.getMessage());
    }

    @Test
    void updateConstraintRightHandSidesForEqualityConstraint() {
        final Model model = new Model();
        final Variable x1 = model.addContinuousVariable(0.0, Double.MAX_VALUE, 1.0);
        final Variable x2 = model.addContinuousVariable(0.0, Double.MAX_VALUE, 1.0);
        final Constraint constraint = model.addEqualityConstraint(1.0, LinearExpression.of(new LinearExpression.Term(x1, 1.0), new LinearExpression.Term(x2, 1.0)));
        constraint.updateConstraintRightHandSide(18.0);

        final Solution firstSolution = model.maximize().orElseThrow();
        assertEquals(18.0, firstSolution.getObjectiveValue(), EPSILON);

        constraint.updateConstraintLeftHandSide(5.0);
        final Solution secondSolution = model.maximize().orElseThrow();
        assertEquals(5.0, secondSolution.getObjectiveValue(), EPSILON);
    }

    @Test
    void updateConstraintSidesForGeneralConstraint() {
        final Model model = new Model();
        final Variable x1 = model.addContinuousVariable(0.0, Double.MAX_VALUE, 1.0);
        final Variable x2 = model.addContinuousVariable(0.0, Double.MAX_VALUE, 1.0);
        final Constraint constraint = model.addGeneralConstraint(12.0, 16.0, LinearExpression.of(new LinearExpression.Term(x1, 1.0), new LinearExpression.Term(x2, 1.0)));
        constraint.updateConstraintLeftHandSide(15.0);
        constraint.updateConstraintRightHandSide(16.0);

        final Solution solution = model.minimize().orElseThrow();

        assertEquals(15.0, solution.getObjectiveValue(), EPSILON);
    }

    @Test
    void updateConstraintSidesForEqualityConstraint() {
        final Model model = new Model();
        final Variable x1 = model.addContinuousVariable(0.0, Double.MAX_VALUE, 1.0);
        final Variable x2 = model.addContinuousVariable(0.0, Double.MAX_VALUE, 1.0);
        final Constraint constraint = model.addEqualityConstraint(1.0, LinearExpression.of(new LinearExpression.Term(x1, 1.0), new LinearExpression.Term(x2, 1.0)));
        constraint.updateConstraintRightHandSide(8.0);

        final Solution solution = model.maximize().orElseThrow();

        assertEquals(8.0, solution.getObjectiveValue(), EPSILON);
    }

    @Test
    void updateConstraintRightHandSideForLessThanOrEqualToConstraint() {
        final Model model = new Model();
        final Variable x1 = model.addContinuousVariable(0.0, Double.MAX_VALUE, 1.0);
        final Variable x2 = model.addContinuousVariable(0.0, Double.MAX_VALUE, 1.0);
        final Constraint constraint = model.addLessThanOrEqualToConstraint(10.0, LinearExpression.of(new LinearExpression.Term(x1, 1.0), new LinearExpression.Term(x2, 1.0)));
        constraint.updateConstraintRightHandSide(37.0);

        final Solution solution = model.maximize().orElseThrow();

        assertEquals(37.0, solution.getObjectiveValue(), EPSILON);
    }

    @Test
    void updateConstraintRightHandSideForGreaterThanOrEqualToConstraint() {
        final Model model = new Model();
        final Variable x1 = model.addIntegerVariable(12.0, Double.MAX_VALUE, 2.0);
        final Variable x2 = model.addContinuousVariable(0.0, Double.MAX_VALUE, 1.0);
        final Constraint constraint = model.addGreaterThanOrEqualToConstraint(20.0, LinearExpression.of(new LinearExpression.Term(x1, 1.0), new LinearExpression.Term(x2, 1.0)));
        constraint.updateConstraintRightHandSide(12.0);

        final Solution solution = model.minimize().orElseThrow();

        assertEquals(24.0, solution.getObjectiveValue(), EPSILON);
    }

    @Test
    void addConstraint() {
        final Model model = new Model();
        final LinearExpression expression = new LinearExpression();
        expression.addVariable(model.addContinuousVariable(1.0, 2.0, 0.0), 1.0);

        assertEquals(0, model.addLessThanOrEqualToConstraint(50.0, expression).getIndex());
        assertEquals(1, model.addLessThanOrEqualToConstraint(LinearExpression.of(50.0), expression).getIndex());
        assertEquals(2, model.addEqualityConstraint(25.0, expression).getIndex());
        assertEquals(3, model.addEqualityConstraint(LinearExpression.of(25.0), expression).getIndex());
        assertEquals(4, model.addGeneralConstraint(14.0, 25.0, expression).getIndex());
        final List<Constraint> generalConstraints = model.addGeneralConstraint(LinearExpression.of(14.0), LinearExpression.of(25.0), expression);
        assertEquals(5, generalConstraints.getFirst().getIndex());
        assertEquals(6, generalConstraints.getLast().getIndex());
        assertEquals(7, model.addGreaterThanOrEqualToConstraint(1.9, expression).getIndex());
        assertEquals(8, model.addGreaterThanOrEqualToConstraint(LinearExpression.of(1.9), expression).getIndex());
    }

    @Test
    void addConstraintMustThrowIfLinearExpressionContainsUnknownVariable() {
        final Model model = new Model();
        final LinearExpression expression = new LinearExpression();
        expression.addVariable(new Variable(0), 1.0);

        final VariableException exception = assertThrows(VariableException.class, () -> model.addEqualityConstraint(2.4, expression));
        assertEquals("Variable with index 0 does not exist in the model", exception.getMessage());
    }

    @Test
    void addConstraintMustThrowIfLinearExpressionHasNoVariable() {
        final Model model = new Model();
        final LinearExpression expression = new LinearExpression();

        final VariableException exception = assertThrows(VariableException.class, () -> model.addEqualityConstraint(18.3, expression));
        assertEquals("Linear expression has no variable", exception.getMessage());
    }

}
