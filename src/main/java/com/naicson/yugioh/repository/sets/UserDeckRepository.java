package com.naicson.yugioh.repository.sets;

import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.naicson.yugioh.entity.sets.UserDeck;

@Repository
public interface UserDeckRepository extends JpaRepository<UserDeck, Long>{
	
	Page<UserDeck> findAllByUserIdAndSetType(long userId, String setType, Pageable pageable);

	List<UserDeck> findTop30ByNomeContaining(String setName);

	Page<UserDeck> findAllBySetType(Pageable pageable, String setType);
	
	@Query(value = "select count(dk.konami_deck_copied) as qtd  from tab_user_deck dk where konami_deck_copied = :konamiDeckId and user_id = :userId", nativeQuery = true)
	Integer countQuantityOfADeckUserHave(Long konamiDeckId, Long userId);
	
	@Query(value = "select ud.id, ud.nome  from tab_user_deck ud where user_id = :userId and set_type = 'DECK' order by nome asc", nativeQuery = true)
	List<Tuple> getAllDecksName(Long userId);
	
	@Query(value = "select card.id, card.numero, card.nome, rel.card_price, rel.card_set_code, rel.card_raridade, "
					+ "rel.quantity, rel.quantity as hasInOtherCollection, rel.is_speed_duel "
					+ "from tab_rel_deckusers_cards rel "
					+ "inner join tab_cards card on card.id = rel.card_id "
					+ "where rel.deck_id = :deckId", nativeQuery = true)	
	List<Tuple> consultCardsForTransfer(Long deckId);
}
