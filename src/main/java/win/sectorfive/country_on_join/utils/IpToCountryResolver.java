package win.sectorfive.country_on_join.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IpToCountryResolver {

    private List<RangeEntry> rangeEntries;

    public IpToCountryResolver() {
        loadCsvData();
    }

    private void loadCsvData() {
        rangeEntries = new ArrayList<>();
        try (InputStream inputStream = IpToCountryResolver.class.getResourceAsStream("/ip_database.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                String startIp = fields[0];
                String endIp = fields[1];
                String country = fields[2];
                String countryName = fields[3];
                String continent = fields[4];
                String continentName = fields[5];

                try {
                    rangeEntries.add(new RangeEntry(startIp, endIp, country, countryName, continent, continentName));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCountryName(String ipAddress) {
        return getDetails(ipAddress, "countryName");
    }

    public String getCountryCode(String ipAddress) {
        return getDetails(ipAddress, "country");
    }

    public String getContinent(String ipAddress) {
        return getDetails(ipAddress, "continent");
    }

    public String getContinentName(String ipAddress) {
        return getDetails(ipAddress, "continentName");
    }

    private String getDetails(String ipAddress, String detailType) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            for (RangeEntry entry : rangeEntries) {
                if (entry.contains(inetAddress)) {
                    switch (detailType) {
                        case "countryName":
                            return entry.getCountryName();
                        case "country":
                            return entry.getCountry();
                        case "continent":
                            return entry.getContinent();
                        case "continentName":
                            return entry.getContinentName();
                        default:
                            return null;
                    }
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class RangeEntry {
        private BigInteger startIp;
        private BigInteger endIp;
        private String country;
        private String countryName;
        private String continent;
        private String continentName;

        public RangeEntry(String startIp, String endIp, String country, String countryName, String continent, String continentName) throws UnknownHostException {
            this.startIp = ipToBigInteger(startIp);
            this.endIp = ipToBigInteger(endIp);
            this.country = country;
            this.countryName = countryName;
            this.continent = continent;
            this.continentName = continentName;
        }

        private static BigInteger ipToBigInteger(String ipAddress) throws UnknownHostException {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            byte[] bytes = inetAddress.getAddress();
            return new BigInteger(1, bytes);
        }

        public boolean contains(InetAddress ipAddress) throws UnknownHostException {
            BigInteger ip = ipToBigInteger(ipAddress.getHostAddress());
            return ip.compareTo(startIp) >= 0 && ip.compareTo(endIp) <= 0;
        }

        public String getCountry() {
            return country;
        }

        public String getCountryName() {
            return countryName;
        }

        public String getContinent() {
            return continent;
        }

        public String getContinentName() {
            return continentName;
        }
    }
}
