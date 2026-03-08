package wrapper.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wrapper.model.option.BooleanOptions;
import wrapper.model.option.DoubleOptions;
import wrapper.model.option.IntegerOptions;
import wrapper.model.option.StringOptions;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelOptionTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

//    @Test
//    void addOptionMustReturnFalseForUnknownType() {
//        class UnknowOption implements Option {
//            @Override
//            public String getOptionName() {
//                return "UnknowOption";
//            }
//        }
//        final Model model = new Model();
//
//        final OptionException exception = assertThrows(OptionException.class, () -> model.addOption(new UnknowOption()));
//        assertEquals("Option is not supported", exception.getMessage());
//    }

    @ParameterizedTest
    @ValueSource(strings = {"on", "off"})
    void addStringOption(final String optionValue) {
        final Model model = new Model();

        assertTrue(model.addOption(StringOptions.PARALLEL.getOption(optionValue)));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void addBooleanOption(final boolean optionValue) {
        final Model model = new Model();

        assertTrue(model.addOption(BooleanOptions.MIP_ALLOW_RESTART.getOption(optionValue)));
    }

    @ParameterizedTest
    @ValueSource(doubles = {12.4, 65.4})
    void addDoubleOption(final double optionValue) {
        final Model model = new Model();

        assertTrue(model.addOption(DoubleOptions.TIME_LIMIT.getOption(optionValue)));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void addIntegerOption(final int optionValue) {
        final Model model = new Model();

        assertTrue(model.addOption(IntegerOptions.NB_THREADS.getOption(optionValue)));
    }

}