package wrapper.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static wrapper.util.Constants.EPSILON;

class ModelValueTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    @Test
    void getValueAndDualValue() {
        final Model model = new Model();
        final Variable x1 = model.addContinuousVariable(0.0, 12.0, 1.0);
        final Variable x2 = model.addContinuousVariable(0.0, 5.6, 2.6);
        final Constraint constraint = model.addLessThanOrEqualToConstraint(3.5, LinearExpression.of(new LinearExpression.Term(x1, 2.0), new LinearExpression.Term(x2, 3.0)));

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
