// AET's field contains current/previous tick height, surface altitude, and a wet mask.
uniform sampler2D aetWakeField;
uniform vec4 aetWakeOrigin;
uniform vec4 aetWakeParameters;

float aetWakeHeight(vec2 uv, float level) {
    vec4 data = texture2D(aetWakeField, uv);
    float valid = data.a * (1.0 - smoothstep(0.15, 0.4, abs(data.b - level)));
    return mix(data.g, data.r, aetWakeParameters.y) * valid;
}

vec3 aetWakeNormal(vec3 n, vec3 geometricNormal, vec3 position, mat3 viewToWorld, mat3 worldToView) {
    if (aetWakeOrigin.w == 0.0 || aetWakeParameters.z == 0.0) return n;
    vec3 geo = viewToWorld * geometricNormal;
    if (geo.y < 0.7) return n;
    vec3 p = position - aetWakeOrigin.xyz;
    vec2 uv = p.xz * aetWakeOrigin.w;
    vec2 edge = min(uv, 1.0 - uv);
    if (min(edge.x, edge.y) <= 0.0) return n;
    vec4 center = texture2D(aetWakeField, uv);
    float mask = center.a * (1.0 - smoothstep(0.2, 0.6, abs(center.b - p.y)));
    if (mask < 0.01) return n;
    vec2 dx = vec2(aetWakeParameters.x, 0.0);
    vec2 dz = dx.yx;
    vec2 slope = vec2(aetWakeHeight(uv - dx, center.b) - aetWakeHeight(uv + dx, center.b),
                      aetWakeHeight(uv - dz, center.b) - aetWakeHeight(uv + dz, center.b));
    slope *= aetWakeParameters.z * mask * smoothstep(0.0, 0.08, min(edge.x, edge.y));
    // Compress steep crests smoothly, preserving their direction and rounded profile.
    // A per-axis hard clamp flattened boat crests and capped the intensity control too early.
    slope *= inversesqrt(1.0 + dot(slope, slope) / 0.64);
    if (dot(slope, slope) < 0.000000000001) return n;
    return normalize(n + worldToView * vec3(slope.x, 0.0, slope.y));
}
