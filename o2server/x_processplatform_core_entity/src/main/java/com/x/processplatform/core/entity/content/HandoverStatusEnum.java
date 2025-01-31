package com.x.processplatform.core.entity.content;


/**
 * @author sword
 */
public enum HandoverStatusEnum {

	WAIT("wait", "待处理"),
	CANCEL("cancel", "已取消"),
	PROCESSING("processing", "处理中"),
	PROCESSED("processed", "已处理");

	private String value;
	private String name;

	private HandoverStatusEnum(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public static String getNameByValue(String value) {
		for (HandoverStatusEnum e : HandoverStatusEnum.values()) {
			if (e.getValue().equals(value)) {
				return e.name;
			}
		}
		return value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value == null ? null : value.trim();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

}
