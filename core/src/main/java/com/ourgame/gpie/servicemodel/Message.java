package com.ourgame.gpie.servicemodel;

import org.apache.mina.core.buffer.IoBuffer;

public class Message {

	public final static int HEADER_LENGTH = 12;

	private MessageType type;
	// private int service;
	// private int method;

	private int id;
	private byte[] body;

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public int getBodyLength() {
		return this.body == null ? 0 : this.body.length;
	}

	public int getTotalLength() {
		return HEADER_LENGTH + this.getBodyLength();
	}

	public void read(IoBuffer buffer) {
	}

	public void write(IoBuffer buffer) {
	}

	public IoBuffer toBuffer() {
		IoBuffer buffer = IoBuffer.allocate(this.getTotalLength());
		this.write(buffer);
		buffer.flip();
		return buffer;
	}
}
