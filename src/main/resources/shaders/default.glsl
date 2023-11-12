#type vertex
#version 330 core
layout (location=0) in vec3 aPos;                            // Position attribute.
layout (location=1) in vec4 aColor;                          // Color attribute.
layout (location=2) in vec2 aTexCoords;                      // Texture position attribute.
layout (location=3) in float aTexId;                         // Texture ID attribute.

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;                                             // Going to fragment shader.
out vec2 fTexCoords;
out float fTexId;

void main() {
    fColor = aColor;                                         // Pass color to fragment shader.
    fTexCoords = aTexCoords;
    fTexId = aTexId;
    gl_Position = uProjection * uView * vec4(aPos, 1.0);     // Create a vec4 using aPos as first three elements, 1.0 as fourth.
}

#type fragment
#version 330 core

in vec4 fColor;                                              // Need an in for vec4 color.
in vec2 fTexCoords;
in float fTexId;

uniform sampler2D uTextures[8];                              // Limit to eight textures to sample from at once.

out vec4 color;                                              // Tells output color.

void main() {
    if (fTexId > 0) {
        int id = int(fTexId);
        color = fColor * texture(uTextures[id], fTexCoords);
    } else {
        color = fColor;
    }
}
