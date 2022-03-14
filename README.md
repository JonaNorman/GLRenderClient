# GLRenderClient

简体中文 | [English](./README_EN.md)

![GLRenderClient](https://github.com/JonaNorman/GLRenderClient/blob/main/screen/logo.png?raw=true "GLRenderClient")

这是一个面向Android开发的OpenGL渲染库，致力于帮助广大Android开发者降低开发成本，它可以使用在图片编辑、视频图像编辑中。

## 接入
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
## 功能
### 功能1：自动解析shader参数
不用根据参数的类型调用不同api设置不同变量 
**之前**
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
**现在**
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

### 功能2 异步渲染Android原生的View
![异步渲染view](https://github.com/JonaNorman/GLRenderClient/blob/main/screen/preview2.gif?raw=true "异步渲染view")

```Java
GLViewLayer viewLayer = renderClient.newLayoutLayer(getApplicationContext(), R.style.AppTheme);
viewLayer.setBackgroundColor(Color.WHITE);
LayoutInflater layoutInflater = LayoutInflater.from(viewLayer.getContext());
View view = layoutInflater.inflate(R.layout.layout_view_layer, null);
viewLayer.addView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
viewLayer.queueTouchEvent(motionEvent);
viewLayer.render(surfaceTexture);
```

### 功能3 多层级多时间线渲染
![多层级](https://github.com/JonaNorman/GLRenderClient/blob/main/screen/preview1.gif?raw=true "多层级")
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

### 功能4 特效嵌套渲染

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

### 功能5 shader参数关键帧

```Java
KeyframeSet keyframes = KeyframeSet.ofFloat(10000, 500, 1000, 0);
layer.setKeyframes(GLLayer.KEY_FRAMES_KEY_LAYER_WIDTH, keyframes);
keyframes = KeyframeSet.ofFloat(10000, 500, 600, 500);
layer.setKeyframes(GLLayer.KEY_FRAMES_KEY_LAYER_HEIGHT, keyframes);
effect.setKeyframes(shaderKey,  KeyframeSet.ofFloat(10000, 500, 1000, 0));
```

### 功能6 obj文件渲染
![obj文件渲染](https://github.com/JonaNorman/GLRenderClient/blob/main/screen/preview3.gif?raw=true "obj文件渲染")



### 功能7 高斯模糊
解决速度慢、模糊半径不能设置、强度不够、颜色没有gamma调整、模糊半透明颜色变黑的问题
![高斯模糊](https://github.com/JonaNorman/GLRenderClient/blob/main/screen/preview4.gif?raw=true "高斯模糊")

```Java
    GLRenderClient client = renderThread.getRenderClient();
    GLGaussianBlurEffect gaussianBlurEffect = new GLGaussianBlurEffect(client) {};
    layer.addEffect(gaussianBlurEffect);
    gaussianBlurEffect.setBlurRadius(radius);
    gaussianBlurEffect.setBlurStep(step);
    gaussianBlurEffect.setBlurSigma(sigma);
```

### 功能8 解决OpenGL各种疑难问题

1. 半透明物体融合有黑边的问题
2. 纹理与图像上下颠倒的问题
3. 旋转时图像压扁的问题
4. BlendMode实现Android原生的15种Xfermode
5. 定位问题难，自动检验OpenGL每个命令的调用成功情况
6. Program、Shader、Framebuffer缓存机制，帮助降低内存
7. 支持不是2的幂次方或直乘的bitmap也可以mipmap
8. 解决屏幕大小和纹理比例不一样的问题，增加GravityMode、ScaleMode

## 开源许可证

查看许可证 [LICENSE](./LICENSE).
