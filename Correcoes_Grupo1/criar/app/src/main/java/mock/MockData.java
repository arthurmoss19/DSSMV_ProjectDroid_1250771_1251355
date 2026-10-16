package mock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.triptrail.BuildConfig;
import model.Category;
import model.Trip;
import model.User;
import util.DateUtils;
import util.PasswordUtils;

/**
 * Dados de demonstração do Grupo 1: utilizadores, categorias e viagens. É o que
 * deixa a app correr sem chaves de API (USE_MOCK_DATA=true).
 */
public final class MockData {

    public static final String DEMO_EMAIL = "demo@triptrail.pt";
    public static final String DEMO_PASSWORD = "triptrail";

    private static MockData instance;

    private final AtomicInteger ids = new AtomicInteger(100);

    private final List<User> users = new ArrayList<>();
    private final List<Category> categories = new ArrayList<>();
    private final List<Trip> trips = new ArrayList<>();

    private MockData() {
        seed();
    }

    public static synchronized MockData getInstance() {
        if (instance == null) {
            instance = new MockData();
        }
        return instance;
    }

    public static boolean isEnabled() {
        return BuildConfig.USE_MOCK_DATA;
    }

    public static boolean isEnrichmentEnabled() {
        return BuildConfig.USE_MOCK_ENRICHMENT;
    }

    public String nextId(String prefix) {
        return prefix + "_" + ids.incrementAndGet();
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    private void seed() {
        seedUsers();
        seedCategories();
        seedTrips();
    }

    private void seedUsers() {
        User demo = new User("Demo Traveller", DEMO_EMAIL, PasswordUtils.hash(DEMO_PASSWORD));
        demo.setId("usr_demo");
        users.add(demo);

        User ana = new User("Ana Silva", "ana@triptrail.pt", PasswordUtils.hash("triptrail"));
        ana.setId("usr_ana");
        users.add(ana);
    }

    private void seedCategories() {
        String[][] data = {
                {"cat_1", "Restaurante", "restaurant"},
                {"cat_2", "Monumento", "monument"},
                {"cat_3", "Natureza", "nature"},
                {"cat_4", "Alojamento", "lodging"},
                {"cat_5", "Praia", "beach"},
                {"cat_6", "Museu", "museum"},
                {"cat_7", "Outro", "other"}
        };
        for (String[] row : data) {
            Category category = new Category(row[1], row[2]);
            category.setId(row[0]);
            categories.add(category);
        }
    }

    private void seedTrips() {
        Trip roadTrip = new Trip("Road trip Lisboa - Porto",
                "Cinco dias a subir a costa, com paragens gastronómicas.",
                DateUtils.todayPlus(-30), DateUtils.todayPlus(-26), "usr_demo");
        roadTrip.setId("trip_1");
        trips.add(roadTrip);

        Trip algarve = new Trip("Fim de semana no Algarve",
                "Praias e falésias entre Portimão e Sagres.",
                DateUtils.todayPlus(-10), DateUtils.todayPlus(-8), "usr_demo");
        algarve.setId("trip_2");
        trips.add(algarve);

        Trip londres = new Trip("Escapadinha em Londres",
                "Três dias de museus e cidade, fora da zona euro.",
                DateUtils.todayPlus(-5), DateUtils.todayPlus(-3), "usr_demo");
        londres.setId("trip_3");
        trips.add(londres);
    }
}
