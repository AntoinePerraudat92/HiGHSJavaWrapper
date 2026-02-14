package wrapper.model;

import org.junit.jupiter.api.Test;
import wrapper.model.expression.ExpressionMember;
import wrapper.model.expression.LinearExpression;
import wrapper.model.variable.Variable;
import wrapper.model.variable.VariableException;
import wrapper.solution.InitialSolution;
import wrapper.solution.Solution;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static wrapper.util.Constants.EPSILON;

class ModelWarmStartTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }


    @Test
    void parseMustThrowForUnknownVariables() {
        final Model model = new Model();
        final InitialSolution initialSolution = InitialSolution.of(Map.of(new Variable(32), -45D));

        final VariableException exception = assertThrows(VariableException.class, () -> model.parseInitialSolution(initialSolution));
        assertEquals("Variable with index 32 does not exist in the model", exception.getMessage());
    }

    @Test
    void parseInitialSolutionMustReturnFalse() {
        final Model model = new Model();
        final Variable x1 = model.addBinaryVariable(1.0);
        model.addEqualityConstraint(1.0, LinearExpression.of(new ExpressionMember(x1, 1.0)));

        assertFalse(model.parseInitialSolution(InitialSolution.of(Map.of(x1, -1.0))));
        final Solution solution = model.maximize().orElseThrow();
        assertEquals(1.0, solution.getObjectiveValue(), EPSILON);
    }

    @Test
    void parseInitialSolutionMustReturnTrue() {
        final Model model = new Model();
        final Variable x1 = model.addBinaryVariable(1.0);
        final Variable x2 = model.addBinaryVariable(1.0);
        final Variable x3 = model.addBinaryVariable(1.0);
        model.addEqualityConstraint(1.0, LinearExpression.of(
                new ExpressionMember(x1, 1.0),
                new ExpressionMember(x2, 1.0),
                new ExpressionMember(x3, 1.0)
        ));

        assertTrue(model.parseInitialSolution(InitialSolution.of(Map.of(x1, 1.0, x2, 0.0, x3, 0.0))));
        final Solution solution = model.maximize().orElseThrow();
        assertEquals(1.0, solution.getObjectiveValue(), EPSILON);
    }

}
