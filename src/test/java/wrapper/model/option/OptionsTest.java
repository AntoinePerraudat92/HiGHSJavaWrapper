package wrapper.model.option;

import org.junit.jupiter.api.Test;
import wrapper.exceptions.OptionException;
import wrapper.model.Model;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static wrapper.util.ObjectCreator.createModel;

class OptionsTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    @Test
    void settingUnknownOptionShouldThrow() {
        final Model model = createModel();

        assertThrows(OptionException.class, () -> model.setOption(new Option("unknownOption", "randomValue")));
    }

}
