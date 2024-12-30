package org.nrnr.opium.impl.event.network;

import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.nrnr.opium.api.event.Cancelable;
import org.nrnr.opium.api.event.Event;
import org.nrnr.opium.init.Managers;

/**
 * @author chronos
 * @since 1.0
 */
public class PacketEvent extends Event {
    //
    private final Packet<?> packet;

    /**
     * @param packet
     */
    public PacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    /**
     * @return
     */
    public Packet<?> getPacket() {
        return packet;
    }

    /**
     *
     */
    @Cancelable
    public static class Inbound extends PacketEvent {

        private final PacketListener packetListener;

        /**
         * @param packet
         */
        public Inbound(PacketListener packetListener, Packet<?> packet) {
            super(packet);
            this.packetListener = packetListener;
        }

        public PacketListener getPacketListener() {
            return packetListener;
        }
    }

    /**
     *
     */
    @Cancelable
    public static class Send extends PacketEvent { // send
        //
        private final boolean cached;

        /**
         * @param packet
         */
        public Send(Packet<?> packet) {
            super(packet);
            this.cached = Managers.NETWORK.isCached(packet);
        }

        /**
         * @return
         */
        public boolean isClientPacket() {
            return cached;
        }
    }

    /**
     *
     */
    @Cancelable
    public static class Outbound extends PacketEvent {
        //
        private final boolean cached;

        /**
         * @param packet
         */
        public Outbound(Packet<?> packet) {
            super(packet);
            this.cached = Managers.NETWORK.isCached(packet);
        }

        /**
         * @return
         */
        public boolean isClientPacket() {
            return cached;
        }
    }
}
