package bittech.lib.utils.logs;

import bittech.lib.utils.FormattedTime;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReadOnlyLog {

    public static class TimeMillisMongo {
        String $numberLong;
    }

    public enum Severity {
        Info, Warning, Error
    }

    FormattedTime time;
    TimeMillisMongo timeMillsec;
    bittech.lib.utils.logs.Log.Severity severity;
    private boolean inspectNeeded;
    Map<String, Object> params = new LinkedHashMap<String, Object>();
    String event;




    public boolean getInspectNeeded() {
        return inspectNeeded;
    }


    public Map<String, Object> getParams() {
        return params;
    }

    public FormattedTime getTime() {
        return time;
    }



    public bittech.lib.utils.logs.Log.Severity getSeverity() {
        return severity;
    }

    public boolean isInspectNeeded() {
        return inspectNeeded;
    }

    public String getEvent() {
        return event;
    }

    public static bittech.lib.utils.logs.Log build() {
        return new bittech.lib.utils.logs.Log();
    }

}