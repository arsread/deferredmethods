package playground;

public class Deferrable {
    public void test() {
        for(int i = 0; i < 1000; i++) {
            method_to_be_deferred();
        }
    }

    private void method_to_be_deferred() {
        System.out.print(".");
    }

}
