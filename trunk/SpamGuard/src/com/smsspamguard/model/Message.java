package com.smsspamguard.model;


public class Message {
	
	private long messageId;
	private long threadId;
	private String address;
	private long contactId;
	private long date;
	private String body;
	private boolean isTrained = false;
	
	public Message() {}
	
	public Message(long messageId, long threadId, String address,
			long contactId, long date, String body) {
		super();
		this.messageId = messageId;
		this.threadId = threadId;
		this.address = address;
		this.contactId = contactId;
		this.date = date;
		this.body = body;
	}

	public long getMessageId() {
		return messageId;
	}

	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}

	public long getThreadId() {
		return threadId;
	}

	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getContactId() {
		return contactId;
	}

	public void setContactId(long contactId) {
		this.contactId = contactId;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public boolean isTrained() {
		return isTrained;
	}

	public void setTrained(boolean isTrained) {
		this.isTrained = isTrained;
	}
}
