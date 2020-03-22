#version 420 core

in vec2 passTextureCoord;
in float visibility;

out vec4 outcolor;

uniform sampler2D tex;
uniform vec3 skyColor;
uniform vec3 ambientLight;

void main() {
    outcolor = mix(vec4(skyColor, 1.0), vec4(ambientLight, 1.0) * texture(tex, passTextureCoord), visibility);
}