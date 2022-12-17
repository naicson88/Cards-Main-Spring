package com.naicson.yugioh.data.strategy.source;

import org.springframework.stereotype.Component;


public enum SourceTypesStrategy {
	
	USER {
		@Override
		SourceStrategy getStrategy() {
			return new SourceUser();
		}
	},
	
	KONAMI {
		@Override
		SourceStrategy getStrategy() {
			return new SourceKonami();
		}
	};
	
	abstract SourceStrategy getStrategy();
}
