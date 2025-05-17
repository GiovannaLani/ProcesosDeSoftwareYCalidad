package com.spq.client.data;

public enum ShipmentStatus {
        SHIPPED("Enviado"),
        IN_TRANSIT("En tránsito"),
        DELIVERED("Entregado"),;

        private final String displayName;

        ShipmentStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
}
