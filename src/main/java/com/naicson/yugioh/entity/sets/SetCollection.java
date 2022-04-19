package com.naicson.yugioh.entity.sets;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.naicson.yugioh.data.dto.set.SetCollectionDto;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.util.enums.SetCollectionTypes;

@Entity
@Table(name = "tab_set_collection")
public class SetCollection {
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private String portugueseName;
	private String imgPath;
	private Boolean onlyDefaultDeck;
	private Date releaseDate;
	private Date registrationDate;
	private Boolean isSpeedDuel;
	@OneToMany(mappedBy = "setCollection", cascade = CascadeType.ALL)
	private List<Deck> decks;
	@Enumerated(EnumType.STRING)
	private SetCollectionTypes setCollectionType;

	public SetCollection() {
		
	}
	
	public static SetCollection setCollectionDtoToEntity(SetCollectionDto dto) {
		
		SetCollection collection = new SetCollection();
		collection.setImgPath(dto.getImgPath());
		collection.setIsSpeedDuel(dto.getIsSpeedDuel());
		collection.setName(dto.getName());
		collection.setOnlyDefaultDeck(dto.getOnlyDefaultDeck());
		collection.setPortugueseName(dto.getPortugueseName());
		collection.setRegistrationDate(new Date());
		collection.setReleaseDate(dto.getReleaseDate());
		collection.setSetCollectionType(SetCollectionTypes.valueOf(dto.getSetType()));
		
		return collection;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPortugueseName() {
		return portugueseName;
	}
	public void setPortugueseName(String portugueseName) {
		this.portugueseName = portugueseName;
	}
	public String getImgPath() {
		return imgPath;
	}
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	public List<Deck> getDecks() {
		return decks;
	}
	public void setDecks(List<Deck> decks) {
		this.decks = decks;
	}
	public SetCollectionTypes getSetCollectionType() {
		return setCollectionType;
	}
	public void setSetCollectionType(SetCollectionTypes setCollectionType) {
		this.setCollectionType = setCollectionType;
	}
	public Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	public Date getRegistrationDate() {
		return registrationDate;
	}
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Boolean getOnlyDefaultDeck() {
		return onlyDefaultDeck;
	}

	public void setOnlyDefaultDeck(Boolean onlyDefaultDeck) {
		this.onlyDefaultDeck = onlyDefaultDeck;
	}

	public Boolean getIsSpeedDuel() {
		return isSpeedDuel;
	}

	public void setIsSpeedDuel(Boolean isSpeedDuel) {
		this.isSpeedDuel = isSpeedDuel;
	}
		
}
