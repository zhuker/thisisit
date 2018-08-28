package com.vg.proc;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.vg.proc.ProcessEvent.EXIT;
import static com.vg.proc.ProcessEvent.STRING;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class Proc {
    public static boolean debug = false;
    private final static Scheduler procScheduler = Schedulers.from(newCachedThreadPool(new BasicThreadFactory.Builder().daemon(true).namingPattern("proc-%d").build()));

    public final static int STDOUT = 1;
    public final static int STDERR = 2;

    public static Observable<ProcessEvent> spawnProcess2(String cmd, List<String> args, int errorStreams) {
        Observable<ProcessEvent> spawnProcess = Proc.spawnProcess(cmd, args);
        String[] lastString = new String[1];
        spawnProcess = spawnProcess.doOnNext(x -> {
            if (STRING.equals(x.type)) {
                String str = (String) x.data;
                boolean watchStdout = ProcessEvent.STDOUT.equals(x.stream) && ((errorStreams & STDOUT) != 0);
                boolean watchStderr = ProcessEvent.STDERR.equals(x.stream) && ((errorStreams & STDERR) != 0);
                if (isNotBlank(str) && (watchStdout || watchStderr)) {
                    lastString[0] = str;
                }
            }
        });
        spawnProcess = spawnProcess.flatMap(x -> {
            if (EXIT.equals(x.type) && 0 != x.code.intValue()) {
                ExitVal value = new ExitVal();
                value.args = args;
                value.errorMessage = lastString[0];
                value.exitCode = x.code.intValue();
                return Observable.error(new ProcessExecutionException(value));
            }
            return Observable.just(x);
        });
        return spawnProcess;
    }

    public static Observable<ProcessEvent> spawnProcess(String cmd, List<String> args) {
        List<String> _args = new ArrayList<>();
        _args.add(cmd);
        _args.addAll(args);
        return Observable.using(() -> Proc.spawn(_args),
                proc -> {
                    proc.getOutputStream().close();
                    Observable<String> stdout = readStringStream(proc.getInputStream());
                    stdout = stdout.subscribeOn(procScheduler);
                    Observable<String> stderr = readStringStream(proc.getErrorStream());
                    stderr = stderr.subscribeOn(procScheduler);
                    Observable<ProcessEvent> _stdout = stdout.map(buf -> ProcessEvent.stringEvent(ProcessEvent.STDOUT, buf));
                    Observable<ProcessEvent> _stderr = stderr.map(buf -> ProcessEvent.stringEvent(ProcessEvent.STDERR, buf));
                    Observable<ProcessEvent> _exitCode = waitFor(proc);
                    _exitCode = _exitCode.subscribeOn(procScheduler);
                    Observable<ProcessEvent> merge = Observable.merge(_stdout, _stderr).concatWith(_exitCode);
                    return merge;
                }, proc -> {
                    if (debug) {
                        System.out.println("kill " + _args);
                    }
                    proc.destroy();
                });
    }

    private static Observable<String> readStringStream(InputStream inputStream) {
        return Observable.generate(() -> new BufferedReader(new InputStreamReader(inputStream)), (s, o) -> {
            String s1 = s.readLine();
            if (s1 == null) {
                o.onComplete();
            } else {
                o.onNext(s1);
            }
        });
    }

    public static Process spawn(Iterable<String> args) {
        return Proc.spawn(list(args).toArray(new String[0]));
    }

    public static Process spawn(String... args) {
        try {
            debug("spawn: " + String.join(" ", args));
            return Runtime.getRuntime().exec(args);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void debug(String string) {
        if (debug) {
            System.out.println(string);
        }
    }

    public static <T> List<T> list(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    public static <T> List<T> list(Iterable<T> iterable) {
        if (iterable instanceof List)
            return (List<T>) iterable;
        List<T> list = new ArrayList<>();
        for (T item : iterable) {
            list.add(item);
        }
        return list;
    }

    public static Observable<ProcessEvent> waitFor(Process proc) {
        return Observable.create(s -> {
            try {
                int waitFor = proc.waitFor();
                s.onNext(ProcessEvent.exitEvent(waitFor, null));
                s.onComplete();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                s.onError(e);
            }
        });
    }

    public static Observable<ProcessEvent> spawn(Logger log, String cmd, List<String> args) {
        return spawnProcess2(cmd, args, STDERR)
                .doOnSubscribe((x) -> log.debug("{} {}", cmd, String.join(" ", args)))
                .takeUntil(e -> {
                    return EXIT.equals(e.type);
                });
    }
}
