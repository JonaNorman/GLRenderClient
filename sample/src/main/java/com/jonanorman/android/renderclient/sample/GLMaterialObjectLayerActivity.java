package com.jonanorman.android.renderclient.sample;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jonanorman.android.renderclient.EGLConfigSimpleChooser;
import com.jonanorman.android.renderclient.GLCapability;
import com.jonanorman.android.renderclient.GLLayer;
import com.jonanorman.android.renderclient.GLMaterialObjectLayer;
import com.jonanorman.android.renderclient.GLRenderClient;
import com.jonanorman.android.renderclient.GLRenderThread;
import com.jonanorman.android.renderclient.GLWindowSurface;
import com.jonanorman.android.renderclient.math.GravityMode;
import com.jonanorman.android.renderclient.math.Matrix4;
import com.jonanorman.android.renderclient.math.Quaternion;
import com.jonanorman.android.renderclient.math.Vector3;

public class GLMaterialObjectLayerActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private static final int MESSAGE_LAYER_CREATE = 1;
    private static final int MESSAGE_SURFACE_RENDER = 2;
    private static final int MESSAGE_SURFACE_MOTION_EVENT = 3;

    private GLRenderThread renderThread;
    private SurfaceTexture surfaceTexture = null;


    private Handler.Callback callback = new Handler.Callback() {

        long startTime;
        GLMaterialObjectLayer rootLayer;
        DragControl dragControl;
        Matrix4 rotationMatrix = new Matrix4();
        float rotationZ = 0;

        @Override
        public boolean handleMessage(@NonNull Message msg) {
            int what = msg.what;
            switch (what) {
                case MESSAGE_LAYER_CREATE: {
                    dragControl = new DragControl(getApplicationContext());
                    GLRenderClient renderClient = renderThread.getRenderClient();
                    rootLayer = new GLMaterialObjectLayer(renderClient);
                    rootLayer.setOnTouchListener(dragControl);
                    rootLayer.getGLEnable().add(GLCapability.DEPTH_TEST);
                    rootLayer.setMaterialObjectFile(getApplicationContext(), "andy.obj");
                    rootLayer.setMaterialTexture(getApplicationContext(), R.drawable.andy);
                    rootLayer.addTransform(new GLLayer.LayerTransform() {
                        @Override
                        public void onLayerTransform(GLLayer layer, long renderTimeMs) {
                            float scale = 4;
                            Matrix4 modelMatrix = rootLayer.getModelMatrix();
                            modelMatrix.setIdentity();
                            modelMatrix.translate(0, -0.1f, 0);
                            scale *= dragControl.getCurrentScale();
                            modelMatrix.scale(scale, scale, scale);

                            if (dragControl.isDragging()) {
                                rotationMatrix.setIdentity();
                                dragControl.currentRotation().toMatrix(rotationMatrix);
                            } else {
                                rotationMatrix.setIdentity();
                                if (rotationZ > 360) {
                                    rotationZ = 0;
                                } else {
                                    rotationZ = rotationZ + 1;
                                }
                                rotationMatrix.rotate(rotationZ, 0, 1, 0);
                            }
                            modelMatrix.postMul(rotationMatrix);
                            modelMatrix.translate(0, 0.1f, 0);
                            float aspect = layer.getRenderWidth() / layer.getRenderHeight();
                            Matrix4 viewMatrix = rootLayer.getViewMatrix();
                            viewMatrix.setIdentity();
                            viewMatrix.lookAt(0.0f, 0.0f, -2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
                            Matrix4 projectionMatrix = rootLayer.getProjectionMatrix();
                            projectionMatrix.setIdentity();
                            projectionMatrix.perspective(45, aspect, 1, 100);

                        }
                    });

                    rootLayer.setGravity(GravityMode.CENTER);
                }

                return true;
                case MESSAGE_SURFACE_MOTION_EVENT: {
                    MotionEvent motionEvent = (MotionEvent) msg.obj;
                    rootLayer.queueTouchEvent(motionEvent);
                    return true;
                }
                case MESSAGE_SURFACE_RENDER:
                    if (surfaceTexture != null) {
                        long time = 0;
                        if (startTime == 0) {
                            startTime = System.currentTimeMillis();
                        } else {
                            time = System.currentTimeMillis() - startTime;
                        }
                        GLRenderClient renderClient = renderThread.getRenderClient();
                        GLWindowSurface windowSurface = renderClient.obtainWindowSurface(surfaceTexture);
                        rootLayer.setTime(time);
                        rootLayer.render(windowSurface);
                        if (!renderThread.hasMessages(MESSAGE_SURFACE_RENDER)) {
                            renderThread.sendEmptyMessageDelayed(MESSAGE_SURFACE_RENDER, 30);
                        }
                        return true;
                    } else {
                        startTime = 0;
                    }
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.texture_view);
        TextureView textureView = findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(this);
        textureView.setOpaque(false);
        textureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Message message = Message.obtain();
                message.what = MESSAGE_SURFACE_MOTION_EVENT;
                MotionEvent motionEvent = MotionEvent.obtain(event);
                message.obj = motionEvent;
                renderThread.sendMessage(message);
                return true;
            }
        });
        GLRenderClient.Builder builder = new GLRenderClient.Builder();
        EGLConfigSimpleChooser.Builder simpleChooser = new EGLConfigSimpleChooser.Builder();
        simpleChooser.setDepthSize(8);
        builder.setEGLConfigChooser(simpleChooser.build());
        renderThread = new GLRenderThread(builder);
        renderThread.start();
        renderThread.setRenderCallback(callback);
        renderThread.sendEmptyMessage(MESSAGE_LAYER_CREATE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        renderThread.quitAndWait();
    }

    @Override
    protected void onPause() {
        super.onPause();
        renderThread.removeMessages(MESSAGE_SURFACE_RENDER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderThread.sendEmptyMessage(MESSAGE_SURFACE_RENDER);
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        renderThread.post(new Runnable() {
            @Override
            public void run() {
                surfaceTexture = surface;
            }
        });
        renderThread.sendEmptyMessage(MESSAGE_SURFACE_RENDER);
    }


    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

    }


    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }

    public static final class DragControl implements GLLayer.OnTouchListener {

        private static final int DRAG_START = 0;
        private static final int DRAG_END = 1;

        // Touch modes.
        private static final int NONE = 0;
        private static final int DRAG = 1;
        private static final int ZOOM = 2;
        private static final int ZOOM_LONG = 3;

        private static final double FLING_REDUCTION = 3000;
        private static final double DRAG_SLOWING = 90;
        // Between 0 and 1 -> 0 to stop, 1 to spin always.
        private double flingDamping = 1;

        private final float[] dragX = new float[2];
        private final float[] dragY = new float[2];


        // Old distance for the pinch-zoom.
        private float oldDist = 1f;
        // The current object scale.
        private float scale;
        // The maximum object scale.
        private final float maxScale;
        // The minimum object scale.
        private final float minScale;
        // The standard object scale.
        private final float standardScale;
        // Current touch mode.
        private int mode = NONE;
        // Base rotation, before drag.
        private final Quaternion rotation = new Quaternion(new Vector3(0, 1, 1));
        // The amount of rotation to add as part of drag.
        private final Quaternion dragRotation = new Quaternion(new Vector3(0, 1, 0));
        // Equal to rotation*dragRotation.
        private final Quaternion intermediateRotation = new Quaternion(new Vector3(0, 1, 0));
        // The current axis about which the object is being rotated
        private Vector3 spinAxis = new Vector3();

        /**
         * Flinging
         * When you flick the screen with your finger it will keep spinning.
         * How fast it is spinning on its own.
         */
        private double flingSpeed = 0;

        // The axis about which we are being flung, if any.
        private final Vector3 flingAxis = new Vector3(0, 0, 0);

        /**
         * Fling rotation we most recent added to rotation.
         * Only here to save creating new objects too often.
         */
        private final Quaternion flingRotation = new Quaternion(new Vector3(0, 1, 0));

        // The gesture detector used to detect special touch events.
        private final GestureDetector gestureDetector;


        public DragControl(Context context) {
            this(context, 0, 5);
        }

        public DragControl(final Context context, float minScale, float maxScale) {
            this.maxScale = maxScale;
            this.minScale = minScale;
            this.standardScale = 1;
            this.scale = this.standardScale;

            this.gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
                @Override
                public boolean onDown(MotionEvent motionEvent) {
                    // Do nothing here.
                    return false;
                }

                @Override
                public void onShowPress(MotionEvent motionEvent) {
                    // Do nothing here.
                }

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    // Do nothing here.
                    return false;
                }

                @Override
                public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                    // Do nothing here.
                    return false;
                }

                @Override
                public void onLongPress(MotionEvent e) {

                }

                @Override
                public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float velocityX, float velocityY) {
                    flingAxis.set(-velocityY, -velocityX, 0);
                    flingSpeed = flingAxis.len() / FLING_REDUCTION;
                    flingAxis.normalize();
                    return true;
                }
            });

            this.gestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                    // Do nothing here.
                    return false;
                }

                @Override
                public boolean onDoubleTap(MotionEvent motionEvent) {
                    resetScale();
                    return true;
                }

                @Override
                public boolean onDoubleTapEvent(MotionEvent motionEvent) {
                    // Do nothing here.
                    return false;
                }
            });
        }

        @Override
        public boolean onTouch(GLLayer layer, MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            int action = event.getAction();
            // Important use mask to distinguish pointer events (multi-touch).
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    dragX[DRAG_START] = dragX[DRAG_END] = event.getX();
                    dragY[DRAG_START] = dragY[DRAG_END] = event.getY();
                    flingSpeed = 0;
                    mode = DRAG;
                    return true;
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    if (oldDist > 20f)
                        mode = ZOOM;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                        dragX[DRAG_END] = event.getX();
                        dragY[DRAG_END] = event.getY();
                    } else if (mode == ZOOM) {
                        float newDist = spacing(event);
                        if (newDist > oldDist) {
                            if (scale < maxScale) {
                                scale += 0.1 * oldDist / newDist;
                                if (scale > maxScale) {
                                    scale = maxScale;
                                }
                            }

                        } else {
                            if (scale > minScale)
                                scale -= 0.1 * newDist / oldDist;
                            if (scale < minScale) {
                                scale = minScale;
                            }
                        }
                        oldDist = newDist;
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    float rotateX = dragX[DRAG_END] - dragX[DRAG_START];
                    float rotateY = dragY[DRAG_END] - dragY[DRAG_START];
                    if (rotateX != 0 || rotateY != 0) {
                        spinAxis = new Vector3(-rotateY, -rotateX, 0);
                        double mag = spinAxis.len();
                        spinAxis.normalize();
                        intermediateRotation.set(spinAxis, mag / DRAG_SLOWING);
                        rotation.mul(intermediateRotation);
                    }
                    dragX[DRAG_END] = dragX[DRAG_START] = 0;
                    dragY[DRAG_END] = dragY[DRAG_START] = 0;
                    mode = NONE;
                    return true;
                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    return true;
            }
            return false;
        }

        /**
         * FIXME do the actual updating in a separate method that
         * Is time-dependent.
         */
        public Quaternion currentRotation() {
            float rotateX = dragX[DRAG_END] - dragX[DRAG_START];
            float rotateY = dragY[DRAG_END] - dragY[DRAG_START];

            if (mode == DRAG && (rotateX != 0 || rotateY != 0)) {
                spinAxis.set(-rotateY, -rotateX, 0);
                double mag = spinAxis.len();
                spinAxis.normalize();

                intermediateRotation.set(spinAxis, mag / DRAG_SLOWING);
                dragRotation.set(rotation);
                dragRotation.mul(intermediateRotation);

                return dragRotation;
            } else {
                if (flingSpeed > 0) {
                    flingSpeed *= flingDamping;
                    flingRotation.set(flingAxis, flingSpeed);
                    rotation.mul(flingRotation);
                    flingSpeed = 0;
                }
                return rotation;
            }
        }

        /**
         * Retrieves current object scale.
         *
         * @return The object scale.
         */
        public float getCurrentScale() {
            return scale;
        }

        /**
         * Sets the new fling damping value with the given one.
         *
         * @param flingDamping New fling damping value.
         */
        public void setFD(double flingDamping) {
            this.flingDamping = flingDamping;
            if (this.flingDamping > 1)
                this.flingDamping = 1;
            if (this.flingDamping < 0)
                this.flingDamping = 0;
        }

        /**
         * Retrieves absolute distance between pinch zoom points.
         *
         * @param event Pinch zoom event.
         * @return Absolute distance.
         */
        private float spacing(MotionEvent event) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        }

        public boolean isDragging() {
            return mode != NONE;
        }


        /**
         * Resets the object scale.
         */
        private void resetScale() {
            scale = standardScale;
        }
    }
}