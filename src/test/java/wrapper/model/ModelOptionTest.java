package wrapper.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wrapper.exceptions.OptionException;
import wrapper.model.option.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModelOptionTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    @Test
    void addOptionMustThrowForUnknownOptionType() {
        class UnknowOption implements Option {
            @Override
            public String getOptionName() {
                return "UnknowOption";
            }
        }
        final Model model = new Model();

        final OptionException exception = assertThrows(OptionException.class, () -> model.addOption(new UnknowOption()));
        assertEquals("Option is not supported", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "parallel, on, true",
            "unknown_option, true, false"
    })
    void addStringOption(final String optionName, final String optionValue, final boolean returnValue) throws OptionException {
        final Model model = new Model();

        assertEquals(returnValue, model.addOption(new StringOption(optionName, optionValue)));
    }

    @ParameterizedTest
    @CsvSource({
            "mip_detect_symmetry, true, true",
            "unknown_option, true, false"
    })
    void addBooleanOption(final String optionName, final boolean optionValue, final boolean returnValue) throws OptionException {
        final Model model = new Model();

        assertEquals(returnValue, model.addOption(new BooleanOption(optionName, optionValue)));
    }

    @ParameterizedTest
    @CsvSource({
            "time_limit, 65.4, true",
            "unknown_option, 12.1, false"
    })
    void addDoubleOption(final String optionName, final double optionValue, final boolean returnValue) throws OptionException {
        final Model model = new Model();

        assertEquals(returnValue, model.addOption(new DoubleOption(optionName, optionValue)));
    }

    @ParameterizedTest
    @CsvSource({
            "threads, 4, true",
            "unknown_option, -1, false"
    })
    void addIntegerOption(final String optionName, final int optionValue, final boolean returnValue) throws OptionException {
        final Model model = new Model();

        assertEquals(returnValue, model.addOption(new IntegerOption(optionName, optionValue)));
    }

}