package wrapper.model.option;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import wrapper.model.Model;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @ParameterizedTest
    @EnumSource(value = BooleanOptions.class)
    void allCommonBooleanOptionsMustBeValidOptions(final BooleanOptions booleanOptions) {
        final Model model = new Model();

        assertTrue(model.addOption(booleanOptions.getOption(true)));
    }

}
