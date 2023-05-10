import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;


import static org.apache.pdfbox.Loader.loadPDF;

public class Main {

    public static void main(String[] args) throws IOException {

        ArrayList<ArrayList<String>>overallList = new ArrayList<>();
        //N.B. No/type engines is before aircraft type and registration in order to filter out gliders
        String [] headers = {"url", "No&TypeEngines", "AircraftType&reg",
                "Year_of_Manufacture", "Date & Time", "Location", "TypeOfFlight", "NoCrew&Passengers",
                "Injuries", "Aircraft Damage", "PIC Licence", "PIC Age",
                "PIC Total & Type", "PIC 90 day", "PIC 28 day"};

        //Read from csv file
        Reader in = new FileReader("C://KC//20 MSc//Semester 3//DISSERTATION//" +
                "AAIB_pdf_Reports//2022//pdf_urls_trial_x3001-3691.csv");
        Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withHeader("url").withFirstRecordAsHeader().parse(in);
        for (CSVRecord record : records) {
            ArrayList<String>innerList = new ArrayList<>();
            String aaibUrl = record.get("url");
            innerList.add(aaibUrl);
            //test
            System.out.println(aaibUrl);

            //open pdf from url
            InputStream inputStream = new URL(aaibUrl).openStream();
            PDDocument document = loadPDF(inputStream);
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            text = text.toLowerCase();

            //filter data required from String

            //NUMBER AND TYPE OF ENGINES
            try {
                String noAndTypeOfEnginesString = StringUtils.substringBetween
                        (text, "no & type of engines", "year of manufacture").trim();
                    innerList.add(noAndTypeOfEnginesString);
                } catch (Exception e) {
                innerList.add("NO DATA");
            }

            //AIRCRAFT TYPE AND REGISTRATION
            try{
                String aircraftTypeAndRegistrationString = StringUtils.substringBetween
                        (text, "aircraft type and registration", "no & type of engines").trim();
                innerList.add(aircraftTypeAndRegistrationString);
            } catch (Exception e) {
                innerList.add("NO DATA");
            }

            //YEAR OF MANUFACTURE
            try {
                String yearOfManufactureString = StringUtils.substringBetween(text, "year of manufacture",
                        "date & time (utc)").trim();
                innerList.add(yearOfManufactureString);
            } catch (Exception e) {
                innerList.add("NO DATA");
            }

            //DATE AND TIME
                try {
                    String dateAndTimeString = StringUtils.substringBetween(text, "date & time (utc)",
                            "location").trim();
                    innerList.add(dateAndTimeString);
                } catch (Exception e) {
                    innerList.add("NO DATA");
                }

            //LOCATION
            try{
                String location = StringUtils.substringBetween(text, "location",
                        "type of flight").trim();
                innerList.add(location);
            } catch (Exception e) {
                innerList.add("NO DATA");
            }

            //TYPE OF FLIGHT
            try {
                String typeOfFlight = StringUtils.substringBetween(text, "type of flight",
                        "persons on board").trim();
                innerList.add(typeOfFlight);
            } catch (Exception e) {
                innerList.add("NO DATA");
            }

            //PERSONS ON BOARD
            try {
                String personsOnBoardString = StringUtils.substringBetween(text, "persons on board",
                        "injuries").trim();
                innerList.add(personsOnBoardString);
            } catch (Exception e) {
            innerList.add("NO DATA");
            }

            //INJURIES
            try {
                String injuriesString = StringUtils.substringBetween(text, "injuries",
                        "nature of damage").trim();
                innerList.add(injuriesString);
            } catch (Exception e) {
                innerList.add("NO DATA");
            }

            //NATURE OF DAMAGE
            try {
                String damageString = StringUtils.substringBetween(text, "nature of damage",
                        "commander").trim();
                innerList.add(damageString);
            } catch (Exception e) {
                innerList.add("NO DATA");
            }

            //COMMANDER'S LICENCE
            try {
                String licence = StringUtils.substringBetween(text, "licence",
                        "commander").trim();
                innerList.add(licence);
            } catch (Exception e) {
                innerList.add("NO DATA");
            }

            //COMMANDER'S AGE
            try{
                String ageString = StringUtils.substringBetween(text, "s age",
                        "commander").trim();
                innerList.add(ageString);
            } catch (Exception e) {
                innerList.add("NO DATA");
            }

            //COMMANDER'S TOTAL AND TYPE HRS
            try {
                String experienceString = StringUtils.substringBetween(text, "experience:", "last 90 days").trim();
                innerList.add(experienceString);
            } catch(Exception e) {
                innerList.add("NO DATA");
            }

            //COMMANDER'S 90 DAY HRS
            try {
                String experienceString = StringUtils.substringBetween(text, "last 90 days - ", "last 28 days - ").trim();
                innerList.add(experienceString);
            } catch(Exception e) {
                innerList.add("NO DATA");
            }

            //COMMANDER'S 28 DAY HRS
            try {
                String experienceString = StringUtils.substringBetween(text, "last 28 days - ", "the").trim();
                innerList.add(experienceString);
            } catch(Exception e) {
                innerList.add("NO DATA");
            }

            //ADD REPORT TO REPORT LIST
            overallList.add(innerList);
        }

        //PARSE REPORT LIST TO .CSV FORMAT
        createCSVFile(headers, overallList);
    }

    public static void createCSVFile(String [] headers, ArrayList<ArrayList<String>>overallList) throws IOException {

        String saveLocation = "C://KC//20 MSc//Semester 3//DISSERTATION//" +
                "AAIB_pdf_Reports//2022//crash_datax3001-3691.csv";
        FileWriter out = new FileWriter(saveLocation);
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                .withHeader(headers))) {

            overallList.forEach((datum) -> {
                try {
                    printer.printRecord(datum);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });


        }
    }

}