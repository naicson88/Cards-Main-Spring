package com.naicson.yugioh.data.chainResponsability.user;

import com.naicson.yugioh.data.dto.AccountManageDTO;
import com.naicson.yugioh.entity.auth.User;

public interface HandleUserValidation {
	
	User validateUserAttribute(User user, AccountManageDTO dto);
}
