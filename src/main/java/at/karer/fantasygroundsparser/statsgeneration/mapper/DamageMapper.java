package at.karer.fantasygroundsparser.statsgeneration.mapper;

import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;
import at.karer.fantasygroundsparser.statsgeneration.model.CampaignStatistics;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DamageMapper {

    DamageMapper INSTANCE = Mappers.getMapper(DamageMapper.class);

    CampaignStatistics.CharacterStats.Damage toStatsDamage(ChatLogEntry.Damage damage);
    CampaignStatistics.CharacterStats.Damage.DamageType toStatsDamageType(ChatLogEntry.DamageType damageType);
}
