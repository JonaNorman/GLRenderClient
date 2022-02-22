package com.byteplay.android.renderclient;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

class GL20CallMonitor implements GL20Monitor {

    public GL20CallMonitor() {

    }

    protected void onCall() {

    }

    @Override
    public void glActiveTexture(int texture) {
        onCall();
    }

    @Override
    public void glAttachShader(int program, int shader) {
        onCall();
    }

    @Override
    public void glBindAttribLocation(int program, int index, String name) {
        onCall();
    }

    @Override
    public void glBindBuffer(int target, int buffer) {
        onCall();
    }

    @Override
    public void glBindFramebuffer(int target, int framebuffer) {
        onCall();
    }

    @Override
    public void glBindRenderbuffer(int target, int renderbuffer) {
        onCall();
    }

    @Override
    public void glBindTexture(int target, int texture) {
        onCall();
    }

    @Override
    public void glBlendColor(float red, float green, float blue, float alpha) {
        onCall();
    }

    @Override
    public void glBlendEquation(int mode) {
        onCall();
    }

    @Override
    public void glBlendEquationSeparate(int modeRGB, int modeAlpha) {
        onCall();
    }

    @Override
    public void glBlendFunc(int sfactor, int dfactor) {
        onCall();
    }

    @Override
    public void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        onCall();
    }

    @Override
    public void glBufferData(int target, int size, Buffer data, int usage) {
        onCall();
    }

    @Override
    public void glBufferSubData(int target, int offset, int size, Buffer data) {
        onCall();
    }

    @Override
    public void glCheckFramebufferStatus(int target, int status) {
        onCall();
    }

    @Override
    public void glClear(int mask) {
        onCall();
    }

    @Override
    public void glClearColor(float red, float green, float blue, float alpha) {
        onCall();
    }

    @Override
    public void glClearDepthf(float depth) {
        onCall();
    }

    @Override
    public void glClearStencil(int s) {
        onCall();
    }

    @Override
    public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        onCall();
    }

    @Override
    public void glCompileShader(int shader) {
        onCall();
    }

    @Override
    public void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border, int imageSize, Buffer data) {
        onCall();
    }

    @Override
    public void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int imageSize, Buffer data) {
        onCall();
    }

    @Override
    public void glCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height, int border) {
        onCall();
    }

    @Override
    public void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) {
        onCall();
    }

    @Override
    public void glCreateProgram(int programId) {
        onCall();
    }

    @Override
    public void glCreateShader(int type, int shaderId) {
        onCall();
    }

    @Override
    public void glCullFace(int mode) {
        onCall();
    }

    @Override
    public void glDeleteBuffers(int n, IntBuffer buffers) {
        onCall();
    }

    @Override
    public void glDeleteBuffers(int n, int[] buffers, int offset) {
        onCall();
    }

    @Override
    public void glDeleteBuffer(int buffer) {
        onCall();
    }

    @Override
    public void glDeleteFramebuffers(int n, IntBuffer framebuffers) {
        onCall();
    }

    @Override
    public void glDeleteFramebuffers(int n, int[] framebuffers, int offset) {
        onCall();
    }

    @Override
    public void glDeleteFramebuffer(int framebuffer) {
        onCall();
    }

    @Override
    public void glDeleteProgram(int program) {
        onCall();
    }

    @Override
    public void glDeleteRenderbuffers(int n, IntBuffer renderbuffers) {
        onCall();
    }

    @Override
    public void glDeleteRenderbuffers(int n, int[] renderbuffers, int offset) {
        onCall();
    }

    @Override
    public void glDeleteRenderbuffer(int renderbuffer) {
        onCall();
    }

    @Override
    public void glDeleteShader(int shader) {
        onCall();
    }

    @Override
    public void glDeleteTextures(int n, IntBuffer textures) {
        onCall();
    }

    @Override
    public void glDeleteTextures(int n, int[] textures, int offset) {
        onCall();
    }

    @Override
    public void glDeleteTexture(int texture) {
        onCall();
    }

    @Override
    public void glDepthFunc(int func) {
        onCall();
    }

    @Override
    public void glDepthMask(boolean flag) {
        onCall();
    }

    @Override
    public void glDepthRangef(float zNear, float zFar) {
        onCall();
    }

    @Override
    public void glDetachShader(int program, int shader) {
        onCall();
    }

    @Override
    public void glDisable(int cap) {
        onCall();
    }

    @Override
    public void glDisableVertexAttribArray(int index) {
        onCall();
    }

    @Override
    public void glDrawArrays(int mode, int first, int count) {
        onCall();
    }

    @Override
    public void glDrawElements(int mode, int count, int type, Buffer indices) {
        onCall();
    }

    @Override
    public void glDrawElements(int mode, int count, int type, int indices) {
        onCall();
    }

    @Override
    public void glEnable(int cap) {
        onCall();
    }

    @Override
    public void glEnableVertexAttribArray(int index) {
        onCall();
    }

    @Override
    public void glFinish() {
        onCall();
    }

    @Override
    public void glFlush() {
        onCall();
    }

    @Override
    public void glFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer) {
        onCall();
    }

    @Override
    public void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
        onCall();
    }

    @Override
    public void glFrontFace(int mode) {
        onCall();
    }

    @Override
    public void glGenBuffers(int n, IntBuffer buffers) {
        onCall();
    }

    @Override
    public void glGenBuffers(int n, int[] buffers, int offset) {
        onCall();
    }

    @Override
    public void glGenBuffer(int bufferId) {
        onCall();
    }

    @Override
    public void glGenerateMipmap(int target) {
        onCall();
    }

    @Override
    public void glGenFramebuffers(int n, IntBuffer framebuffers) {
        onCall();
    }

    @Override
    public void glGenFramebuffers(int n, int[] framebuffers, int offset) {
        onCall();
    }

    @Override
    public void glGenFramebuffer(int frameBufferId) {
        onCall();
    }

    @Override
    public void glGenRenderbuffers(int n, IntBuffer renderbuffers) {
        onCall();
    }

    @Override
    public void glGenRenderbuffer(int renderBufferId) {
        onCall();
    }

    @Override
    public void glGenTextures(int n, IntBuffer textures) {
        onCall();
    }

    @Override
    public void glGenTextures(int n, int[] textures, int offset) {
        onCall();
    }

    @Override
    public void glGenTexture(int textureId) {
        onCall();
    }

    @Override
    public void glGetActiveAttrib(int program, int index, int bufsize, int[] length, int lengthOffset, int[] size, int sizeOffset, int[] type, int typeOffset, byte[] name, int nameOffset) {
        onCall();
    }

    @Override
    public void glGetActiveAttrib(int program, int index, int[] size, int sizeOffset, int[] type, int typeOffset, String attribute) {
        onCall();
    }

    @Override
    public void glGetActiveAttrib(int program, int index, IntBuffer size, IntBuffer type, String attribute) {
        onCall();
    }

    @Override
    public void glGetActiveAttrib(int program, int index, int[] size, int[] type, String name) {
        onCall();
    }

    @Override
    public void glGetActiveUniform(int program, int index, int bufsize, int[] length, int lengthOffset, int[] size, int sizeOffset, int[] type, int typeOffset, byte[] name, int nameOffset) {
        onCall();
    }

    @Override
    public void glGetActiveUniform(int program, int index, int[] size, int sizeOffset, int[] type, int typeOffset, String name) {
        onCall();
    }

    @Override
    public void glGetActiveUniform(int program, int index, IntBuffer size, IntBuffer type, String name) {
        onCall();
    }

    @Override
    public void glGetActiveUniform(int program, int index, int[] size, int[] type, String name) {
        onCall();
    }

    @Override
    public void glGetAttachedShaders(int program, int maxcount, IntBuffer count, IntBuffer shaders) {
        onCall();
    }

    @Override
    public void glGetAttribLocation(int program, String name, int location) {
        onCall();
    }

    @Override
    public void glGetBooleanv(int pname, IntBuffer params) {
        onCall();
    }

    @Override
    public void glGetBufferParameteriv(int target, int pname, IntBuffer params) {
        onCall();
    }

    @Override
    public void glGetError(int error) {
        onCall();
    }

    @Override
    public void glGetFloatv(int pname, FloatBuffer params) {
        onCall();
    }

    @Override
    public void glGetFramebufferAttachmentParameteriv(int target, int attachment, int pname, IntBuffer params) {
        onCall();
    }

    @Override
    public void glGetIntegerv(int pname, IntBuffer params) {
        onCall();
    }

    @Override
    public void glGetInteger(int pname, int value) {
        onCall();
    }

    @Override
    public void glGetIntegerv(int pname, int[] params, int offset) {
        onCall();
    }

    @Override
    public void glGetProgramiv(int program, int pname, IntBuffer params) {
        onCall();
    }

    @Override
    public void glGetProgramiv(int program, int pname, int[] params, int offset) {
        onCall();
    }

    @Override
    public void glGetProgram(int program, int pname, int value) {
        onCall();
    }

    @Override
    public void glGetProgramInfoLog(int program, String log) {
        onCall();
    }

    @Override
    public void glGetRenderbufferParameteriv(int target, int pname, IntBuffer params) {
        onCall();
    }

    @Override
    public void glGetShaderiv(int shader, int pname, IntBuffer params) {
        onCall();
    }

    @Override
    public void glGetShaderiv(int shader, int pname, int[] params, int offset) {
        onCall();
    }

    @Override
    public void glGetShaderiv(int shader, int pname, int value) {
        onCall();
    }

    @Override
    public void glGetShaderInfoLog(int shader, String log) {
        onCall();
    }

    @Override
    public void glGetShaderPrecisionFormat(int shadertype, int precisiontype, IntBuffer range, IntBuffer precision) {
        onCall();
    }

    @Override
    public void glGetString(int name, String value) {
        onCall();
    }

    @Override
    public void glGetTexParameterfv(int target, int pname, FloatBuffer params) {
        onCall();
    }

    @Override
    public void glGetTexParameteriv(int target, int pname, IntBuffer params) {
        onCall();
    }

    @Override
    public void glGetUniformfv(int program, int location, FloatBuffer params) {
        onCall();
    }

    @Override
    public void glGetUniformiv(int program, int location, IntBuffer params) {
        onCall();
    }

    @Override
    public void glGetUniformLocation(int program, String name, int location) {
        onCall();
    }

    @Override
    public void glGetVertexAttribfv(int index, int pname, FloatBuffer params) {
        onCall();
    }

    @Override
    public void glGetVertexAttribiv(int index, int pname, IntBuffer params) {
        onCall();
    }

    @Override
    public void glHint(int target, int mode) {
        onCall();
    }

    @Override
    public void glIsBuffer(int buffer, boolean isBuffer) {
        onCall();
    }

    @Override
    public void glIsEnabled(int cap, boolean isEnable) {
        onCall();
    }

    @Override
    public void glIsFramebuffer(int framebuffer, boolean isFrameBuffer) {
        onCall();
    }

    @Override
    public void glIsProgram(int program, boolean isProgram) {
        onCall();
    }

    @Override
    public void glIsRenderbuffer(int renderbuffer, boolean isRenderBuffer) {
        onCall();
    }

    @Override
    public void glIsShader(int shader, boolean isShader) {
        onCall();
    }

    @Override
    public void glIsTexture(int texture, boolean isTexture) {
        onCall();
    }

    @Override
    public void glLineWidth(float width) {
        onCall();
    }

    @Override
    public void glLinkProgram(int program) {
        onCall();
    }

    @Override
    public void glPixelStorei(int pname, int param) {
        onCall();
    }

    @Override
    public void glPolygonOffset(float factor, float units) {
        onCall();
    }

    @Override
    public void glReadPixels(int x, int y, int width, int height, int format, int type, Buffer pixels) {
        onCall();
    }

    @Override
    public void glReleaseShaderCompiler() {
        onCall();
    }

    @Override
    public void glRenderbufferStorage(int target, int internalformat, int width, int height) {
        onCall();
    }

    @Override
    public void glSampleCoverage(float value, boolean invert) {
        onCall();
    }

    @Override
    public void glScissor(int x, int y, int width, int height) {
        onCall();
    }

    @Override
    public void glShaderBinary(int n, IntBuffer shaders, int binaryformat, Buffer binary, int length) {
        onCall();
    }

    @Override
    public void glShaderSource(int shader, String string) {
        onCall();
    }

    @Override
    public void glStencilFunc(int func, int ref, int mask) {
        onCall();
    }

    @Override
    public void glStencilFuncSeparate(int face, int func, int ref, int mask) {
        onCall();
    }

    @Override
    public void glStencilMask(int mask) {
        onCall();
    }

    @Override
    public void glStencilMaskSeparate(int face, int mask) {
        onCall();
    }

    @Override
    public void glStencilOp(int fail, int zfail, int zpass) {
        onCall();
    }

    @Override
    public void glStencilOpSeparate(int face, int fail, int zfail, int zpass) {
        onCall();
    }

    @Override
    public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, Buffer pixels) {
        onCall();
    }

    @Override
    public void glTexParameterf(int target, int pname, float param) {
        onCall();
    }

    @Override
    public void glTexParameterfv(int target, int pname, FloatBuffer params) {
        onCall();
    }

    @Override
    public void glTexParameteri(int target, int pname, int param) {
        onCall();
    }

    @Override
    public void glTexParameteriv(int target, int pname, IntBuffer params) {
        onCall();
    }

    @Override
    public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, Buffer pixels) {
        onCall();
    }

    @Override
    public void glUniform1f(int location, float x) {
        onCall();
    }

    @Override
    public void glUniform1fv(int location, int count, FloatBuffer v) {
        onCall();
    }

    @Override
    public void glUniform1fv(int location, int count, float[] v, int offset) {
        onCall();
    }

    @Override
    public void glUniform1i(int location, int x) {
        onCall();
    }

    @Override
    public void glUniform1iv(int location, int count, IntBuffer v) {
        onCall();
    }

    @Override
    public void glUniform1iv(int location, int count, int[] v, int offset) {
        onCall();
    }

    @Override
    public void glUniform2f(int location, float x, float y) {
        onCall();
    }

    @Override
    public void glUniform2fv(int location, int count, FloatBuffer v) {
        onCall();
    }

    @Override
    public void glUniform2fv(int location, int count, float[] v, int offset) {
        onCall();
    }

    @Override
    public void glUniform2i(int location, int x, int y) {
        onCall();
    }

    @Override
    public void glUniform2iv(int location, int count, IntBuffer v) {
        onCall();
    }

    @Override
    public void glUniform2iv(int location, int count, int[] v, int offset) {
        onCall();
    }

    @Override
    public void glUniform3f(int location, float x, float y, float z) {
        onCall();
    }

    @Override
    public void glUniform3fv(int location, int count, FloatBuffer v) {
        onCall();
    }

    @Override
    public void glUniform3fv(int location, int count, float[] v, int offset) {
        onCall();
    }

    @Override
    public void glUniform3i(int location, int x, int y, int z) {
        onCall();
    }

    @Override
    public void glUniform3iv(int location, int count, IntBuffer v) {
        onCall();
    }

    @Override
    public void glUniform3iv(int location, int count, int[] v, int offset) {
        onCall();
    }

    @Override
    public void glUniform4f(int location, float x, float y, float z, float w) {
        onCall();
    }

    @Override
    public void glUniform4fv(int location, int count, FloatBuffer v) {
        onCall();
    }

    @Override
    public void glUniform4fv(int location, int count, float[] v, int offset) {
        onCall();
    }

    @Override
    public void glUniform4i(int location, int x, int y, int z, int w) {
        onCall();
    }

    @Override
    public void glUniform4iv(int location, int count, IntBuffer v) {
        onCall();
    }

    @Override
    public void glUniform4iv(int location, int count, int[] v, int offset) {
        onCall();
    }

    @Override
    public void glUniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer value) {
        onCall();
    }

    @Override
    public void glUniformMatrix2fv(int location, int count, boolean transpose, float[] value, int offset) {
        onCall();
    }

    @Override
    public void glUniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer value) {
        onCall();
    }

    @Override
    public void glUniformMatrix3fv(int location, int count, boolean transpose, float[] value, int offset) {
        onCall();
    }

    @Override
    public void glUniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value) {
        onCall();
    }

    @Override
    public void glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int offset) {
        onCall();
    }

    @Override
    public void glUseProgram(int program) {
        onCall();
    }

    @Override
    public void glValidateProgram(int program) {
        onCall();
    }

    @Override
    public void glVertexAttrib1f(int indx, float x) {
        onCall();
    }

    @Override
    public void glVertexAttrib1fv(int indx, FloatBuffer values) {
        onCall();
    }

    @Override
    public void glVertexAttrib2f(int indx, float x, float y) {
        onCall();
    }

    @Override
    public void glVertexAttrib2fv(int indx, FloatBuffer values) {
        onCall();
    }

    @Override
    public void glVertexAttrib3f(int indx, float x, float y, float z) {
        onCall();
    }

    @Override
    public void glVertexAttrib3fv(int indx, FloatBuffer values) {
        onCall();
    }

    @Override
    public void glVertexAttrib4f(int indx, float x, float y, float z, float w) {
        onCall();
    }

    @Override
    public void glVertexAttrib4fv(int indx, FloatBuffer values) {
        onCall();
    }

    @Override
    public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, Buffer ptr) {
        onCall();
    }

    @Override
    public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int ptr) {
        onCall();
    }

    @Override
    public void glViewport(int x, int y, int width, int height) {
        onCall();
    }

}
