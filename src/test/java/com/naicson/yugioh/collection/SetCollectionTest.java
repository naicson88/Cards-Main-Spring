package com.naicson.yugioh.collection;

import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
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

import com.naicson.yugioh.data.dto.set.AssociationDTO;
import com.naicson.yugioh.mocks.AssociationDTOMock;
import com.naicson.yugioh.repository.SetCollectionRepository;
import com.naicson.yugioh.service.setcollection.SetCollectionServiceImpl;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class) // Usado com Junit5 ao inves do RunWith
public class SetCollectionTest {
	
	@Spy
	@InjectMocks
	SetCollectionServiceImpl setService;
	
	@Mock
	SetCollectionRepository setColRepository;
	
	@BeforeEach
	public void setup(){
	    MockitoAnnotations.openMocks(this); //without this you will get NPE
	}
	
//	@Test
//	public void createNewAssociationDTO() {
//		AssociationDTO dto = AssociationDTOMock.returnAssociationDTO();
//		
//		AssociationDTO registeredAssociadion = setService.newAssociation(dto);
//		
//		assertNotNull(registeredAssociadion);
//		assertEquals(dto.getSourceId(), registeredAssociadion.getSourceId());
//		assertEquals(2, registeredAssociadion.getArrayToAssociate().size());
//	}
	
	@Test
	public void errorWhenDeckIdNotExistsOnCreateNewAssociation() {		
		AssociationDTO dto = AssociationDTOMock.returnAssociationDTO();
		
		Mockito.when(setColRepository.getSetDeckRelationId(dto.getSourceId())).thenReturn(null);
		
		EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
			 setService.newAssociation(dto);  
		});
				
		String expected = "Deck ID not found: " + dto.getSourceId();
		String actual = exception.getMessage();
		
		assertTrue(actual.contains(expected));			
	}
}
