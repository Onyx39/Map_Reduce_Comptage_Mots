import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class Reducer extends Thread {

    private HashMap<String, ArrayList<Integer>> a_reduire;
    private CountDownLatch compte_a_rebours;
    private static HashMap<String, Integer> resultats;


    public Reducer(HashMap<String, ArrayList<Integer>> a_red, CountDownLatch car, HashMap<String, Integer> res) {
        a_reduire = a_red;
        compte_a_rebours = car;
        resultats = res;
    }

    public void run () {
        Set<String> liste_cle = a_reduire.keySet();
        for (String cle : liste_cle) {
            ArrayList<Integer> maListe = a_reduire.get(cle);
            Integer occurrence = maListe.stream().mapToInt(i -> i).sum();
            resultats.put(cle, occurrence);
        }
        compte_a_rebours.countDown();


    }

}