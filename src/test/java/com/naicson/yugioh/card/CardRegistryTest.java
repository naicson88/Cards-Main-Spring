package com.naicson.yugioh.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.naicson.yugioh.data.dto.CardYuGiOhAPI;
import com.naicson.yugioh.entity.Archetype;
import com.naicson.yugioh.entity.Atributo;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.TipoCard;
import com.naicson.yugioh.mocks.CardYuGiOhAPIMock;
import com.naicson.yugioh.repository.AtributoRepository;
import com.naicson.yugioh.repository.TipoCardRepository;
import com.naicson.yugioh.service.ArchetypeServiceImpl;
import com.naicson.yugioh.service.card.CardRegistry;
import com.naicson.yugioh.util.enums.GenericTypesCards;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class) // Usado com Junit5 ao inves do RunWith
public class CardRegistryTest {
	
	@InjectMocks
	@Spy
	CardRegistry cardRegistry;
	
	@Mock
	ArchetypeServiceImpl archService;
	
	@Mock
	TipoCardRepository cardTypeRepository;
	
	@Mock
	AtributoRepository attRepository;
	
	@BeforeEach
	public void setup(){
	    MockitoAnnotations.initMocks(this); //without this you will get NPE
	}
	
//	@Test
//	public void createCardToBeRegisteredNoNMagic() {
//		
//		CardYuGiOhAPI card = CardYuGiOhAPIMock.createCardAPI();
//		
//		Archetype arch = mock(Archetype.class);
//		Atributo attr = mock(Atributo.class);
//		TipoCard tipo = mock(TipoCard.class);
//		
//		Mockito.when(archService.getCardArchetype(card.getArchetype())).thenReturn(arch);
//		Mockito.when(attRepository.findByName(card.getAttribute())).thenReturn(attr);
//		Mockito.when(cardTypeRepository.findByName(card.getAttribute())).thenReturn(tipo);
//		
//		Card c = cardRegistry.createCardToBeRegistered(card);
//		
//		assertNotNull(c);
//		assertEquals(1000, c.getAtk());
//		assertEquals(1500, c.getDef());
//		assertEquals(GenericTypesCards.MONSTER.toString(), c.getGenericType());
//	
//	}
	
}
