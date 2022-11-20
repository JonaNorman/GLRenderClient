package com.jonanorman.android.renderclient.layer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.jonanorman.android.renderclient.math.Matrix4;
import com.jonanorman.android.renderclient.math.Vector3;
import com.jonanorman.android.renderclient.opengl.GLDraw;
import com.jonanorman.android.renderclient.opengl.GLFrameBuffer;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLShaderParam;
import com.jonanorman.android.renderclient.opengl.GLTexture;
import com.jonanorman.android.renderclient.opengl.gl20.GL20Texture;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;

public class GLMaterialObjectLayer extends GLShaderLayer {

    public static final String KEY_TEXTURE_COORDINATE = "textureCoordinate";
    public static final String KEY_NORMAL = "normal";
    public static final String KEY_MODEL_VIEW_MATRIX = "modelViewMatrix";
    public static final String KEY_MVP_MATRIX = "mvpMatrix";
    public static final String KEY_INPUT_TEXTURE = "inputTexture";
    public static final String KEY_LIGHT_STRENGTH = "lightStrength";
    public static final String KEY_LIGHT_DIRECTION = "lightDirection";
    public static final String KEY_INPUT_TEXTURE_PRE_MUL = "inputTexturePreMul";
    public static final String KEY_INPUT_TEXTURE_MATRIX = "inputTextureMatrix";

    private final static String VERTEX_CODE = "uniform mat4 modelViewMatrix;\n" +
            "uniform mat4 mvpMatrix;\n" +
            "uniform mat4 inputTextureMatrix;\n" +
            "uniform mat4 positionMatrix;\n" +
            "\n" +
            "attribute vec3 position;\n" +
            "attribute vec3 normal;\n" +
            "attribute vec2 textureCoordinate;\n" +
            "\n" +
            "varying vec3 v_ViewPosition;\n" +
            "varying vec3 v_ViewNormal;\n" +
            "varying vec2 v_TexCoord;\n" +
            "\n" +
            "void main() {\n" +
            "    v_ViewPosition = (modelViewMatrix * vec4(position,1.0)).xyz;\n" +
            "    v_ViewNormal = normalize((modelViewMatrix * vec4(normal, 0.0)).xyz);\n" +
            "    v_TexCoord = (inputTextureMatrix*vec4(textureCoordinate,0.0,1.0)).xy;\n" +
            "    gl_Position = mvpMatrix*positionMatrix * vec4(position,1.0);\n" +
            "}\n";

    private final static String FRAGMENT_CODE = "\n" +
            "precision mediump float;\n" +
            "\n" +
            "uniform sampler2D inputTexture;\n" +
            "uniform vec2 viewPortSize;\n" +
            "uniform float renderTime;\n" +
            "uniform bool inputTexturePreMul;\n" +
            "\n" +
            "uniform vec4 lightDirection;\n" +
            "uniform vec4 lightStrength;\n" +
            "\n" +
            "varying vec3 v_ViewPosition;\n" +
            "varying vec3 v_ViewNormal;\n" +
            "varying vec2 v_TexCoord;\n" +
            "\n" +
            "void main() {\n" +
            "    // We support approximate sRGB gamma.\n" +
            "    const float kGamma = 0.4545454;\n" +
            "    const float kInverseGamma = 2.2;\n" +
            "    const float kMiddleGrayGamma = 0.466;\n" +
            "\n" +
            "    // Unpack lighting and material parameters for better naming.\n" +
            "    vec3 viewLightDirection = lightDirection.xyz;\n" +
            "\n" +
            "    float materialAmbient = lightStrength.x;\n" +
            "    float materialDiffuse = lightStrength.y;\n" +
            "    float materialSpecular = lightStrength.z;\n" +
            "    float materialSpecularPower = lightStrength.w;\n" +
            "\n" +
            "    // Normalize varying parameters, because they are linearly interpolated in the vertex shader.\n" +
            "    vec3 viewFragmentDirection = normalize(v_ViewPosition);\n" +
            "    vec3 viewNormal = normalize(v_ViewNormal);\n" +
            "\n" +
            "    // Flip the y-texture coordinate to address the texture from top-left.\n" +
            "    vec4 objectColor = texture2D(inputTexture, v_TexCoord);\n" +
            "    if(!inputTexturePreMul) objectColor = vec4(objectColor.rgb*objectColor.a,objectColor.a);\n" +
            "\n" +
            "\n" +
            "    // Apply inverse SRGB gamma to the texture before making lighting calculations.\n" +
            "    objectColor.rgb = pow(objectColor.rgb, vec3(kInverseGamma));\n" +
            "\n" +
            "    // Ambient light is unaffected by the light intensity.\n" +
            "    float ambient = materialAmbient;\n" +
            "\n" +
            "    // Approximate a hemisphere light (not a harsh directional light).\n" +
            "    float diffuse = materialDiffuse *\n" +
            "            0.5 * (dot(viewNormal, viewLightDirection) + 1.0);\n" +
            "\n" +
            "    // Compute specular light.\n" +
            "    vec3 reflectedLightDirection = reflect(viewLightDirection, viewNormal);\n" +
            "    float specularStrength = max(0.0, dot(viewFragmentDirection, reflectedLightDirection));\n" +
            "    float specular = materialSpecular *\n" +
            "            pow(specularStrength, materialSpecularPower);\n" +
            "\n" +
            "    vec3 color = objectColor.rgb * (ambient + diffuse) + specular;\n" +
            "    // Apply SRGB m before writing the fragment color.\n" +
            "    color.rgb = pow(color, vec3(kGamma));\n" +
            "    // Apply average pixel intensity and color shift\n" +
            "    gl_FragColor.rgb = color;\n" +
            "    gl_FragColor.a = objectColor.a;\n" +
            "}\n";


