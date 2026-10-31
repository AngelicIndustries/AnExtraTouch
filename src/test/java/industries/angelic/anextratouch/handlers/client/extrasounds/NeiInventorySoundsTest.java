package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.junit.Test;

public class NeiInventorySoundsTest {

    @Test
    public void requestWithoutReplyAndRejectedRequestDoNotConfirm() {
        AtomicInteger count = new AtomicInteger(12);
        NeiInventorySounds.Change request = new NeiInventorySounds.Change(
            null,
            count::get,
            new ItemStack(new Item()),
            false,
            false);
        assertFalse(request.confirmed());
        count.set(11);
        assertFalse(request.confirmed());
        count.set(12);
        assertFalse(request.confirmed());
        count.set(13);
        assertTrue(request.confirmed());
    }

    @Test
    public void deletionNeedsAReductionAndEmptyDeletionDoesNotConfirm() {
        AtomicInteger count = new AtomicInteger(4);
        NeiInventorySounds.Change request = new NeiInventorySounds.Change(null, count::get, null, false, true);
        assertFalse(request.confirmed());
        count.set(5);
        assertFalse(request.confirmed());
        count.set(3);
        assertTrue(request.confirmed());
        count.set(0);
        request = new NeiInventorySounds.Change(null, count::get, null, false, true);
        assertFalse(request.confirmed());
    }
}
