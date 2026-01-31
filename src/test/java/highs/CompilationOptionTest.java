package highs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompilationOptionTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    @Test
    void toto() {
        final Highs highs = new Highs();

        assertEquals(4, highs.getSizeofHighsInt());
    }

}
