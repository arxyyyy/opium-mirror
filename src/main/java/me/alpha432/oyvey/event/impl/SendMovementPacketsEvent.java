package me.alpha432.oyvey.event.impl;

public class SendMovementPacketsEvent {
    public static class Pre {
        private static final Pre INSTANCE = new Pre();

        public static Pre get() {
            return INSTANCE;
        }
    }

    public static class Rotation {
        public float yaw;
        public float pitch;
        public boolean forceFull;

        public Rotation(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
        }
    }

    public static class Post {
        private static final Post INSTANCE = new Post();

        public static Post get() {
            return INSTANCE;
        }
    }
}