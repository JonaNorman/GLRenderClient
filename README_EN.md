# GLRenderClient

[![License](https://img.shields.io/badge/license-MIT-blue.svg?style=flat)](http://opensource.org/licenses/MIT)

![GLRenderClient](https://github.com/JonaNorman/GLRenderClient/blob/main/screen/logo.png?raw=true "GLRenderClient")

This is a lightweight OpenGL rendering library for Android development, dedicated to help Android developers to reduce development costs,
forget the technical details of OpenGL, it can be used in the image editing, video  editing.

## Import

```gradle
implementation('io.github.jonanorman.android:glrenderclient:0.2.0')
```

```Java
GLRenderMessage renderMessage = GLRenderMessage.obtain();
renderMessage.post(new Runnable() {
@Override
public void run() {
GLLayer layerGroup = new GLLayerGroup();
layerGroup.render(surfaceTexture);
}
});
renderMessage.recycle();
```

## Feature

### Feature1：Automatically parse shader parameters

There is no need to call different APIs to set different variables according to the type of parameters

```Java
GLShaderLayer shaderLayer = new GLShaderLayer(); 
shaderLayer.setShaderParam("viewSize", width,height);
shaderLayer.setShaderParam("inputTexture", textureId1);
shaderLayer.setShaderParam("inputTexture2", textureId2);
shaderLayer.setShaderParam("position", position);
```

### Feature2: Asynchronously render Android native View

![Render view async](screen/preview01.gif?raw=true "Render view async")

```Java
GLViewLayer viewLayer = new GLViewLayer(getApplicationContext());
viewLayer.setBackgroundColor(Color.WHITE);
LayoutInflater layoutInflater = LayoutInflater.from(viewLayer.getContext());
View view = layoutInflater.inflate(R.layout.layout_view_layer, null);
viewLayer.addView(view);
viewLayer.render(surfaceTexture);
```

### Feature3: multi-Layer multi-timeline render

![multi-Layer](screen/preview02.gif?raw=true "multi-Layer")

```Java
GLLayerGroup rootLayer = new GLLayerGroup();
rootLayer.setDuration(TimeStamp.ofMills(10000));
GLLayerGroup group = new GLLayerGroup();
group.setDuration(TimeStamp.ofMills(5000));
group.setStartTime(TimeStamp.ofMills(5000));
rootLayer.addLayer(group);
rootLayer.render(surfaceTexture);
```

### Feature4: Effect

```Java
GLEffectGroup effectGroup = new GLEffectGroup();
GLShaderEffect shaderEffect =new GLShaderEffect();
shaderEffect.setVertexShaderCode(vertexCode);
shaderEffect.setFragmentShaderCode(fragmentCode);
effectGroup.add(shaderEffect);
GLShaderEffect shaderEffect1 =new GLShaderEffect();
shaderEffect1.setStartTime(TimeStamp.ofMills(2000));
shaderEffect1.setDuration(TimeStamp.ofMills(2000));
shaderEffect1.setVertexShaderCode(vertexCode1);
shaderEffect1.setFragmentShaderCode(fragmentCode2);
effectGroup.add(shaderEffect1);
layer.addEffect(effectGroup);
layer.render(surfaceTexture);
```

### Feature5: shader keyframe

```Java
KeyframeSet keyframes = KeyframeSet.ofFloat(10000, 500, 1000, 0);
layer.setKeyframes(GLLayer.KEY_FRAMES_WIDTH, keyframes);
keyframes = KeyframeSet.ofFloat(10000, 500, 600, 500);
layer.setKeyframes(GLLayer.KEY_FRAMES_HEIGHT, keyframes);
layer.setKeyframes(shaderKey,  KeyframeSet.ofFloat(10000, 500, 1000, 0));
```

### Feature6: View Effect

Add special effects to Android native View
![View Effect](screen/preview03.gif?raw=true "View Effect")

```xml
<com.jonanorman.android.renderclient.view.GLEffectLayout
        android:id="@+id/effectView"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <FrameLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent">

            <Button
                android:id="@+id/Button"
                android:layout_width="200dp"
                android:layout_height="200dp"
                android:text="click me">

            </Button>

        </FrameLayout>

    </com.jonanorman.android.renderclient.view.GLEffectLayout>
```

```java
GLEffectLayout effectView = findViewById(R.id.effectView);
effectView.addEffect(new GLWobbleEffect());
effectView.enableRefreshMode();
```

### Feature7: obj render

![obj render](screen/preview04.gif?raw=true "obj render")

### Feature8: GaussianBlurEffect

![GaussianBlurEffect](screen/preview05.gif?raw=true "高斯模糊")

```Java
GLGaussianBlurEffect gaussianBlurEffect = new GLGaussianBlurEffect();
gaussianBlurEffect.setBlurRadius(radius);
gaussianBlurEffect.setBlurStep(step);
gaussianBlurEffect.setBlurSigma(sigma);
```

### Feature9: OutlineEffect

![OutlineEffect](screen/preview06.gif?raw=true "OutlineEffect")

```java
GLAlphaOutlineEffect outlineEffect = new GLAlphaOutlineEffect();
outlineEffect.setMaxOutlineWidth(maxWidth);
outlineEffect.setIntensity(intensity);
outlineEffect.setOutlineStyle(style);
outlineEffect.setOutlineColor(color);
```

### Feature10: Solve OpenGL intractable diseases

```
1. Translucent objects are merged with black borders
2. The problem of texture and image upside down
3. The problem of image squashing when rotating
4. Use BlendMode to implement 15 native Xfermodes of Android
5. automatically check the success of each OpenGL command call
6. Program, Shader, Framebuffer caching  to help reduce memory
7. Support bitmaps that are not a power of 2 or a direct multiplication can also mipmap
8. Solve the problem of different screen size and texture ratio, increase GravityMode, ScaleMode
```

## License

See the [LICENSE](./LICENSE) file for details.

