#version 300 es

uniform vec3 uCircleColor;

in vec2 outPosition;

out vec4 outFragColor;

void main()
{
    outFragColor = vec4(uCircleColor, 1.0);
    if (length(outPosition) > 1.0)
    {
        discard;
    }
}
