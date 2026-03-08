package wrapper.model.option;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import wrapper.model.Model;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @ParameterizedTest
    @EnumSource(value = IntegerOptions.class)
    void allCommonIntegerOptionsMustBeValidOptions(final IntegerOptions integerOptions) {
        final Model model = new Model();

        assertTrue(model.addOption(integerOptions.getOption(1)));
    }

}
