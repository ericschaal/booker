package performance;

import common.TransactionalResourceManager;

import java.util.concurrent.ThreadLocalRandom;

public class ResourceManager {

    private TransactionalResourceManager rm;

    public ResourceManager(TransactionalResourceManager middlewareRM) {
        this.rm = middlewareRM;
    }

    public int runRandom() {
        int choice = ThreadLocalRandom.current().nextInt(0, 4 + 1);
        switch (choice) {
            case 0:
                tx0();
                break;
            case 1:
                tx1();
                break;
            case 2:
                tx2();
                break;
            case 3:
                tx3();
                break;
            case 4:
                tx4();
                break;
            default:
                throw new RuntimeException("Random number not in range!");
        }
        return choice;
    }

    private void tx0() {

    }

    private void tx1() {

    }

    private void tx2() {

    }

    private void tx3() {

    }

    private void tx4() {

    }


}
