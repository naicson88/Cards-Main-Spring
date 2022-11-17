package com.naicson.yugioh.data.factories.card;

import java.util.Date;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.naicson.yugioh.data.builders.CardBuilder;
import com.naicson.yugioh.data.dto.CardYuGiOhAPI;
import com.naicson.yugioh.entity.Archetype;
import com.naicson.yugioh.entity.Atributo;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.repository.AtributoRepository;
import com.naicson.yugioh.service.ArchetypeServiceImpl;
import com.naicson.yugioh.util.enums.GenericTypesCards;

@Component
public class BuildCommonCardAttributes implements BuildCard {

	@Autowired
	ArchetypeServiceImpl archService;

	@Autowired
	AtributoRepository attRepository;
	

	@Override
	public Card buildCard(CardYuGiOhAPI apiCard, Card c) {

		Card card = CardBuilder.builder().numero(apiCard.getId()).categoria(apiCard.getType())
				.nome(apiCard.getName().trim()).registryDate(new Date())
				.archetype(apiCard.getRace() != null ? this.getCardArchetype(apiCard.getArchetype()) : null)
				.atributo(this.getCardAttribute(apiCard)).genericType(this.setCardGenericType(apiCard.getType()))
				.build();

		card = this.setCardDescription(card, apiCard);

		return card;
	}

	private Archetype getCardArchetype(String archetype) {
		return archService.getCardArchetype(archetype);
	}

	private Atributo getCardAttribute(CardYuGiOhAPI card) {

		String attr = null;

		if (card.getType().contains("Spell") || card.getType().contains("Trap"))
			attr = card.getType().contains("Spell") ? "SPELL_CARD" : "TRAP_CARD";
		else if (card.getType().equalsIgnoreCase("Skill Card"))
			attr = "SKILL";
		else
			attr = card.getAttribute();

		Atributo att = attRepository.findByName(attr.trim());

		if (att == null)
			throw new EntityNotFoundException("Cant found Attribute: " + attr);

		return att;
	}

	private String setCardGenericType(String type) {

		for (GenericTypesCards gen : GenericTypesCards.values()) {
			if (type.equalsIgnoreCase(gen.toString())) {
				return gen.toString();
			}
		}
		
		throw new IllegalArgumentException("Invalid Generic Type: " + type);
	}

//	private String setCardGenericType(String type) {
//		String generic = null;
//
//		if (StringUtils.containsIgnoreCase(type, "xyz"))
//			generic = GenericTypesCards.XYZ.toString();
//		else if (StringUtils.containsIgnoreCase(type, "fusion"))
//			generic = GenericTypesCards.FUSION.toString();
//		else if (StringUtils.containsIgnoreCase(type, "link"))
//			generic = GenericTypesCards.LINK.toString();
//		else if (StringUtils.containsIgnoreCase(type, "pendulum"))
//			generic = GenericTypesCards.PENDULUM.toString();
//		else if (StringUtils.containsIgnoreCase(type, "spell"))
//			generic = GenericTypesCards.SPELL.toString();
//		else if (StringUtils.containsIgnoreCase(type, "trap"))
//			generic = GenericTypesCards.TRAP.toString();
//		else if (StringUtils.containsIgnoreCase(type, "synchro"))
//			generic = GenericTypesCards.SYNCHRO.toString();
//		else if (StringUtils.containsIgnoreCase(type, "ritual"))
//			generic = GenericTypesCards.RITUAL.toString();
//		else if (StringUtils.containsIgnoreCase(type, "token"))
//			generic = GenericTypesCards.TOKEN.toString();
//		else if (StringUtils.containsIgnoreCase(type, "monster"))
//			generic = GenericTypesCards.MONSTER.toString();
//		else if (StringUtils.containsIgnoreCase(type, "skill"))
//			generic = GenericTypesCards.SKILL.toString();
//		else
//			throw new IllegalArgumentException("Invalid monster type");
//
//		return generic;
//	}

	private Card setCardDescription(Card cardToBeRegistered, CardYuGiOhAPI apiCard) {

		if (StringUtils.containsIgnoreCase("pendulum", apiCard.getType())) {

			String fullDesc = apiCard.getDesc();
			// Verify if has pendulum description
			if (fullDesc.contains("----------------------------------------")) {
				String[] parts = fullDesc.split("----------------------------------------");
				String pendulumDesc = parts[0];
				String simpleDesc = parts[1];
				cardToBeRegistered.setDescricao(simpleDesc);
				cardToBeRegistered.setDescr_pendulum(pendulumDesc);
			} else {
				cardToBeRegistered.setDescricao(fullDesc);
			}

		} else {
			cardToBeRegistered.setDescricao(apiCard.getDesc());
		}

		return cardToBeRegistered;
	}
}
