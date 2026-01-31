package highs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HighsTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    @Test
    void HighsIntMustBeEqualToFour() {
        final Highs highs = new Highs();

        assertEquals(4, highs.getSizeofHighsInt());
    }

}
