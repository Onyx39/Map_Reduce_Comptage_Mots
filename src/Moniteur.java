import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class Moniteur {

    private String chemin;
    private int nbMapper;
    private int nbReducer;


    public Moniteur(String unChemin, int unNbMapper, int unNbReducer) {
        chemin = unChemin;
        nbMapper = unNbMapper;
        nbReducer = unNbReducer;
    }

    public void executerProcessus () throws IOException, InterruptedException {
        FileReader fichier = new FileReader(chemin);
        BufferedReader buffer = new BufferedReader(fichier);
        String ligne;
        ArrayList<String> liste_sous_textes = new ArrayList<String>();
        for (int j = 0; j < nbMapper; j++) {
            liste_sous_textes.add("");
        } 
        int compteur = 0;
        while ((ligne = buffer.readLine()) != null) {
            ligne = ligne.replaceAll("[^\\p{L}\\p{M}\\-]+", " ");     
            String nouvelle_valeur = liste_sous_textes.get(compteur%nbMapper) + " " + ligne;
            liste_sous_textes.set(compteur%nbMapper, nouvelle_valeur);
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

        HashMap<String, Integer> res_reducer = new HashMap<String, Integer>();
        CountDownLatch compte_a_rebours_2 = new CountDownLatch(nbReducer);
        for (int j = 0; j < nbReducer; j++) {
            Reducer test = new Reducer(res_mappers.get(j), compte_a_rebours_2, res_reducer);
            test.start();
        }
        compte_a_rebours_2.await();
        System.out.println("\n\nCroisons les doigts c'est bon !\n" + sortHashMap(res_reducer) + "\n"
            + "Nombre de mots comptés : " + compteMotTotal(res_reducer));

    }

    public LinkedHashMap<String, Integer> sortHashMap (HashMap<String, Integer> hm) {
        LinkedHashMap<String, Integer> sortedHM = new LinkedHashMap<>();
        hm.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .forEachOrdered(x -> sortedHM.put(x.getKey(), x.getValue()));
        return sortedHM;
    }

    public int compteMotTotal (HashMap<String, Integer> hm) {
        int compteur = 0;
        Set<String> cles = hm.keySet();
        for (String cle : cles) {
            compteur += hm.get(cle);
        }
        return compteur;
    }

}