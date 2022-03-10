package com.jonanorman.android.renderclient;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.jonanorman.android.renderclient.math.Matrix4;
import com.jonanorman.android.renderclient.math.Vector3;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Objects;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;

public class GLMaterialObjectLayer extends GLShaderLayer {

    private final static String VERTEX_CODE = "uniform mat4 modelViewMatrix;\n" +
            "uniform mat4 mvpMatrix;\n" +
            "uniform mat4 viewPortMatrix;\n" +
            "uniform mat4 inputTextureMatrix;\n" +
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
            "    gl_Position =viewPortMatrix* mvpMatrix * vec4(position,1.0);\n" +
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
            "    // Apply SRGB gamma before writing the fragment color.\n" +
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

    private int[] elementIndices;
    private float[] positionCoords;
    private float[] textureCoords;
    private float[] positionNormals;
    private float[] lightDirection = new float[]{0.250f, 0.866f, 0.433f, 0.0f};
    private GLTexture texture;
    private WeakReference<Bitmap> textureBitmap;
    private int textureResId;
    private Uri textureUri;
    private FileDescriptor objectFileDescriptor;
    private File objectFile;
    private String objectAssetFile;

    public GLMaterialObjectLayer(GLRenderClient client) {
        super(client, VERTEX_CODE, FRAGMENT_CODE);
        setDrawMode(GLDrawMode.TRIANGLES);
        setDrawType(GLDrawType.DRAW_ELEMENT);
        texture = client.newTexture(GLTextureType.TEXTURE_2D);
        texture.setMinFilter(GLTextureFilter.LINEAR_MIPMAP_LINEAR);
        texture.setMagFilter(GLTextureFilter.LINEAR);
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
        elementIndices = indicesArray;
        positionCoords = verticesArray;
        positionNormals = normalsArray;
        textureCoords = texCoordsArray;
        setDrawElementIndices(elementIndices);
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

    @Override
    protected void onDispose() {
        super.onDispose();
        texture.dispose();
    }

    public void setMaterialTexture(Bitmap bitmap) {
        if (textureBitmap == null || textureBitmap.get() == bitmap) {
            return;
        }
        textureBitmap = new WeakReference<>(bitmap);
        textureResId = 0;
        textureUri = null;
        texture.updateBitmap(bitmap);
    }

    public void setMaterialTexture(Context context, int drawResId) {
        if (textureResId == drawResId) {
            return;
        }
        this.textureResId = drawResId;
        this.textureBitmap = null;
        this.textureUri = null;
        Resources resources = context.getResources();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, drawResId, options);
        texture.updateBitmap(bitmap);
        bitmap.recycle();
    }

    public void setMaterialTexture(Context context, Uri uri) {
        if (textureUri == uri) {
            return;
        }
        this.textureUri = uri;
        this.textureBitmap = null;
        this.textureResId = 0;
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


    @Override
    protected boolean onShaderLayerRender(long renderTimeMs) {
        boolean render = super.onShaderLayerRender(renderTimeMs);
        if (!render) {
            return false;
        }
        if (elementIndices == null
                || positionCoords == null
                || positionNormals == null
                || textureCoords == null) {
            return false;
        }
        modelViewMatrix.setIdentity();
        modelViewMatrix.postMul(modelMatrix);
        modelViewMatrix.postMul(viewMatrix);
        modelViewProjectionMatrix.setIdentity();
        modelViewProjectionMatrix.postMul(modelViewMatrix);
        modelViewProjectionMatrix.postMul(projectionMatrix);

        modelViewMatrix.mapPoints(viewLightPoint, lightDirection);
        viewLightDirection.set(viewLightPoint[0], viewLightPoint[1], viewLightPoint[2]);
        viewLightDirection.normalize();

        GLShaderParam shaderParam = getDefaultShaderParam();
        shaderParam.put("position", positionCoords);
        shaderParam.put("textureCoordinate", textureCoords);
        shaderParam.put("normal", positionNormals);
        shaderParam.put("modelViewMatrix", modelViewMatrix.get());
        shaderParam.put("mvpMatrix", modelViewProjectionMatrix.get());
        shaderParam.put("inputTexture", texture.getTextureId());
        shaderParam.put("lightStrength", lightAmbientStrength, lightDiffuseStrength, lightSpecularStrength, lightSpecularPower);
        shaderParam.put("lightDirection",
                viewLightDirection.getX(),
                viewLightDirection.getY(),
                viewLightDirection.getZ(),
                1.f);
        shaderParam.put("inputTexturePreMul", texture.isPremultiplied());
        shaderParam.put("inputTextureMatrix", texture.getTextureMatrix().get());
        return true;
    }


}
