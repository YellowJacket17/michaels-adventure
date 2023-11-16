package render;

import org.joml.*;
import org.lwjgl.BufferUtils;
import utility.UtilityTool;
import utility.exceptions.AssetLoadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

/**
 * This class loads and stores a shader for use.
 */
public class Shader {

    // FIELDS
    /**
     * Shader file path.
     */
    private final String filePath;

    /**
     * Shader ID.
     */
    private int shaderProgramId;

    /**
     * Vertex from this shader.
     */
    private String vertexSource;

    /**
     * Fragment from this shader.
     */
    private String fragmentSource;

    /**
     * Boolean indicating whether this shader is currently in use or not.
     */
    private boolean inUse = false;


    // CONSTRUCTOR
    /**
     * Constructs a Shader instance.
     * The shader at the provided file path is loaded, compiled and linked upon construction.
     *
     * @param filePath file path of the shader from resources directory
     */
    public Shader(String filePath) {

        this.filePath = filePath;
        load();
        compileAndLink();
    }


    // METHODS
    /**
     * Uses this shader if not already in use.
     */
    public void use() {

        if (!inUse) {

            inUse = true;
            glUseProgram(shaderProgramId);
        }
    }


    /**
     * Detaches this shader.
     */
    public void detach() {

        glUseProgram(0);                                                                                                // 0 is a flag that states to bind nothing.
        inUse = false;
    }


