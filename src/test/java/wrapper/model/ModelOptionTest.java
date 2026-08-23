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
    @ValueSource(strings = {
            "on",
            "off"
    })
    void setStringOption(final String optionValue) {
        final Model model = new Model();

        assertDoesNotThrow(() -> model.setOption(StringOptions.PARALLEL.getOption(optionValue)));
    }

    @ParameterizedTest
    @ValueSource(booleans = {
            true,
            false
    })
    void setBooleanOption(final boolean optionValue) {
        final Model model = new Model();

        assertDoesNotThrow(() -> model.setOption(BooleanOptions.MIP_ALLOW_RESTART.getOption(optionValue)));
    }

    @ParameterizedTest
    @ValueSource(doubles = {
            12.4,
            65.4
    })
    void setDoubleOption(final double optionValue) {
        final Model model = new Model();

        assertDoesNotThrow(() -> model.setOption(DoubleOptions.TIME_LIMIT.getOption(optionValue)));
    }

    @ParameterizedTest
    @ValueSource(ints = {
            1,
            2,
            3,
            4,
            5
    })
    void setIntegerOption(final int optionValue) {
        final Model model = new Model();

        assertDoesNotThrow(() -> model.setOption(IntegerOptions.NB_THREADS.getOption(optionValue)));
    }

}
