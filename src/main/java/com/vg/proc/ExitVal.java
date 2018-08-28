package com.vg.proc;

import java.util.List;

public class ExitVal {
    public List<String> args;
    public int exitCode = -1;
    public byte[] out;
    public byte[] err;
    public Throwable error;
    public String errorMessage;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(Proc.list(args)).append(' ');
        sb.append(exitCode).append(' ');
        if (out != null) {
            sb.append(new String(out)).append(' ');
        }
        if (err != null) {
            sb.append(new String(err)).append(' ');
        }
        if (error != null) {
            sb.append(error);
        }
        sb.append(']');
        return sb.toString();
    }

}

