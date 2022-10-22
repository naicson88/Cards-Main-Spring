package com.naicson.yugioh.util.enums;

import java.util.Arrays;
import java.util.List;

import com.naicson.yugioh.util.exceptions.ErrorMessage;

public enum ECardRarity {
	
	
	COMMON("Common", List.of("(NR)","(SP)", "(HFR)", "(DNPR)")),
	RARE("Rare", List.of("(R)","(DNRPR)", "(DRPR)")),
	SUPER_RARE("Super Rare", List.of("(SPR)", "(DSPR)")),
	ULTRA_RARE("Ultra Rare", List.of("(UR(PR))", "(UPR)", "(DUPR)", "(PG)")),
	SECRET_RARE("Secret Rare", 
			List.of("(ScR)","(HGR)","(PlR)", "(StR)","(PScR)","(EScR)","(PlScR)", "(UScR)","(20ScR)", "(ScUR)","(10000ScR)", "(ScPR)", "(EScPR)", "(DScPR)")),
	ULTIMATE_RARE("Ultimate Rare",List.of("(UtR)")),
	GOLD_RARE("Gold Rare", List.of("(GScR)", "(GGR)", "(PGR)")),
	PARALLEL_RARE("Parallel Rare",
			List.of("(NPR)", "(SFR)", "(MSR)", "(SHR)", "(CR)", "(HGPR)", "(KCC)", "(KCR)", "(KCUR)")),
	GHOST_RARE("Ghost Rare", List.of("(GR)")),
	UNKNOWN("Not Defined", List.of(""));
	
	private final String name;
	private final List<String> listRarityCode;
	public static final List<String> DEFAULT_RARITIES = List.of("Common", "Rare",
			"Super Rare", "Ultra Rare", "Ultimate Rare", "Gold Rare", "Ghost Rare");
	
	ECardRarity(String rarity, List<String> listRarityCode){
		name = rarity;
		this.listRarityCode = listRarityCode;
	}
	
	public static ECardRarity getRarityByRarityCode(String rarityCode) {
		return Arrays.stream(ECardRarity.values())
				.filter(rarity -> rarity.listRarityCode
				.contains(rarityCode))
				.findFirst().orElseThrow(() -> new ErrorMessage("Rarity Not Found!"));
	}
	
	public String getCardRarity() {
		return name;
	}
	
	public List<String> getListRarityCode(){
		return listRarityCode;
	}
	
	
}
