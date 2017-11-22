package performance;

import common.io.Logger;
import common.net.NetworkAddress;
import common.resource.RMI;
import common.resource.TransactionalResourceManager;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class PerformanceRunner {

    private static final int TXCOUNT = 8 + 1;

    private LoadEvolution loadEvolution;
    private int maxIterations;
    private long maxTime;
    private long finishTime;
    private boolean oneRM;
    private boolean random;

    private int MAXDELAY = 13000; // must be higher than deadlock
    private int LOGGER_DISPLAY = 3000;

    private int counter = 1;
    private DescriptiveStatistics[] runningTimeStatsPerTransaction = new DescriptiveStatistics[TXCOUNT];
    private DescriptiveStatistics runningTimeStats = new DescriptiveStatistics();
    private DescriptiveStatistics currentAverageLoad = new DescriptiveStatistics();

    private long lastTimerTrigger;
    private Timer triggerAverage;
    private int txCountSinceLastrigger = 0;


    private DecimalFormat df = new DecimalFormat();


    private ResourceManager rm;

    public PerformanceRunner(NetworkAddress registryAddress, LoadEvolution loadEvolution, long maxTimeSeconds, int maxIterations, boolean singleRM, boolean random) throws RemoteException, NotBoundException {
        this.loadEvolution = loadEvolution;
        this.maxIterations = maxIterations;
        this.maxTime = maxTimeSeconds;
        triggerAverage = new Timer();

        this.random = random;
        this.oneRM = singleRM;

        Registry registry = LocateRegistry.getRegistry(registryAddress.getIp(), registryAddress.getPort());
        TransactionalResourceManager resourceManager = (TransactionalResourceManager) registry.lookup(RMI.MIDDLEWARE);

        if (resourceManager != null) {
            this.rm = new ResourceManager(resourceManager);
            Logger.print().statement("PerformanceRunner ready");
        } else {
            Logger.print().error("Client failed to connect to Middleware.");
        }

        for (int i = 0; i < TXCOUNT; i++) {
            runningTimeStatsPerTransaction[i] = new DescriptiveStatistics();
        }

        df.setMaximumFractionDigits(4);

    }

    private void printLocalStats() {
        Logger.print().statement("-----------------------STATISTICS-----------------------");
        System.out.println("-----------------------");
        System.out.println("Average Execution time per transaction number");
        for (int i = 0; i < TXCOUNT; i++) {
            Logger.print().statement("-----------------------");
            Logger.print().statement("", String.valueOf(i));
            System.out.println("Mean: " + df.format(runningTimeStatsPerTransaction[i].getMean()) + "ms");
            System.out.println("Variance: " + df.format(runningTimeStatsPerTransaction[i].getVariance()));
            System.out.println("Standard Deviation: " + df.format(Math.sqrt(runningTimeStatsPerTransaction[i].getVariance())) + "ms");
        }
        System.out.println("");
        Logger.print().statement("-----------------------");
        System.out.println("Average Execution time");
        System.out.println("Mean: " + df.format(runningTimeStats.getMean()) + "ms");
        System.out.println("Variance: " + df.format(runningTimeStats.getVariance()));
        System.out.println("Standard Deviation: " + df.format(Math.sqrt(runningTimeStats.getVariance())) + "ms");
    }

    private boolean shouldTerminate() {
        if (maxIterations > 0 && counter >= maxIterations) {
            return true;
        } else if (maxTime > 0 && finishTime <= System.currentTimeMillis()) {
            return true;
        } else if (counter > Integer.MAX_VALUE) {
            return true;
        } else return false;
    }

    public void start() {
        long startTxTime;
        long txRunTime;

        if (maxTime > 0) finishTime = System.currentTimeMillis() + maxTime * 1000;
        Logger.print().info("Performance runner stating with load " + loadEvolution.getLoad(counter) + "Tx/s");
        Logger.print().info("Will stop at " + DateFormat.getTimeInstance().format(new Date(finishTime)) + " or at " + maxIterations);

        lastTimerTrigger = System.currentTimeMillis();
        triggerAverage.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long elapsedTime = System.currentTimeMillis() - lastTimerTrigger;
                if (txCountSinceLastrigger > 0 && elapsedTime > 600) {
                    currentAverageLoad.addValue(txCountSinceLastrigger / (elapsedTime / 1000.0));
                    txCountSinceLastrigger = 0;
                    lastTimerTrigger = System.currentTimeMillis();
                    Logger.print().info("", "----[" + DateFormat.getTimeInstance().format(new Date()) + "]----");
                    Logger.print().info("Target Load: " + loadEvolution.getLoad(counter));
                    Logger.print().info("Current average load: " + df.format(currentAverageLoad.getMean()) + "Tx/s");
                    Logger.print().info("Current average transactionTime: " + df.format(runningTimeStats.getMean()) + "ms");
                }
            }
        }, new Date(), LOGGER_DISPLAY);

        while (true) {
            try {

                if (shouldTerminate()) break;


                startTxTime = System.currentTimeMillis();

                int txNumber;
                
                if (oneRM) {
                    txNumber = rm.runSingle();
                } else if (random) {
                    txNumber = rm.runRandom();
                } else {
                    txNumber = rm.runMultiple();
                }

                txRunTime = System.currentTimeMillis() - startTxTime;

                txCountSinceLastrigger++;

                runningTimeStatsPerTransaction[txNumber].addValue(txRunTime);
                runningTimeStats.addValue(txRunTime);

                long txInterval = (long) (1.0 / loadEvolution.getLoad(counter) * 1000.0);
                long waitTime = txInterval - txRunTime;


                if (waitTime < 0 && counter > loadEvolution.getLoad(counter) * 5) {
                    Logger.print().warning("Approaching client sending limit. Or Deadlock?");
                    if (waitTime < - MAXDELAY) {
                        stop();
                        return;
                    }
                    continue;
                }

                if (txRunTime > txInterval && counter > loadEvolution.getLoad(counter) * 5) {
                    Logger.print().warning("Approaching system's max throughput. Or Deadlock?");
                    if (txRunTime > MAXDELAY) {
                        stop();
                        return;
                    }
                }

                int rand = ThreadLocalRandom.current().nextInt(-50, 50 + 1);
                TimeUnit.MILLISECONDS.sleep(waitTime + rand);
                counter++;


            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }

        }

        stop();

    }

    public void stop() {
        try {
            triggerAverage.cancel();
            triggerAverage.purge();
            printLocalStats();
            rm.shutdown();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}
