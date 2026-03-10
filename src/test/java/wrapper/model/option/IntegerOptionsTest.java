package wrapper.model.option;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

}
