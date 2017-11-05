package performance;

public class PerformanceConfiguration {
    LoadEvolution loadEvolution;
    boolean singleRM;
    boolean random;

    public PerformanceConfiguration(LoadEvolution loadEvolution, boolean singleRM, boolean random) {
        this.loadEvolution = loadEvolution;
        this.singleRM = singleRM;
        this.random = random;
    }

    public LoadEvolution getLoadEvolution() {
        return loadEvolution;
    }

    public boolean isSingleRM() {
        return singleRM;
    }

    public boolean isRandom() {
        return random;
    }
}
