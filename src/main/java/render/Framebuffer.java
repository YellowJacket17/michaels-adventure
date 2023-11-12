package render;

import static org.lwjgl.opengl.GL30.*;

/**
 * This class represents a framebuffer.
 * An entire frame can be rendered as a texture, which then allows it to be scaled.
 * This texture is then what is rendered to the screen.
 */
public class Framebuffer {

    // FIELDS
    /**
     * Framebuffer object ID.
     */
    private int fboId;

    /**
     * Texture that frame is rendered to.
     */
    private Texture texture;


    // CONSTRUCTOR
    /**
     * Constructs a Framebuffer instance.
     * This framebuffer is generated upon construction.
     *
     * @param width width of this framebuffer (recommended to be native monitor width)
     * @param height height of this framebuffer (recommended to be native monitor height)
     */
    public Framebuffer(int width, int height) {
        generate(width, height);
    }


    // METHODS
    /**
     * Binds this framebuffer for all subsequent render calls.
     */
    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, fboId);
    }


    /**
     * Unbinds this framebuffer for all subsequent render calls.
     */
    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }


    /**
     * Generates this framebuffer.
     *
     * @param width width of this framebuffer
     * @param height height of this framebuffer
     */
    private void generate(int width, int height) {

        // Generate framebuffer object.
        fboId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboId);

        // Create texture to render data to and attach to framebuffer.
        texture = new Texture(width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getTextureId(), 0);

        // Create renderbuffer object to store depth info and attach to framebuffer.
        int rboId = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboId);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboId);

        // Check if everything generated correctly.
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            // TODO : Throw more specific exception.
            throw new RuntimeException("Failed to generate framebuffer");
        }

        // Bind framebuffer zero to display to window again.
        // When binding to a framebuffer, every subsequent render call will go to said framebuffer.
        // So, when we previously bound to framebuffer fboId, all subsequent render calls would go there.
        // We're returning to directly rendering to the window for now.
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }


    // GETTERS
    public int getFboId() {
        return fboId;
    }

    public int getTextureId() {
        return texture.getTextureId();
    }
}
