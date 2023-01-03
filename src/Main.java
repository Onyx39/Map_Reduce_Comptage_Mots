import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


public class Main {

    public static void main(String[] args) throws IOException {
        FileReader fichier = new FileReader("src/data/data1.txt");
        BufferedReader buffer = new BufferedReader(fichier);
        String ligne;
        while ((ligne = buffer.readLine()) != null) {
            System.out.println(ligne);
        }
        
        buffer.close();


    }
}