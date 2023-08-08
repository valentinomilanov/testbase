package com.project.test.util.junit.rules;

import org.joda.time.LocalDateTime;

import ch.qos.logback.classic.Level;

public class LogLine {

	private LocalDateTime date;
    private Level loggerLevel;
    private String message;
    
    public LogLine() {
        super();
    }
    
    public LogLine(LocalDateTime date, Level loggerLevel, String message) {
        super();
        this.date = date;
        this.loggerLevel = loggerLevel;
        this.message = message;
    }
    
    public LocalDateTime getDate() {
        return date;
    }
    
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
    public Level getLoggerLevel() {
        return loggerLevel;
    }
    
    public void setLoggerLevel(Level loggerLevel) {
        this.loggerLevel = loggerLevel;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((loggerLevel == null) ? 0 : loggerLevel.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LogLine other = (LogLine) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (loggerLevel == null) {
            if (other.loggerLevel != null)
                return false;
        } else if (!loggerLevel.equals(other.loggerLevel))
            return false;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "LogLine [date=" + date + ", loggerLevel=" + loggerLevel + ", message=" + message + "]";
    }
}
