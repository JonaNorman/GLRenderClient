package com.byteplay.android.renderclient;

class GL20ViewPort extends GLViewPort {

    private GL20 gl;
    public GL20ViewPort(GLRenderClient client) {
        super(client);
        gl = client.getGL20();
    }


    @Override
    protected void onCall() {
        gl.glViewport(getX(), getY(), getWidth(), getHeight());
    }
}
