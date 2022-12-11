package com.naicson.yugioh.data.composite;

public abstract class JsonConverterValidationComposite<T> {
	//Composite Design Pattern
	
	public Class<T> objetoRetorno;
	//public JsonConverterValidationComposite subValidation;
	
//	public JsonConverterValidationComposite() {
//		this.objetoRetorno = objetoRetorno;
//	}
	
	public abstract boolean validate(String obj);
}
