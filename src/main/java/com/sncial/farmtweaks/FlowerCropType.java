package com.sncial.farmtweaks;

enum FlowerCropType {
    DANDELION("dandelion"),
    POPPY("poppy"),
    BLUE_ORCHID("blue_orchid"),
    ALLIUM("allium"),
    AZURE_BLUET("azure_bluet"),
    RED_TULIP("red_tulip"),
    ORANGE_TULIP("orange_tulip"),
    WHITE_TULIP("white_tulip"),
    PINK_TULIP("pink_tulip"),
    OXEYE_DAISY("oxeye_daisy"),
    CORNFLOWER("cornflower"),
    LILY_OF_THE_VALLEY("lily_of_the_valley"),
    WITHER_ROSE("wither_rose");

    private final String id;

    FlowerCropType(String id) {
        this.id = id;
    }

    String id() {
        return id;
    }

}
