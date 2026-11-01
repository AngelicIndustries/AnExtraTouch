package industries.angelic.anextratouch.handlers.client.effects;

import net.coderbot.iris.gl.sampler.SamplerHolder;
import net.coderbot.iris.gl.uniform.UniformHolder;
import net.coderbot.iris.gl.uniform.UniformUpdateFrequency;
import net.coderbot.iris.uniforms.CameraUniforms;

import org.joml.Vector3d;
import org.joml.Vector4f;

import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.handlers.client.effects.water.RippleField;

/** Optional Angelica types live here, outside the manager loaded on every client. */
public final class AngelicaWakeBindings {

    private AngelicaWakeBindings() {}

    public static void samplers(SamplerHolder holder) {
        if (!holder.hasSampler("aetWakeField")) return;
        if (holder.addDynamicSampler(ExperimentalWaterWakes.INSTANCE::texture, "aetWakeField")) {
            ExperimentalWaterWakes.INSTANCE.setSupported(true);
        }
    }

    public static void uniforms(UniformHolder holder) {
        holder.uniform4f(UniformUpdateFrequency.PER_FRAME, "aetWakeOrigin", () -> {
            RippleField field = ExperimentalWaterWakes.INSTANCE.field();
            if (!ExperimentalWaterWakes.isActive() || field == null || !field.isActive()) return new Vector4f();
            Vector3d camera = CameraUniforms.getUnshiftedCameraPosition();
            // Use the actual camera and subtract in double precision, including Iris camera rebasing.
            return new Vector4f(
                (float) (field.originX - camera.x),
                (float) -camera.y,
                (float) (field.originZ - camera.z),
                1.0F / RippleField.WIDTH);
        });
        holder.uniform4f(UniformUpdateFrequency.PER_FRAME, "aetWakeParameters", () -> {
            RippleField field = ExperimentalWaterWakes.INSTANCE.field();
            if (field == null) return new Vector4f();
            return new Vector4f(
                1.0F / field.size,
                ExperimentalWaterWakes.INSTANCE.interpolation(),
                field.samplesPerBlock * 0.5F * Config.experimentalWaterWakeStrength,
                0);
        });
    }
}
