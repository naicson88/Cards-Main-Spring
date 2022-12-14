package com.naicson.yugioh.data.strategy.setDetails;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SetDetailsStrategyConfig {
	
	private final List<SetDetailsStrategy> setDetailsStrategies;

	public SetDetailsStrategyConfig(List<SetDetailsStrategy> setDetailsStrategies) {
		super();
		this.setDetailsStrategies = setDetailsStrategies;
	}

	@Bean
	public Map<SetDetailsType, SetDetailsStrategy> getDetailByType() {
		
		 Map<SetDetailsType, SetDetailsStrategy> detailsByType = new EnumMap<>(SetDetailsType.class);
		 
		 setDetailsStrategies.forEach(strategy -> detailsByType.put(strategy.setDetailsType(), strategy));
		 
		 return detailsByType;
	}
}
