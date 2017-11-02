package performance;

public class Increasing implements LoadEvolution{

    private double startingLoad;
    private double coefficient;
    private long trigger = 0;

    public Increasing(int startingLoad, double coefficient) {
        this.startingLoad = startingLoad;
        this.coefficient = coefficient;
        System.out.println("Linear load with" + startingLoad + ", " + coefficient);
    }

    @Override
    public int getLoad(int iteration) {
        if (trigger == 0) {
            trigger = System.currentTimeMillis();
            return (int) startingLoad;
        }
        long elapsedTime = System.currentTimeMillis() - trigger;
        if (elapsedTime > 1000) {
            trigger = System.currentTimeMillis();
            startingLoad = (startingLoad*coefficient);
            return (int) (startingLoad);
        } else return (int) startingLoad;
    }
}
