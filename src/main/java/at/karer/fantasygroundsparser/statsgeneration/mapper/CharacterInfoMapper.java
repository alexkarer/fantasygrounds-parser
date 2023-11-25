package at.karer.fantasygroundsparser.statsgeneration.mapper;

import at.karer.fantasygroundsparser.fantasygrounds.model.CharacterSheet;
import at.karer.fantasygroundsparser.statsgeneration.model.CampaignStatistics;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CharacterInfoMapper {

    CharacterInfoMapper INSTANCE = Mappers.getMapper(CharacterInfoMapper.class);

    CampaignStatistics.CharacterStats.CharacterInfo toCharacterInfo(CharacterSheet characterSheet);
}