    private final Vector3 viewLightDirection = new Vector3();
    private final float[] viewLightPoint = new float[4];


    private final Matrix4 modelMatrix = new Matrix4();
    private final Matrix4 viewMatrix = new Matrix4();
    private final Matrix4 projectionMatrix = new Matrix4();
    private final Matrix4 modelViewMatrix = new Matrix4();
    private final Matrix4 modelViewProjectionMatrix = new Matrix4();

    private float lightAmbientStrength = 0.05f;
    private float lightDiffuseStrength = 1.0f;
    private float lightSpecularStrength = 0.5f;
    private float lightSpecularPower = 10.0f;


    private float[] textureCoords;
    private float[] positionNormals;
    private float[] lightDirection = new float[]{0.250f, 0.866f, 0.433f, 0.0f};
    private GLTexture texture;

    private FileDescriptor objectFileDescriptor;
    private File objectFile;
    private String objectAssetFile;

    public GLMaterialObjectLayer() {
        super(VERTEX_CODE, FRAGMENT_CODE);
        setDrawMode(GLDraw.Mode.TRIANGLES);
        setDrawType(GLDraw.Type.DRAW_ELEMENT);
    }

    @Override
    protected void onRenderInit(GLRenderClient client) {
        super.onRenderInit(client);
        texture = new GL20Texture(client, GLTexture.Type.TEXTURE_2D);
        texture.setMinFilter(GLTexture.FilterMode.LINEAR_MIPMAP_LINEAR);
        texture.setMagFilter(GLTexture.FilterMode.LINEAR);
    }

    @Override
    protected void onRenderClean(GLRenderClient client) {
        super.onRenderClean(client);
        texture.dispose();
    }

    public float getLightAmbientStrength() {
        return lightAmbientStrength;
    }

    public void setLightAmbientStrength(float lightAmbientStrength) {
        this.lightAmbientStrength = lightAmbientStrength;
    }

    public float getLightDiffuseStrength() {
        return lightDiffuseStrength;
    }

    public void setLightDiffuseStrength(float lightDiffuseStrength) {
        this.lightDiffuseStrength = lightDiffuseStrength;
    }

    public float getLightSpecularStrength() {
        return lightSpecularStrength;
    }

    public void setLightSpecularStrength(float lightSpecularStrength) {
        this.lightSpecularStrength = lightSpecularStrength;
    }

    public float getLightSpecularPower() {
        return lightSpecularPower;
    }

    public void setLightSpecularPower(float lightSpecularPower) {
        this.lightSpecularPower = lightSpecularPower;
    }


    public void setLightDirection(float[] lightDirection) {
        if (lightDirection == null) {
            return;
        }
        if (lightDirection.length != 4) {
            throw new IllegalArgumentException("lightDirection length must 4");
        }
        System.arraycopy(lightDirection, 0, this.lightDirection, 0, 4);
    }

