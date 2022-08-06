# Shulker Clone Backport to 1.15.2

**Forked directly from 
[https://github.com/manyrandomthings/minitweaks](https://github.com/manyrandomthings/minitweaks)**
, **code for shulker cloning backport is entirely written by them**.
I only modified the bindings to work with fabric 1.15.2 and removed the other
mixins from this branch. The code for `shulkerBlockFaceFix` is Mojang's code from 1.16.5, used with
fabric/yarn bindings to make it work for 1.15.2

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
Fixes [MC-139265](https://bugs.mojang.com/browse/MC-139265) / [MC-168900](https://bugs.mojang.com/browse/MC-168900),which makes shulkers use portals correctly now,  
and fixes [MC-183884](https://bugs.mojang.com/browse/MC-183884) which makes shulkers able to be next to each other without teleporting
* Type: `boolean`
* Default value: `false`
* Required options: `true`, `false`
* Categories: `shulkerclone`, `mobs`, `survival`, `bugfix`

## shulkerBlockFaceFix
Fixes [MC-159773](https://bugs.mojang.com/browse/MC-159773), so shulkers properly teleport away from the faces of blocks that aren't flat,
and allows them to attach to bottom slabs/stairs. Most 1.17+ shulker farms depend on this mechanic.
* Type: `boolean`
* Default value: `false`
* Required options: `true`, `false`
* Categories: `shulkerclone`, `mobs`, `survival`, `bugfix`

