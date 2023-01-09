import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        long start = System.currentTimeMillis();
        
        
        Moniteur moniteurTest = new Moniteur ("src/data/data3.txt", 5, 3);
        moniteurTest.executerProcessus();

        long end = System.currentTimeMillis();
        long temps_execution = end - start;
        System.out.println("\nTemps d'exec : " + temps_execution+ "\n");

    }
}