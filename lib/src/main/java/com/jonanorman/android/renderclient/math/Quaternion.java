package com.jonanorman.android.renderclient.math;

public final class Quaternion {
    private double x;
    private double y;
    private double z;
    private double w;

    public Quaternion(Vector3 axis) {
        set(axis, 0);
    }

    public void set(final Quaternion q) {
        this.x = q.x;
        this.y = q.y;
        this.z = q.z;
        this.w = q.w;
    }

    public void set(Vector3 axis, double angle) {
        double s = Math.sin(angle / 2);
        w = Math.cos(angle / 2);
        x = axis.getX() * s;
        y = axis.getY() * s;
        z = axis.getZ() * s;
    }

    public void mul(Quaternion q) {
        double nw = w * q.w - x * q.x - y * q.y - z * q.z;
        double nx = w * q.x + x * q.w + y * q.z - z * q.y;
        double ny = w * q.y + y * q.w + z * q.x - x * q.z;
        z = w * q.z + z * q.w + x * q.y - y * q.x;
        w = nw;
        x = nx;
        y = ny;
    }


    public void toMatrix(Matrix4 matrix4) {
        toMatrix(matrix4.get());
    }

    public void toMatrix(float[] matrixs) {
        matrixs[3] = 0.0f;
        matrixs[7] = 0.0f;
        matrixs[11] = 0.0f;
        matrixs[12] = 0.0f;
        matrixs[13] = 0.0f;
        matrixs[14] = 0.0f;
        matrixs[15] = 1.0f;

        matrixs[0] = (float) (1.0f - (2.0f * ((y * y) + (z * z))));
        matrixs[1] = (float) (2.0f * ((x * y) - (z * w)));
        matrixs[2] = (float) (2.0f * ((x * z) + (y * w)));

        matrixs[4] = (float) (2.0f * ((x * y) + (z * w)));
        matrixs[5] = (float) (1.0f - (2.0f * ((x * x) + (z * z))));
        matrixs[6] = (float) (2.0f * ((y * z) - (x * w)));

        matrixs[8] = (float) (2.0f * ((x * z) - (y * w)));
        matrixs[9] = (float) (2.0f * ((y * z) + (x * w)));
        matrixs[10] = (float) (1.0f - (2.0f * ((x * x) + (y * y))));
    }

}
