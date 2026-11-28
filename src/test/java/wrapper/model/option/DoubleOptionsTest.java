package wrapper.model.option;

import org.junit.jupiter.api.Test;
import wrapper.model.Model;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static wrapper.util.ObjectCreator.createModel;

class DoubleOptionsTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    private static int computeNmbOptions() {
        return Arrays.stream(DoubleOptions.values())
                .map(DoubleOptions::getHighsOptionName)
                .collect(Collectors.toSet())
                .size();
    }

    @Test
    void commonDoubleOptionsMustNotHaveDuplicates() {
        assertEquals(DoubleOptions.values().length, computeNmbOptions());
    }

    @Test
    void mipRelativeGapOptionShouldExist() {
        final Model model = createModel();

        assertDoesNotThrow(() -> model.setOption(DoubleOptions.MIP_RELATIVE_GAP.getOption(0.1)));
    }

    @Test
    void mipAbsoluteGapOptionShouldExist() {
        final Model model = createModel();

        assertDoesNotThrow(() -> model.setOption(DoubleOptions.MIP_ABSOLUTE_GAP.getOption(150.2)));
    }

    @Test
    void mipObjectiveValueTargetOptionShouldExist() {
        final Model model = createModel();

        assertDoesNotThrow(() -> model.setOption(DoubleOptions.MIP_OBJECTIVE_VALUE_TARGET.getOption(1245.2)));
    }

    @Test
    void timeLimitOptionShouldExist() {
        final Model model = createModel();

        assertDoesNotThrow(() -> model.setOption(DoubleOptions.TIME_LIMIT.getOption(60.0)));
    }

}