    /**
     * Uploads a 4x4 matrix of floats to this shader.
     *
     * @param varName name of target variable in this shader to upload to
     * @param mat4 matrix to upload
     */
    public void uploadMat4f(String varName, Matrix4f mat4) {

        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();                                                                                                          // Ensures this shader is being used.
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);                                                      // 16 since 4x4 matrix.
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }


    /**
     * Uploads a 3x3 matrix of floats to this shader.
     *
     * @param varName name of target variable in this shader to upload to
     * @param mat3 matrix to upload
     */
    public void uploadMat3f(String varName, Matrix3f mat3) {

        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();                                                                                                          // Ensures this shader is being used.
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);                                                       // 9 since 3x3 matrix.
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }


    /**
     * Uploads a vector of four floats to this shader.
     *
     * @param varName name of target variable in this shader to upload to
     * @param vec vector to upload
     */
    public void uploadVec4f(String varName, Vector4f vec) {

        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();                                                                                                          // Ensures this shader is being used.
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }


    /**
     * Uploads a vector of three floats to this shader.
     *
     * @param varName name of target variable in this shader to upload to
     * @param vec vector to upload
     */
    public void uploadVec3f(String varName, Vector3f vec) {

        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();                                                                                                          // Ensures this shader is being used.
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }


    /**
     * Uploads a vector of two floats to this shader.
     *
     * @param varName name of target variable in this shader to upload to
     * @param vec vector to upload
     */
    public void uploadVec2f(String varName, Vector2f vec) {

        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();                                                                                                          // Ensures this shader is being used.
        glUniform2f(varLocation, vec.x, vec.y);
    }


    /**
     * Uploads a float to this shader.
     *
     * @param varName name of target variable in this shader to upload to
     * @param val float to upload
     */
    public void uploadFloat(String varName, float val) {

        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();                                                                                                          // Ensures this shader is being used.
        glUniform1f(varLocation, val);
    }


    /**
     * Uploads an int to this shader.
     *
     * @param varName name of target variable in this shader to upload to
     * @param val int to upload
     */
    public void uploadInt(String varName, int val) {

        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();                                                                                                          // Ensures this shader is being used.
        glUniform1i(varLocation, val);
    }


    /**
     * Uploads a texture to this shader.
     * When uploading a texture, we're really just uploading an integer.
     *
     * @param varName name of target variable in this shader to upload to
     * @param slot slot of texture to upload
     */
    public void uploadTexture(String varName, int slot) {

        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();                                                                                                          // Ensures this shader is being used.
        glUniform1i(varLocation, slot);
    }


    /**
     * Uploads an array of ints to this shader.
     *
     * @param varName name of target variable in this shader to upload to
     * @param array array to upload
     */
    public void uploadIntArray(String varName, int[] array) {

        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();                                                                                                          // Ensures this shader is being used.
        glUniform1iv(varLocation, array);
    }


    /**
     * Loads this shader from file.
     *
     * @throws RuntimeException
     */
    private void load() {

        try (InputStream is = getClass().getResourceAsStream(filePath)) {

            // Load file.
            String contents = readFromInputStream(is);
            String[] splitString = contents.split("(#type)( )+([a-zA-Z]+)");

            // First set of data (vertex or fragment).
            int startIndex = contents.indexOf("#type") + 6;
            int endOfLineIndex = contents.indexOf("\n", startIndex);
            String firstPattern = contents.substring(startIndex, endOfLineIndex).trim();

            // Second set of data (vertex or fragment).
            startIndex = contents.indexOf("#type", endOfLineIndex) + 6;
            endOfLineIndex = contents.indexOf("\n", startIndex);
            String secondPattern = contents.substring(startIndex, endOfLineIndex).trim();

            // Set first source with parsed data.
            if (firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            } else if (firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token '" + firstPattern + "' in " + filePath);
            }

            // Set second source with parsed data.
            if (secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            } else if (secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token '" + secondPattern + "' in " + filePath);
            }

        } catch (Exception e) {
            throw new AssetLoadException(e.getMessage());
        }
    }


    /**
     * Compiles and links this shader.
     *
     * @throws RuntimeException
     */
    private void compileAndLink() {

        // Compile vertex shaders.
        int vertexId = glCreateShader(GL_VERTEX_SHADER);                                                                // Load and compile vertex shader.
        glShaderSource(vertexId, vertexSource);                                                                         // Pass shader source to GPU.
        glCompileShader(vertexId);
        int success = glGetShaderi(vertexId, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {                                                                                      // Check for compilation errors.
            int length = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
            String[] errors = glGetShaderInfoLog(vertexId, length).split("\\r?\\n|\\r");
            for (String error : errors) {
                UtilityTool.logError(error);
            }
            throw new AssetLoadException("Failed to compile vertex shader from " + filePath);
        }

        // Compile fragment shaders.
        int fragmentId = glCreateShader(GL_FRAGMENT_SHADER);                                                            // Load and compile vertex shader.
        glShaderSource(fragmentId, fragmentSource);                                                                     // Pass shader source to GPU.
        glCompileShader(fragmentId);
        success = glGetShaderi(fragmentId, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {                                                                                      // Check for compilation errors.
            int length = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);
            String[] errors = glGetShaderInfoLog(fragmentId, length).split("\\r?\\n|\\r");
            for (String error : errors) {
                UtilityTool.logError(error);
            }
            throw new AssetLoadException("Failed to compile fragment shader from " + filePath);
        }

        // Link shaders.
        shaderProgramId = glCreateProgram();
        glAttachShader(shaderProgramId, vertexId);
        glAttachShader(shaderProgramId, fragmentId);
        glLinkProgram(shaderProgramId);
        success = glGetProgrami(shaderProgramId, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int length = glGetProgrami(shaderProgramId, GL_INFO_LOG_LENGTH);
            String[] errors = glGetProgramInfoLog(shaderProgramId, length).split("\\r?\\n|\\r");
            for (String error : errors) {
                UtilityTool.logError(error);
            }
            throw new AssetLoadException("Failed to link shaders from " + filePath);
        }
    }


    /**
     * Extracts file contents from an input stream and returns them as a string.
     *
     * @param inputStream file input stream
     * @return file contents
     * @throws RuntimeException
     */
    private String readFromInputStream(InputStream inputStream) {

        StringBuilder resultStringBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;

            while ((line = br.readLine()) != null) {

                resultStringBuilder.append(line).append("\n");
            }

        } catch (Exception e) {

            throw new AssetLoadException("Failed to load shader from " + filePath);
        }
        return resultStringBuilder.toString();
    }


    // GETTERS
    public String getFilePath() {
        return filePath;
    }

    public int getShaderProgramId() {
        return shaderProgramId;
    }

    public boolean isInUse() {
        return inUse;
    }
}
