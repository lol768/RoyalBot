package org.royaldev.royalbot.commands;

import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.BotUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MCPingCommand implements IRCCommand {
    public String getName() {
        return "mcping";
    }

    public String getUsage() {
        return "<command> [server] (port)";
    }

    public String getDescription() {
        return "Pings a Minecraft server and returns its info.";
    }

    public void onCommand(GenericMessageEvent event, String[] args) {
        if (args.length < 1) {
            event.respond("Not enough arguments.");
            return;
        }
        int port;
        if (args.length > 1) {
            try {
                port = Integer.valueOf(args[1]);
            } catch (NumberFormatException e) {
                event.respond(BotUtils.formatException(e));
                return;
            }
        } else port = 25565;
        MinecraftPingReply mpr;
        try {
            mpr = new MinecraftPing().getPing(args[0], port);
        } catch (IOException e) {
            event.respond(BotUtils.formatException(e));
            return;
        }
        event.respond(mpr.getMotd() + " (" + mpr.getOnlinePlayers() + "/" + mpr.getMaxPlayers() + ", " + mpr.getVersion() + ")");
    }

    public CommandType getCommandType() {
        return CommandType.BOTH;
    }

    public AuthLevel getAuthLevel() {
        return AuthLevel.PUBLIC;
    }

    private class MinecraftPing {

        /**
         * Fetches a {@link MinecraftPingReply} for the supplied hostname on default port (25565). Will revert to pre-12w42b ping message if required.
         *
         * @param hostname - the IP of the server to request ping from
         * @return {@link MinecraftPingReply} - list of basic server information
         * @throws IOException thrown when failed to receive packet or when incorrect packet is received
         */
        public MinecraftPingReply getPing(final String hostname) throws IOException {
            return this.getPing(hostname, 25565);
        }

        /**
         * Fetches a {@link MinecraftPingReply} for the supplied hostname and port. Will revert to pre-12w42b ping message if required.
         *
         * @param hostname - the IP of the server to request ping from
         * @param port     - the port of the server to request ping from
         * @return {@link MinecraftPingReply} - list of basic server information
         * @throws IOException thrown when failed to receive packet or when incorrect packet is received
         */
        public MinecraftPingReply getPing(final String hostname, final int port) throws IOException {
            this.validate(hostname, "Hostname cannot be null.");
            this.validate(port, "Port cannot be null.");
            final Socket socket = new Socket();
            socket.connect(new InetSocketAddress(hostname, port), 3000);
            final DataInputStream in = new DataInputStream(socket.getInputStream());
            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.write(0xFE);
            out.write(0x01);
            out.write(0xFA);
            out.writeShort(11);
            out.writeChars("MC|PingHost");
            out.writeShort(7 + 2 * hostname.length());
            out.writeByte(73); // Protocol version
            out.writeShort(hostname.length());
            out.writeChars(hostname);
            out.writeInt(port);
            out.flush();
            if (in.read() != 255) throw new IOException("Bad message: An incorrect packet was received.");
            final short bit = in.readShort();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bit; ++i) sb.append(in.readChar());
            out.close();
            final String[] bits = sb.toString().split("\0");
            if (bits.length != 6) return this.getPing(sb.toString(), hostname, port);
            return new MinecraftPingReply(hostname, port, bits[3], bits[1], bits[2], Integer.valueOf(bits[4]), Integer.valueOf(bits[5]));
        }

        /**
         * Returns a {@link MinecraftPingReply} for the response supplied. <b>Only call from {@link MinecraftPing#getPing(String, int)}.</b>
         * <p/>
         * <p/>
         * This method isn't intended for use outside of the {@link MinecraftPing} class.
         *
         * @param response - the pre-12w42b ping reply message
         * @param hostname - the IP of the server ping was requested from
         * @param port     - the port of the server ping was requested from
         * @return {@link MinecraftPingReply} - list of basic server information
         * @throws IOException thrown when incorrect message supplied
         */
        @Deprecated
        public MinecraftPingReply getPing(final String response, final String hostname, final int port) throws IOException {
            this.validate(response, "Response cannot be null. Try calling MinecraftPing.getPing().");
            this.validate(hostname, "Hostname cannot be null.");
            this.validate(port, "Port cannot be null.");
            final String[] bits = response.split("\u00a7");
            if (bits.length != 3)
                throw new IOException("Bad message: Failed to parse pre-12w42b ping message, check to see if it's correct?");
            return new MinecraftPingReply(hostname, port, bits[0], Integer.valueOf(bits[2]), Integer.valueOf(bits[1]));
        }

        private void validate(final Object o, final String m) {
            if (o == null) throw new RuntimeException(m);
        }
    }

    public class MinecraftPingReply {
        /**
         * The IP of the server
         */
        private final String ip;
        /**
         * The port of the server
         */
        private final int port;
        /**
         * The MOTD of the server
         */
        private final String motd;
        /**
         * The protocol version of the server
         */
        private final String protocolVersion;
        /**
         * The game version of the server
         */
        private final String version;
        /**
         * The max player count of the server
         */
        private final int maxPlayers;
        /**
         * The current online player count of the server
         */
        private final int onlinePlayers;

        protected MinecraftPingReply(final String ip, final int port, final String motd, final int onlinePlayers, final int maxPlayers) {
            this(ip, port, motd, "Pre-47", "Pre-12w42b", onlinePlayers, maxPlayers);
        }

        protected MinecraftPingReply(final String ip, final int port, final String motd, final String protocolVersion, final String version, final int onlinePlayers, final int maxPlayers) {
            this.ip = ip;
            this.port = port;
            this.motd = motd;
            this.protocolVersion = protocolVersion;
            this.version = version;
            this.maxPlayers = maxPlayers;
            this.onlinePlayers = onlinePlayers;
        }

        /**
         * Gets the server's IP
         *
         * @return the server's IP
         */
        public String getIp() {
            return this.ip;
        }

        /**
         * Gets the server's maximum player count
         *
         * @return the server's maximum player count
         */
        public int getMaxPlayers() {
            return this.maxPlayers;
        }

        /**
         * Gets the server's MOTD
         *
         * @return the server's MOTD
         */
        public String getMotd() {
            return this.motd;
        }

        /**
         * Gets the server's current online player count
         *
         * @return the server's current online player count
         */
        public int getOnlinePlayers() {
            return this.onlinePlayers;
        }

        /**
         * Gets the server's port
         *
         * @return the server's port
         */
        public int getPort() {
            return this.port;
        }

        /**
         * Gets the server's protocol version
         *
         * @return the server's protocol version
         */
        public String getProtocolVersion() {
            return this.protocolVersion;
        }

        /**
         * Gets the server's game version
         *
         * @return the server's game version
         */
        public String getVersion() {
            return this.version;
        }

        /**
         * Returns a JSON representation of the data contained within this ping reply.
         */
        @Override
        public String toString() {
            return String.format("{\"ip\":\"%s\",\"port\":%s,\"maxPlayers\":%s,\"onlinePlayers\":%s,\"motd\":\"%s\",\"protocolVersion\":\"%s\",\"serverVersion\":\"%s\"}", this.getIp(), this.getPort(), this.getMaxPlayers(), this.getOnlinePlayers(), this.getMotd(), this.getProtocolVersion(), this.getVersion());
        }
    }
}
