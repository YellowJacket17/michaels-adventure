#type vertex
#version 330 core
layout (location=0) in vec3 aPos;                                                                                       // Position attribute from vertex array.
layout (location=1) in vec4 aColor;                                                                                     // Color attribute from vertex array.
layout (location=2) in vec2 aTexCoords;                                                                                 // Texture position attribute from vertex array.
layout (location=3) in float aTexId;                                                                                    // Texture ID attribute from vertex array.

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;                                                                                                        // Send out to fragment shader.
out vec2 fTexCoords;                                                                                                    // ^^^
out float fTexId;                                                                                                       // ^^^

void main() {
    fColor = aColor;                                                                                                    // Pass color to fragment shader.
    fTexCoords = aTexCoords;                                                                                            // Pass texture coordinates to fragment shader.
    fTexId = aTexId;                                                                                                    // Pass texture ID to fragment shader.
    gl_Position = uProjection * uView * vec4(aPos, 1.0);                                                                // Create a vec4 using aPos as first three elements, 1.0 as fourth.
}

#type fragment
#version 330 core

in vec4 fColor;                                                                                                         // Take in from vertex shader.
in vec2 fTexCoords;                                                                                                     // ^^^
in float fTexId;                                                                                                        // ^^^

uniform sampler2D uTextures[8];                                                                                         // Limit to eight textures to sample from at once.
uniform vec2 uDimensions;                                                                                               // Dimensions (width, height) of rectangle to render.
uniform float uRadius;                                                                                                  // Radius of each rounded corner (same scale as uDimensions).

out vec4 color;                                                                                                         // Tells output color.

void main() {

    // The fragment shader tells the GPU how to render a pixel.
    // Below, we check each coordinate with the texture coordinate range.
    // That is what the variable coords is below.
    // We multiply fTexCoords by uDimensions to convert fTexCoords from a 0-1 range to a 0-width and 0-height range,
    // where width and height are the dimensions of the rectangle to render.
    //
    // Texture coordinates range from 0 to 1 in both the x- and y-directions.
    // The following are the texture coordinates at the four corners of a quad:
    // (0, 0)    (1, 0)
    // (0, 1)    (1, 1)
    //
    // Note that (1, 1) is the bottom-right since the positive y-direction is defined as downwards in this application.
    //
    // Essentially, the method to round corners is the following steps:
    // 1) Calculate the center of the four circles that form the rounded corners.
    // 2) Check if the current coordinate being processed is within the square formed by a quad's corner (ex. (0, 0))
    //    and it's respective circle center (ex. (uRadius, uRadius)).
    // 3) If the current coordinate being analyzed is within the square, see if its distance from the respective
    //    circle's center is greater than the circle's radius (i.e., uRadius).
    // 4) If the aformentioned distance is greater, discard the pixel (i.e., don't do anything with it).
    //
    // As an aside, note that we don't necessarily need an uploaded texture to use texture coordinates.
    // Texture coordinate just describe points within the quad.

    // Calculate center of each circle that forms the rounded edges.
    vec2 center00 = vec2(uRadius, uRadius);                                                                             // Circle for rounded corner closest to texture coordinate (0, 0).
    vec2 center10 = vec2(uDimensions.x - uRadius, uRadius);                                                             // Circle for rounded corner closest to texture coordinate (1, 0).
    vec2 center01 = vec2(uRadius, uDimensions.y - uRadius);                                                             // Circle for rounded corner closest to texture coordinate (0, 1).
    vec2 center11 = vec2(uDimensions.x - uRadius, uDimensions.y - uRadius);                                             // Circle for rounded corner closest to texture coordinate (1, 1).

    // Calculate coordinate of current pixel being processed by fragment shader.
    vec2 coords = fTexCoords * uDimensions;

    // Calculate whether a pixel is outside of a rounded edge.
    if ((coords.x < center00.x) && (coords.y < center00.y)) {                                                           // If current pixel is near (0, 0).
        if (length(coords - center00) > uRadius) {                                                                      // Vector length from coordinate of current pixel to circle center.
            discard;                                                                                                    // Discard pixel (don't render it in this quad).
        }
    }

    if ((coords.x > center10.x) && (coords.y < center10.y)) {                                                           // If current pixel is near (1, 0).
        if (length(coords - center10) > uRadius) {
            discard;
        }
    }

    if ((coords.x < center01.x) && (coords.y > center01.y)) {                                                           // If current pixel is near (0, 1).
        if (length(coords - center01) > uRadius) {
            discard;
        }
    }

    if ((coords.x > center11.x) && (coords.y > center11.y)) {                                                           // If current pixel is near (1, 1).
        if (length(coords - center11) > uRadius) {
            discard;
        }
    }

    // If the current pixel has not been discarded, decide what color to render it as.
    if (fTexId > 0) {
        int id = int(fTexId);
        color = fColor * texture(uTextures[id], fTexCoords);
    } else {
        color = fColor;
    }
}
