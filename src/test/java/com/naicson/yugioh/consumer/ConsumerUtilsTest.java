package com.naicson.yugioh.consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class) // Usado com Junit5 ao inves do RunWith
public class ConsumerUtilsTest {
	
	@InjectMocks
	@Spy
	ConsumerUtils consumerUtils;
	
	@Test
	public void testConvertJsonToObject() {
		String json = "teste";
	}
}
