package com.ourgame.gpie.servicemodel;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * ����ͻ���������ͨ�ŵ���Ϣ���ݡ�
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
	 * @return ��ȡ��Ϣ���ܳ��ȡ�
	 */
	public int getTotalLength() {
		return HEADER_LENGTH + this.getBodyLength();
	}

	/**
	 * �� {@link IoBuffer} �ж�ȡ��Ϣ��
	 * 
	 * @param buffer
	 *            �����Ϣ���ݵ� {@link IoBuffer}��
	 */
	public void read(IoBuffer buffer) {
	}

	/**
	 * ����Ϣ������д�뵽{@link IoBuffer}�С�
	 * 
	 * @param buffer
	 *            ��д�����ݵ� {@link IoBuffer}��
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
