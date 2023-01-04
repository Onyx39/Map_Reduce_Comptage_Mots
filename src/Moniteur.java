import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class Moniteur {

    private String chemin;
    private int nbMapper = 30;
    private int nbReducer = 30;


    public Moniteur(String unChemin) {
        chemin = unChemin;
    }

    public void executerProcessus () throws IOException, InterruptedException {
        FileReader fichier = new FileReader(chemin);
        BufferedReader buffer = new BufferedReader(fichier);
        String ligne;
        //String texte = "";
        ArrayList<String> liste_sous_textes = new ArrayList<String>();
        for (int j = 0; j < nbMapper; j++) {
            liste_sous_textes.add("");
        } 
        int compteur = 0;
        while ((ligne = buffer.readLine()) != null) {
            String nouvelle_valeur = liste_sous_textes.get(compteur%nbMapper) + ligne;
            liste_sous_textes.set(compteur%nbMapper, nouvelle_valeur);
            //texte += ligne;
            compteur++;
        }

        buffer.close();

        ArrayList<HashMap<String, ArrayList<Integer>>> res_mappers = new ArrayList<>();
        for (int j = 0; j < nbReducer; j++) {
            res_mappers.add(new HashMap<>());
        } 
        CountDownLatch compte_a_rebours = new CountDownLatch(nbMapper);
        for (int i = 0; i < nbMapper; i++) {
            Mapper test = new Mapper(liste_sous_textes.get(i), compte_a_rebours, res_mappers);
            test.start();
        }
        compte_a_rebours.await();
        //System.out.println("Normalement, tout est bon...");
        //System.out.println(res_mappers);

        HashMap<String, Integer> res_reducer = new HashMap<String, Integer>();
        CountDownLatch compte_a_rebours_2 = new CountDownLatch(nbReducer);
        for (int j = 0; j < nbReducer; j++) {
            Reducer test = new Reducer(res_mappers.get(j), compte_a_rebours_2, res_reducer);
            test.start();
        }
        compte_a_rebours_2.await();
        System.out.println("\n\nCroisons les doigts c'est bon !\n" + sortHashMap(res_reducer)+  "\n");


        

    }

    public LinkedHashMap<String, Integer> sortHashMap (HashMap<String, Integer> hm) {
        LinkedHashMap<String, Integer> sortedHM = new LinkedHashMap<>();
        hm.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .forEachOrdered(x -> sortedHM.put(x.getKey(), x.getValue()));
        return sortedHM;
    }

}