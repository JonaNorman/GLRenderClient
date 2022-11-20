package com.jonanorman.android.renderclient.opengl.gl20;

import com.jonanorman.android.renderclient.opengl.GL;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public interface GL20 extends GL {

    int GL_DEPTH_BUFFER_BIT = 0x00000100;
    int GL_STENCIL_BUFFER_BIT = 0x00000400;
    int GL_COLOR_BUFFER_BIT = 0x00004000;
    int GL_FALSE = 0;
    int GL_TRUE = 1;
    int GL_POINTS = 0x0000;
    int GL_LINES = 0x0001;
    int GL_LINE_LOOP = 0x0002;
    int GL_LINE_STRIP = 0x0003;
    int GL_TRIANGLES = 0x0004;
    int GL_TRIANGLE_STRIP = 0x0005;
    int GL_TRIANGLE_FAN = 0x0006;
    int GL_ZERO = 0;
    int GL_ONE = 1;
    int GL_SRC_COLOR = 0x0300;
    int GL_ONE_MINUS_SRC_COLOR = 0x0301;
    int GL_SRC_ALPHA = 0x0302;
    int GL_ONE_MINUS_SRC_ALPHA = 0x0303;
    int GL_DST_ALPHA = 0x0304;
    int GL_ONE_MINUS_DST_ALPHA = 0x0305;
    int GL_DST_COLOR = 0x0306;
    int GL_ONE_MINUS_DST_COLOR = 0x0307;
    int GL_SRC_ALPHA_SATURATE = 0x0308;
    int GL_FUNC_ADD = 0x8006;
    int GL_BLEND_EQUATION = 0x8009;
    int GL_BLEND_EQUATION_RGB = 0x8009;
    int GL_BLEND_EQUATION_ALPHA = 0x883D;
    int GL_FUNC_SUBTRACT = 0x800A;
    int GL_FUNC_REVERSE_SUBTRACT = 0x800B;
    int GL_BLEND_DST_RGB = 0x80C8;
    int GL_BLEND_SRC_RGB = 0x80C9;
    int GL_BLEND_DST_ALPHA = 0x80CA;
    int GL_BLEND_SRC_ALPHA = 0x80CB;
    int GL_CONSTANT_COLOR = 0x8001;
    int GL_ONE_MINUS_CONSTANT_COLOR = 0x8002;
    int GL_CONSTANT_ALPHA = 0x8003;
    int GL_ONE_MINUS_CONSTANT_ALPHA = 0x8004;
    int GL_BLEND_COLOR = 0x8005;
    int GL_ARRAY_BUFFER = 0x8892;
    int GL_ELEMENT_ARRAY_BUFFER = 0x8893;
    int GL_ARRAY_BUFFER_BINDING = 0x8894;
    int GL_ELEMENT_ARRAY_BUFFER_BINDING = 0x8895;
    int GL_STREAM_DRAW = 0x88E0;
    int GL_STATIC_DRAW = 0x88E4;
    int GL_DYNAMIC_DRAW = 0x88E8;
    int GL_BUFFER_SIZE = 0x8764;
    int GL_BUFFER_USAGE = 0x8765;
    int GL_CURRENT_VERTEX_ATTRIB = 0x8626;
    int GL_FRONT = 0x0404;
    int GL_BACK = 0x0405;
    int GL_FRONT_AND_BACK = 0x0408;
    int GL_TEXTURE_2D = 0x0DE1;
    int GL_CULL_FACE = 0x0B44;
    int GL_BLEND = 0x0BE2;
    int GL_DITHER = 0x0BD0;
    int GL_STENCIL_TEST = 0x0B90;
    int GL_DEPTH_TEST = 0x0B71;
    int GL_SCISSOR_TEST = 0x0C11;
    int GL_POLYGON_OFFSET_FILL = 0x8037;
    int GL_SAMPLE_ALPHA_TO_COVERAGE = 0x809E;
    int GL_SAMPLE_COVERAGE = 0x80A0;
    int GL_NO_ERROR = 0;
    int GL_INVALID_ENUM = 0x0500;
    int GL_INVALID_VALUE = 0x0501;
    int GL_INVALID_OPERATION = 0x0502;
    int GL_OUT_OF_MEMORY = 0x0505;
    int GL_CW = 0x0900;
    int GL_CCW = 0x0901;
    int GL_LINE_WIDTH = 0x0B21;
    int GL_ALIASED_POINT_SIZE_RANGE = 0x846D;
    int GL_ALIASED_LINE_WIDTH_RANGE = 0x846E;
    int GL_CULL_FACE_MODE = 0x0B45;
    int GL_FRONT_FACE = 0x0B46;
    int GL_DEPTH_RANGE = 0x0B70;
    int GL_DEPTH_WRITEMASK = 0x0B72;
    int GL_DEPTH_CLEAR_VALUE = 0x0B73;
    int GL_DEPTH_FUNC = 0x0B74;
    int GL_STENCIL_CLEAR_VALUE = 0x0B91;
    int GL_STENCIL_FUNC = 0x0B92;
    int GL_STENCIL_FAIL = 0x0B94;
    int GL_STENCIL_PASS_DEPTH_FAIL = 0x0B95;
    int GL_STENCIL_PASS_DEPTH_PASS = 0x0B96;
    int GL_STENCIL_REF = 0x0B97;
    int GL_STENCIL_VALUE_MASK = 0x0B93;
    int GL_STENCIL_WRITEMASK = 0x0B98;
    int GL_STENCIL_BACK_FUNC = 0x8800;
    int GL_STENCIL_BACK_FAIL = 0x8801;
    int GL_STENCIL_BACK_PASS_DEPTH_FAIL = 0x8802;
    int GL_STENCIL_BACK_PASS_DEPTH_PASS = 0x8803;
    int GL_STENCIL_BACK_REF = 0x8CA3;
    int GL_STENCIL_BACK_VALUE_MASK = 0x8CA4;
    int GL_STENCIL_BACK_WRITEMASK = 0x8CA5;
    int GL_VIEWPORT = 0x0BA2;
    int GL_SCISSOR_BOX = 0x0C10;
    int GL_COLOR_CLEAR_VALUE = 0x0C22;
    int GL_COLOR_WRITEMASK = 0x0C23;
    int GL_UNPACK_ALIGNMENT = 0x0CF5;
    int GL_PACK_ALIGNMENT = 0x0D05;
    int GL_MAX_TEXTURE_SIZE = 0x0D33;
    int GL_MAX_TEXTURE_UNITS = 0x84E2;
    int GL_MAX_VIEWPORT_DIMS = 0x0D3A;
    int GL_SUBPIXEL_BITS = 0x0D50;
    int GL_RED_BITS = 0x0D52;
    int GL_GREEN_BITS = 0x0D53;
    int GL_BLUE_BITS = 0x0D54;
    int GL_ALPHA_BITS = 0x0D55;
    int GL_DEPTH_BITS = 0x0D56;
    int GL_STENCIL_BITS = 0x0D57;
    int GL_POLYGON_OFFSET_UNITS = 0x2A00;
    int GL_POLYGON_OFFSET_FACTOR = 0x8038;
    int GL_TEXTURE_BINDING_2D = 0x8069;
    int GL_SAMPLE_BUFFERS = 0x80A8;
    int GL_SAMPLES = 0x80A9;
    int GL_SAMPLE_COVERAGE_VALUE = 0x80AA;
    int GL_SAMPLE_COVERAGE_INVERT = 0x80AB;
    int GL_NUM_COMPRESSED_TEXTURE_FORMATS = 0x86A2;
    int GL_COMPRESSED_TEXTURE_FORMATS = 0x86A3;
    int GL_DONT_CARE = 0x1100;
    int GL_FASTEST = 0x1101;
    int GL_NICEST = 0x1102;
    int GL_GENERATE_MIPMAP = 0x8191;
    int GL_GENERATE_MIPMAP_HINT = 0x8192;
    int GL_BYTE = 0x1400;
    int GL_UNSIGNED_BYTE = 0x1401;
    int GL_SHORT = 0x1402;
    int GL_UNSIGNED_SHORT = 0x1403;
    int GL_INT = 0x1404;
    int GL_UNSIGNED_INT = 0x1405;
    int GL_FLOAT = 0x1406;
    int GL_FIXED = 0x140C;
    int GL_DEPTH_COMPONENT = 0x1902;
    int GL_ALPHA = 0x1906;
    int GL_RGB = 0x1907;
    int GL_RGBA = 0x1908;
    int GL_LUMINANCE = 0x1909;
    int GL_LUMINANCE_ALPHA = 0x190A;
    int GL_UNSIGNED_SHORT_4_4_4_4 = 0x8033;
    int GL_UNSIGNED_SHORT_5_5_5_1 = 0x8034;
    int GL_UNSIGNED_SHORT_5_6_5 = 0x8363;
    int GL_FRAGMENT_SHADER = 0x8B30;
    int GL_VERTEX_SHADER = 0x8B31;
    int GL_MAX_VERTEX_ATTRIBS = 0x8869;
    int GL_MAX_VERTEX_UNIFORM_VECTORS = 0x8DFB;
    int GL_MAX_VARYING_VECTORS = 0x8DFC;
    int GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS = 0x8B4D;
    int GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS = 0x8B4C;
    int GL_MAX_TEXTURE_IMAGE_UNITS = 0x8872;
    int GL_MAX_FRAGMENT_UNIFORM_VECTORS = 0x8DFD;
    int GL_SHADER_TYPE = 0x8B4F;
    int GL_DELETE_STATUS = 0x8B80;
    int GL_LINK_STATUS = 0x8B82;
    int GL_VALIDATE_STATUS = 0x8B83;
    int GL_ATTACHED_SHADERS = 0x8B85;
    int GL_ACTIVE_UNIFORMS = 0x8B86;
    int GL_ACTIVE_UNIFORM_MAX_LENGTH = 0x8B87;
    int GL_ACTIVE_ATTRIBUTES = 0x8B89;
    int GL_ACTIVE_ATTRIBUTE_MAX_LENGTH = 0x8B8A;
    int GL_SHADING_LANGUAGE_VERSION = 0x8B8C;
    int GL_CURRENT_PROGRAM = 0x8B8D;
    int GL_NEVER = 0x0200;
    int GL_LESS = 0x0201;
    int GL_EQUAL = 0x0202;
    int GL_LEQUAL = 0x0203;
    int GL_GREATER = 0x0204;
    int GL_NOTEQUAL = 0x0205;
    int GL_GEQUAL = 0x0206;
    int GL_ALWAYS = 0x0207;
    int GL_KEEP = 0x1E00;
    int GL_REPLACE = 0x1E01;
    int GL_INCR = 0x1E02;
    int GL_DECR = 0x1E03;
    int GL_INVERT = 0x150A;
    int GL_INCR_WRAP = 0x8507;
    int GL_DECR_WRAP = 0x8508;
    int GL_VENDOR = 0x1F00;
    int GL_RENDERER = 0x1F01;
    int GL_VERSION = 0x1F02;
    int GL_EXTENSIONS = 0x1F03;
    int GL_NEAREST = 0x2600;
    int GL_LINEAR = 0x2601;
    int GL_NEAREST_MIPMAP_NEAREST = 0x2700;
    int GL_LINEAR_MIPMAP_NEAREST = 0x2701;
    int GL_NEAREST_MIPMAP_LINEAR = 0x2702;
    int GL_LINEAR_MIPMAP_LINEAR = 0x2703;
    int GL_TEXTURE_MAG_FILTER = 0x2800;
    int GL_TEXTURE_MIN_FILTER = 0x2801;
    int GL_TEXTURE_WRAP_S = 0x2802;
    int GL_TEXTURE_WRAP_T = 0x2803;
    int GL_TEXTURE = 0x1702;
    int GL_TEXTURE_CUBE_MAP = 0x8513;
    int GL_TEXTURE_BINDING_CUBE_MAP = 0x8514;
    int GL_TEXTURE_CUBE_MAP_POSITIVE_X = 0x8515;
    int GL_TEXTURE_CUBE_MAP_NEGATIVE_X = 0x8516;
    int GL_TEXTURE_CUBE_MAP_POSITIVE_Y = 0x8517;
    int GL_TEXTURE_CUBE_MAP_NEGATIVE_Y = 0x8518;
    int GL_TEXTURE_CUBE_MAP_POSITIVE_Z = 0x8519;
    int GL_TEXTURE_CUBE_MAP_NEGATIVE_Z = 0x851A;
    int GL_MAX_CUBE_MAP_TEXTURE_SIZE = 0x851C;
    int GL_TEXTURE0 = 0x84C0;
    int GL_TEXTURE1 = 0x84C1;
    int GL_TEXTURE2 = 0x84C2;
    int GL_TEXTURE3 = 0x84C3;
    int GL_TEXTURE4 = 0x84C4;
    int GL_TEXTURE5 = 0x84C5;
    int GL_TEXTURE6 = 0x84C6;
    int GL_TEXTURE7 = 0x84C7;
    int GL_TEXTURE8 = 0x84C8;
    int GL_TEXTURE9 = 0x84C9;
    int GL_TEXTURE10 = 0x84CA;
    int GL_TEXTURE11 = 0x84CB;
    int GL_TEXTURE12 = 0x84CC;
    int GL_TEXTURE13 = 0x84CD;
    int GL_TEXTURE14 = 0x84CE;
    int GL_TEXTURE15 = 0x84CF;
    int GL_TEXTURE16 = 0x84D0;
    int GL_TEXTURE17 = 0x84D1;
    int GL_TEXTURE18 = 0x84D2;
    int GL_TEXTURE19 = 0x84D3;
    int GL_TEXTURE20 = 0x84D4;
    int GL_TEXTURE21 = 0x84D5;
    int GL_TEXTURE22 = 0x84D6;
    int GL_TEXTURE23 = 0x84D7;
    int GL_TEXTURE24 = 0x84D8;
    int GL_TEXTURE25 = 0x84D9;
    int GL_TEXTURE26 = 0x84DA;
    int GL_TEXTURE27 = 0x84DB;
    int GL_TEXTURE28 = 0x84DC;
    int GL_TEXTURE29 = 0x84DD;
    int GL_TEXTURE30 = 0x84DE;
    int GL_TEXTURE31 = 0x84DF;
    int GL_ACTIVE_TEXTURE = 0x84E0;
    int GL_REPEAT = 0x2901;
    int GL_CLAMP_TO_EDGE = 0x812F;
    int GL_MIRRORED_REPEAT = 0x8370;
    int GL_FLOAT_VEC2 = 0x8B50;
    int GL_FLOAT_VEC3 = 0x8B51;
    int GL_FLOAT_VEC4 = 0x8B52;
    int GL_INT_VEC2 = 0x8B53;
    int GL_INT_VEC3 = 0x8B54;
    int GL_INT_VEC4 = 0x8B55;
    int GL_BOOL = 0x8B56;
    int GL_BOOL_VEC2 = 0x8B57;
    int GL_BOOL_VEC3 = 0x8B58;
    int GL_BOOL_VEC4 = 0x8B59;
    int GL_FLOAT_MAT2 = 0x8B5A;
    int GL_FLOAT_MAT3 = 0x8B5B;
    int GL_FLOAT_MAT4 = 0x8B5C;
    int GL_SAMPLER_2D = 0x8B5E;
    int GL_SAMPLER_CUBE = 0x8B60;
    int GL_VERTEX_ATTRIB_ARRAY_ENABLED = 0x8622;
    int GL_VERTEX_ATTRIB_ARRAY_SIZE = 0x8623;
    int GL_VERTEX_ATTRIB_ARRAY_STRIDE = 0x8624;
    int GL_VERTEX_ATTRIB_ARRAY_TYPE = 0x8625;
    int GL_VERTEX_ATTRIB_ARRAY_NORMALIZED = 0x886A;
    int GL_VERTEX_ATTRIB_ARRAY_POINTER = 0x8645;
    int GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING = 0x889F;
    int GL_IMPLEMENTATION_COLOR_READ_TYPE = 0x8B9A;
    int GL_IMPLEMENTATION_COLOR_READ_FORMAT = 0x8B9B;
    int GL_COMPILE_STATUS = 0x8B81;
    int GL_INFO_LOG_LENGTH = 0x8B84;
    int GL_SHADER_SOURCE_LENGTH = 0x8B88;
    int GL_SHADER_COMPILER = 0x8DFA;
    int GL_SHADER_BINARY_FORMATS = 0x8DF8;
    int GL_NUM_SHADER_BINARY_FORMATS = 0x8DF9;
    int GL_LOW_FLOAT = 0x8DF0;
    int GL_MEDIUM_FLOAT = 0x8DF1;
    int GL_HIGH_FLOAT = 0x8DF2;
    int GL_LOW_INT = 0x8DF3;
    int GL_MEDIUM_INT = 0x8DF4;
    int GL_HIGH_INT = 0x8DF5;
    int GL_FRAMEBUFFER = 0x8D40;
    int GL_RENDERBUFFER = 0x8D41;
    int GL_RGBA4 = 0x8056;
    int GL_RGB5_A1 = 0x8057;
    int GL_RGB565 = 0x8D62;
    int GL_DEPTH_COMPONENT16 = 0x81A5;
    int GL_STENCIL_INDEX = 0x1901;
    int GL_STENCIL_INDEX8 = 0x8D48;
    int GL_RENDERBUFFER_WIDTH = 0x8D42;
    int GL_RENDERBUFFER_HEIGHT = 0x8D43;
    int GL_RENDERBUFFER_INTERNAL_FORMAT = 0x8D44;
    int GL_RENDERBUFFER_RED_SIZE = 0x8D50;
    int GL_RENDERBUFFER_GREEN_SIZE = 0x8D51;
    int GL_RENDERBUFFER_BLUE_SIZE = 0x8D52;
    int GL_RENDERBUFFER_ALPHA_SIZE = 0x8D53;
    int GL_RENDERBUFFER_DEPTH_SIZE = 0x8D54;
    int GL_RENDERBUFFER_STENCIL_SIZE = 0x8D55;
    int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE = 0x8CD0;
    int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME = 0x8CD1;
    int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL = 0x8CD2;
    int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE = 0x8CD3;
    int GL_COLOR_ATTACHMENT0 = 0x8CE0;
    int GL_DEPTH_ATTACHMENT = 0x8D00;
    int GL_STENCIL_ATTACHMENT = 0x8D20;
    int GL_NONE = 0;
    int GL_FRAMEBUFFER_COMPLETE = 0x8CD5;
    int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 0x8CD6;
    int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 0x8CD7;
    int GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS = 0x8CD9;
    int GL_FRAMEBUFFER_UNSUPPORTED = 0x8CDD;
    int GL_FRAMEBUFFER_BINDING = 0x8CA6;
    int GL_RENDERBUFFER_BINDING = 0x8CA7;
    int GL_MAX_RENDERBUFFER_SIZE = 0x84E8;
    int GL_INVALID_FRAMEBUFFER_OPERATION = 0x0506;
    int GL_VERTEX_PROGRAM_POINT_SIZE = 0x8642;

    // Extensions
    int GL_COVERAGE_BUFFER_BIT_NV = 0x8000;
    int GL_TEXTURE_MAX_ANISOTROPY_EXT = 0x84FE;
    int GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT = 0x84FF;
    int GL_TEXTURE_EXTERNAL_OES = 0x8D65;
    int GL_SAMPLER_EXTERNAL_OES = 0x8D66;
    int GL_TEXTURE_BINDING_EXTERNAL_OES = 0x8D67;

    void glActiveTexture(int texture);

    void glAttachShader(int program, int shader);

    void glBindAttribLocation(int program, int index, String name);

    void glBindBuffer(int target, int buffer);

    void glBindFramebuffer(int target, int framebuffer);

    void glBindRenderbuffer(int target, int renderbuffer);

    void glBindTexture(int target, int texture);

    void glBlendColor(float red, float green, float blue, float alpha);

    void glBlendEquation(int mode);

    void glBlendEquationSeparate(int modeRGB, int modeAlpha);

    void glBlendFunc(int sfactor, int dfactor);

    void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha);

    void glBufferData(int target, int size, Buffer data, int usage);

    void glBufferSubData(int target, int offset, int size, Buffer data);

    int glCheckFramebufferStatus(int target);

    void glClear(int mask);

    void glClearColor(float red, float green, float blue, float alpha);

    void glClearDepthf(float depth);

    void glClearStencil(int s);

    void glColorMask(boolean red, boolean green, boolean blue, boolean alpha);

    void glCompileShader(int shader);

    void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border,
                                int imageSize, Buffer data);

    void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format,
                                   int imageSize, Buffer data);

    void glCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height, int border);

    void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height);

    int glCreateProgram();

    int glCreateShader(int type);

    void glCullFace(int mode);

    void glDeleteBuffers(int n, IntBuffer buffers);

    void glDeleteBuffers(int n,
                         int[] buffers,
                         int offset);

    void glDeleteBuffer(int buffer);

    void glDeleteFramebuffers(int n, IntBuffer framebuffers);

    void glDeleteFramebuffers(int n, int[] framebuffers, int offset);

    void glDeleteFramebuffer(int framebuffer);

    void glDeleteProgram(int program);

    void glDeleteRenderbuffers(int n, IntBuffer renderbuffers);

    void glDeleteRenderbuffers(int n, int[] renderbuffers, int offset);

    void glDeleteRenderbuffer(int renderbuffer);

    void glDeleteShader(int shader);

    void glDeleteTextures(int n, IntBuffer textures);

    void glDeleteTextures(int n, int[] textures, int offset);

    void glDeleteTexture(int texture);

    void glDepthFunc(int func);

    void glDepthMask(boolean flag);

    void glDepthRangef(float zNear, float zFar);

    void glDetachShader(int program, int shader);

    void glDisable(int cap);

    void glDisableVertexAttribArray(int index);

    void glDrawArrays(int mode, int first, int count);

    void glDrawElements(int mode, int count, int type, Buffer indices);

    void glDrawElements(int mode, int count, int type, int indices);

    void glEnable(int cap);

    void glEnableVertexAttribArray(int index);

    void glFinish();

    void glFlush();

    void glFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer);

    void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level);

    void glFrontFace(int mode);

    void glGenBuffers(int n, IntBuffer buffers);

    void glGenBuffers(int n, int[] buffers, int offset);

    int glGenBuffer();

    void glGenerateMipmap(int target);

    void glGenFramebuffers(int n, IntBuffer framebuffers);

    void glGenFramebuffers(int n, int[] framebuffers, int offset);

    int glGenFramebuffer();

    void glGenRenderbuffers(int n, IntBuffer renderbuffers);

    int glGenRenderbuffer();

    void glGenTextures(int n, IntBuffer textures);

    void glGenTextures(int n, int[] textures, int offset);

    int glGenTexture();

    void glGetActiveAttrib(
            int program,
            int index,
            int bufsize,
            int[] length,
            int lengthOffset,
            int[] size,
            int sizeOffset,
            int[] type,
            int typeOffset,
            byte[] name,
            int nameOffset);

    String glGetActiveAttrib(
            int program,
            int index,
            int[] size,
            int sizeOffset,
            int[] type,
            int typeOffset
    );

    String glGetActiveAttrib(
            int program,
            int index,
            IntBuffer size,
            IntBuffer type);

    String glGetActiveAttrib(int program, int index, int[] size, int[] type);

    void glGetActiveUniform(
            int program,
            int index,
            int bufsize,
            int[] length,
            int lengthOffset,
            int[] size,
            int sizeOffset,
            int[] type,
            int typeOffset,
            byte[] name,
            int nameOffset);

    String glGetActiveUniform(
            int program,
            int index,
            int[] size,
            int sizeOffset,
            int[] type,
            int typeOffset
    );

    String glGetActiveUniform(
            int program,
            int index,
            IntBuffer size,
            IntBuffer type);

    String glGetActiveUniform(int program, int index, int[] size, int[] type);

    void glGetAttachedShaders(int program, int maxcount, IntBuffer count, IntBuffer shaders);

    int glGetAttribLocation(int program, String name);

    void glGetBooleanv(int pname, IntBuffer params);

    void glGetBufferParameteriv(int target, int pname, IntBuffer params);

    int glGetError();

    void glGetFloatv(int pname, FloatBuffer params);

    void glGetFramebufferAttachmentParameteriv(int target, int attachment, int pname, IntBuffer params);

    int glGetInteger(int pname);

    void glGetIntegerv(int pname, IntBuffer params);

    void glGetIntegerv(int pname, int[] params, int offset);

    void glGetProgramiv(int program, int pname, IntBuffer params);

    void glGetProgramiv(int program,
                        int pname,
                        int[] params,
                        int offset);

    int glGetProgram(int program,
                     int pname);

    String glGetProgramInfoLog(int program);

    void glGetRenderbufferParameteriv(int target, int pname, IntBuffer params);

    void glGetShaderiv(int shader, int pname, IntBuffer params);

    void glGetShaderiv(int shader, int pname, int[] params, int offset);

    int glGetShaderiv(int shader, int pname);

    String glGetShaderInfoLog(int shader);

    void glGetShaderPrecisionFormat(int shadertype, int precisiontype, IntBuffer range, IntBuffer precision);

    String glGetString(int name);

    void glGetTexParameterfv(int target, int pname, FloatBuffer params);

    void glGetTexParameteriv(int target, int pname, IntBuffer params);

    void glGetUniformfv(int program, int location, FloatBuffer params);

    void glGetUniformiv(int program, int location, IntBuffer params);

    int glGetUniformLocation(int program, String name);

    void glGetVertexAttribfv(int index, int pname, FloatBuffer params);

    void glGetVertexAttribiv(int index, int pname, IntBuffer params);

    void glHint(int target, int mode);

    boolean glIsBuffer(int buffer);

    boolean glIsEnabled(int cap);

    boolean glIsFramebuffer(int framebuffer);

    boolean glIsProgram(int program);

    boolean glIsRenderbuffer(int renderbuffer);

    boolean glIsShader(int shader);

    boolean glIsTexture(int texture);

    void glLineWidth(float width);

    void glLinkProgram(int program);

    void glPixelStorei(int pname, int param);

    void glPolygonOffset(float factor, float units);

    void glReadPixels(int x, int y, int width, int height, int format, int type, Buffer pixels);

    void glReleaseShaderCompiler();

    void glRenderbufferStorage(int target, int internalformat, int width, int height);

    void glSampleCoverage(float value, boolean invert);

    void glScissor(int x, int y, int width, int height);

    void glShaderBinary(int n, IntBuffer shaders, int binaryformat, Buffer binary, int length);

    void glShaderSource(int shader, String string);

    void glStencilFunc(int func, int ref, int mask);

    void glStencilFuncSeparate(int face, int func, int ref, int mask);

    void glStencilMask(int mask);

    void glStencilMaskSeparate(int face, int mask);

    void glStencilOp(int fail, int zfail, int zpass);

    void glStencilOpSeparate(int face, int fail, int zfail, int zpass);

    void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type,
                      Buffer pixels);

    void glTexParameterf(int target, int pname, float param);

    void glTexParameterfv(int target, int pname, FloatBuffer params);

    void glTexParameteri(int target, int pname, int param);

    void glTexParameteriv(int target, int pname, IntBuffer params);

    void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type,
                         Buffer pixels);

    void glUniform1f(int location, float x);

    void glUniform1fv(int location, int count, FloatBuffer v);

    void glUniform1fv(int location, int count, float[] v, int offset);

    void glUniform1i(int location, int x);

    void glUniform1iv(int location, int count, IntBuffer v);

    void glUniform1iv(int location, int count, int[] v, int offset);

    void glUniform2f(int location, float x, float y);

    void glUniform2fv(int location, int count, FloatBuffer v);

    void glUniform2fv(int location, int count, float[] v, int offset);

    void glUniform2i(int location, int x, int y);

    void glUniform2iv(int location, int count, IntBuffer v);

    void glUniform2iv(int location, int count, int[] v, int offset);

    void glUniform3f(int location, float x, float y, float z);

    void glUniform3fv(int location, int count, FloatBuffer v);

    void glUniform3fv(int location, int count, float[] v, int offset);

    void glUniform3i(int location, int x, int y, int z);

    void glUniform3iv(int location, int count, IntBuffer v);

    void glUniform3iv(int location, int count, int[] v, int offset);

    void glUniform4f(int location, float x, float y, float z, float w);

    void glUniform4fv(int location, int count, FloatBuffer v);

    void glUniform4fv(int location, int count, float[] v, int offset);

    void glUniform4i(int location, int x, int y, int z, int w);

    void glUniform4iv(int location, int count, IntBuffer v);

    void glUniform4iv(int location, int count, int[] v, int offset);

    void glUniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer value);

    void glUniformMatrix2fv(int location, int count, boolean transpose, float[] value, int offset);

    void glUniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer value);

    void glUniformMatrix3fv(int location, int count, boolean transpose, float[] value, int offset);

    void glUniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value);

    void glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int offset);

    void glUseProgram(int program);

    void glValidateProgram(int program);

    void glVertexAttrib1f(int indx, float x);

    void glVertexAttrib1fv(int indx, FloatBuffer values);

    void glVertexAttrib2f(int indx, float x, float y);

    void glVertexAttrib2fv(int indx, FloatBuffer values);

    void glVertexAttrib3f(int indx, float x, float y, float z);

    void glVertexAttrib3fv(int indx, FloatBuffer values);

    void glVertexAttrib4f(int indx, float x, float y, float z, float w);

    void glVertexAttrib4fv(int indx, FloatBuffer values);

    void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, Buffer ptr);

    void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int ptr);

    void glViewport(int x, int y, int width, int height);

}
