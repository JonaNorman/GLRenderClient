package com.jonanorman.android.renderclient.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

public abstract class GLVariable extends GLClass {

    private static final double MORE_CAPACITY_FACTOR = 1.5;

    private final int id;
    private final int length;
    private final String name;
    private final int type;
    private ByteBuffer buffer;
    private GLUpdateMethod updateMethod;
    private GLBindMethod bindMethod;
    private GLUnBindMethod unBindMethod;

    public GLVariable(GLRenderClient client, int id, int type, String name, int length) {
        super(client);
        this.id = id;
        this.type = type;
        this.name = name;
        this.length = length;
    }


    @Override
    protected void onRegisterMethod() {
        updateMethod = new GLUpdateMethod();
        bindMethod = new GLBindMethod();
        unBindMethod = new GLUnBindMethod();
    }

    public final void update(float[] value) {
        updateMethod.setValue(value);
        updateMethod.apply();
    }

    public final void bind() {
        bindMethod.apply();
    }

    public final void unBind() {
        unBindMethod.apply();
    }


    public abstract int getTypeSize();

    public abstract String getTypeName();

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getLength() {
        return length;
    }

    public int getType() {
        return type;
    }

    public float get(int i) {
        int oldOffset = buffer == null ? -1 : buffer.position();
        int oldLimit = buffer == null ? -1 : buffer.limit();
        int offset = i * 4;
        if (offset < oldOffset || offset >= oldLimit) {
            return 0;
        }
        return isTypeFloat() ? buffer.getFloat(offset) : buffer.getInt(offset);
    }

    public abstract boolean isTypeFloat();

    protected abstract void onBufferUpdate(ByteBuffer buffer, boolean change);


    class GLUpdateMethod extends GLMethod {
        private float[] value;

        public GLUpdateMethod() {
            super();
        }

        public void setValue(float[] value) {
            this.value = value;
        }

        @Override
        protected final void onMethodCall() {
            int size = value == null ? getLength() * getTypeSize() : value.length;
            int bufferSize = size * 4;
            ByteBuffer temp = buffer;
            if (buffer == null || bufferSize > buffer.capacity()) {
                buffer = ByteBuffer.allocateDirect((int) (bufferSize * MORE_CAPACITY_FACTOR)).order(ByteOrder.nativeOrder());
            }
            boolean update = buffer.limit() != bufferSize;
            int oldOffset = temp == null ? -1 : temp.position();
            int oldLimit = temp == null ? -1 : temp.limit();
            buffer.position(0);
            buffer.limit(bufferSize);
            boolean typeFloat = isTypeFloat();
            for (int i = 0; i < size; i++) {
                int offset = i * 4;
                if (typeFloat) {
                    float data = value == null ? 0 : value[i];
                    if (!update) {
                        float old = offset >= oldOffset && offset < oldLimit ? temp.getFloat(offset) : 0;
                        if (!Objects.equals(old, data)) {
                            update = true;
                        }
                    }
                    buffer.putFloat(offset, data);
                } else {
                    int data = value == null ? 0 : (int) value[i];
                    if (!update) {
                        int old = offset >= oldOffset && offset < oldLimit ? temp.getInt(offset) : 0;
                        if (!Objects.equals(old, data)) {
                            update = true;
                        }
                    }
                    buffer.putInt(offset, data);
                }
            }
            onBufferUpdate(buffer, update);
        }
    }

    class GLBindMethod extends GLMethod {
        public GLBindMethod() {
            super();
        }

        @Override
        protected void onMethodCall() {
            onBind();
        }
    }

    class GLUnBindMethod extends GLMethod {
        public GLUnBindMethod() {
            super();
        }

        @Override
        protected void onMethodCall() {
            onUnBind();
        }
    }

    protected abstract void onUnBind();

    protected abstract void onBind();


}
