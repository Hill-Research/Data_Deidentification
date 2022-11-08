import Processor.PatientInfoProcessor;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

class Demo {
    private static final void callProcessor(final File file) throws IOException, NoSuchAlgorithmException {
        String fileName = file.getName();
        switch (fileName) {
            case "tb_patientinfo.txt": {
                PatientInfoProcessor.process(file);
                break;
            }
            default: {
                System.out.format("We don't know how to process data file %s!", fileName);
                System.exit(-1);
            }
        }
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        System.out.println("This is the data de-identification demo.\n");
        final File folder = new File("../data/");
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) {
            System.out.format("We could not find any data files in %s!", folder.getName());
            System.exit(-1);
        }
        for (File file : listOfFiles) {
            if (!file.isFile()) {
                System.out.format("We found abnormal file %s!", file.getName());
                System.exit(-1);
            }
            callProcessor(file);
        }
//
//        for (final File fileEntry : folder.listFiles()) {
//            if (fileEntry.isDirectory()) {
//                listFilesForFolder(fileEntry);
//            } else {
//                System.out.println(fileEntry.getName());
//            }
//        }
//        BufferedReader reader = new BufferedReader(new FileReader(file));
//        String record;
//        while ((record = reader.readLine()) != null) {
//            String[] keywords = record.split("\\s+");
//            for (String keyword : keywords) {
//                System.out.println(keyword);
//            }
//        }
    }
}
