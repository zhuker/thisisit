package com.vg.ffprobe;

import java.util.Map;

public class FfprobeFormat {
    public String filename; //": "GoProHeaders2/HERO3/HD3.02.03.00/3-3.MP4",
    public Integer nb_streams; //": 2,
    public Integer nb_programs; //": 0,
    public String format_name; //": "mov,mp4,m4a,3gp,3g2,mj2",
    public String format_long_name; //": "QuickTime / MOV",
    public Double start_time; //": "0.000000",
    public Double duration; //": "9.045333",
    public Long size; //": "18316649",
    public Long bit_rate; //": "16199867",
    public Integer probe_score; //": 100,

    /**
     * <pre>
        "tags": {
            "major_brand": "mp42",
            "minor_version": "0",
            "compatible_brands": "avc1isom",
            "creation_time": "2014-10-03 12:39:57"
        }
     * </pre>
     */
    public Map<String, String> tags;
}