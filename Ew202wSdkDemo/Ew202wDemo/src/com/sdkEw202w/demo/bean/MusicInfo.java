package com.sdkEw202w.demo.bean;

import com.sleepace.sdk.domain.BaseBean;

public class MusicInfo extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int musicID;
	
	private String musicName;

	public int getMusicID() {
		return musicID;
	}

	public void setMusicID(int musicID) {
		this.musicID = musicID;
	}

	public String getMusicName() {
		return musicName;
	}

	public void setMusicName(String musicName) {
		this.musicName = musicName;
	}
	
	
}
