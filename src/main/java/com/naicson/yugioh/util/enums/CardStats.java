package com.naicson.yugioh.util.enums;

import java.util.List;

import com.naicson.yugioh.entity.stats.CardPriceInformation;
import com.naicson.yugioh.entity.stats.CardViewsInformation;
import com.naicson.yugioh.repository.CardPriceInformationRepository;
import com.naicson.yugioh.repository.CardViewsInformationRepository;



public enum CardStats {
	
    HIGH {
    	
        @Override
        public List<CardPriceInformation> getStats(CardPriceInformationRepository cardInfoRepo) {
        	List<CardPriceInformation> highCards = cardInfoRepo.findTop6ByOrderByWeeklyPercentVariableDesc();     	
        	return highCards;
      	
        }

		@Override
		public List<CardViewsInformation> getStatsView(CardViewsInformationRepository cardViewRepo) {
			return null;
		} 
    },

    LOW {
        @Override
        public List<CardPriceInformation> getStats(CardPriceInformationRepository cardInfoRepo) {
        	return  cardInfoRepo.findTop6ByOrderByWeeklyPercentVariableAsc();
        }

		@Override
		public List<CardViewsInformation> getStatsView(CardViewsInformationRepository cardViewRepo) {
			return null;
		}
    },

    VIEW {
        @Override
        public List<CardPriceInformation> getStats(CardPriceInformationRepository cardInfoRepo) {
        	return null;
        }

		@Override
		public List<CardViewsInformation> getStatsView(CardViewsInformationRepository cardViewRepo) {
			List<CardViewsInformation> cardViews = cardViewRepo.findTop6ByOrderByQtdViewsWeeklyDesc();
			return cardViews;
		}
    };
	
	
    public abstract List<CardPriceInformation> getStats(CardPriceInformationRepository cardInfoRepo);
    public abstract List<CardViewsInformation> getStatsView(CardViewsInformationRepository cardViewRepo);

}
