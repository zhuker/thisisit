package com.vg.proc;

public class ProcessEvent {
    public static final String EXIT = "exit";
    public static final String MESSAGE = "message";
    public static final String CLOSE = "close";
    public static final String STRING = "string";
    public static final String DATA = "data";

    public String type;
    public Object data;
    public String stream;
    public Number code;
    public String signal;
    public Object message;
    public static final String STDOUT = "stdout";
    public static final String STDERR = "stderr";

    public static ProcessEvent dataEvent(String stream, Object data) {
        ProcessEvent e = new ProcessEvent();
        e.type = DATA;
        e.stream = stream;
        e.data = data;
        return e;
    }

    public static ProcessEvent stringEvent(String stream, String data) {
        ProcessEvent e = new ProcessEvent();
        e.type = STRING;
        e.stream = stream;
        e.data = data;
        return e;
    }

    static ProcessEvent closeEvent(Number _code, String _signal, String reason) {
        ProcessEvent e = new ProcessEvent();
        e.type = CLOSE;
        e.code = _code;
        e.signal = _signal;
        e.message = reason;
        return e;
    }

    static ProcessEvent messageEvent(Object _message) {
        ProcessEvent e = new ProcessEvent();
        e.type = MESSAGE;
        e.message = _message;
        return e;
    }

    public static ProcessEvent exitEvent(Number _code, String _signal) {
        ProcessEvent e = new ProcessEvent();
        e.type = EXIT;
        e.code = _code;
        e.signal = _signal;
        return e;
    }
}