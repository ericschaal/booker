package common;

import java.io.IOException;

public class Logger {

    private static Logger instance = null;

    private int logLevel;

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";


    private Logger(int logLevel) {
        this.logLevel = logLevel;
    }

    public static void init(int logLevel) {
        if (instance == null) {
            instance = new Logger(logLevel);
            instance.info("Ready.", ".com.ericschaal.ds.utils.Logger");
        } else {
            instance.warning("Already started. Ignoring.", ".com.ericschaal.ds.utils.Logger");
        }
    }

    public static Logger print() {
        if(instance == null) {
            instance = new Logger(0);
        }
        return instance;
    }


    public void info(String message) {
        if (logLevel == 3) System.out.println(ANSI_BLUE + "[INFO] " + ANSI_RESET + message);
    }

    public void warning(String message) {
        if (logLevel >= 1) System.out.println(ANSI_YELLOW + "[WARNING] " + ANSI_RESET + message);
    }

    public void error(String message) {
        if (logLevel > 0) System.out.println(ANSI_RED + "[ERROR] " + ANSI_RESET + message);
    }

    public void info(String message, String caller) {
        if (logLevel == 3) System.out.println(ANSI_BLUE + "[INFO]" + ANSI_PURPLE + "[" + caller + "] " + ANSI_RESET + message);
    }

    public void warning(String message, String caller) {
        if (logLevel >= 1) System.out.println(ANSI_YELLOW + "[WARNING]" + ANSI_PURPLE + "[" + caller + "] " + ANSI_RESET + message);
    }

    public void error(String message, String caller) {
        if (logLevel > 0) System.out.println( ANSI_RED + "[ERROR]" + ANSI_PURPLE + "[" + caller + "] " + ANSI_RESET + message);
    }

    public void statement(String message) {
        System.out.println(ANSI_GREEN + message + ANSI_RESET);
    }

    public void statement(String message, String caller) {
        System.out.println(ANSI_PURPLE + "[" + caller + "] " + ANSI_GREEN + message + ANSI_RESET);
    }

    public void clear() {
        System.out.print(String.format("\033[2J"));
    }


}
