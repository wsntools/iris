package com.wsntools.iris.tools.datacollector.sending;

public class SendingConfiguration {

	private AbstractEncoder encoder;
	private ISender sender;
	private String port;

	public SendingConfiguration(AbstractEncoder encoder, ISender sender,
			String port) {
		this.encoder = encoder;
		this.sender = sender;
		this.port = port;
	}

	public AbstractEncoder getEncoder() {
		return encoder;
	}

	public void setEncoder(AbstractEncoder encoder) {
		this.encoder = encoder;
	}

	public ISender getSender() {
		return sender;
	}

	public void setSender(ISender sender) {
		this.sender = sender;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
}
