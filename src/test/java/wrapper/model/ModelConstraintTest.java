package wrapper.model;

import org.junit.jupiter.api.Test;
import wrapper.model.constraint.Constraint;
import wrapper.model.constraint.ConstraintException;
import wrapper.model.constraint.ConstraintType;
import wrapper.model.expression.LinearExpression;
import wrapper.model.variable.Variable;
import wrapper.model.variable.VariableException;
import wrapper.solution.Solution;
import wrapper.util.Term;

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
    void updateConstraintCoefficient() throws ConstraintException {
        final Model model = new Model();
        final Variable x1 = model.addBinaryVariable(1.0);
        final Variable x2 = model.addBinaryVariable(1.0);
        final Constraint constraint = model.addLessThanOrEqualToConstraint(1.0, LinearExpression.of(new Term(x1, 1.0)));

        final Solution firstSolution = model.maximize().orElseThrow();
        assertEquals(2.0, firstSolution.getObjectiveValue(), EPSILON);

        model.updateConstraintCoefficient(1.0, x2, constraint);

        final Solution secondSolution = model.maximize().orElseThrow();
        assertEquals(1.0, secondSolution.getObjectiveValue(), EPSILON);
    }

    @Test
    void updateConstraintCoefficientMustThrowForUnknownVariable() {
        final Model model = new Model();
        final Variable x1 = model.addBinaryVariable(1.0);
        final Constraint constraint = model.addLessThanOrEqualToConstraint(4.0, LinearExpression.of(new Term(x1, 0.5)));
        final Variable unknownVariable = new Variable(12);

        final VariableException exception = assertThrows(VariableException.class, () -> model.updateConstraintCoefficient(0.5, unknownVariable, constraint));
        assertEquals("Variable with index 12 does not exist in the model", exception.getMessage());
    }

    @Test
    void updateConstraintCoefficientMustThrowForUnknownConstraint() {
        final Model model = new Model();
        final Variable x1 = model.addBinaryVariable(1.0);
        final Constraint unknownConstraint = new Constraint(0, ConstraintType.EQUALITY);

        final ConstraintException exception = assertThrows(ConstraintException.class, () -> model.updateConstraintCoefficient(0.5, x1, unknownConstraint));
        assertEquals("Constraint with index 0 does not exist in the model", exception.getMessage());
    }

    @Test
    void updateConstraintRightHandSideForEqualityConstraint() throws ConstraintException {
        final Model model = new Model();
        final Variable x1 = model.addContinuousVariable(0.0, Double.MAX_VALUE, 1.0);
        final Variable x2 = model.addContinuousVariable(0.0, Double.MAX_VALUE, 1.0);
        final Constraint constraint = model.addEqualityConstraint(1.0, LinearExpression.of(new Term(x1, 1.0), new Term(x2, 1.0)));
        model.updateConstraintRightHandSide(18.0, constraint);

        final Solution solution = model.maximize().orElseThrow();

        assertEquals(18.0, solution.getObjectiveValue(), EPSILON);
    }

    @Test
    void updateConstraintRightHandSideMustNotHaveEffectForGeneralConstraint() throws ConstraintException {
        final Model model = new Model();
        final Variable x1 = model.addContinuousVariable(0.0, Double.MAX_VALUE, 1.0);
        final Variable x2 = model.addContinuousVariable(0.0, Double.MAX_VALUE, 1.0);
        final Constraint constraint = model.addGeneralConstraint(0.0, 30.0, LinearExpression.of(new Term(x1, 1.0), new Term(x2, 1.0)));
        model.updateConstraintRightHandSide(10.0, constraint);

        final Solution solution = model.maximize().orElseThrow();

        assertEquals(30.0, solution.getObjectiveValue(), EPSILON);
    }

    @Test
    void updateConstraintRightHandSideMustThrowForUnknowConstraint() {
        final Model model = new Model();

        final ConstraintException exception = assertThrows(ConstraintException.class, () -> model.updateConstraintRightHandSide(37.0, new Constraint(1, ConstraintType.GREATER_THAN_OR_EQUAL_TO)));
        assertEquals("Constraint with index 1 does not exist in the model", exception.getMessage());
    }

    @Test
    void updateConstraintSides() throws ConstraintException {
        final Model model = new Model();
        final Variable x1 = model.addContinuousVariable(0.0, Double.MAX_VALUE, 1.0);
        final Variable x2 = model.addContinuousVariable(0.0, Double.MAX_VALUE, 1.0);
        final Constraint constraint = model.addGeneralConstraint(12.0, 16.0, LinearExpression.of(new Term(x1, 1.0), new Term(x2, 1.0)));
        model.updateConstraintSides(15.0, 16.0, constraint);

        final Solution solution = model.minimize().orElseThrow();

        assertEquals(15.0, solution.getObjectiveValue(), EPSILON);
    }

    @Test
    void updateConstraintSidesMustNotHaveEffectForEqualityConstraint() throws ConstraintException {
        final Model model = new Model();
        final Variable x1 = model.addContinuousVariable(0.0, Double.MAX_VALUE, 1.0);
        final Variable x2 = model.addContinuousVariable(0.0, Double.MAX_VALUE, 1.0);
        final Constraint constraint = model.addEqualityConstraint(1.0, LinearExpression.of(new Term(x1, 1.0), new Term(x2, 1.0)));
        model.updateConstraintSides(0.0, 2.0, constraint);

        final Solution solution = model.maximize().orElseThrow();

        assertEquals(1.0, solution.getObjectiveValue(), EPSILON);
    }

    @Test
    void updateConstraintSidesMustThrowForUnknowConstraint() {
        final Model model = new Model();

        final ConstraintException exception = assertThrows(ConstraintException.class, () -> model.updateConstraintSides(12.0, 14.0, new Constraint(900, ConstraintType.GENERAL)));
        assertEquals("Constraint with index 900 does not exist in the model", exception.getMessage());
    }

    @Test
    void updateConstraintRightHandSideForLessThanOrEqualToConstraint() throws ConstraintException {
        final Model model = new Model();
        final Variable x1 = model.addContinuousVariable(0.0, Double.MAX_VALUE, 1.0);
        final Variable x2 = model.addContinuousVariable(0.0, Double.MAX_VALUE, 1.0);
        final Constraint constraint = model.addLessThanOrEqualToConstraint(10.0, LinearExpression.of(new Term(x1, 1.0), new Term(x2, 1.0)));
        model.updateConstraintRightHandSide(37.0, constraint);

        final Solution solution = model.maximize().orElseThrow();

        assertEquals(37.0, solution.getObjectiveValue(), EPSILON);
    }

    @Test
    void updateConstraintRightHandSideForGreaterThanOrEqualToConstraint() throws ConstraintException {
        final Model model = new Model();
        final Variable x1 = model.addIntegerVariable(12.0, Double.MAX_VALUE, 2.0);
        final Variable x2 = model.addContinuousVariable(0.0, Double.MAX_VALUE, 1.0);
        final Constraint constraint = model.addGreaterThanOrEqualToConstraint(20.0, LinearExpression.of(new Term(x1, 1.0), new Term(x2, 1.0)));
        model.updateConstraintRightHandSide(12.0, constraint);

        final Solution solution = model.minimize().orElseThrow();

        assertEquals(24.0, solution.getObjectiveValue(), EPSILON);
    }

    @Test
    void addConstraint() {
        final Model model = new Model();
        final LinearExpression expression = new LinearExpression();
        expression.addVariable(model.addContinuousVariable(1.0, 2.0, 0.0), 1.0);

        assertEquals(0, model.addLessThanOrEqualToConstraint(50.0, expression).index());
        assertEquals(1, model.addLessThanOrEqualToConstraint(LinearExpression.of(50.0), expression).index());
        assertEquals(2, model.addEqualityConstraint(25.0, expression).index());
        assertEquals(3, model.addEqualityConstraint(LinearExpression.of(25.0), expression).index());
        assertEquals(4, model.addGeneralConstraint(14.0, 25.0, expression).index());
        final List<Constraint> generalConstraints = model.addGeneralConstraint(LinearExpression.of(14.0), LinearExpression.of(25.0), expression);
        assertEquals(5, generalConstraints.getFirst().index());
        assertEquals(6, generalConstraints.getLast().index());
        assertEquals(7, model.addGreaterThanOrEqualToConstraint(1.9, expression).index());
        assertEquals(8, model.addGreaterThanOrEqualToConstraint(LinearExpression.of(1.9), expression).index());
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
