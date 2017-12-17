package models;

public class Card {
    public enum ColorType {
        SPADE(0),
        HEART(1),
        DIAMOND(2),
        CLUB(3);

        public final int value;

        private ColorType(int val) {
            value = val;
        }

        @Override
        public String toString() {
            switch (value) {
                case 0:
                    return "♠";
                case 1:
                    return "♥";
                case 2:
                    return "♦";
                case 3:
                    return "♣";
            }
            return super.toString();
        }

        public static ColorType forInt(int newVal) {
            for (ColorType val : values()) {
                if (val.value == newVal) {
                    return val;
                }
            }
            throw new IllegalArgumentException("Invalid ColorType value: " + newVal);
        }
    }

    public enum ValueType {
        SEVEN(0),
        EIGHT(1),
        NINE(2),
        TEN(3),
        JACK(4),
        QUEEN(5),
        KING(6),
        ACE(7);

        public final int value;

        private ValueType(int val) {
            value = val;
        }

        public static ValueType forInt(int newVal) {
            for (ValueType val : values()) {
                if (val.value == newVal) {
                    return val;
                }
            }
            throw new IllegalArgumentException("Invalid ValueType value: " + newVal);
        }
    }

    public ColorType mColorType;
    public ValueType mCardValue;
    public String mOwnerName;

    public Card() {

    }

    public Card(ColorType colorType, ValueType cardValue) {
        mColorType = colorType;
        mCardValue = cardValue;
    }
}
