package wrapper.model.option;

import org.junit.jupiter.api.Test;
import wrapper.model.Model;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static wrapper.util.ObjectCreator.createModel;

class IntegerOptionsTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    private static int computeNmbOptions() {
        return Arrays.stream(IntegerOptions.values())
                .map(IntegerOptions::getHighsOptionName)
                .collect(Collectors.toSet())
                .size();
    }

    @Test
    void commonIntegerOptionsMustNotHaveDuplicates() {
        assertEquals(IntegerOptions.values().length, computeNmbOptions());
    }

    @Test
    void nbThreadsOptionShouldExist() {
        final Model model = createModel();

        assertDoesNotThrow(() -> model.setOption(IntegerOptions.NB_THREADS.getOption(2)));
    }

}
