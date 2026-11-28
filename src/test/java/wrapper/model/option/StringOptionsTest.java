package wrapper.model.option;

import org.junit.jupiter.api.Test;
import wrapper.model.Model;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static wrapper.util.ObjectCreator.createModel;

class StringOptionsTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    private static int computeNmbOptions() {
        return Arrays.stream(StringOptions.values())
                .map(StringOptions::getHighsOptionName)
                .collect(Collectors.toSet())
                .size();
    }

    @Test
    void commonStringOptionsMustNotHaveDuplicates() {
        assertEquals(StringOptions.values().length, computeNmbOptions());
    }

    @Test
    void parallelOptionShouldExist() {
        final Model model = createModel();

        assertDoesNotThrow(() -> model.setOption(StringOptions.PARALLEL.getOption("on")));
    }

    @Test
    void solverOptionShouldExist() {
        final Model model = createModel();

        assertDoesNotThrow(() -> model.setOption(StringOptions.SOLVER.getOption("simplex")));
    }

    @Test
    void logToFileShouldExist() {
        final Model model = createModel();

        assertDoesNotThrow(() -> model.setOption(StringOptions.LOG_FILE.getOption("./file.log")));
    }

}
