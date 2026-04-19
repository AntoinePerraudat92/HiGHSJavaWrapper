package wrapper.model;

class ModelConcurrentUseTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    private static class MockModel extends Model {


    }

}
