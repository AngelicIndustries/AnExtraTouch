package industries.angelic.anextratouch.handlers.client.effects.water;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

/** Deliberately narrow adapters: an unknown source is returned intact. Runs after pack preprocessing. */
public final class WakeShaderAdapter {

    private static final Pattern MAIN = Pattern.compile("\\bvoid\\s+main\\s*\\(\\s*(?:void\\s*)?\\)\\s*\\{");
    private static final Pattern COMPLEMENTARY = Pattern.compile(
        "\\bDoLighting\\s*\\(\\s*color\\s*,\\s*shadowMult\\s*,\\s*playerPos\\s*,\\s*viewPos\\s*,\\s*lViewPos\\s*,\\s*geoNormal\\s*,\\s*normalM\\s*,");
    private static final Pattern BSL = Pattern.compile(
        "\\bfloat\\s+fresnel\\s*=\\s*pow\\s*\\(\\s*clamp\\s*\\(\\s*1\\.0\\s*\\+\\s*dot\\s*\\(\\s*newNormal\\s*,");
    private static final String FUNCTIONS = loadFunctions();

    private WakeShaderAdapter() {}

    public static String patch(String name, String source) {
        if (!"gbuffers_water".equals(name) || source == null || source.contains("aetWakeField")) return source;
        Matcher main = MAIN.matcher(source);
        if (!main.find()) return source;
        int mainStart = main.start();
        Matcher anchor = COMPLEMENTARY.matcher(source);
        String insertion;
        if (anchor.find() && source.contains("worldGeoNormal") && source.contains("fresnelM")) {
            insertion = "if (mat == 32000) {\n"
                + "vec3 aetWakeN = aetWakeNormal(normalM, geoNormal, playerPos, mat3(gbufferModelViewInverse), mat3(gbufferModelView));\n"
                + "if (any(notEqual(aetWakeN, normalM))) { normalM = aetWakeN;\n"
                + "fresnel = clamp(1.0 + dot(normalM, nViewPos), 0.0, 1.0);\n"
                + "}\n}\n";
        } else {
            anchor = BSL.matcher(source);
            if (!anchor.find() || !source.contains("GetWaterNormal") || !source.contains("float water")) return source;
            insertion = "if (water > 0.5) {\n"
                + "newNormal = aetWakeNormal(newNormal, normal, worldPos, mat3(gbufferModelViewInverse), mat3(gbufferModelView));\n}\n";
        }
        int start = anchor.start();
        if (start < main.end() || anchor.find()) return source;
        return source.substring(0, mainStart) + FUNCTIONS
            + "\n"
            + source.substring(mainStart, start)
            + insertion
            + source.substring(start);
    }

    private static String loadFunctions() {
        try (InputStream stream = WakeShaderAdapter.class
            .getResourceAsStream("/assets/anextratouch/shaders/wake.glsl")) {
            if (stream == null) throw new IOException("Missing wake shader resource");
            return IOUtils.toString(stream, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load experimental wake shader", e);
        }
    }
}
