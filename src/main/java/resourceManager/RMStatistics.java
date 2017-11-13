package resourceManager;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class RMStatistics {

    public static RMStatistics instance = new RMStatistics();

    private DescriptiveStatistics averageExecutionTime = new DescriptiveStatistics();
    private DescriptiveStatistics averageCommitTime = new DescriptiveStatistics();
    private DescriptiveStatistics averageAbortTime = new DescriptiveStatistics();

    public DescriptiveStatistics getAverageExecutionTime() {
        return averageExecutionTime;
    }

    public DescriptiveStatistics getAverageCommitTime() {
        return averageCommitTime;
    }

    public DescriptiveStatistics getAverageAbortTime() {
        return averageAbortTime;
    }
}
