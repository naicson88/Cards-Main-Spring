package com.naicson.yugioh.service.card;

import cardscommons.dto.CardYuGiOhAPI;
import com.naicson.yugioh.data.builders.CardBuilder;
import com.naicson.yugioh.entity.Archetype;
import com.naicson.yugioh.entity.Atributo;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.TipoCard;
import com.naicson.yugioh.repository.AtributoRepository;
import com.naicson.yugioh.repository.TipoCardRepository;
import com.naicson.yugioh.service.ArchetypeServiceImpl;
import com.naicson.yugioh.util.enums.CardProperty;
import com.naicson.yugioh.util.enums.GenericTypesCards;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class BuildCardFromYuGiOhAPI {

	@Autowired
	ArchetypeServiceImpl archService;

	@Autowired
	AtributoRepository attRepository;
	
	@Autowired
	TipoCardRepository cardTypeRepository;
	
	Logger logger = LoggerFactory.getLogger(BuildCardFromYuGiOhAPI.class);
	
	public Card createCard(CardYuGiOhAPI apiCard) {
		logger.info("Creating new Card... {}", apiCard.getName());
		return  CardBuilder.builder()
				.numero(apiCard.getId())
				.categoria(apiCard.getType())
				.nome(apiCard.getName().trim())
				.registryDate(new Date())
				.descricao(apiCard.getDesc())
				.archetype(this.getCardArchetype(apiCard.getArchetype()))
				.atributo(this.getCardAttribute(apiCard))
				.genericType(this.setCardGenericType(apiCard.getType()))
				.propriedade(this.getCardProperty(apiCard.getRace(), apiCard.getType()))
				.nivel(apiCard.getLevel())
				.atk(apiCard.getAtk())
				.def(apiCard.getDef())
				.qtdLink(apiCard.getLinkval() > 0 ? String.valueOf(apiCard.getLinkval()) : null)
				.escala(apiCard.getScale() != null ? Integer.parseInt(apiCard.getScale()) : null)
				.tipo(apiCard.getRace() != null ? this.getCardType(apiCard.getRace()) : null)	
				.descricao(getMapCardDescription(apiCard).get("descricao"))
				.descricaoPendulum(getMapCardDescription(apiCard).get("pendulumDesc"))
				.build();
	}
	
	private TipoCard getCardType(String race) {

		if (!"Normal".equalsIgnoreCase(race)) // Trap and Spell cards has "normal" type
			return cardTypeRepository.findByName(race != null ? race.trim() : null);
		else
			return null;
	}
	
	private String getCardProperty(String race, String categoria) {	
		
		if(StringUtils.containsIgnoreCase("spell", categoria) || 
				StringUtils.containsIgnoreCase("trap", categoria)) {
			
			if (!EnumUtils.isValidEnum(CardProperty.class, race))
				throw new IllegalArgumentException("Invalid property");
			
			return race;		
		}
		
		return null;				
	}
	
	private Archetype getCardArchetype(String archetype) {
		if(archetype == null || archetype.isBlank())
			return null;
		
	   Archetype arch = archService.getCardArchetypeByName(archetype.trim());
			
	   if(arch == null) {
		   arch = archService.saveArchetype(archetype.trim());
		   logger.info("New Archetype registered: {} ", arch.toString());
	   }
	
		return arch;	
	}

	private Atributo getCardAttribute(CardYuGiOhAPI card) {

		String attr = null;

		if (card.getType().contains("Spell") || card.getType().contains("Trap"))
			attr = card.getType().contains("Spell") ? "SPELL" : "TRAP";
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
		
		if("Normal Monster".equalsIgnoreCase(type) || "Effect Monster".equalsIgnoreCase(type) || "Tuner Monster".equalsIgnoreCase(type))
			return GenericTypesCards.MONSTER.toString();
		
		for (GenericTypesCards gen : GenericTypesCards.values()) {
			if (StringUtils.containsIgnoreCase(type, gen.toString()) && gen.getId() > 1) {
				return gen.toString();
			}
		}
		
		throw new IllegalArgumentException("Invalid Generic Type: " + type);
	}

	private Map<String, String> getMapCardDescription(CardYuGiOhAPI apiCard) {
		
		Map<String, String> mapDescriptions = new HashMap<>();

		if (StringUtils.containsIgnoreCase("pendulum", apiCard.getType())) {
			
			if (apiCard.getDesc().contains("----------------------------------------")) {
				String[] parts = apiCard.getDesc().split("----------------------------------------");
				String pendulumDesc = parts[0];
				String simpleDesc = parts[1];
				mapDescriptions.put("descricao", simpleDesc);
				mapDescriptions.put("pendulumDesc", pendulumDesc);
			} else 
				throw new IllegalArgumentException("Invalid Pendulum description pattern");
		} else 
			mapDescriptions.put("descricao", apiCard.getDesc());
		
		return mapDescriptions;
	}
}
