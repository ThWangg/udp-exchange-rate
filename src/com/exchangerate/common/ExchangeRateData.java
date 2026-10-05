package com.exchangerate.common;

public class ExchangeRateData {

    public String time;
    public double tokyo;
    public double paris;
    public double seoul;

    public ExchangeRateData(String time, double tokyo, double paris, double seoul) {
        this.time = time;
        this.tokyo = tokyo;
        this.paris = paris;
        this.seoul = seoul;
    }

    // encode sang chuoi gui qua udp
    public String encode() {
        return time + "|" + tokyo + "|" + paris + "|" + seoul;
    }

    // decode chuoi nhan duoc tu udp
    public static ExchangeRateData decode(String raw) {
        String[] parts = raw.split("\\|");
        if (parts.length != 4)
            return null;
        try {
            String time = parts[0];
            double tokyo = Double.parseDouble(parts[1]);
            double paris = Double.parseDouble(parts[2]);
            double seoul = Double.parseDouble(parts[3]);
            return new ExchangeRateData(time, tokyo, paris, seoul);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
