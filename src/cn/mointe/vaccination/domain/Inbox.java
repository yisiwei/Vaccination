package cn.mointe.vaccination.domain;

import java.io.Serializable;

public class Inbox implements Serializable {

	private static final long serialVersionUID = 4092705917523921832L;

	private int id;
	private String username;
	private String type;

	private String date;
	private String title;
	private String content;

	private String isRead;

	public Inbox() {

	}

	public Inbox(int id, String username, String type, String date,
			String title, String content, String isRead) {
		this.id = id;
		this.username = username;
		this.type = type;
		this.date = date;
		this.title = title;
		this.content = content;
		this.isRead = isRead;
	}

	public Inbox(String username, String type, String date, String title,
			String content, String isRead) {
		this.username = username;
		this.type = type;
		this.date = date;
		this.title = title;
		this.content = content;
		this.isRead = isRead;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getIsRead() {
		return isRead;
	}

	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}

}
