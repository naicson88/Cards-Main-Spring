package com.naicson.yugioh.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.Tuple;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.naicson.yugioh.entity.sets.UserSetCollection;
import com.naicson.yugioh.util.enums.SetType;

@Repository
public interface UserSetCollectionRepository extends JpaRepository<UserSetCollection, Long>{
	
	@Modifying
	@Query(value = "insert into tab_user_setcollection_deck (user_set_collection_id, deck_id) values (:setId, :deckId)",
			nativeQuery = true)
	public void saveSetUserDeckRelation(Long setId, Long deckId);
	
	@Modifying
	@Query(value = "delete from tab_user_setcollection_deck where user_set_collection_id = :setId",nativeQuery = true)		
	public void deleteSetUserDeckRelation(Long setId);
	
	@Query(value = "select deck_id from tab_user_setcollection_deck where user_set_collection_id = :setId",nativeQuery = true)		
	public List<Long> consultSetUserDeckRelation(Long setId);
	
	public Optional<UserSetCollection> findById(Long id);

	public Page<UserSetCollection> findAllBySetCollectionTypeOrderByIdDesc(Pageable pageable, SetType setType);
	
	
	@Query(value = " select count(dk.konami_set_copied) as qtd from tab_user_set_collection dk "
			+ " where konami_set_copied = :konamiSetId and user_id = :userId", nativeQuery = true)
	Integer countQuantityOfASetUserHave(Integer konamiSetId, Long userId);
	
	@Query(value = " select card.id, rel.card_numero, card.nome, rel.card_price, rel.card_set_code, rel.card_raridade, rel.set_rarity_code, rel.rarity_details, rel.quantity, "
			+ " 0 as hasInOtherCollection, rel.is_speed_duel,card.generic_type "
			+ " from tab_rel_deckusers_cards rel "
			+ " inner join tab_cards card on card.id = rel.card_id "
			+ " left join ( "
			+ "	select rel2.card_set_code , sum(rel2.quantity) as qtd "
			+ "    from tab_rel_deckusers_cards rel2 "
			+ "    where deck_id in (select id from tab_user_deck where user_id = :userId and id != :userDeckId) "
			+ "    group by rel2.card_set_code "
			+ " ) as counterTwo on counterTwo.card_set_code = rel.card_set_code "
			+ " where rel.deck_id = :userDeckId  "
			+ " and rel.card_set_code not in (select card_set_code from tab_rel_deck_cards "
			+ " where deck_id in (select deck_id from tab_setcollection_deck where set_collection_id = :konamiCollectionId)) "
			
			+ " union "
			
			+ " select c.id, ifnull(counter.card_numero, rdc.card_numero) as card_numero, c.nome, rdc.card_price, rdc.card_set_code, rdc.card_raridade,  rdc.set_rarity_code, rdc.rarity_details, "
			+ " ifnull(counter.qtd,0) as quantityUserHave, 0 as hasInOtherCollection, rdc.is_speed_duel, c.generic_type "
			+ " from tab_cards c "
			+ " inner join tab_rel_deck_cards rdc on rdc.card_id = c.id "
			+ " left join ( "
			+ "	select rel.card_set_code , sum(rel.quantity) as qtd, rel.card_numero "
			+ "    from tab_rel_deckusers_cards rel "
			+ "    where deck_id = :userDeckId "
			+ "    group by rel.card_set_code "
			+ " ) as counter on counter.card_set_code = rdc.card_set_code "
			+ " where rdc.deck_id in (select deck_id from tab_setcollection_deck where set_collection_id = :konamiCollectionId) "
				, nativeQuery = true)
	public List<Tuple> consultUserSetCollection(Long userDeckId, Long userId, Integer konamiCollectionId);
	
	@Query(value = "select usc.id, usc.name FROM yugioh.tab_user_set_collection usc where user_id = :userId and set_collection_type = :setType order by name asc", nativeQuery = true)
	public List<Tuple> getAllSetsBySetType(String setType, Long userId);
	

	
}
