package com.vg.thisisit;

import org.junit.Test;

import java.io.IOException;

public class OneFolderTest {
    @Test
    public void testRender() throws IOException {
        Scene.scenes(ScenesTest.SCENES_CSV).forEach(s -> {
            ScenesTest.printCommands(new Moment(s.start, s.end),
                    "/Users/zhukov/Desktop/sony/thisisit/oles_frames/bad_x2_down_x4_upscale",
                    "1920x1080@rgb24",
                    s.name);
        });
    }
}
