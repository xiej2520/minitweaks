# Shulker Clone Backport to 1.15.2

**Forked directly from 
[https://github.com/manyrandomthings/minitweaks](https://github.com/manyrandomthings/minitweaks)**
, **code for shulker cloning backport is entirely written by them**.
I only modified the bindings to work with fabric 1.15.2 and removed the other
mixins from this branch.

This fork only contains the mixins for shulker cloning and the shulker portal
coordinate fix.

Requires [fabric-carpet](https://github.com/gnembon/fabric-carpet)

# Settings

## shulkerCloning
1.17 Shulker cloning  
"A shulker hitting a shulker with a shulker bullet can make a new shulker"  
Feature from 20w45a, subject to change  
* Type: `boolean`
* Default value: `false`
* Required options: `true`, `false`
* Categories: `shulkerclone`, `mobs`, `survival`, `backport`

## shulkerPortalFix
Fixes [MC-139265](https://bugs.mojang.com/browse/MC-139265) / [MC-168900](https://bugs.mojang.com/browse/MC-168900), which makes shulkers use portals correctly now,  
and fixes [MC-183884](https://bugs.mojang.com/browse/MC-183884) which makes shulkers able to be next to each other without teleporting
* Type: `boolean`
* Default value: `false`
* Required options: `true`, `false`
* Categories: `shulkerclone`, `mobs`, `survival`, `bugfix`
