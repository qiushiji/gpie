package com.ourgame.gpie.servicemodel;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 代表客户端与服务端通信的消息数据。
 * 
 * @author qius
 *
 */
public class Message {

	public final static int HEADER_LENGTH = 12;

	private MessageType type;
//	private int service;
//	private int method;

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

	/**
	 * @return 获取消息的总长度。
	 */
	public int getTotalLength() {
		return HEADER_LENGTH + this.getBodyLength();
	}

	/**
	 * 从 {@link IoBuffer} 中读取消息。
	 * 
	 * @param buffer
	 *            存放消息数据的 {@link IoBuffer}。
	 */
	public void read(IoBuffer buffer) {
	}

	/**
	 * 将消息的数据写入到{@link IoBuffer}中。
	 * 
	 * @param buffer
	 *            待写入数据的 {@link IoBuffer}。
	 */
	public void write(IoBuffer buffer) {
	}

	public IoBuffer toBuffer() {
		IoBuffer buffer = IoBuffer.allocate(this.getTotalLength());
		this.write(buffer);
		buffer.flip();
		return buffer;
	}
}
