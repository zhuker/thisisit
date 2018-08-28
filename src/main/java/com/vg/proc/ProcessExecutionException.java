package com.vg.proc;

public class ProcessExecutionException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private ExitVal value;

    public ProcessExecutionException(ExitVal value) {
        super("exit code " + value.exitCode + " " + value.errorMessage + " " + value.args);
        this.value = value;
    }

    public ExitVal getExitVal() {
        return value;
    }
}