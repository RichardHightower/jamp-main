package org.jamp.websocket.impl;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Rick Hightower
 * 
 */
public class Frame {

    public enum Opcode {
        CONTINIOUS, TEXT, BINARY, PING, PONG, CLOSING
        // more to come
    }

    protected static final byte[] emptyarray = {};
    protected boolean finished;
    protected Opcode optcode;
    private ByteBuffer payLoadBuffer;
    protected boolean transferMask;

    public Frame() {
    }

    public Frame(Opcode op) {
        this.optcode = op;
        payLoadBuffer = ByteBuffer.wrap(emptyarray);
    }

    public Frame(Frame f) {
        finished = f.isFinished();
        optcode = f.getOpcode();
        payLoadBuffer = ByteBuffer.wrap(f.getPayloadData());
        transferMask = f.isTransferMask();
    }

    public boolean isFinished() {
        return finished;
    }

    public Opcode getOpcode() {
        return optcode;
    }

    public boolean isTransferMask() {
        return transferMask;
    }

    public byte[] getPayloadData() {
        if (!head) {
            return payLoadBuffer.array();
        }
        int size = 0;
        List<byte[]> buffers = new ArrayList<byte[]>();
        if (payLoadBuffer != null) {
            byte[] buffer = payLoadBuffer.array();
            size += buffer.length;
            buffers.add(buffer);
        }
        for (Frame frame : payloadFrameSeries) {
            byte[] buffer = frame.payLoadBuffer.array();
            size += buffer.length;
            buffers.add(buffer);
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(size);

        for (byte[] buf : buffers) {
            byteBuffer.put(buf);
        }

        return byteBuffer.array();
    }

    public void setFinished(boolean fin) {
        this.finished = fin;
    }

    public void setOptcode(Opcode optcode) {
        this.optcode = optcode;
    }

    public void setPayload(byte[] payload) throws WebSocketException {
        payLoadBuffer = ByteBuffer.wrap(payload);
    }

    public void setTransferMask(boolean transferMask) {
        this.transferMask = transferMask;
    }

    List<Frame> payloadFrameSeries;

    public void append(Frame nextFrameInSeries) {
        payloadFrameSeries.add(nextFrameInSeries);
        finished = nextFrameInSeries.finished;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return "Framedata{ optcode:" + getOpcode() + ", fin:" + isFinished()
                + ", payloadlength:" + payLoadBuffer.limit() + ", payload:"
                + convertToUTF8Bytes(new String(payLoadBuffer.array())) + "}";
    }

    boolean head;

    public void setSeriesHead() {
        head = true;
        payloadFrameSeries = new ArrayList<Frame>();
    }

    public void setPayload(String text) {
        this.setPayload(convertToUTF8Bytes(text));

    }

    @SuppressWarnings("nls")
    byte[] convertToUTF8Bytes(String s) {
        try {
            return s.getBytes("UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    String stringUtf8(byte[] bytes) throws WebSocketException {
        return stringUtf8(bytes, 0, bytes.length);
    }

    String stringUtf8(byte[] bytes, int off, int length)
            throws WebSocketException {
        @SuppressWarnings("nls")
        CharsetDecoder decode = Charset.forName("UTF8").newDecoder();
        decode.onMalformedInput(CodingErrorAction.REPORT);
        decode.onUnmappableCharacter(CodingErrorAction.REPORT);
        String s;
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes, off, length);
            s = decode.decode(byteBuffer).toString();
        } catch (CharacterCodingException e) {
            throw new WebSocketException(CloseFrame.NO_UTF8, e);
        }
        return s;
    }

    public String getPayloadDataAsUTF8() {
        return this.stringUtf8(getPayloadData());
    }

}
