package com.bryan.hello.preword.info.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FileData {
	private String filename;
	private String url;
	private Long size;
}