    public void setMaterialObjectFile(FileDescriptor fileDescriptor) {
        if (Objects.equals(fileDescriptor, objectFileDescriptor)) {
            return;
        }
        InputStream fileStream = null;
        try {
            fileStream = new FileInputStream(fileDescriptor);
            loadObjInputStream(fileStream);
            objectFileDescriptor = fileDescriptor;
            objectFile = null;
            objectAssetFile = null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {

            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setMaterialObjectFile(File file) {
        if (Objects.equals(objectFile, file)) {
            return;
        }
        InputStream fileStream = null;
        try {
            fileStream = new FileInputStream(file);
            loadObjInputStream(fileStream);
            objectFile = file;
            objectFileDescriptor = null;
            objectAssetFile = null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setMaterialObjectFile(Context context, String asset) {
        if (Objects.equals(objectAssetFile, asset)) {
            return;
        }
        InputStream fileStream = null;
        try {
            fileStream = context.getAssets().open(asset);
            loadObjInputStream(fileStream);
            objectAssetFile = asset;
            objectFile = null;
            objectFileDescriptor = null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadObjInputStream(InputStream fileStream) throws IOException {
        Obj obj = ObjReader.read(fileStream);
        obj = ObjUtils.convertToRenderable(obj);
        int[] indicesArray = ObjData.getFaceVertexIndicesArray(obj, 3);
        float[] verticesArray = ObjData.getVerticesArray(obj);
        float[] texCoordsArray = ObjData.getTexCoordsArray(obj, 2);
        float[] normalsArray = ObjData.getNormalsArray(obj);
        setDrawElementIndices(indicesArray);
        setPositionCoordinates(verticesArray);
        positionNormals = normalsArray;
        textureCoords = texCoordsArray;

    }


    public Matrix4 getModelMatrix() {
        return modelMatrix;
    }

    public Matrix4 getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4 getProjectionMatrix() {
        return projectionMatrix;
    }


    public void setMaterialTexture(Bitmap bitmap) {
        postRender(new Runnable() {
            @Override
            public void run() {
                texture.updateBitmap(bitmap);
            }
        });

    }

    public void setMaterialTexture(Context context, int drawResId) {
        postRender(new Runnable() {
            @Override
            public void run() {
                Resources resources = context.getResources();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                Bitmap bitmap = BitmapFactory.decodeResource(resources, drawResId, options);
                texture.updateBitmap(bitmap);
                bitmap.recycle();
            }
        });
    }

    public void setMaterialTexture(Context context, Uri uri) {
        postRender(new Runnable() {
            @Override
            public void run() {
                ContentResolver contentResolver = context.getContentResolver();
                InputStream inputStream = null;
                try {
                    inputStream = contentResolver.openInputStream(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                texture.updateBitmap(bitmap);
            }
        });
    }

    @Override
    protected boolean onRenderLayer(GLRenderClient client, GLFrameBuffer inputBuffer) {
        if (getPositionCoordinates() == null
                || positionNormals == null
                || textureCoords == null) {
            return false;
        }
        return super.onRenderLayer(client, inputBuffer);
    }

    @Override
    protected void onRenderLayerParam(GLFrameBuffer inputBuffer, GLShaderParam shaderParam) {
        super.onRenderLayerParam(inputBuffer, shaderParam);
        modelViewMatrix.clearIdentity();
        modelViewMatrix.postMul(modelMatrix);
        modelViewMatrix.postMul(viewMatrix);
        modelViewProjectionMatrix.clearIdentity();
        modelViewProjectionMatrix.postMul(modelViewMatrix);
        modelViewProjectionMatrix.postMul(projectionMatrix);

        modelViewMatrix.mapPoints(viewLightPoint, lightDirection);
        viewLightDirection.set(viewLightPoint[0], viewLightPoint[1], viewLightPoint[2]);
        viewLightDirection.normalize();

        shaderParam.set(KEY_TEXTURE_COORDINATE, textureCoords);
        shaderParam.set(KEY_NORMAL, positionNormals);
        shaderParam.set(KEY_MODEL_VIEW_MATRIX, modelViewMatrix);
        shaderParam.set(KEY_MVP_MATRIX, modelViewProjectionMatrix);
        shaderParam.set(KEY_INPUT_TEXTURE, texture);
        shaderParam.set(KEY_LIGHT_STRENGTH, lightAmbientStrength, lightDiffuseStrength, lightSpecularStrength, lightSpecularPower);
        shaderParam.set(KEY_LIGHT_DIRECTION,
                viewLightDirection);
        shaderParam.set(KEY_INPUT_TEXTURE_PRE_MUL, texture.isPremultiplied());
        shaderParam.set(KEY_INPUT_TEXTURE_MATRIX, texture.getTextureMatrix());
    }
}
