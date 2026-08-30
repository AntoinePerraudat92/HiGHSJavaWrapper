package wrapper.model;

import org.junit.jupiter.api.Test;
import wrapper.exceptions.ModelStateException;

import static org.junit.jupiter.api.Assertions.*;
import static wrapper.util.Constants.EPSILON;
import static wrapper.util.ObjectCreator.createModel;

class ModelValueTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    @Test
    void requestingVariableValuesWhenModelNotSolvedMustThrow() {
        final Model model = createModel();
        final Variable x = model.addBinaryVariable(5.0);

        assertThrows(ModelStateException.class, x::getValue);
        assertThrows(ModelStateException.class, x::getDualValue);
    }

    @Test
    void requestingConstraintValuesWhenModelNotSolvedMustThrow() {
        final Model model = createModel();
        final Variable x = model.addBinaryVariable(1.0);
        final Constraint constraint = model.addEqualityConstraint(
                LinearExpression.of(new LinearExpression.Term(
                        x,
                        1.0
                )), 0.0
        );

        assertThrows(ModelStateException.class, constraint::getValue);
        assertThrows(ModelStateException.class, constraint::getDualValue);
    }

    @Test
    void getValueAndDualValue() {
        final Model model = createModel();
        final Variable x1 = model.addContinuousVariable(0.0, 12.0, 1.0);
        final Variable x2 = model.addContinuousVariable(0.0, 5.6, 2.6);
        final Constraint constraint = model.addLessThanOrEqualToConstraint(
                LinearExpression.of(
                        new LinearExpression.Term(
                                x1,
                                2.0
                        ), new LinearExpression.Term(x2, 3.0)
                ), 3.5
        );

        final Solution solution = model.maximize().orElseThrow();

        assertTrue(solution.isFeasible());
        assertEquals(0.0, x1.getValue(), EPSILON);
        assertEquals(-0.7333333333333334, x1.getDualValue(), EPSILON);
        assertEquals(1.1666666666666667, x2.getValue(), EPSILON);
        assertEquals(0.0, x2.getDualValue(), EPSILON);
        assertEquals(3.5, constraint.getValue(), EPSILON);
        assertEquals(0.8666666666666667, constraint.getDualValue(), EPSILON);
    }

}
