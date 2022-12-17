package com.naicson.yugioh.data.composite;

public class ConsumerTypeValidation extends JsonConverterValidationComposite {

	public String consumerType;
	public JsonConverterValidationComposite composite;
	
	public ConsumerTypeValidation(String consumerType) {
		this.consumerType = consumerType;
	}

	@Override
	public boolean validate(String obj) {
		return (obj.equals(consumerType));
		//return (obj.equals(consumerType) && composite.validate(obj));
	}

}
