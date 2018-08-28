package com.vg.thisisit;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.gson.Gson;
import com.vg.ffprobe.FFProbe;
import com.vg.ffprobe.FfprobeJS;
import com.vg.ffprobe.FfprobeStream;
import io.reactivex.Flowable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.io.*;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FilenameUtils.getName;
import static org.apache.commons.io.FilenameUtils.getPath;

public class ScenesTest {
    final static File SCENES_CSV = new File("Scenes.csv");

    @Test
    public void testScenes() throws IOException {
        ArrayListMultimap<Integer, String> frames = ArrayListMultimap.create();
        lines(new File("/Users/zhukov/Desktop/sony/thisisit/find.txt")).filter(line -> line.endsWith(".dpx.png")).subscribe(line -> {
            int fn = mseq(line);
//            System.out.println(fn + " " + line);
            frames.put(fn, line);
        });


        Gson gson = new Gson();
        Scene.scenes(SCENES_CSV).subscribe(s -> {
            System.out.println(s);
            Map<String, List<String>> framesByFolder = Flowable.range(s.start, s.duration()).concatMapIterable(fn -> frames.get(fn))
                    .groupBy(filename -> getPath(filename))
                    .flatMapSingle(g -> {
                        String folder = g.getKey();
                        return g.toList().map(files -> Pair.of(folder, files));
                    })
                    .toMap(p -> p.getKey(), p -> p.getValue())
                    .blockingGet();

            Optional<Moment> shortestMoment = framesByFolder.values().stream().map(e -> longestRange(e)).reduce((m1, m2) -> {
                if (m2.duration() < m1.duration()) {
                    return m2;
                }
                return m1;
            });
            if (!shortestMoment.isPresent()) {
                System.err.println("no moments for " + s);
                printCommands(new Moment(s.start, s.end), "reel02/elabv1/1920x1080", "1920x1080@rgb48", s.name);
            } else {
                Moment moment = shortestMoment.get();
                System.out.println(moment.duration() + " vs " + s.duration());
                framesByFolder.forEach((folder, files) -> {
                    System.out.println(folder);

                    List<String> picSizes = Flowable.fromIterable(files).map(file -> {
                        String path = "/Users/zhukov/Desktop/sony/thisisit/" + file;
                        String s1 = FileUtils.readFileToString(new File(path + ".json"), UTF_8);
                        FfprobeJS ffprobeJS = gson.fromJson(s1, FfprobeJS.class);
                        FfprobeStream pic = ffprobeJS.streams[0];
                        return pic.width + "x" + pic.height + "@" + pic.pix_fmt;
                    }).distinct().toList().blockingGet();

                    if (picSizes.size() != 1) {
                        System.err.println("different image sizes " + picSizes);
                    } else {
                        String picSize = picSizes.get(0);
                        printCommands(moment, "/Users/zhukov/Desktop/sony/thisisit/"+folder, picSize, s.name);

                    }
                });
                printCommands(moment, "/Users/zhukov/Desktop/sony/thisisit/reel02/elabv1/1920x1080", "1920x1080@rgb48", s.name);
            }
        }, e -> {
            e.printStackTrace();
        });
    }

    private final static String RENDERS = "/Users/zhukov/Desktop/sony/thisisit/renders";

    static void printCommands(Moment moment, String folder, String picSize, String sceneName) {
        String[] split = picSize.split("[x@]");
        int w = Integer.parseInt(split[0]);
        int h = Integer.parseInt(split[1]);

        String output = String.format(RENDERS + "/%s/%s.mp4", sceneName.replaceAll("\\s+", ""), getName(folder.replaceAll("/$", "")));

        StringBuilder sb = new StringBuilder();
        sb.append("# ffmpeg");
        sb.append(" -r 24");
        sb.append(" -start_number ").append(moment.start);
        sb.append(" -f image2");
        sb.append(" -i '").append(folder).append("/elabv1_reel_2ab.0%06d.dpx.png'");
        sb.append(" -r 24");
        sb.append(" -vframes ").append(moment.duration());
        if (h == 1080) {
            sb.append(" -vf scale=w=3840:h=2160");
        }
        sb.append(" -pix_fmt yuv420p -vcodec libx264 -profile:v baseline -crf 18 -movflags faststart ");
        sb.append(" -y ").append(output);

        String dir = new File(output).getParent();
        System.out.println("# test -d " + dir + " || mkdir -p " + dir);
        System.out.println(sb.toString());
    }

    private static Moment longestRange(Collection<String> files) {
        Preconditions.checkNotNull(files);
        Preconditions.checkArgument(!files.isEmpty());
        String folder = getPath(files.iterator().next());

        IntSummaryStatistics stats = files.stream().mapToInt(s -> mseq(s)).summaryStatistics();
        final int _start = stats.getMin();
        final int _end = stats.getMax();
        final int expectedDuration = _end - _start + 1;

        BitSet existing = new BitSet();
        for (String file : files) {
            int mseq = mseq(file);
            existing.set(mseq - _start);
        }

        Moment longestRange = new Moment(0, -1);
        int start = -1;
        do {
            int end = existing.nextClearBit(start + 1);
            int _duration = end - start;
            if (_duration > longestRange.duration()) {
                longestRange = new Moment(_start + start + 1, _start + end - 1);
            }
            if (_start + end > _end) break;
            start = end;
        } while (start < expectedDuration - 1);
        return longestRange;
    }

    private static int mseq(String line) {
        String[] split = line.split("\\.");
        return Integer.parseInt(split[split.length - 3], 10);
    }

    private static Flowable<String> lines(File f) {
        return Flowable.generate(() -> new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(f)))), (r, o) -> {
            String s1 = r.readLine();
            if (s1 == null) {
                o.onComplete();
            } else {
                o.onNext(s1);
            }
        }, r -> r.close());
    }

    private final static int CPUs = Runtime.getRuntime().availableProcessors();

    @Test
    public void testFFProbeAll() throws IOException {
        FFProbe ffp = new FFProbe(new File("/usr/local/bin/ffprobe"));
        Gson gson = new Gson();
        lines(new File("/Users/zhukov/Desktop/sony/thisisit/find.txt"))
                .filter(line -> line.endsWith(".dpx.png"))
                .map(line -> "/Users/zhukov/Desktop/sony/thisisit/" + line)
                .flatMapSingle(url -> {
                    return ffp.ffprobe(url).map(ff -> Pair.of(url, ff));
                }, false, CPUs)
                .blockingSubscribe(ff -> {
                    System.out.println(ff);
                    FileUtils.writeStringToFile(new File(ff.getKey() + ".json"), gson.toJson(ff.getValue()), UTF_8);
                }, e -> {
                    e.printStackTrace();
                });
    }


}
