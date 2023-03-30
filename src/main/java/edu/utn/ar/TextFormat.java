package edu.utn.ar;

public class TextFormat {
    public static class colors {
        public static final String reset = "\u001B[0m";
        public static final String black = "\u001B[30m";
        public static final String red = "\u001B[31m";
        public static final String green = "\u001B[32m";
        public static final String yellow = "\u001B[33m";
        public static final String blue = "\u001B[34m";
        public static final String purple = "\u001B[35m";
        public static final String cyan = "\u001B[36m";
        public static final String white = "\u001B[37m";
    }
    public static class effects {
        public static final String bold = "\u001B[1m";
        public static final String dim = "\u001B[2m";
        public static final String italic = "\u001B[3m";
        public static final String underline = "\u001B[4m";
        public static final String blink = "\u001B[5m";
        public static final String reverse = "\u001B[7m";
        public static final String hidden = "\u001B[8m";
    }
    public static class icons {
        public static final String success = "\t[" + colors.purple + "s" + colors.reset + "] ";
        public static final String info = "\t[" + colors.blue + "i" + colors.reset + "] ";
        public static final String error = "\t[" + colors.red + "e" + colors.reset + "] ";
        public static final String help = "\t[" + colors.green + "h" + colors.reset + "] ";
        public static final String warning = "\t[" + colors.yellow + "w" + colors.reset + "] ";
    }
}