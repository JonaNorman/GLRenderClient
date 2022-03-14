# GLRenderClient
[![License](https://img.shields.io/badge/license-MIT-blue.svg?style=flat)](http://opensource.org/licenses/MIT)

![GLRenderClient](https://github.com/JonaNorman/GLRenderClient/blob/main/screen/logo.png?raw=true "GLRenderClient")


This is a lightweight OpenGL rendering library for Android development, dedicated to help Android developers to reduce development costs, 
forget the technical details of OpenGL, it can be used in the image editing, video  editing.
## Import
``` 
implementation('io.github.jonanorman.android:glrenderclient:0.1.1')
```
```Java
SurfaceTexture surfaceTexture = ((TextureView) findViewById(R.id.textureView)).getSurfaceTexture();
GLRenderThread renderThread = new GLRenderThread(new GLRenderClient.Builder());
renderThread.start();
renderThread.post(new Runnable() {
@Override
public void run() {
GLRenderClient renderClient = renderThread.getRenderClient();
GLLayer layerGroup = renderClient.newLayerGroup();
layerGroup.setBackgroundColor(Color.WHITE);
layerGroup.render(surfaceTexture);
}
});
renderThread.quitAndWait();
```
## Feature

### Feature 1: automatically parses shader parameter
You don't have to call different apis to set different variables depending on the type of the argument
**before**
```Java
int uniformLocation1 = GLES20.glGetUniformLocation(programId, "viewSize");
int textureUniformLocation = GLES20.glGetUniformLocation(programId, "inputTexture");
int textureUniformLocation2 = GLES20.glGetUniformLocation(programId, "inputTexture2");
int attributeLocation = GLES20.glGetAttribLocation(programId, "position");
GLES20.glUniform2f(uniformLocation1,width,height);
GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId1);
GLES20.glUniform1i(textureUniformLocation, 0);
GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId2);
GLES20.glUniform1i(textureUniformLocation2, 1);
float[] position = new float[8];
int size =4;
FloatBuffer floatBuffer= ByteBuffer.allocateDirect(position.length * 4)
.order(ByteOrder.nativeOrder())
.asFloatBuffer();
floatBuffer.put(position);
GLES20.glEnableVertexAttribArray(attributeLocation);
floatBuffer.position(0);
GLES20.glVertexAttribPointer(attributeLocation, size, GLES20.GL_FLOAT, false, 0,floatBuffer);
GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
```
**now**
```Java
GLShaderLayer shaderLayer = renderClient.newShaderLayer(vertexCode,fragmentCode);
GLShaderParam shaderParam = shaderLayer.getShaderParam();
shaderParam.put("viewSize", width,height);
shaderParam.put("inputTexture", textureId1);
shaderParam.put("inputTexture2", textureId2);
shaderParam.put("position", position);
shaderLayer.setDrawType(GLDrawType.DRAW_ARRAY);
shaderLayer.setDrawArrayCount(4);
```

### Feature 2: async render android native View
![async render View](https://github.com/JonaNorman/GLRenderClient/blob/main/screen/preview2.gif?raw=true "async render View")

```Java
GLViewLayer viewLayer = renderClient.newLayoutLayer(getApplicationContext(), R.style.AppTheme);
viewLayer.setBackgroundColor(Color.WHITE);
LayoutInflater layoutInflater = LayoutInflater.from(viewLayer.getContext());
View view = layoutInflater.inflate(R.layout.layout_view_layer, null);
viewLayer.addView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
viewLayer.queueTouchEvent(motionEvent);
viewLayer.render(surfaceTexture);
```

### Feature 3: multi-layer and multi-timeline rendering
![multi-layer](https://github.com/JonaNorman/GLRenderClient/blob/main/screen/preview1.gif?raw=true "multi-layer")
```Java
GLRenderClient renderClient = renderThread.getRenderClient();
GLLayerGroup rootLayer = renderClient.newLayerGroup();
rootLayer.setDuration(10000);
GLLayerGroup group = renderClient.newLayerGroup();
group.setDuration(5000);
group.setStartTime(5000);
group.setGravity(GravityMode.TOP_CENTER_HORIZONTAL);
group.setLayerScaleMode(ScaleMode.FILL);
group.setWidth(500);
group.setHeight(500);
rootLayer.addLayer(group);
rootLayer.render(surfaceTexture);
```

### Feature 4: effect group

```Java
GLEffectGroup effectGroup = renderClient.newEffectSet();
GLShaderEffect shaderEffect =renderClient.newShaderEffect();
shaderEffect.setVertexShaderCode(vertexCode);
shaderEffect.setFragmentShaderCode(fragmentCode);
effectGroup.addEffect(shaderEffect);
GLShaderEffect shaderEffect1 =renderClient.newShaderEffect();
shaderEffect1.setStartTime(2000);
shaderEffect1.setDuration(2000);
shaderEffect1.setVertexShaderCode(vertexCode1);
shaderEffect1.setFragmentShaderCode(fragmentCode2);
effectGroup.addEffect(shaderEffect1);
layer.addEffect(effectGroup);
layer.render(surfaceTexture);
```

### Function 5: keyframe

```Java
KeyframeSet keyframes = KeyframeSet.ofFloat(10000, 500, 1000, 0);
layer.setKeyframes(GLLayer.KEY_FRAMES_KEY_LAYER_WIDTH, keyframes);
keyframes = KeyframeSet.ofFloat(10000, 500, 600, 500);
layer.setKeyframes(GLLayer.KEY_FRAMES_KEY_LAYER_HEIGHT, keyframes);
effect.setKeyframes(shaderKey,  KeyframeSet.ofFloat(10000, 500, 1000, 0));
```

### Function 6: obj file rendering
![obj](https://github.com/JonaNorman/GLRenderClient/blob/main/screen/preview3.gif?raw=true "obj")



### Function 7: gaussian blur
Solve slow speed, color without gamma adjustment, blurred translucent color is black 
![gaussian blur](https://github.com/JonaNorman/GLRenderClient/blob/main/screen/preview4.gif?raw=true "gaussian blur")

```Java
    GLRenderClient client = renderThread.getRenderClient();
    GLGaussianBlurEffect gaussianBlurEffect = new GLGaussianBlurEffect(client) {};
    layer.addEffect(gaussianBlurEffect);
    gaussianBlurEffect.setBlurRadius(radius);
    gaussianBlurEffect.setBlurStep(step);
    gaussianBlurEffect.setBlurSigma(sigma);
```

### Function 8: solve various problems in OpenGL

1. Translucent objects are merged with black borders
2. The problem of texture and image upside down
3. The problem of image squashing when rotating
4. Use BlendMode to implement 15 native Xfermodes of Android
5. automatically check the success of each OpenGL command call
6. Program, Shader, Framebuffer caching  to help reduce memory
7. Support bitmaps that are not a power of 2 or a direct multiplication can also mipmap
8. Solve the problem of different screen size and texture ratio, increase GravityMode, ScaleMode


## License

See the [LICENSE](./LICENSE) file for details.