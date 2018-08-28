package com.vg.thisisit;

import io.reactivex.Flowable;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.File;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

class Scene {
    String name;
    int start; //inclusive
    int end;//inclusive
    boolean good;
    String comment;


    @Override
    public String toString() {
        return "Scene{" +
                "name='" + name + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", good=" + good +
                ", comment='" + comment + '\'' +
                '}';
    }

    int duration() {
        return end - start + 1;
    }

    static Flowable<Scene> scenes(File f) throws IOException {
        return Flowable.fromIterable(CSVParser.parse(f, UTF_8, CSVFormat.DEFAULT.withFirstRecordAsHeader())).map(x -> {
            Scene scene = new Scene();
            scene.name = x.get(0);
            scene.start = Integer.parseInt(x.get(1).trim(), 10);
            scene.end = Integer.parseInt(x.get(2).trim(), 10);
            scene.good = x.get(3).startsWith("good");
            scene.comment = x.get(4);
            return scene;
        });
    }
}
