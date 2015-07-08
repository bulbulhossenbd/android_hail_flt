#version 300 es

uniform mat4 uModelViewProjMat;

layout (location = 0) in vec4 inPosition;

out vec2 outPosition;

void main()
{
    gl_Position = uModelViewProjMat * inPosition;
    outPosition = inPosition.xy;
}
