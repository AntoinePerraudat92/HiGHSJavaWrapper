package wrapper.model;

import org.junit.jupiter.api.Test;
import wrapper.exceptions.ModelStateException;
import wrapper.exceptions.VariableException;

import static org.junit.jupiter.api.Assertions.*;
import static wrapper.util.Constants.EPSILON;
import static wrapper.util.ObjectCreator.createModel;

class ModelConstraintTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    @Test
    void updateCoefficient() {
        final Model model = createModel();
        final Variable x1 = model.addBinaryVariable(1.0);
        final Variable x2 = model.addBinaryVariable(1.0);
        final Constraint constraint = model.addLessThanOrEqualToConstraint(
                LinearExpression.of(new LinearExpression.Term(
                        x1,
                        1.0
                )), 1.0
        );

        final Solution firstSolution = model.maximize().orElseThrow();
        assertEquals(2.0, firstSolution.getObjectiveValue(), EPSILON);

        constraint.updateCoefficient(1.0, x2);

        final Solution secondSolution = model.maximize().orElseThrow();
        assertEquals(1.0, secondSolution.getObjectiveValue(), EPSILON);
    }

    @Test
    void updateCoefficientMustThrowForUnknownVariable() {
        final Model otherModel = createModel();
        final Variable unknownVariable = otherModel.addBinaryVariable(1.0);
        final Model model = createModel();
        final Variable x1 = model.addBinaryVariable(1.0);
        final Constraint constraint = model.addLessThanOrEqualToConstraint(
                LinearExpression.of(new LinearExpression.Term(
                        x1,
                        0.5
                )), 4.0
        );

        final ModelStateException exception = assertThrows(
                ModelStateException.class,
                () -> constraint.updateCoefficient(0.5, unknownVariable)
        );
        assertEquals(
                "Trying to access or modify variable/constraint associated with wrong model",
                exception.getMessage()
        );
    }

    @Test
    void updateConstraintRightHandSidesForEquality() {
        final Model model = createModel();
        final Variable x1 = model.addContinuousVariable(0.0, Double.POSITIVE_INFINITY, 1.0);
        final Variable x2 = model.addContinuousVariable(0.0, Double.POSITIVE_INFINITY, 1.0);
        final Constraint constraint = model.addEqualityConstraint(
                LinearExpression.of(
                        new LinearExpression.Term(
                                x1,
                                1.0
                        ), new LinearExpression.Term(x2, 1.0)
                ), 1.0
        );
        constraint.updateRightHandSide(18.0);

        final Solution firstSolution = model.maximize().orElseThrow();
        assertEquals(18.0, firstSolution.getObjectiveValue(), EPSILON);

        constraint.updateRightHandSide(5.0);
        final Solution secondSolution = model.maximize().orElseThrow();
        assertEquals(5.0, secondSolution.getObjectiveValue(), EPSILON);
    }

    @Test
    void updateConstraintRightHandSideForLessThanOrEqualTo() {
        final Model model = createModel();
        final Variable x1 = model.addContinuousVariable(0.0, Double.POSITIVE_INFINITY, 1.0);
        final Variable x2 = model.addContinuousVariable(0.0, Double.POSITIVE_INFINITY, 1.0);
        final Constraint constraint = model.addLessThanOrEqualToConstraint(
                LinearExpression.of(
                        new LinearExpression.Term(x1,
                                1.0
                        ), new LinearExpression.Term(x2, 1.0)
                ), 10.0
        );
        constraint.updateRightHandSide(37.0);

        final Solution solution = model.maximize().orElseThrow();

        assertEquals(37.0, solution.getObjectiveValue(), EPSILON);
    }

    @Test
    void updateConstraintRightHandSideForGreaterThanOrEqualTo() {
        final Model model = createModel();
        final Variable x1 = model.addIntegerVariable(12.0, Double.POSITIVE_INFINITY, 2.0);
        final Variable x2 = model.addContinuousVariable(0.0, Double.POSITIVE_INFINITY, 1.0);
        final Constraint constraint = model.addGreaterThanOrEqualToConstraint(
                LinearExpression.of(
                        new LinearExpression.Term(x1,
                                1.0
                        ), new LinearExpression.Term(x2, 1.0)
                ), 20.0
        );
        constraint.updateRightHandSide(12.0);

        final Solution solution = model.minimize().orElseThrow();

        assertEquals(24.0, solution.getObjectiveValue(), EPSILON);
    }

    @Test
    void addConstraint() {
        final Model model = createModel();
        final LinearExpression expression = new LinearExpression();
        expression.addVariable(model.addContinuousVariable(1.0, 2.0, 0.0), 1.0);

        assertEquals(0, model.addLessThanOrEqualToConstraint(expression, 50.0).getIndex());
        assertEquals(1, model.addLessThanOrEqualToConstraint(expression, LinearExpression.of(50.0)).getIndex());
        assertEquals(2, model.addEqualityConstraint(expression, 25.0).getIndex());
        assertEquals(3, model.addEqualityConstraint(expression, LinearExpression.of(25.0)).getIndex());
        assertEquals(4, model.addGreaterThanOrEqualToConstraint(expression, 1.9).getIndex());
        assertEquals(5, model.addGreaterThanOrEqualToConstraint(expression, LinearExpression.of(1.9)).getIndex());
    }

    @Test
    void addConstraintMustThrowIfLinearExpressionContainsUnknownVariable() {
        final Model otherModel = createModel();
        final Model model = createModel();
        final LinearExpression expression = LinearExpression.of(
                new LinearExpression.Term(model.addBinaryVariable(1.0), 2.0),
                new LinearExpression.Term(otherModel.addBinaryVariable(1.0), 2.0),
                new LinearExpression.Term(otherModel.addContinuousVariable(0.0, 2.4, 0.1), 5.0)
        );

        final ModelStateException exception = assertThrows(
                ModelStateException.class,
                () -> model.addEqualityConstraint(expression, 2.4)
        );
        assertEquals(
                "Trying to access or modify variable/constraint associated with wrong model",
                exception.getMessage()
        );
    }

    @Test
    void addConstraintMustThrowIfLinearExpressionHasNoVariable() {
        final Model model = createModel();
        final LinearExpression expression = new LinearExpression();

        final VariableException exception = assertThrows(
                VariableException.class,
                () -> model.addEqualityConstraint(expression, 18.3)
        );
        assertEquals("Linear expression has no variable", exception.getMessage());
    }

    @Test
    void requestingConstraintValuesForInfeasibleModelMustBePossible() {
        final Model model = createModel();
        final Variable x1 = model.addBinaryVariable(1.0);
        final Variable x2 = model.addBinaryVariable(1.0);
        final Variable x3 = model.addBinaryVariable(1.0);
        final Constraint constraint = model.addEqualityConstraint(
                LinearExpression.of(
                        new LinearExpression.Term(x1, 1.0),
                        new LinearExpression.Term(x2, 1.0),
                        new LinearExpression.Term(x3, 1.0)
                ), 5.0
        );

        model.maximize().orElseThrow();

        assertDoesNotThrow(constraint::getValue);
        assertDoesNotThrow(constraint::getDualValue);
    }


}
