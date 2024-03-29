package com.naicson.yugioh.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;

@Schema
public class TesteDTO {
	@Schema(defaultValue = "Teste Schema")
	private String teste;
	private LocalDate date;
	private Integer age;
	
	public String getTeste() {
		return teste;
	}
	public void setTeste(String teste) {
		this.teste = teste;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	
	@Override
	public String toString() {
		return "TesteDTO [teste=" + teste + ", date=" + date + ", age=" + age + "]";
	}
	
	
	
}
