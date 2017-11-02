package performance;

public class ConstantLoad implements LoadEvolution {
    private int load;

    public ConstantLoad(int load) {
        this.load = load;
    }

    @Override
    public int getLoad(int iteration) {
        return load;
    }
}
