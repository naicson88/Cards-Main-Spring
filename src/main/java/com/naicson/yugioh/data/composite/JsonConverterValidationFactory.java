package com.naicson.yugioh.data.composite;

import java.util.ArrayList;
import java.util.List;

import cardscommons.dto.*;

@SuppressWarnings("rawtypes")
public class JsonConverterValidationFactory {
		
	public static final String KONAMI_DECK = "KONAMI DECK";
	public static final String ADD_NEW_CARD = "ADD NEW CARD";
	public static final String COLLECTION_DECK = "COLLECTION DECK";
	public static final String SET_COLLECTION = "SET COLLECTION";
	public static final String SET_PRICE = "SET PRICE";
	
//	 Exemplo com mais de um argumento 
//	IValidation assiantSalesAssociate = new DepartmentValidation(Department.SALES,
//              new YearValidation(0, 5,
//                      new QualificationValidation( Degree.HIGH_SCHOOL)));
//      assiantSalesAssociate.designation = Designation.ASSISTANT_SALES_ASSOCIATE;
//
//      criterias.add(assiantSalesAssociate);

	@SuppressWarnings("unchecked")
	public static List<JsonConverterValidationComposite> getAllCriterias(){
		
		List<JsonConverterValidationComposite> criterias = new ArrayList<>();
		
		JsonConverterValidationComposite konamiDeckValidation = new ConsumerTypeValidation(KONAMI_DECK);
		konamiDeckValidation.objetoRetorno = KonamiDeckDTO.class;
		criterias.add(konamiDeckValidation);
		
		JsonConverterValidationComposite consumerTypeValidation = new ConsumerTypeValidation(ADD_NEW_CARD);
		consumerTypeValidation.objetoRetorno = AddNewCardToDeckDTO.class;
		criterias.add(consumerTypeValidation);
		
		JsonConverterValidationComposite collectionDeckValidation = new ConsumerTypeValidation(COLLECTION_DECK);
		collectionDeckValidation.objetoRetorno = CollectionDeckDTO.class;
		criterias.add(collectionDeckValidation);
		
		JsonConverterValidationComposite setCollectionDeck = new ConsumerTypeValidation(SET_COLLECTION);
		setCollectionDeck.objetoRetorno = SetCollectionDTO.class;
		criterias.add(setCollectionDeck);
		
		JsonConverterValidationComposite setPrice = new ConsumerTypeValidation(SET_PRICE);
		setPrice.objetoRetorno = PriceDTO[].class; // Array
		criterias.add(setPrice);
		
		return criterias;
	}
}
