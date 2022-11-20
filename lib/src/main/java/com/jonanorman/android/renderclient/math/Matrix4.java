package com.jonanorman.android.renderclient.math;

import android.opengl.Matrix;

import java.util.Arrays;
import java.util.Stack;

public class Matrix4 implements Cloneable {
    private static final float[] MULTIPLY_MM_TEMP = new float[16];
    private static final float[] POINT_TEMP = new float[4];
    private static final float[] ANDROID_MATRIX_TEMP = new float[9];

    private final Stack<float[]> matrixStack = new Stack<>();
    private final float[] temp = new float[16];
    private Matrix4 graphicsMatrixToOpenglMatrixTemp;

    private float[] value = new float[16];

    public Matrix4(float[] matrix) {
        set(matrix);
    }


    public Matrix4(Matrix4 matrix) {
        set(matrix.get());
    }

    public Matrix4(android.graphics.Matrix matrix) {
        set(matrix);
    }


    public Matrix4() {
        clearIdentity();
    }


    public Matrix4 save() {
        matrixStack.push(Arrays.copyOf(value, 16));
        return this;
    }

    public Matrix4 restore() {
        if (matrixStack.empty())
            return this;
        value = matrixStack.pop();
        return this;
    }

    public Matrix4 lookAt(Vector3 eye, Vector3 center, Vector3 up) {
        return lookAt(eye.getX(), eye.getY(), eye.getZ(), center.getX(), center.getY(), center.getZ(), up.getX(), up.getY(), up.getZ());
    }

    public Matrix4 lookAt(float eyeX, float eyeY, float eyeZ,
                          float centerX, float centerY, float centerZ, float upX, float upY,
                          float upZ) {
        Matrix.setLookAtM(temp, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
        postMul(temp);
        return this;
    }

    public Matrix4 frustum(float left, float right, float bottom, float top, float near, float far) {
        Matrix.frustumM(temp, 0, left, right, bottom, top, near, far);
        postMul(temp);
        return this;
    }

    public Matrix4 perspective(float fovy, float aspect, float zNear, float zFar) {
        Matrix.perspectiveM(temp, 0, fovy, aspect, zNear, zFar);
        postMul(temp);
        return this;
    }

    public Matrix4 ortho(float left, float right, float bottom, float top, float near, float far) {
        Matrix.orthoM(temp, 0, left, right, bottom, top, near, far);
        postMul(temp);
        return this;

    }

    public Matrix4 rotate(float angle, float x, float y, float z) {
        while (angle >= 360.0f) {
            angle -= 360.0f;
        }
        while (angle <= -360.0f) {
            angle += 360.0f;
        }
        Matrix.setRotateM(temp, 0, angle, x, y, z);
        postMul(temp);
        return this;
    }

    public Matrix4 rotate(float angle, Vector3 rotate) {
        return rotate(angle, rotate.getX(), rotate.getY(), rotate.getZ());
    }

    public Matrix4 translate(float x, float y, float z) {
        Matrix.setIdentityM(temp, 0);
        Matrix.translateM(temp, 0, x, y, z);
        postMul(temp);
        return this;
    }

    public Matrix4 translate(Vector3 translate) {
        return translate(translate.getX(), translate.getY(), translate.getZ());
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
        return scale(scale.getX(), scale.getY(), scale.getZ());
    }


    public Matrix4 scaleX(float scale) {
        return scale(scale, 1, 1);
    }

    public Matrix4 scaleY(float scale) {
        return scale(1, scale, 1);
    }

    public Matrix4 scaleZ(float scale) {
        return scale(1, 1, scale);
    }

    public Matrix4 scale(float x, float y, float z) {
        Matrix.setIdentityM(temp, 0);
        Matrix.scaleM(temp, 0, x, y, z);
        postMul(temp);
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


    public Matrix4 clearIdentity() {
        Matrix.setIdentityM(value, 0);
        return this;
    }

    public Matrix4 preMul(Matrix4 matrix4) {
        multiplyMM(this.value, value, matrix4.get());
        return this;
    }

    public Matrix4 preMul(float[] matrix) {
        multiplyMM(this.value, this.value, matrix);
        return this;
    }

    public Matrix4 postMul(Matrix4 matrix) {
        multiplyMM(this.value, matrix.get(), this.value);
        return this;
    }


    public Matrix4 postMul(float[] matrix) {
        multiplyMM(this.value, matrix, this.value);
        return this;
    }

    public Matrix4 postMul(android.graphics.Matrix matrix) {
        if (graphicsMatrixToOpenglMatrixTemp == null) {
            graphicsMatrixToOpenglMatrixTemp = new Matrix4();
        }
        graphicsMatrixToOpenglMatrixTemp.set(matrix);
        return postMul(graphicsMatrixToOpenglMatrixTemp);
    }

    public float[] get() {
        return value;
    }

    public Matrix4 set(float[] matrix) {
        System.arraycopy(matrix, 0, value, 0, value.length);
        return this;
    }

    public Matrix4 set(Matrix4 matrix4) {
        System.arraycopy(matrix4.get(), 0, value, 0, value.length);
        return this;
    }


    public Matrix4 set(android.graphics.Matrix matrix) {
        synchronized (ANDROID_MATRIX_TEMP) {
            float[] values = ANDROID_MATRIX_TEMP;
            matrix.getValues(values);
            value[0] = values[0 * 3 + 0];
            value[1] = values[1 * 3 + 0];
            value[2] = 0;
            value[3] = values[2 * 3 + 0];

            value[4] = values[0 * 3 + 1];
            value[5] = values[1 * 3 + 1];
            value[6] = 0;
            value[7] = values[2 * 3 + 1];

            value[8] = 0;
            value[9] = 0;
            value[10] = 1;
            value[11] = 0;

            value[12] = values[0 * 3 + 2];
            value[13] = values[1 * 3 + 2];
            value[14] = 0;
            value[15] = values[2 * 3 + 2];
        }
        return this;
    }

    public void invert(Matrix4 matrix4) {
        boolean success = Matrix.invertM(matrix4.get(), 0, value, 0);
        if (!success) {
            matrix4.clearIdentity();
        }
    }

    public void mapPoints(float[] point) {
        multiplyMV(point, value, point);
    }

    public void mapPoints(float[] resultPoint, float[] point) {
        multiplyMV(resultPoint, value, point);
    }

    @Override
    public Matrix4 clone() {
        Matrix4 matrix4 = new Matrix4(value);
        return matrix4;
    }

    static void multiplyMM(float[] result, float[] left, float[] right) {
        boolean useTemp = result == left || result == right;
        if (!useTemp) {
            Matrix.multiplyMM(result, 0, left, 0, right, 0);
            return;
        }
        synchronized (MULTIPLY_MM_TEMP) {
            Matrix.multiplyMM(MULTIPLY_MM_TEMP, 0, left, 0, right, 0);
            System.arraycopy(MULTIPLY_MM_TEMP, 0, result, 0, 16);
        }
    }

    static void multiplyMV(float[] resultPoint, float[] matrix, float[] point) {
        boolean useTemp = resultPoint == point;
        if (!useTemp) {
            Matrix.multiplyMV(resultPoint, 0, matrix, 0, point, 0);
            return;
        }
        synchronized (POINT_TEMP) {
            Matrix.multiplyMV(POINT_TEMP, 0, matrix, 0, point, 0);
            System.arraycopy(POINT_TEMP, 0, resultPoint, 0, 4);
        }
    }

}
