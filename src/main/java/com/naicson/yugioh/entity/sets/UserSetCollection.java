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

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.naicson.yugioh.util.GeneralFunctions;
import com.naicson.yugioh.util.enums.SetType;

@Entity
@Table(name = "tab_user_set_collection")
public class UserSetCollection {
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long userId;
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
	@JsonFormat(pattern="MM-dd-yyyy")
	private Date DtUpdate;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="tab_user_setcollection_deck",
    joinColumns={@JoinColumn(name="user_set_collection_id")},
    inverseJoinColumns={@JoinColumn(name="deck_id")})
	private List<UserDeck> userDeck;
	@Enumerated(EnumType.STRING)
	private SetType setCollectionType;
	
	public UserSetCollection() {}
	
	public static UserSetCollection setCollectionToUserSetCollection(SetCollection set) {
		
		UserSetCollection userSet = new UserSetCollection();
		BeanUtils.copyProperties(userSet, set);
		
		userSet.setDtUpdate(new Date());
		userSet.setId(null);
		userSet.setName(set.getName()+"_"+GeneralFunctions.momentAsString());
		userSet.setRegistrationDate(new Date());
		userSet.setUserId(GeneralFunctions.userLogged().getId());
		
		List<UserDeck> listDeckUser =  set.getDecks().stream().map(d -> {
			UserDeck du = UserDeck.userDeckFromDeck(d);		
			return du;
		}).collect(Collectors.toList());
		
		userSet.setUserDeck(listDeckUser);
		
		return userSet;
	
	}
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
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
	public Boolean getOnlyDefaultDeck() {
		return onlyDefaultDeck;
	}
	public void setOnlyDefaultDeck(Boolean onlyDefaultDeck) {
		this.onlyDefaultDeck = onlyDefaultDeck;
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
	public Boolean getIsSpeedDuel() {
		return isSpeedDuel;
	}
	public void setIsSpeedDuel(Boolean isSpeedDuel) {
		this.isSpeedDuel = isSpeedDuel;
	}
	public String getImgurUrl() {
		return imgurUrl;
	}
	public void setImgurUrl(String imgurUrl) {
		this.imgurUrl = imgurUrl;
	}
	public Date getDtUpdate() {
		return DtUpdate;
	}
	public void setDtUpdate(Date dtUpdate) {
		DtUpdate = dtUpdate;
	}
	public List<UserDeck> getUserDeck() {
		return userDeck;
	}
	public void setUserDeck(List<UserDeck> userDeck) {
		this.userDeck = userDeck;
	}
	public SetType getSetCollectionType() {
		return setCollectionType;
	}
	public void setSetCollectionType(SetType setCollectionType) {
		this.setCollectionType = setCollectionType;
	}
	
	
}
