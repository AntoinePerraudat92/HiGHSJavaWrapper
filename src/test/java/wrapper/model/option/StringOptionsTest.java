package wrapper.model.option;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import wrapper.model.Model;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @ParameterizedTest
    @EnumSource(value = StringOptions.class)
    void allCommonStringOptionsMustBeValidOptions(final StringOptions stringOptions) {
        final Model model = new Model();

        assertTrue(model.addOption(stringOptions.getOption("on")));
    }

}
