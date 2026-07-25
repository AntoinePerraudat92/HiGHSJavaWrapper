package wrapper.model.option;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

}
