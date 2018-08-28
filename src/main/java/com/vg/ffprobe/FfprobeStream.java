package com.vg.ffprobe;

import java.util.Map;

public class FfprobeStream {
    public Integer index;
    public String codec_name; //": "h264",
    public String profile; //": "High",
    public String codec_type;
    public String codec_long_name; //": "PCM signed 24-bit little-endian",
    public String codec_time_base; //": "1/48000",
    public String codec_tag_string; //": "[1][0][0][0]",
    public String codec_tag; //": "0x0001",
    public Integer width;
    public Integer height;
    public Integer coded_width; //": 1280,
    public Integer coded_height; //": 720,
    public String sample_fmt; //": "s32",
    public Integer channels; //": 2,
    public String channel_layout; //": "stereo",

    public Integer bits_per_sample; //": 24,
    public String r_frame_rate; //": "0/0",
    public Integer has_b_frames; //": 2,
    public String pix_fmt; //": "yuv420p",
    public Integer level; //": 41,
    public String color_range; //": "tv",
    public String color_space; //": "bt709",
    public String chroma_location; //": "left",
    public Integer refs; //": 5,
    public Object is_avc; //": "1", or "true" in ffmpeg 3.0
    public Integer nal_length_size; //": "4",

    public String time_base; // "1/24000"
    public Long start_pts; //": 0,
    public Double duration_ts;
    public Double duration; //": "1318.000000",
    public Long bit_rate; //": "2304000",
    public Double start_time; //": "0.000000",
    public Integer bits_per_raw_sample; //: "24",
    public Integer nb_frames;
    public Integer nb_read_frames;
    public String avg_frame_rate;
    public Integer sample_rate;
    public String sample_aspect_ratio; //": "1:1",
    public String display_aspect_ratio; //": "16:9
    public Double dmix_mode; //": "-1",
    public Double ltrt_cmixlev; //": "-1.000000",
    public Double ltrt_surmixlev; //": "-1.000000",
    public Double loro_cmixlev; //": "-1.000000",
    public Double loro_surmixlev; //": "-1.000000",
    /**
     * 
     * <pre>
            "disposition": {
                "default": 0,
                "dub": 0,
                "original": 0,
                "comment": 0,
                "lyrics": 0,
                "karaoke": 0,
                "forced": 0,
                "hearing_impaired": 0,
                "visual_impaired": 0,
                "clean_effects": 0,
                "attached_pic": 0
            }
     * </pre>
     */
    public Map<String, Integer> disposition;

    /**
     * <pre>
    "tags": {
        "BPS": "1959336",
        "BPS-eng": "1959336",
        "DURATION": "00:49:29.425000000",
        "DURATION-eng": "00:49:29.425000000",
        "NUMBER_OF_FRAMES": "71195",
        "NUMBER_OF_FRAMES-eng": "71195",
        "NUMBER_OF_BYTES": "727262922",
        "NUMBER_OF_BYTES-eng": "727262922",
        "_STATISTICS_WRITING_APP": "mkvmerge v8.5.2 ('Crosses') 32bit",
        "_STATISTICS_WRITING_APP-eng": "mkvmerge v8.5.2 ('Crosses') 32bit",
        "_STATISTICS_WRITING_DATE_UTC": "2015-11-22 08:32:05",
        "_STATISTICS_WRITING_DATE_UTC-eng": "2015-11-22 08:32:05",
        "_STATISTICS_TAGS": "BPS DURATION NUMBER_OF_FRAMES NUMBER_OF_BYTES",
        "_STATISTICS_TAGS-eng": "BPS DURATION NUMBER_OF_FRAMES NUMBER_OF_BYTES"
    }
     * </pre>
     */
    public Map<String, String> tags;

}