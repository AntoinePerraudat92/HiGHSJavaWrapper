package wrapper.model.option;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wrapper.model.Model;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static wrapper.util.ObjectCreator.createModel;

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
    @MethodSource("wrapper.model.option.BooleanOptions#values")
    void all_booleans_options_must_be_valid(final BooleanOptions booleanOptions) {
        final Model model = createModel();

        assertDoesNotThrow(() -> model.setOption(booleanOptions.getOption(true)));
        assertDoesNotThrow(() -> model.setOption(booleanOptions.getOption(false)));
    }

}
