package com.naicson.yugioh.entity.sets;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
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
import javax.persistence.Transient;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.naicson.yugioh.data.dto.set.UserSetCollectionDTO;
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
	private Integer konamiSetCopied;
	
	@ManyToMany( fetch = FetchType.EAGER)
	@JoinTable(name="tab_user_setcollection_deck",
    joinColumns={@JoinColumn(name="user_set_collection_id")},
    inverseJoinColumns={@JoinColumn(name="deck_id")})
	private List<UserDeck> userDeck;
	
	@Enumerated(EnumType.STRING)
	private SetType setCollectionType;

	public UserSetCollection() {}
	
	public UserSetCollection(Long id, Long userId, String name, String portugueseName, String imgPath,
			Boolean onlyDefaultDeck, Date releaseDate, Date registrationDate, Boolean isSpeedDuel, String imgurUrl,
			Date dtUpdate, Integer konamiSetCopied, List<UserDeck> userDeck, SetType setCollectionType) {
		super();
		this.id = id;
		this.userId = userId;
		this.name = name;
		this.portugueseName = portugueseName;
		this.imgPath = imgPath;
		this.onlyDefaultDeck = onlyDefaultDeck;
		this.releaseDate = releaseDate;
		this.registrationDate = registrationDate;
		this.isSpeedDuel = isSpeedDuel;
		this.imgurUrl = imgurUrl;
		this.DtUpdate = dtUpdate;
		this.konamiSetCopied = konamiSetCopied;
		this.userDeck = userDeck;
		this.setCollectionType = setCollectionType;
	}

	public Long getId() {
		return id;
	}

	public Long getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public String getPortugueseName() {
		return portugueseName;
	}

	public String getImgPath() {
		return imgPath;
	}

	public Boolean getOnlyDefaultDeck() {
		return onlyDefaultDeck;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public Boolean getIsSpeedDuel() {
		return isSpeedDuel;
	}

	public String getImgurUrl() {
		return imgurUrl;
	}

	public Date getDtUpdate() {
		return DtUpdate;
	}

	public Integer getKonamiSetCopied() {
		return konamiSetCopied;
	}

	public List<UserDeck> getUserDeck() {
		return userDeck;
	}

	public SetType getSetCollectionType() {
		return setCollectionType;
	}	
	
	
	
}
