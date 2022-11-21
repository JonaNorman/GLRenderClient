# GLRenderClient

简体中文 | [English](./README_EN.md)

![GLRenderClient](screen/logo.png?raw=true "GLRenderClient")

这是一个面向Android开发的OpenGL渲染库，致力于帮助广大Android开发者降低开发成本，它可以使用在图片编辑、视频图像编辑中。

## 接入

```gradle
implementation('io.github.jonanorman.android:glrenderclient:0.2.1')
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

## 功能

### 功能1：自动解析shader参数

不用根据参数的类型调用不同api设置不同变量

```Java
GLShaderLayer shaderLayer = new GLShaderLayer(); 
shaderLayer.setShaderParam("viewSize", width,height);
shaderLayer.setShaderParam("inputTexture", textureId1);
shaderLayer.setShaderParam("inputTexture2", textureId2);
shaderLayer.setShaderParam("position", position);
```

### 功能2 异步渲染Android原生的View

![异步渲染view](screen/preview01.gif?raw=true "异步渲染view")

```Java
GLViewLayer viewLayer = new GLViewLayer(getApplicationContext());
viewLayer.setBackgroundColor(Color.WHITE);
LayoutInflater layoutInflater = LayoutInflater.from(viewLayer.getContext());
View view = layoutInflater.inflate(R.layout.layout_view_layer, null);
viewLayer.addView(view);
viewLayer.render(surfaceTexture);
```

### 功能3 多层级多时间线渲染

![多层级](screen/preview02.gif?raw=true "多层级")

```Java
GLLayerGroup rootLayer = new GLLayerGroup();
rootLayer.setDuration(TimeStamp.ofMills(10000));
GLLayerGroup group = new GLLayerGroup();
group.setDuration(TimeStamp.ofMills(5000));
group.setStartTime(TimeStamp.ofMills(5000));
rootLayer.addLayer(group);
rootLayer.render(surfaceTexture);
```

### 功能4 特效嵌套渲染

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

### 功能5 shader参数关键帧

```Java
KeyframeSet keyframes = KeyframeSet.ofFloat(10000, 500, 1000, 0);
layer.setKeyframes(GLLayer.KEY_FRAMES_WIDTH, keyframes);
keyframes = KeyframeSet.ofFloat(10000, 500, 600, 500);
layer.setKeyframes(GLLayer.KEY_FRAMES_HEIGHT, keyframes);
layer.setKeyframes(shaderKey,  KeyframeSet.ofFloat(10000, 500, 1000, 0));
```

### 功能6 View特效

给Android原生View加上特效
![View特效](screen/preview03.gif?raw=true "View特效")

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

### 功能7 obj文件渲染

![obj文件渲染](screen/preview04.gif?raw=true "obj文件渲染")

### 功能8 高斯模糊特效

![高斯模糊](screen/preview05.gif?raw=true "高斯模糊")

```Java
GLGaussianBlurEffect gaussianBlurEffect = new GLGaussianBlurEffect();
gaussianBlurEffect.setBlurRadius(radius);
gaussianBlurEffect.setBlurStep(step);
gaussianBlurEffect.setBlurSigma(sigma);
```

### 功能9 描边特效

![描边特效](screen/preview06.gif?raw=true "描边特效")

```java
GLAlphaOutlineEffect outlineEffect = new GLAlphaOutlineEffect();
outlineEffect.setMaxOutlineWidth(maxWidth);
outlineEffect.setIntensity(intensity);
outlineEffect.setOutlineStyle(style);
outlineEffect.setOutlineColor(color);
```

### 功能十 解决OpenGL疑难杂症

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

