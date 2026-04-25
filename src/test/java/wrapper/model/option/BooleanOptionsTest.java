package wrapper.model.option;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BooleanOptionsTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    private static int computeNmbOptions() {
        return Arrays.stream(BooleanOptions.values())
                .map(BooleanOptions::getHighsOptionName)
                .collect(Collectors.toSet())
                .size();
    }

    @Test
    void commonBooleanOptionsMustNotHaveDuplicates() {
        assertEquals(BooleanOptions.values().length, computeNmbOptions());
    }

}
