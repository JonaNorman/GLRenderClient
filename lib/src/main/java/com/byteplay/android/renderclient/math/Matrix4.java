package com.byteplay.android.renderclient.math;

import java.util.Arrays;
import java.util.Stack;

public class Matrix4 {
    private static final float[] MULTIPLY_MM_TEMP = new float[16];
    private final Stack<float[]> matrixStack = new Stack<>();
    private float[] val;
    private final float[] mTemp = new float[16];

    public Matrix4(float[] matrix) {
        this.val = matrix;
    }


    public Matrix4(Matrix4 matrix) {
        this.val = matrix.get();
    }


    public Matrix4() {
        float[] matrix = new float[16];
        android.opengl.Matrix.setIdentityM(matrix, 0);
        val = matrix;
    }


    public Matrix4 save() {
        matrixStack.push(Arrays.copyOf(val, 16));
        return this;
    }

    public Matrix4 restore() {
        if (matrixStack.empty())
            return this;
        val = matrixStack.pop();
        return this;
    }

    public Matrix4 lookAt(Vector3 eye, Vector3 center, Vector3 up) {
        return lookAt(eye.x, eye.y, eye.z, center.x, center.y, center.z, up.x, up.y, up.z);
    }

    public Matrix4 lookAt(float eyeX, float eyeY, float eyeZ,
                          float centerX, float centerY, float centerZ, float upX, float upY,
                          float upZ) {
        android.opengl.Matrix.setLookAtM(mTemp, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
        postMul(mTemp);
        return this;
    }

    public Matrix4 frustum(float left, float right, float bottom, float top, float near, float far) {
        android.opengl.Matrix.frustumM(mTemp, 0, left, right, bottom, top, near, far);
        postMul(mTemp);
        return this;
    }

    public Matrix4 perspective(float fovy, float aspect, float zNear, float zFar) {
        android.opengl.Matrix.perspectiveM(mTemp, 0, fovy, aspect, zNear, zFar);
        postMul(mTemp);
        return this;
    }

    public Matrix4 ortho(float left, float right, float bottom, float top, float near, float far) {
        android.opengl.Matrix.orthoM(mTemp, 0, left, right, bottom, top, near, far);
        postMul(mTemp);
        return this;

    }

    public Matrix4 rotate(float angle, float x, float y, float z) {
        while (angle >= 360.0f) {
            angle -= 360.0f;
        }
        while (angle <= -360.0f) {
            angle += 360.0f;
        }
        android.opengl.Matrix.setRotateM(mTemp, 0, angle, x, y, z);
        postMul(mTemp);
        return this;
    }

    public Matrix4 rotate(float angle, Vector3 rotate) {
        return rotate(angle, rotate.x, rotate.y, rotate.z);
    }

    public Matrix4 translate(float x, float y, float z) {
        android.opengl.Matrix.setIdentityM(mTemp, 0);
        android.opengl.Matrix.translateM(mTemp, 0, x, y, z);
        postMul(mTemp);
        return this;
    }

    public Matrix4 translate(Vector3 translate) {
        return translate(translate.x, translate.y, translate.z);
    }

    public Matrix4 translateX(float x) {
        translate(x, 0, 0);
        return this;
    }

    public Matrix4 translateY(float y) {
        translate(0, y, 0);
        return this;
    }

    public Matrix4 translateZ(float z) {
        scale(0, 0, z);
        return this;
    }

    public Matrix4 scale(Vector3 scale) {
        return scale(scale.x, scale.y, scale.z);
    }

    public Matrix4 scale(float x, float y, float z) {
        android.opengl.Matrix.setIdentityM(mTemp, 0);
        android.opengl.Matrix.scaleM(mTemp, 0, x, y, z);
        postMul(mTemp);
        return this;
    }

    public Matrix4 flipX() {
        scale(-1, 1, 1);
        return this;
    }


    public Matrix4 flipY() {
        scale(1, -1, 1);
        return this;
    }

    public Matrix4 flipZ() {
        scale(1, 1, -1);
        return this;
    }


    public Matrix4 clear() {
        android.opengl.Matrix.setIdentityM(val, 0);
        return this;
    }

    public Matrix4 preMul(Matrix4 matrix4) {
        multiplyMM(this.val, val, matrix4.get());
        return this;
    }

    public Matrix4 preMul(float[] matrix) {
        multiplyMM(this.val, this.val, matrix);
        return this;
    }

    public Matrix4 postMul(Matrix4 matrix) {
        multiplyMM(this.val, matrix.get(), this.val);
        return this;
    }


    public Matrix4 postMul(float[] matrix) {
        multiplyMM(this.val, matrix, this.val);
        return this;
    }

    public float[] get() {
        return val;
    }

    public Matrix4 set(float[] matrix) {
        System.arraycopy(matrix, 0, val, 0, val.length);
        return this;
    }

    public Matrix4 set(Matrix4 matrix4) {
        System.arraycopy(matrix4.get(), 0, val, 0, val.length);
        return this;
    }

    static void multiplyMM(float[] result, float[] left, float[] right) {
        boolean useTemp = result == left || result == right;
        if (!useTemp) {
            android.opengl.Matrix.multiplyMM(result, 0, left, 0, right, 0);
            return;
        }
        synchronized (MULTIPLY_MM_TEMP) {
            android.opengl.Matrix.multiplyMM(MULTIPLY_MM_TEMP, 0, left, 0, right, 0);
            System.arraycopy(MULTIPLY_MM_TEMP, 0, result, 0, 16);
        }
    }

}
