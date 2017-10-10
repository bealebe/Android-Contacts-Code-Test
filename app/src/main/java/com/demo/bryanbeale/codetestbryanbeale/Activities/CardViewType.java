package com.demo.bryanbeale.codetestbryanbeale.Activities;

/**
 * Created by bryanbeale on 9/23/17.
 *
 */

public enum CardViewType {

    NAME_CARD(0),
    PHONE_CARD(1),
    EMAIL_CARD(2),
    ADDRESS_CARD(3);


    private int value;

    public int getValue(){
        return value;
    }

    CardViewType(int value){
        this.value = value;
    }

    public static CardViewType fromInt(int value){
        switch (value){
            case 0:
                return NAME_CARD;
            case 1:
                return PHONE_CARD;
            case 2:
                return EMAIL_CARD;
            case 3:
                return ADDRESS_CARD;
            default:
                return NAME_CARD;
        }
    }
}
