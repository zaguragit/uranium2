#version 420 core

in vec3 position;
in vec2 textureCoords;

out vec2 passTextureCoord;
out float visibility;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

const float DENSITY = 0.006;
const float GRADIENT = 2.2;

void main() {
    vec4 positionRelativeToCam = view * model * vec4(position, 1.0);
    gl_Position = projection * positionRelativeToCam;
    passTextureCoord = textureCoords;

    visibility = min(exp(-pow((length(positionRelativeToCam.xyz) * DENSITY), GRADIENT)), 1.0);
}