package wrapper.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wrapper.model.option.BooleanOptions;
import wrapper.model.option.DoubleOptions;
import wrapper.model.option.IntegerOptions;
import wrapper.model.option.StringOptions;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ModelOptionTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    @ParameterizedTest
    @ValueSource(strings = {"on", "off"})
    void addStringOption(final String optionValue) {
        final Model model = new Model();

        assertDoesNotThrow(() -> model.parseOption(StringOptions.PARALLEL.getOption(optionValue)));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void addBooleanOption(final boolean optionValue) {
        final Model model = new Model();

        assertDoesNotThrow(() -> model.parseOption(BooleanOptions.MIP_ALLOW_RESTART.getOption(optionValue)));
    }

    @ParameterizedTest
    @ValueSource(doubles = {12.4, 65.4})
    void addDoubleOption(final double optionValue) {
        final Model model = new Model();

        assertDoesNotThrow(() -> model.parseOption(DoubleOptions.TIME_LIMIT.getOption(optionValue)));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void addIntegerOption(final int optionValue) {
        final Model model = new Model();

        assertDoesNotThrow(() -> model.parseOption(IntegerOptions.NB_THREADS.getOption(optionValue)));
    }

}
