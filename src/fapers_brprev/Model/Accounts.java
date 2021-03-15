package fapers_brprev.Model;

import fileManager.FileManager;
import fileManager.StringFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Accounts {

    private static List<Map<String, Object>> list = new ArrayList<>();

    public static void addOnList(File file) {
        String text = FileManager.getText(file);
        String[] lines = text.split("\r\n");

        for (String line : lines) {
            if (!line.startsWith("#")) {
                String[] cols = line.split(";", -1);

                Map<String, Object> map = new HashMap<>();
                map.put("conta", Integer.valueOf(cols[0]));
                map.put("historicoPadrao", Integer.valueOf(cols[1]));
                map.put("filtro", new StringFilter(cols[2].replaceAll(" ", ";")));

                list.add(map);
            }
        }
    }
    
    /**
     * Retorna o objeto do mapa se o filtro bater
     *
     * @param filter Filtro de string
     * @return objeto do mapa se o filtro bater
     */
    public static Map<String, Object> get(String filter) {
        Object[] obj = new Object[]{null};

        list.forEach((m) -> {
            if (((StringFilter) m.get("filtro")).filterOfString(filter)) {
                obj[0] = m;
            }
        });

        return (Map<String, Object>) obj[0];
    }
}
