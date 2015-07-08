#version 300 es

uniform mat4 uModelViewMat;
uniform mat4 uProjMat;

uniform float uAngle;
uniform float uCount;
uniform float uTime;

layout (location = 0) in vec4 inPosition;

void main()
{
    vec4 pos = uModelViewMat * inPosition;
    pos /= pos.w;

    float ii = float(gl_InstanceID);
    float phi = atan(1.0 / sqrt(ii)) + 2.0 * sqrt(ii);
    if (ii > 0.0) phi += uAngle + 7.0 * sin(2.0 * 3.141592653589793  * uTime) * (ii / uCount);

    float sinphi = sin(phi);
    float cosphi = cos(phi);
    vec2 dir = vec2(sinphi, cosphi) * sqrt(ii) * 0.1;
    float scaleDist = length(dir) * 0.05;
    float scale = (-dir.y + 2.0) * 0.02;

    pos.xy = pos.xy * mat2(cosphi, sinphi, -sinphi, cosphi);
    pos.xy *= scale;
    pos.xy += dir;

    gl_Position = uProjMat * pos;
}
