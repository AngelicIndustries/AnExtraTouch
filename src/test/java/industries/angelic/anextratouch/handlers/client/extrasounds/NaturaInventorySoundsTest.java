package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class NaturaInventorySoundsTest {

    private static JsonObject events;

    @BeforeClass
    public static void definitions() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(
            NaturaInventorySoundsTest.class.getResourceAsStream("/assets/anextratouch/sounds.json"),
            StandardCharsets.UTF_8)) {
            events = new JsonParser().parse(reader)
                .getAsJsonObject();
        }
    }

    private void aliases(String target, String... registryNames) {
        for (String name : registryNames) {
            String key = "item.pickup.Natura." + name;
            assertTrue("Missing case-sensitive registration: " + key, events.has(key));
            JsonObject definition = events.getAsJsonObject(key);
            assertEquals(
                "player",
                definition.get("category")
                    .getAsString());
            JsonObject sound = definition.getAsJsonArray("sounds")
                .get(0)
                .getAsJsonObject();
            assertEquals(
                name,
                "anextratouch:item." + target,
                sound.get("name")
                    .getAsString());
            assertEquals(
                "event",
                sound.get("type")
                    .getAsString());
        }
    }

    @Test
    public void woodenToolsSoundWoodenWhateverTheirTier() {
        // Darkwood, fusewood and bloodwood tools reuse stone, iron and a custom material.
        aliases("category.gear.wood", "natura.sword.darkwood", "natura.pickaxe.fusewood", "natura.kama.bloodwood");
        aliases("category.gear.leather", "natura.armor.imphelmet", "natura.armor.impboots");
        aliases("pickup.minecraft.stick", "natura.stick");
        aliases("pickup.minecraft.flint_and_steel", "natura.flintandblaze");
        assertFalse(events.has("item.pickup.Natura.natura.bow.ghostwood"));
        assertFalse(events.has("item.pickup.Natura.natura.sword.netherquartz"));
    }

    @Test
    public void foodAndMaterialsFollowTheirVanillaFamilies() {
        aliases("category.fruit", "berry", "berry.nether", "saguaro.fruit", "Natura.netherfood");
        aliases("category.dust", "barleyFood", "wheatBag", "boneBag");
        aliases("category.hay", "barleyFood.0");
        aliases("pickup.minecraft.bread", "barleyFood.3");
        aliases("pickup.minecraft.leather", "barleyFood.6");
        aliases("category.bowl", "natura.stewbowl");
        aliases("pickup.minecraft.bowl", "berryMedley", "natura.emptybowl");
        // Per jack, cactus juice sounds wet and fleshy like meat.
        aliases("category.meat", "impmeat", "waterdrop");
    }

    @Test
    public void blocksWithMisleadingStepSoundsAreCorrected() {
        aliases("category.wood", "bloodwood", "redwoodDoorItem");
        aliases("category.rail", "Blazerail", "BrailPowered", "BrailDetector", "BrailActivator");
        aliases("category.leaves", "floraleaves", "Rare Leaves", "Rare Sapling", "BerryBush", "NetherBerryBush");
        aliases("category.crunchy_dry", "Saguaro");
        aliases("pickup.minecraft.mushroom_block", "greenGlowshroom", "blueGlowshroom");
        // Planks, logs and Natura's other wood blocks already step like wood.
        assertFalse(events.has("item.pickup.Natura.planks"));
        assertFalse(events.has("item.pickup.Natura.Glowshroom"));
    }
}
