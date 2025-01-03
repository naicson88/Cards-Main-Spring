package com.naicson.yugioh.repository;

import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.naicson.yugioh.data.dto.home.HomeDTO;


@Repository
public interface HomeRepository extends JpaRepository<HomeDTO, Long>{
	
	@Query(value = "select  count(set_type) from tab_user_deck as du where set_type = :setType and du.user_id = :userId ", nativeQuery = true)
	long returnQuantitySetType(String setType, Long userId);
	
	@Query(value = "select  count(card_numero) "
			+ " from tab_rel_deckusers_cards rel "
			+ " inner join tab_user_deck du on du.id = rel.deck_id "
			+ " where du.user_id = :userId ", nativeQuery = true)
	long returnQuantityCardsUserHave(long userId);
	
	@Query(value = "select distinct * from tab_user_deck where user_id = :userId  and set_type = 'DECK' order by dt_criacao desc limit 10", nativeQuery = true)
	List<Tuple> returnLastDecksAddedToUser(long userId);
	
	@Query(value = "SELECT * FROM yugioh.tab_user_set_collection where user_id = :userId  order by registration_date desc limit 10", nativeQuery = true)
	List<Tuple> returnLastSetsAddedToUser(long userId);
	
	@Query(value = 	
			 " select distinct cards.numero, cards.nome, rel.card_set_code, rel.card_price, du.dt_criacao from tab_cards cards "
			+ " inner join tab_rel_deckusers_cards rel on rel.card_numero = cards.numero "
			+ " inner join tab_user_deck du on du.id = rel.deck_id "
			+ " where du.user_id = :userId"
			+ " order by du.dt_criacao "
			+ " limit 10 ", nativeQuery = true)
	List<Tuple> lastCardsAddedToUser(Long userId);
	
	@Query(value = " select * from (  "
			+ " select d1.id, d1.nome, d1.imgur_url, d1.dt_criacao as dt_criacao, d1.set_type  as dt_reg  "
			+ " from tab_decks d1 where set_type = 'DECK' "
			+ " UNION "
			+ " select us.id, us.name, us.imgur_url, us.registration_date as dt_criacao, us.set_collection_type  as dt_reg "
			+ " from tab_set_collection as us "
			+ " ) as u "
			+ " order by u.dt_criacao desc "
			+ " limit 5 ",
			nativeQuery = true)
	List<Tuple> getHotNews();
	
	@Query(value = "select IFNULL(ROUND(sum(card_price),2), 0.0) as total from tab_rel_deckusers_cards where deck_id = :setId", nativeQuery = true)
	Double findTotalDeckPrice(Long setId);
	
	@Query(value = "select IFNULL(ROUND(sum(card_price * quantity),2), 0.0) as total from tab_rel_deckusers_cards where deck_id = :setId", nativeQuery = true)
	Double findTotalSetPrice(Long setId);
	
	@Query(value = "select distinct card.numero, card.nome, card.numero as img, \"\" as setCode, 'CARD' as entity_type "
			+ "from tab_cards card "
			+ "union "
			+ "select d.id, d.nome, d.imagem, d.set_code, 'DECK' as entity_type "
			+ "from tab_decks d "
			+ "where d.set_type = 'DECK' "
			+ "union "
			+ "select sc.id, sc.name, sc.img_path, sc.set_code, 'COLLECTION' "
			+ "from tab_set_collection sc ",
			nativeQuery = true)
	List<Tuple> generalSearch ();
	
}
