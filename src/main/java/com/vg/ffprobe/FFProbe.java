package com.vg.ffprobe;

import com.google.gson.Gson;
import com.vg.proc.Proc;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.vg.proc.ProcessEvent.STDOUT;
import static com.vg.proc.ProcessEvent.STRING;
import static java.util.Arrays.asList;

public class FFProbe {
    private final static Logger log = LoggerFactory.getLogger(FFProbe.class);
    private final File binFfprobe;

    public FFProbe(File binFfprobe) {
        this.binFfprobe = checkNotNull(binFfprobe);
        checkArgument(binFfprobe.canExecute(), "cant execute %s", binFfprobe);
    }

    public Single<FfprobeJS> ffprobe(String url) {
        return ffprobe0(binFfprobe.getAbsolutePath(), asList("-print_format", "json", "-show_format", "-show_streams", url));
    }

    public static Single<FfprobeJS> ffprobe0(String ffprobe, List<String> args) {
        Observable<String> lines = Proc.spawn(log, ffprobe, args).concatMap(e -> {
            if (STRING.equals(e.type) && STDOUT.equals(e.stream)) {
                String str = (String) e.data;
                return Observable.just(str);
            }
            return Observable.empty();
        });

        return lines.reduce(new StringBuilder(), (sb, cur) -> sb.append(cur).append('\n'))
                .map(sb -> sb.toString())
                .map(json -> new Gson().fromJson(json, FfprobeJS.class));
    }

}
