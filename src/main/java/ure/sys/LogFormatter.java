package ure.sys;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Created by mel on 3/20/17.
 */
public class LogFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        String logger = record.getLoggerName();
        if (logger.indexOf('.') >= 0) {
            String caller = logger.substring(logger.lastIndexOf('.') + 1, logger.length());
            return caller + ": " + record.getMessage() + "\n";
        } else {
            return logger + ": " + record.getMessage() + "\n";
        }
    }
}
