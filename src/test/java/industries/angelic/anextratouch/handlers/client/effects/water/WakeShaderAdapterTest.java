package industries.angelic.anextratouch.handlers.client.effects.water;

import static org.junit.Assert.*;

import org.junit.Test;

public class WakeShaderAdapterTest {

    private static final String COMPLEMENTARY = "#version 330 compatibility\n"
        + "void main() { vec3 worldGeoNormal; float fresnelM; "
        + "DoLighting(color, shadowMult, playerPos, viewPos, lViewPos, geoNormal, normalM, dither); }";
    private static final String BSL = "#version 120\nvec3 GetWaterNormal() { return vec3(0); }\n"
        + "void main() { float water = 1.0; float fresnel = pow(clamp(1.0 + dot(newNormal, normalize(viewPos)), 0.0, 1.0), 5.0); }";

    @Test
    public void patchesBeforeLightingAndReflectionsWithoutChangingVersion() {
        for (String source : new String[] { COMPLEMENTARY, BSL }) {
            String result = WakeShaderAdapter.patch("gbuffers_water", source);
            assertTrue(result.startsWith(source.substring(0, source.indexOf('\n'))));
            assertTrue(result.contains("uniform sampler2D aetWakeField;"));
            assertTrue(result.indexOf("uniform sampler2D") < result.indexOf("void main"));
            assertTrue(result.lastIndexOf("aetWakeNormal(") > result.indexOf("void main"));
            assertTrue(
                result.lastIndexOf("aetWakeNormal(")
                    < result.lastIndexOf(source == BSL ? "float fresnel" : "DoLighting("));
            assertEquals(result, WakeShaderAdapter.patch("gbuffers_water", result));
        }
    }

    @Test
    public void leavesUnknownAmbiguousAndNonWaterProgramsIntact() {
        String unknown = "#version 120\nvoid main() { gl_FragColor = vec4(1); }";
        assertEquals(unknown, WakeShaderAdapter.patch("gbuffers_water", unknown));
        assertEquals(COMPLEMENTARY, WakeShaderAdapter.patch("gbuffers_terrain", COMPLEMENTARY));
        String ambiguous = COMPLEMENTARY + "\n" + COMPLEMENTARY;
        assertEquals(ambiguous, WakeShaderAdapter.patch("gbuffers_water", ambiguous));
        assertNull(WakeShaderAdapter.patch("gbuffers_water", null));
    }
}
