package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import org.junit.Test;

public class KitchenDoorsTest {

    @Test
    public void doorsSoundWhenTheyStartOpeningAndWhenTheyShut() {
        assertEquals(KitchenDoors.Move.OPEN, KitchenDoors.move(0, 0.2F));
        assertEquals(KitchenDoors.Move.CLOSE, KitchenDoors.move(0.1F, 0));
        // Still swinging, or at rest.
        assertNull(KitchenDoors.move(0.2F, 0.4F));
        assertNull(KitchenDoors.move(1, 1));
        assertNull(KitchenDoors.move(0.5F, 0.4F));
        assertNull(KitchenDoors.move(0, 0));
    }
}
