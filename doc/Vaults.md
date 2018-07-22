# Vaults

In URE, a *vault* is a room template -- a block of terrain which can be stamped by a Landscaper into a generated area, with associated
spawn info for Actors and Things, and possibly other metadata.  Vaults are loaded by various functions of Landscaper from JSON files in
*vault sets*, named collections of vaults sharing a theme or purpose.  In writing your own Landscapers you can use provided functions to load,
select from, and stamp these vaults into your own areas.

## VaultEd

The ExampleGame implements an in-engine editor for creating Vaults.  Hit F1 once in-game to enter VaultEd.
