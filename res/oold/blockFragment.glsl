#version 420 core

in vec2 passTextureCoord;
in float visibility;

out vec4 outColor;

uniform sampler2D tex;
uniform vec3 skyColor;
uniform vec3 ambientLight;
uniform float emission;

void main() {
    outColor = mix(vec4(skyColor, 1.0), vec4(ambientLight * (emission + 1), 1.0) * texture(tex, passTextureCoord), visibility);
}