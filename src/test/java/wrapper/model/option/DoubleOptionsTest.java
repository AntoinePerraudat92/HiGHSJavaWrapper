package wrapper.model.option;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import wrapper.model.Model;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @ParameterizedTest
    @EnumSource(value = DoubleOptions.class)
    void allCommonDoubleOptionsMustBeValidOptions(final DoubleOptions doubleOptions) {
        final Model model = new Model();

        assertTrue(model.addOption(doubleOptions.getOption(35.0)));
    }

}
