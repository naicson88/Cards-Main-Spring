package com.naicson.yugioh.entity.sets;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.naicson.yugioh.data.dto.set.SetCollectionDto;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.util.enums.SetType;

@Entity
@Table(name = "tab_set_collection")
public class SetCollection {
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(unique = true)
	private String name;
	private String portugueseName;
	private String imgPath;
	private Boolean onlyDefaultDeck;
	@JsonFormat(pattern="MM-dd-yyyy")
	private Date releaseDate;
	private Date registrationDate;
	private Boolean isSpeedDuel;
	private String imgurUrl;
	
	@ManyToMany( fetch = FetchType.LAZY)
	@JoinTable(name="tab_setcollection_deck",
    joinColumns={@JoinColumn(name="set_collection_id")},
    inverseJoinColumns={@JoinColumn(name="deck_id")})
	private List<Deck> decks;
	@Enumerated(EnumType.STRING)
	private SetType setCollectionType;


	public SetCollection() {
		
	}
	
	public static SetCollection setCollectionDtoToEntity(SetCollectionDto dto) {
		
		SetCollection collection = new SetCollection();
		collection.setImgurUrl(dto.getImgPath());
		collection.setIsSpeedDuel(dto.getIsSpeedDuel());
		collection.setName(dto.getName());
		collection.setOnlyDefaultDeck(dto.getOnlyDefaultDeck());
		collection.setPortugueseName(dto.getPortugueseName());
		collection.setRegistrationDate(new Date());
		collection.setReleaseDate(dto.getReleaseDate());
		collection.setSetCollectionType(SetType.valueOf(dto.getSetType()));
		
		List<Deck> decks = dto.getDecks().stream().map(id -> new Deck(id.longValue())).collect(Collectors.toList());
		
		collection.setDecks(decks);
		
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
	public SetType getSetCollectionType() {
		return setCollectionType;
	}
	public void setSetCollectionType(SetType setCollectionType) {
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

	@Override
	public String toString() {
		return "SetCollection [id=" + id + ", name=" + name + ", portugueseName=" + portugueseName + ", imgPath="
				+ imgPath + ", onlyDefaultDeck=" + onlyDefaultDeck + ", releaseDate=" + releaseDate
				+ ", registrationDate=" + registrationDate + ", isSpeedDuel=" + isSpeedDuel + ", decks=" + decks
				+ ", setCollectionType=" + setCollectionType + "]";
	}

	public String getImgurUrl() {
		return imgurUrl;
	}

	public void setImgurUrl(String imgurUrl) {
		this.imgurUrl = imgurUrl;
	}
	
	
		
}
