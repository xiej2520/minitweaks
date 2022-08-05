package shulkerclone;

import carpet.settings.Rule;
import carpet.settings.RuleCategory;

public class ShulkerCloneSettings {

    // 1.17 shulker cloning
    @Rule(
        desc = "1.17 Shulker cloning",
        extra = {
            "A shulker hitting a shulker with a shulker bullet can make a new shulker",
            "Feature from 20w45a, subject to change"
        },
        category = {"shulkerclone", "mobs", RuleCategory.SURVIVAL, "backport"}
    )
    public static boolean shulkerCloning = false;

    // Shulker portal fix
    @Rule(
        desc = "Shulker portal teleportation fix",
        extra = {
            "Fixes MC-139265 / MC-168900, which makes shulkers use portals correctly now,",
            "and fixes MC-183884 which makes shulkers able to be next to each other without teleporting",
            "(well, probably at least)"
        },
        category = {"shulkerclone", "mobs", RuleCategory.SURVIVAL, RuleCategory.BUGFIX}
    )
    public static boolean shulkerPortalFix = false;
}
