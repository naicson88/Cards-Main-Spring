package com.naicson.yugioh.util.search;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.naicson.yugioh.entity.Atributo;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.TipoCard;

public class GeneralSpecification<T> implements Specification<T> {

	private static final long serialVersionUID = 1L;
	private List<SearchCriteria> list;

	public GeneralSpecification() {
		this.list = new ArrayList<>();
	}

	public void add(SearchCriteria criteria) {
		list.add(criteria);
	}

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		for (SearchCriteria criteria : list) {
			if (criteria.getOperation().equals(SearchOperation.GREATER_THAN)) {
				predicates.add(builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString()));
			}

			else if (criteria.getOperation().equals(SearchOperation.LESS_THAN)) {
				predicates.add(builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString()));
			}

			else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_EQUAL)) {
				predicates
						.add(builder.greaterThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString()));
			}

			else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL)) {
				predicates.add(builder.lessThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString()));
			}

			else if (criteria.getOperation().equals(SearchOperation.NOT_EQUAL)) {
				predicates.add(builder.notEqual(root.get(criteria.getKey()), criteria.getValue()));
			}

			else if (criteria.getOperation().equals(SearchOperation.EQUAL)) {
				predicates.add(builder.equal(root.get(criteria.getKey()), criteria.getValue()));
			}

			else if (criteria.getOperation().equals(SearchOperation.MATCH)) {
				predicates.add(builder.like(builder.lower(root.get(criteria.getKey())),
						"%" + criteria.getValue().toString().toLowerCase() + "%"));
			}

			else if (criteria.getOperation().equals(SearchOperation.MATCH_END)) {
				predicates.add(builder.like(builder.lower(root.get(criteria.getKey())),
						criteria.getValue().toString().toLowerCase() + "%"));
			}

			else if (criteria.getOperation().equals(SearchOperation.MATCH_START)) {
				predicates.add(builder.like(builder.lower(root.get(criteria.getKey())),
						"%" + criteria.getValue().toString().toLowerCase()));
			}

			else if (criteria.getOperation().equals(SearchOperation.IN)) {
				Predicate predicate = null;

				if (criteria.getKey().equals("tipo"))
					predicate = getPredicateTipoCard(root, builder, criteria);
				else if (criteria.getKey().equals("atributo"))
					predicate = getPredicateAtributos(root, builder, criteria);
				else
					predicate = builder.in(root.get(criteria.getKey())).value(criteria.getValue());

				predicates.add(predicate);
			}

			else if (criteria.getOperation().equals(SearchOperation.NOT_IN)) {
				predicates.add(builder.not(root.get(criteria.getKey())).in(criteria.getValue()));
			}
		}

		return builder.and(predicates.toArray(new Predicate[0]));
	}

	private Predicate getPredicateAtributos(Root<T> root, CriteriaBuilder builder, SearchCriteria criteria) {
		Join<Card, Atributo> joinTipo = root.join("atributo");
		Path<Long> tipoId = joinTipo.get("id");
		ArrayList<String> arr = (ArrayList<String>) criteria.getValue();
		List<String> ids = arr.stream().filter(id -> !id.isBlank()).collect(Collectors.toList()); // new ArrayList<>();
		Predicate predTipo = builder.isTrue(tipoId.in(ids));
		return predTipo;
	}

	private Predicate getPredicateTipoCard(Root<T> root, CriteriaBuilder builder, SearchCriteria criteria) {
		Join<Card, TipoCard> joinTipo = root.join("tipo");
		Path<Long> tipoId = joinTipo.get("id");
		ArrayList<String> arr = (ArrayList<String>) criteria.getValue();
		List<String> ids = arr.stream().filter(id -> !id.isBlank()).collect(Collectors.toList()); // new ArrayList<>();
		Predicate predTipo = builder.isTrue(tipoId.in(ids));
		return predTipo;
	}

}
