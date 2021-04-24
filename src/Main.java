import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws Exception {
        Path currentRelativePath = Paths.get("src", "data");
        Path textPath = currentRelativePath.resolve("test.txt");
        final Stream<String> lines = Files.lines(textPath);

        final ArrayList<PrintWriter> pwList = new ArrayList<>();
        AtomicInteger fileCount = new AtomicInteger();
        AtomicInteger writeCount = new AtomicInteger();
        writeCount.set(-1);
        lines.map((line) -> {
            if (line.contains("SZ")) {
                Path csvPath = generateFile(currentRelativePath, "output" + fileCount.getAndIncrement() + ".csv");
                try {
                    pwList.add(new PrintWriter(Files.newBufferedWriter(csvPath, StandardOpenOption.CREATE)));
                    line = line.replace(line, "SZ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return line;
        }).map((line) -> {
            if (line.contains("record")) {
                if (line.contains("SZ")) {
                    line = line.replace(line, "SZ");
                } else {
                    line = line.replace(line, "");
                }
            } else if (line.substring(line.length() - 1).contains("]") || line.substring(line.length() - 1).contains("[")) {
                line = line.replace("]", "\n");
                line = line.replace("[", "");
            }
            line = line.trim();
            line = line.replace(" ", ",");
            line = line.replace("\",\"", " ");
            System.out.println(line);
            return line;
        }).forEach((line) -> {
                    if (line.contains("SZ")) {
                        writeCount.incrementAndGet();
                    } else if (!line.equals("")) {
                        pwList.get(writeCount.get()).write(line + "\n");
                        pwList.get(writeCount.get()).flush();
                    }
                }
        );


    }

    private static Path generateFile(Path textPath, String fileName) {
        return textPath.resolve(fileName);
    }

    private static Path populateCSVFile(Path textPath) {
        return textPath.resolve("output.csv");
    }
}