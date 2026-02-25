package wrapper.examples;

import org.junit.jupiter.api.Test;
import wrapper.model.LinearExpression;
import wrapper.model.Model;
import wrapper.model.Solution;
import wrapper.model.Variable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static wrapper.util.Constants.EPSILON;

class KnapsackProblemTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    @Test
    void example() {
        // Instance.
        final int nmbItems = 5;
        final int capacity = 50;
        final double[] values = {1, 2, 3.5, 4.6, 7.2};
        final double[] weights = {0.5, 1, 4.5, 1.0, 4.3};
        // Model creation.
        final Model model = new Model();
        // x[i] = number of times item i is picked.
        final Variable[] x = new Variable[nmbItems];
        for (int i = 0; i < nmbItems; ++i) {
            x[i] = model.addIntegerVariable(0.0, Double.MAX_VALUE, values[i]);
        }
        // Knapsack capacity constraint: \sum_{i}x_{i} <= capacity.
        final LinearExpression capacityExpression = new LinearExpression();
        for (int i = 0; i < nmbItems; ++i) {
            capacityExpression.addVariable(x[i], weights[i]);
        }
        model.addLessThanOrEqualToConstraint(capacity, capacityExpression);

        final Solution solution = model.maximize().orElseThrow();

        assertTrue(solution.isFeasible());
        assertEquals(230.0, solution.getObjectiveValue(), EPSILON);
        assertEquals(0.0, x[0].getValue(), EPSILON);
        assertEquals(0.0, x[1].getValue(), EPSILON);
        assertEquals(0.0, x[2].getValue(), EPSILON);
        assertEquals(50.0, x[3].getValue(), EPSILON);
        assertEquals(0.0, x[4].getValue(), EPSILON);
    }

}
