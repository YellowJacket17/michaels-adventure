#type vertex
#version 410 core
layout (location=0) in vec2 aPos;
layout (location=1) in vec3 aColor;
layout (location=2) in vec2 aTexCoords;

out vec3 fColor;
out vec2 fTexCoords;

uniform mat4 uProjection;
uniform mat4 uView;

void main() {
    fTexCoords = aTexCoords;
    fColor = aColor;
    gl_Position = uProjection * uView * vec4(aPos, -5, 1);
}

#type fragment
#version 410 core

in vec3 fColor;
in vec2 fTexCoords;

uniform sampler2D uFontTexture;

out vec4 color;

void main() {
    color = vec4(fColor, 1) * texture(uFontTexture, fTexCoords); // Include 1 since alpha value wasn't manually provided.
}
