package ru.lumberjackcode.vacls.transfere;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
@SuppressWarnings("all")

public class AdminResponse {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static DateTimeFormatter getDateTimeFormatter() {
        return formatter;
    }

    public static class EntriesRange implements IResponse{
        private String error_message;
        private String minimum_date, maximum_date;

        public EntriesRange() {
            error_message = "";
            minimum_date = "00.00.0000 00:00";
            maximum_date = "00.00.0000 00:00";
        }

        public EntriesRange(String error_message, LocalDateTime minDate, LocalDateTime maxDate) {
            this.error_message = error_message;
            minimum_date = minDate.format(formatter);
            maximum_date = maxDate.format(formatter);
        }

        public static EntriesRange fromUtf8Json(byte[] json){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.fromJson(new String(json, Charset.forName("UTF-8")), EntriesRange.class);
        }

        public String getError_message() {
            return error_message;
        }

        public String getMinimum_date() {
            return minimum_date;
        }

        public String getMaximum_date() {
            return maximum_date;
        }
    }

    public static class Entry implements IResponse{
        private String identifier;
        private String date;

        public Entry() {
            identifier = "0";
            date = "00.00.0000 00:00";
        }

        public Entry(String identifier, LocalDate date) {
            this.identifier = identifier;
            this.date = date.format(formatter);
        }

        public static Entry fromUtf8Json(byte[] json){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.fromJson(new String(json, Charset.forName("UTF-8")), Entry.class);
        }

        public String getDate() {
            return date;
        }

        public String getIdentifier() {
            return identifier;
        }
    }

    public static class Entries implements IResponse{
        private String error_message;
        private  List<Entry> entries;

        public Entries() {
            error_message = "";
            entries = new LinkedList<>();
        }

        public Entries(String error_message) {
            this.error_message = error_message;
            entries = new LinkedList<>();
        }

        public static Entries fromUtf8Json(byte[] json){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.fromJson(new String(json, Charset.forName("UTF-8")), Entries.class);
        }

        public void add(Entry entry) {
            entries.add(entry);
        }

        public String getError_message() {
            return error_message;
        }
    }

    public static class JSDownload implements IResponse {
        private String error_message;
        private String script;

        public JSDownload() {
            error_message = "";
            script = "function () { return \"Test message\" }";
        }

        public JSDownload(String error_message, String script) {
            this.error_message = error_message;
            this.script = script;
        }

        public static JSDownload fromUtf8Json(byte[] json){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.fromJson(new String(json, Charset.forName("UTF-8")), JSDownload.class);
        }

        public String getError_message() {
            return error_message;
        }

        public String getScript() {
            return script;
        }
    }
}
