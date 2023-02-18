package com.naicson.yugioh.repository;


import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.naicson.yugioh.data.dto.RelUserDeckDTO;
import com.naicson.yugioh.data.dto.cards.CardSetDetailsDTO;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.sets.UserDeck;

@Repository
public interface DeckRepository extends JpaRepository<Deck, Long> {


	List<Deck> findTop30ByNomeContaining(String nomeDeck);
	
	Page<Deck> findAllBySetType(String setType, Pageable pageable);
	
	List<Deck> findAllByIdIn(Long[] arraySetsIds);	
	
	List<Deck> findByNome(String nome);
	
	@Query(value = "Select * from tab_user_deck where user_id = :userId", countQuery = "SELECT count(*) FROM yugioh.tab_cards", nativeQuery = true)
	Page<UserDeck> listDeckUser(Pageable page, Long userId);
	
	@Query(value = " SELECT DK.id, DK.user_id, KONAMI_DECK_COPIED AS deck_id, COUNT(KONAMI_DECK_COPIED) AS qtd " +
				   " FROM tab_user_deck DK " +
				   " WHERE USER_ID = :userId and KONAMI_DECK_COPIED IN (:deckId) " +
				   " GROUP BY (KONAMI_DECK_COPIED) ", nativeQuery = true)	
	List<RelUserDeckDTO> searchForDecksUserHave(Long userId, String deckId);

	Page<Deck> findAllBySetTypeOrderByLancamentoDesc(Pageable pageable, String setType);
	
	@Query(value = "select c.id, rel.card_numero as numero, c.categoria, c.nome, c.atributo, c.propriedade, c.nivel, c.atk, c.def, c.descricao, " +
			   " c.imagem, c.escala, c.descr_pendulum, c.qtd_link, c.generic_type as genericType, c.registry_date as registryDate, " +
			   " rel.card_set_code as cardSetCode, rel.card_price, rel.card_raridade, rel.is_side_deck as isSideDeck, rel.is_speed_duel as isSpeedDuel " +				  
			   " from tab_cards c " +
			   " inner join tab_rel_deck_cards rel on rel.card_id = c.id "+
			   " where deck_id = :deckId ",  nativeQuery = true)	
	List<CardSetDetailsDTO> findAllCardSetDetailsDTOByDeckId(Long deckId);
	

	@Query(value = "select id, nome from tab_decks where set_type = 'DECK' "
			+ " UNION "
			+ " select setcol.id, setcol.name "
			+ " from tab_set_collection setcol ",  nativeQuery = true)
	List<Tuple> autocompleteSet();
	
	@Query(value = "select deck.id, deck.nome, deck.nome_portugues, deck.imgur_url,  deck.lancamento,  deck.set_type, count(ud.konami_deck_copied) as quantityUserHave "
			+ " from tab_decks deck "
			+ " left join tab_user_deck ud on ud.konami_deck_copied = deck.id "
			+ " where deck.nome like CONCAT('%',:setName,'%') "
			+ " and deck.set_type = 'DECK'"
			+ " group by ud.konami_deck_copied"
			+ " UNION"
			+ " select setcol.id, setcol.name, setcol.portuguese_name, setcol.imgur_url, setcol.release_date, setcol.set_collection_type, count(usc.konami_set_copied) as quantityUserHave "
			+ " from tab_set_collection setcol "
			+ " left join tab_user_set_collection usc on usc.konami_set_copied = setcol.id "
			+ " where setcol.name like CONCAT('%',:setName,'%') "
			+ " group by usc.konami_set_copied"
			,  nativeQuery = true)
	List<Tuple> searchSetsByNameKonami(String setName);
	
	@Query(value = "select deck.id, deck.nome,'none' as nome_portugues, deck.imgur_url,  deck.dt_criacao,  deck.set_type, 0 as quantityUserHave "
			+ " from tab_user_deck deck "
			+ " where deck.nome like CONCAT('%',:setName,'%') "
			+ " and user_id = :userId "
			+ " and deck.set_type = 'DECK'"
			+ " UNION"
			+ " select setcol.id, setcol.name, '' as portuguese_name, setcol.imgur_url, setcol.release_date, setcol.set_collection_type, 0 as quantityUserHave "
			+ " from tab_user_set_collection setcol "
			+ " where setcol.name like CONCAT('%',:setName,'%') and user_id = :userId"
			,  nativeQuery = true)
	List<Tuple> searchSetsByNameUser(String setName, Long userId);
	
	@Query(value = "select ud.id, ud.nome  from tab_decks ud where set_type = 'DECK' order by 1 desc", nativeQuery = true)	
	List<Tuple> getAllDecksName();
	
	@Query(value = "select ud.id, ud.nome  from tab_decks ud order by 1 desc", nativeQuery = true)	
	List<Tuple> getAllDecksNameIncludeCollections();
	
	
}
