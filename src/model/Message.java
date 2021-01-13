package model;

public class Message {
	final static public int USER=0;
	final static public int CLIENT=1;
	String content;
	int isSendBy;
	public Message(String content, int isSendBy) {
		// TODO Auto-generated constructor stub
		this.content=content;
		this.isSendBy=isSendBy;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getContent() {
		return content;
	}
	public void setIsSendBy(int isSendBy) {
		this.isSendBy = isSendBy;
	}
	public int getIsSendBy() {
		return isSendBy;
	}
}
