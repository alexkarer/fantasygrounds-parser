# fantasygrounds-parser (WIP)

A small CLI tool that reads a Fantasygrounds 5e campaign and generates statistics for it.

## Local Development

The following tools are required for local development:
- JDK version 17 or above
- Maven version 3.9.5, lower versions might work but haven't tested it.

All required dependencies should be loaded via maven

## Usage

no jar file generation implement yet

## Known Issues/Quirks

Here follows a list of current Issues or possible strange behaviour with the Parser.

### Partial Resistance against damage with multiple damage types

**Example:** if Hero 1 attacks a Creature with Fire Resistance with a Flaming Sword.
The regular slashing damage is not reduced however the fire damage is.

Unfortunately for cases like this the Fantasygrounds Chatlog does not provide all the necessary Information to fully parse that since the Chatlog does not state what damage was resisted, we just know how much was resisted of the total damage (fire+slashing)

For cases like this it is assumed that the creature is resistant against all types of damage done by the attacker and the damage done will be evenly reduced across all damage types.

### No Difference between receiving half damage and resisting

**Example:** Hero succeeds against a dexterity saving throw of a fireball it will receive half damage. Hero 2 fails the saving throw  but has resistance against fire damage.

In this example both will count as having resisted the damage.

## Planned Features

It should generate the following statistics:
- Damage done
- Damage received
- Healing done
- Death saves succeed failed
- Critical Hits/Misses
- Amount of times fallen prone
- Amount of times gone unconscious
- Concentration Held
- Number of Nat 20s dropped with disadvantage
- Number of Nat 1s dropped with disadvantage

Maximum stats:
- most single target damage
- most damage with single ability
- most single target healing
- most healing with single ability
- highest skill check
- highest saving throw

It also should generate various "Titles" for Characters:
- **?**: Most damage done
- **Saint:** Most healing done
- **Deadly:** Most critical Hits
- **Greedy:** Most items taken from inventory
- **Unyielding:** Rolled 3 Natural 20s on Death Saving Throws
- **Linguist:** Was able to translate most messages
- **?:** Had top initiative most fights
- **Tank:** Most damage received
- **Killer:** Most finishing blows dealt
- **Overkill:** Most damage dealt that was necessary
- **Lucky:** rolled a natural 20 when you had disadvantage
- **Unlucky:** Rolled a natural 1 when you had advantage

There should also be campaigns/party stats:
- Gold earned
- total party kills
- creatures left at 1 HP