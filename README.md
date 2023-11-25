# fantasygrounds-parser (WIP)

A small CLI tool that reads a Fantasygrounds 5e campaign and generates statistics for it.

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