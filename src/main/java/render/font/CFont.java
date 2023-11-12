package render.font;

import org.lwjgl.BufferUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;

/**
 * This class represents a loaded font.
 */
public class CFont {

    // FIELDS
    /**
     * Font file path.
     */
    private final String filePath;

    /**
     * Font scale (controls font resolution).
     */
    private final int fontSize;

    /**
     * Font name.
     */
    private String name;

    /**
     * Map to store information on all characters contained in this font.
     */
    private final HashMap<Integer, CharInfo> charMap = new HashMap<>();

    /**
     * Texture ID of rendered parent texture containing this font.
     */
    private int textureId;

    /**
     * Height adjustment for all loaded characters.
     * This amount is trimmed off the top of the characters.
     * It can be used to avoid excess whitespace on top of characters.
     */
    private final int heightAdjustment = 50;

    /**
     * Spacing adjustment for space between all characters drawn onto the generated parent font image.
     * This amount is added to the spacing between each character in this image.
     * Increased spacing helps to prevent bleed-over from neighboring characters when rendering a target character.
     */
    private final int spacingAdjustment = 10;


    // CONSTRUCTOR
    /**
     * Constructs a CFont instance.
     * The font provided at the provided file path is loaded upon construction.
     *
     * @param filePath file path of font from resources directory
     * @param fontSize font scale (controls font resolution)
     */
    public CFont(String filePath, int fontSize) {
        this.filePath = filePath;
        this.fontSize = fontSize;
        generateBitmap();
    }


    // METHODS
    /**
     * Retrieves a character from this font.
     * Note that any lengths (width or height) are native and will need to be adjusted to the correct size by
     * multiplying by the scale.
     *
     * @param codepoint character to retrieve (!, A, B, C, etc.)
     * @return character
     */
    public CharInfo getCharacter(int codepoint) {

        return charMap.getOrDefault(codepoint, new CharInfo(0, 0, 0, 0, 0));
    }


    /**
     * Generates a bitmap for this font and uploads the result to the GPU.
     */
    private void generateBitmap() {

        // Create new font from loaded file.
        Font font = registerFont();
        font = new Font(font.getName(), Font.PLAIN, fontSize);
        name = font.getName();

        // Create fake image to get font information.
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setFont(font);
        FontMetrics fontMetrics = g2d.getFontMetrics();

        // Initialize fake image dimensions.
        int estimatedWidth = (int)Math.sqrt(font.getNumGlyphs()) * font.getSize()
                + ((int)Math.sqrt(font.getNumGlyphs()) * spacingAdjustment);
        int width = 0;                                                                                                  // Width of rendered image containing all characters.
        int height = fontMetrics.getHeight();                                                                           // Height of rendered image containing all characters.
        int x = 0;
        int y = fontMetrics.getHeight();

        // Loop through all glyphs and calculate what actual image dimensions must be.
        for (int i = 0; i < font.getNumGlyphs(); i++) {
            if (font.canDisplay(i)) {
                CharInfo charInfo = new CharInfo(x, y,
                        fontMetrics.charWidth(i), fontMetrics.getHeight() - heightAdjustment, fontMetrics.getDescent());
                charMap.put(i, charInfo);
                width = Math.max(x + fontMetrics.charWidth(i), width);                                                  // Take whichever width is bigger.
                x += charInfo.getWidth() + spacingAdjustment;
                if (x > estimatedWidth) {
                    x = 0;
                    y += fontMetrics.getHeight();
                    height += fontMetrics.getHeight();
                }
            }
        }
        height += fontMetrics.getHeight();

        // Dispose of graphics context of fake image since no longer needed.
        g2d.dispose();

        // Create real image.
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(font);
        g2d.setColor(Color.WHITE);

        // Draw glyphs onto real image.
        for (int i = 0; i < font.getNumGlyphs(); i++) {
            if (font.canDisplay(i)) {
                CharInfo info = charMap.get(i);
                charMap.get(i).calculateTextureCoordinates(width, height);
                g2d.drawString("" + (char)i, info.getSourceX(), info.getSourceY());
            }
        }

        // Dispose of graphics context of real image (buffered image still contains all data).
        g2d.dispose();

        // Create texture from real image.
        uploadTexture(image);
    }


    /**
     * Uploads the passed image to the GPU as a texture.
     *
     * @param image target image
     */
    private void uploadTexture(BufferedImage image) {

        // Place all pixels from image into an array.
        int[] pixels = new int[image.getHeight() * image.getWidth()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        // Create ByteBuffer.
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);                     // Multiply by four since four bytes (rgba) are in one integer.
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                byte alphaComponent = (byte)((pixel >> 24) & 0xFF);
                buffer.put(alphaComponent);
                buffer.put(alphaComponent);
                buffer.put(alphaComponent);
                buffer.put(alphaComponent);
            }
        }
        buffer.flip();

        // Upload image to GPU.
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.getWidth(), image.getHeight(),
                0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        buffer.clear();                                                                                                 // Clear allocated memory for buffer.
    }


    /**
     * Loads and registers this font to be used by Java.
     *
     * @return font
     */
    private Font registerFont() {

        try (InputStream is = getClass().getResourceAsStream(filePath)) {

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            ge.registerFont(font);
            return font;

        } catch (Exception e) {

            e.printStackTrace();
            // TODO : Throw specific exception here.
        }
        return null;
    }


    // GETTERS
    public String getName() {
        return name;
    }

    public int getTextureId() {
        return textureId;
    }
}
